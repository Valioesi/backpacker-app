package com.interactivemedia.backpacker.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.interactivemedia.backpacker.helpers.Preferences;
import com.interactivemedia.backpacker.helpers.Request;


/**
 * This service handles the action of the notification.
 * If the user presses the "Share locations, too" button, this service will be called and makes it
 * perform the appropriate API request.
 */
public class NotificationActionService extends IntentService {


    // TODO: Rename parameters
    public static final String EXTRA_PARAM_FRIEND_ID = "com.interactivemedia.backpacker.services.extra.PARAM_FRIEND_ID";

    public NotificationActionService() {
        super("NotificationActionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            //dismiss notification
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);
            String friendId = intent.getStringExtra(EXTRA_PARAM_FRIEND_ID);
            addFriend(friendId);
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
            Log.e("Notification service", "Error while adding friend");
        } else {
            Log.d("Notification service", "Successfully added friend");
        }
    }


}
