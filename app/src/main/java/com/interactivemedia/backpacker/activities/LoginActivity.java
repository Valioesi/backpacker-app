package com.interactivemedia.backpacker.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.helpers.Preferences;
import com.interactivemedia.backpacker.helpers.Request;
import com.interactivemedia.backpacker.models.User;


public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private static final int SIGN_IN_REQUEST = 1;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        //add onClickListener for sign in button
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        progressBar = findViewById(R.id.progress_bar_login);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //start home activity, when account is not null (user already signed in)
        if (account != null) {
            startHomeActivity();
            //logout for testing purposes
           /* mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // ...
                        }
                    });*/
            //TODO: validate via Server, if necessary
        }
    }

    /**
     * this function starts the home activity (e.g. user is signed in)
     */
    private void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }


    /**
     * this function is called in onClickListener of sign in button, starts the sign in intent
     */
    private void signIn() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, SIGN_IN_REQUEST);
    }

    /**
     * this function ic called when the result of the sign in intent kicks in
     *
     * @param requestCode in this case we only have SING_IN_REQUEST, but might be used to differentiate between different intents
     * @param resultCode  signifies if everything worked
     * @param data        holds the intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == SIGN_IN_REQUEST) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    /**
     * handles, what happens after the attempted sign in
     * starts HomeActivity, when sign in was successful
     *
     * @param completedTask holds information about the sign in attempt
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            //show progress bar
            progressBar.setVisibility(View.VISIBLE);
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            //save token in Shared Preferences
            Preferences.saveIdToken(this, account.getIdToken());
            //let's make an api call with the token, so that the backend can check, if the user already exists in our db and can create it if necessary
            String jsonBody = "{ \"firstName\": \"" + account.getGivenName() +
                    "\", \"lastName\": \"" + account.getFamilyName() +
                    "\", \"googleId\": \"" + account.getId() +
                    "\", \"email\": \"" + account.getEmail() + "\"}";
            Log.d("User json", jsonBody);
            // TODO: uncomment later, once endpoint is up and running
            new PostUser().execute("/users", jsonBody);
            //startHomeActivity();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Sign in", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "There was an error signing in", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * this AsyncTask sends a post request to the users endpoint to create a new user, if it does not exists
     * if successful, the Home Activity is started
     */
    private class PostUser extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            return Request.post(getApplicationContext(), strings[0], strings[1]);
            //debug:    return Request.get(getApplicationContext(), "/users");
        }

        @Override
        protected void onPostExecute(String result) {
            //hide progress bar
            progressBar.setVisibility(View.GONE);
            if (result == null) {
                Toast.makeText(getApplicationContext(), "There was an Error logging in", Toast.LENGTH_LONG).show();
            } else {
                Log.d("JSON response: ", result);
                //use JsonObject instead of Gson to not have to define an exclusion strategy
                Gson gson = new Gson();
                User user = gson.fromJson(result, User.class);
                //save _id in shared preferences
                Preferences.saveUserId(getApplicationContext(), user.getId());

                Toast.makeText(getApplicationContext(), "Signed in successfully", Toast.LENGTH_LONG).show();
                //redirect to EditProfileActivity to give the user the option to edit his data
                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(intent);
                finish();
            }

        }
    }

}
