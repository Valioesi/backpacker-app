package com.interactivemedia.backpacker.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.helpers.Preferences;
import com.interactivemedia.backpacker.helpers.Request;
import com.interactivemedia.backpacker.models.User;


public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private static final int SIGN_IN_REQUEST = 1;
    private ProgressBar progressBar;
    private ConstraintLayout layout;
    private boolean neverSignedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //this is needed for android 8
        createNotificationChannel();

        progressBar = findViewById(R.id.progress_bar_login);
        //show progress bar
        progressBar.setVisibility(View.VISIBLE);
        //and hide rest
        layout = findViewById(R.id.login_layout);
        layout.setVisibility(View.GONE);

        // Configure sign-in to request the user's ID, email address, ID token and basic
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
                if (Request.hasInternetConnection(getApplicationContext())) {
                    signIn();
                } else {
                    Toast.makeText(getApplicationContext(), "It seems like you have no internet connection", Toast.LENGTH_LONG).show();
                }
            }
        });

        //check if there is already an id token (user id would not work, because
        // we set it to null after logout) saved in preferences and set boolean accordingly
        //we need this to either redirect to Edit Profile or Home
        if (Preferences.getIdToken(this) == null) {
            neverSignedIn = true;
        } else {
            neverSignedIn = false;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        //use silent sign in to sign in, if the user had already signed in in the past
        //we use the silent sign in approach to be able to refresh the token
        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
            @Override
            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                handleSilentSignInResult(task);
            }
        });
    }

    /**
     * this function starts the home activity (e.g. user is signed in)
     */
    private void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }


    /**
     * handles, what happens after the attempted silent sign in
     * We want to refresh our ID token.
     *
     * @param completedTask holds information about the sign in attempt
     */
    private void handleSilentSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //save token in Shared Preferences
            Preferences.saveIdToken(this, account.getIdToken());
            startHomeActivity();
            finish();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Sign in", "silent signInResult:failed code=" + e.getStatusCode());

            //if code is 4, we need to sign in
            if (e.getStatusCode() == 4) {
                //show login screen and hide progress bar
                layout = findViewById(R.id.login_layout);
                progressBar.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);

            } else {
                Toast.makeText(this, "There was an error signing in", Toast.LENGTH_LONG).show();
            }
        }
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
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Sign in", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "There was an error signing in", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * This function creates a notification channel. It is required for Android 8.
     */
    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // The id of the channel.
            String id = getString(R.string.channel_id);
            // The user-visible name of the channel.
            CharSequence name = getString(R.string.channel_name);
            // The user-visible description of the channel.
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = null;
            mChannel = new NotificationChannel(id, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }


    }


    /**
     * this AsyncTask sends a post request to the users endpoint to create a new user, if it does not exists
     */
    private class PostUser extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            return Request.post(getApplicationContext(), strings[0], strings[1]);
            //debug:    return Request.get(getApplicationContext(), "/users");
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(getApplicationContext(), "There was an Error logging in", Toast.LENGTH_LONG).show();
            } else {
                Log.d("JSON response: ", result);
                //use JsonObject instead of Gson to not have to define an exclusion strategy
                Gson gson = new Gson();
                User user = gson.fromJson(result, User.class);
                //save _id in shared preferences
                Preferences.saveUserId(getApplicationContext(), user.getId());

                //now that the user is logged in we will also send the fcm token to the server
                String fcmToken = FirebaseInstanceId.getInstance().getToken();
                if (fcmToken != null) {
                    Log.d("FCM Token in Login", fcmToken);
                    String jsonBody = "{ \"deviceToken\": \"" + fcmToken + "\"}";
                    new PatchFcmToken().execute("/users/" + user.getId(), jsonBody);
                } else {
                    if (neverSignedIn) {
                        //redirect to EditProfileActivity to give the user the option to edit his data
                        Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                        startActivity(intent);
                    } else {
                        startHomeActivity();
                    }
                    finish();
                }
            }

        }
    }

    /**
     * This AsyncTask sends a patch request to the users endpoint to add the fcm token.
     * If successful, the Home Activity is started.
     */
    private class PatchFcmToken extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            return Request.patch(getApplicationContext(), strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            //hide progress bar
            progressBar.setVisibility(View.GONE);
            if (result == null) {
                Toast.makeText(getApplicationContext(), "There was an Error logging in", Toast.LENGTH_LONG).show();
            } else {


                Toast.makeText(getApplicationContext(), "Signed in successfully", Toast.LENGTH_LONG).show();

                if (neverSignedIn) {
                    //redirect to EditProfileActivity to give the user the option to edit his data
                    Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                    startActivity(intent);
                } else {
                    startHomeActivity();
                }

                finish();
            }

        }
    }

}
