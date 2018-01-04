package com.interactivemedia.backpacker.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.helpers.Request;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private static final int SIGN_IN_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        Log.d("server client id", getString(R.string.server_client_id));
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
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
          /*  mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // ...
                        }
                    });
                    */
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
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Toast.makeText(this, "Signed in successfully", Toast.LENGTH_LONG).show();
            //save token in Shared Preferences
            saveIdTokenAsPreference(account.getIdToken());
            //let's make an api call with the token, so that the backend can check, if the user already exists in our db and can create it if necessary
            String jsonBody = "{ token: " + account.getIdToken() + "}";
            // TODO: uncomment later, once endpoint is up and running
            //new SendToken().execute("/users", jsonBody);
           // Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
           // startActivity(intent);
            startHomeActivity();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Sign in", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "There was an error signing in", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * this function saves the id token (taken from our GoogleSignInAccount in the SharedPreference
     *
     * @param token is a String, taken from account
     */
    private void saveIdTokenAsPreference(String token) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.shared_preference_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.saved_token), token);
        editor.apply();
        //this will log the token saved in shared preferences
        Log.d("Preferences", sharedPreferences.getString(getString(R.string.saved_token), "key not present"));
    }

    /**
     * this AsyncTask sends the token via post request to the server
     * if successful, the Home Activity is started
     */
    private class SendToken extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            return Request.post(strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("JSON response: ", result);
            if (result.equals("error")) {
                Toast.makeText(getApplicationContext(), "There was an Error logging in", Toast.LENGTH_LONG).show();
            } else {
                //redirect to EditProfileActivity to give the user the option to edit his data
                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(intent);
            }

        }
    }

}
