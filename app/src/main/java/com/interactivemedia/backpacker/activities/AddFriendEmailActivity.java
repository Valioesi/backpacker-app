package com.interactivemedia.backpacker.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.activities.HomeActivity;
import com.interactivemedia.backpacker.helpers.Preferences;
import com.interactivemedia.backpacker.helpers.Request;
import com.interactivemedia.backpacker.models.User;

/**
 * This activity is adding a new friend via Email if nfc is not available or user presses "Not working" Button.
 * {@link AddFriendNfcActivity} will redirect to this activity, if NFC is not available.
 * The user can search for another user via EMail. After a successful search the found
 * user, with whom the locations can now be shared, is shown. That user will then receive
 * a push notification and can share his locations as well.
 */
public class AddFriendEmailActivity extends AppCompatActivity {

    private User newFriend;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_email);
        progressBar = findViewById(R.id.progress_bar);

    }

    /**
     * This function is called upon click of "Search User" button and starts the async task to find users by email address.
     *
     * @param view the button "Search User"
     */
    public void searchUser(View view) {
        EditText editText = findViewById(R.id.edit_text_email);
        String email = editText.getText().toString();
        //show progess bar
        progressBar.setVisibility(View.VISIBLE);
        String query = "?email=" + email;
        new FindUser().execute("/users" + query);
    }

    /**
     * This function is called upon click of "share locations" button and starts the async task
     * to add the user as a friend (or more precisely "to share locations" with this user).
     *
     * @param view the button "Share Locations"
     */
    public void shareLocations(View view) {
        //get user id from shared preferences
        String userId = Preferences.getUserId(this);
        if (userId != null) {
            //userId is the id of the logged in user, newFriend is the found user
            new AddFriend().execute("/users/" + userId + "/friends/" + newFriend.getId() + "?notify=true");
        }
    }

    /**
     * Async Task to get User Information from Backend.
     */
    @SuppressLint("StaticFieldLeak")
    private class FindUser extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            return Request.get(getApplicationContext(), strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(getApplicationContext(), "There was an Error while searching the user", Toast.LENGTH_LONG).show();
            } else if (result.equals("401")) {
                //unauthorized -> we need new token -> redirect to Login Activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            } else {
                Log.d("JSON response: ", result);
                Gson gson = new Gson();
                //an array is returned, therefore we need to parse it with User[]
                User[] userArray = gson.fromJson(result, User[].class);
                //but it is only one value in that array, the found user
                newFriend = userArray[0];
                //get text views
                TextView textViewName = findViewById(R.id.text_view_name);
                TextView textViewLocations = findViewById(R.id.text_view_number_locations);

                //hide progress bar
                progressBar.setVisibility(View.GONE);
                //if there is no user found, then display appropriate text
                if (newFriend == null || newFriend.getId() == null) {
                    findViewById(R.id.text_view_no_user).setVisibility(View.VISIBLE);
                } else {
                    //...and display user info
                    String name = newFriend.getFirstName() + " " + newFriend.getLastName();
                    textViewName.setText(name);
                    //load profile picture into image view
                    ImageView imageView = findViewById(R.id.image_view_profile_picture);
                    if (newFriend.getAvatar() != null) {
                        Glide.with(getApplicationContext()).load(Request.DOMAIN_URL + newFriend.getAvatar()).into(imageView);
                    } else {
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_grey_180dp));
                    }

                    //show button to share locations
                    findViewById(R.id.button_share_locations).setVisibility(View.VISIBLE);
                }

            }
        }

    }

    /**
     * This Async Task sets the friends Id into the users' Array "friends",
     * so both users can see each others locations.
     */
    @SuppressLint("StaticFieldLeak")
    private class AddFriend extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            return Request.put(getApplicationContext(), strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(getApplicationContext(), "There was an Error sharing your locations", Toast.LENGTH_LONG).show();
            } else if (result.equals("401")) {
                //unauthorized -> we need new token -> redirect to Login Activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Log.d("JSON response: ", result);
                Toast.makeText(getApplicationContext(), "Successfully shared your locations with " + newFriend.getFirstName(), Toast.LENGTH_LONG).show();
                //return to home activity
                finish();
            }
        }

    }
}
