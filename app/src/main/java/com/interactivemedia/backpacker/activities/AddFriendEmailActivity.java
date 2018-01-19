package com.interactivemedia.backpacker.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.activities.HomeActivity;
import com.interactivemedia.backpacker.helpers.Preferences;
import com.interactivemedia.backpacker.helpers.Request;
import com.interactivemedia.backpacker.models.User;

public class AddFriendEmailActivity extends AppCompatActivity {

    private User newFriend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_email);
    }

    /**
     * this function is called upon click of search user button and starts the async task to find users by email address
     *
     * @param view the button
     */
    public void searchUser(View view) {
        EditText editText = findViewById(R.id.edit_text_email);
        String email = editText.getText().toString();
        new FindUser().execute("/users?email=" + email);
    }

    /**
     * this function is called upon click of share locations button and starts the async task
     * to add the user as friend (or better: share locations with this user)
     * @param view the button
     */
    public void shareLocations(View view) {
        //get user id from shared preferences
        String userId = Preferences.getUserId(this);
        if(userId != null){
            //userId is the id of the logged in user, newFriend is the found user
            new AddFriend().execute("/users/" + userId + "/friends/" + newFriend.getId());
        }
    }

    private class FindUser extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            return Request.get(getApplicationContext(), strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(getApplicationContext(), "There was an Error while searching the user", Toast.LENGTH_LONG).show();
            } else {
                Log.d("JSON response: ", result);
                Gson gson = new Gson();
                newFriend = gson.fromJson(result, User.class);
                //get text views
                TextView textViewName = findViewById(R.id.text_view_name);
                TextView textViewLocations = findViewById(R.id.text_view_number_locations);

                //...and display user info
                String name = newFriend.getFirstName() + " " + newFriend.getLastName();
                textViewName.setText(name);
                String locationInfo = newFriend.getLocations().size() + " Locations";
                textViewLocations.setText(locationInfo);

                //load profile picture into image view
                ImageView imageView = findViewById(R.id.image_view_profile_picture);
                Glide.with(getApplicationContext()).load(Request.DOMAIN_URL + newFriend.getAvatar()).into(imageView);

            }
        }

    }


    private class AddFriend extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            return Request.put(getApplicationContext(), strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(getApplicationContext(), "There was an Error sharing your locations", Toast.LENGTH_LONG).show();
            } else {
                Log.d("JSON response: ", result);
                Toast.makeText(getApplicationContext(), "Successfully shared your locations with " + newFriend.getFirstName(), Toast.LENGTH_LONG).show();
                //return to home activity
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        }

    }
}
