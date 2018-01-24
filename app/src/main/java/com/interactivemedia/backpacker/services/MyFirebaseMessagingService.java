package com.interactivemedia.backpacker.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.helpers.Preferences;
import com.interactivemedia.backpacker.helpers.Request;

/**
 * This service handles receiving push notifications from backend.
 * <p>
 * Created by vali_ on 20.01.2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        Log.d(TAG, "From: " + remoteMessage.getFrom());


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (remoteMessage.getData().containsKey("userId")) {
                String friendId = remoteMessage.getData().get("userId");
                String firstName = remoteMessage.getData().get("firstName");
                String lastName = remoteMessage.getData().get("lastName");
                Log.d(TAG, friendId);

                //we want to differentiate whether the notification came from NFC event or because the user was added as friend via mail
                //we do this by checking, when the last nfc event was
                long nfcEventTime = Preferences.getNfcEventTime(this);
                long currentTime = System.currentTimeMillis();
                if (nfcEventTime != 0 && nfcEventTime > currentTime - 30000) { //was recent nfc, because it was not more than 30 seconds ago
                    //in this case we will have no user actions and no notifications
                    //only the friend will be added in the background
                    addFriend(friendId);
                    Log.d(TAG, "came from nfc");
                    //reset preferences to false
                    Preferences.saveNfcEventTime(this, (long) 0);
                } else {
                    //notification came because of email
                    //now we want to show notification on phone, which will need the user to a new activity to share his locations as well

                    //only send notification, if user is logged in (user id not null in preferences)
                    if (Preferences.getUserId(this) != null) {
                        String notificationMessage = firstName + " " + lastName + " shared his/her locations with you";
                        sendNotification(notificationMessage, friendId);
                    }

                    Log.d(TAG, "came from email");
                }


                //TOKEN: fjtJ8AKJZGA:APA91bH_HU8N58ZFMAntQu2qdyJGPPQ5z6rztpYebX0g9HrOS-epEdoPlVkZvz3gXqJRJa2CBN4Uy5ycfSqK80crKK4RfkvS0Ts-ZzYs_XckbSSYsn1J3YhQAtd_eqpIqRx3YB3V8BNZ
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }


    /**
     * This function makes a PUT request to our API to share the locations of this user with a new friend.
     *
     * @param friendId The id of a user to share the locations with.
     */
    private void addFriend(String friendId) {
        //get id of logged in user
        String userId = Preferences.getUserId(this);
        String result = Request.put(this, "/users/" + userId + "/friends/" + friendId);
        if (result == null) {
            Log.e(TAG, "Error while adding friend");
        } else {
            Log.d(TAG, "Successfully added friend");
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody Message body we want to send.
     * @param friendId    Id of friend, with whom the user might share his locations. Gets passed with Pending intent.
     */
    private void sendNotification(String messageBody, String friendId) {
        //instead of an activity we will use a service to handle the intent
        Intent intent = new Intent(this, NotificationActionService.class);
        intent.putExtra(NotificationActionService.EXTRA_PARAM_FRIEND_ID, friendId);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.channel_id);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_backpack_2)
                        .setContentTitle("Notification from Wanderlust")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setVibrate(new long[]{100, 200})
                        .addAction(R.drawable.ic_person_add_black_24dp, "Share your's, too", pendingIntent)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }

}