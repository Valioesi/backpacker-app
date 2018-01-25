package com.interactivemedia.backpacker.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.interactivemedia.backpacker.helpers.Preferences;
import com.interactivemedia.backpacker.helpers.Request;

/**
 * This service handles everything regarding the Firebase Cloud Messaging token.
 * This class is based on the FCM quickstart project: https://github.com/firebase/quickstart-android
 *
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        //only send token, if user is logged in
        if(Preferences.getUserId(this) != null){
            sendRegistrationToServer(refreshedToken);
        }
    }

    /**
     * This function sends the fcm token to the server.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        String jsonBody = "{ \"deviceToken\": \"" + token + "\"}";
        //get user id
        String userId = Preferences.getUserId(this);
        String result = Request.patch(this, "/users/" + userId, jsonBody);
        if(result == null){
            Log.e(TAG, "Error while sending token to server");
        } else {
            Log.d(TAG, "Saved FCM token successfully");
        }
    }
}