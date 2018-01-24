package com.interactivemedia.backpacker.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.fragments.MyFriendsFragment;
import com.interactivemedia.backpacker.fragments.MyLocationsFragment;
import com.interactivemedia.backpacker.fragments.MyMapFragment;
import com.interactivemedia.backpacker.fragments.SettingsFragment;
import com.interactivemedia.backpacker.helpers.Preferences;
import com.interactivemedia.backpacker.helpers.Request;

public class HomeActivity extends AppCompatActivity {

    private Context context;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        //this is where the fragments are chosen depending on which menu item is selected
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_map:
                    selectedFragment = MyMapFragment.newInstance();
                    break;
                case R.id.navigation_list:
                    selectedFragment = MyLocationsFragment.newInstance();
                    break;
                case R.id.navigation_friends:
                    selectedFragment = MyFriendsFragment.newInstance();
                    break;
                case R.id.navigation_more:
                    selectedFragment = SettingsFragment.newInstance();
                    break;
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, selectedFragment);
            transaction.commit();
            return true;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, MyMapFragment.newInstance());
        transaction.commit();

        context = this;

        //check if the user has internet and friends in the shared preferences, that need to be added
        if(Request.hasInternetConnection(this)){
            String[] friendIds = Preferences.getArrayOfFriendIds(this);
            if(friendIds != null){
                String userId = Preferences.getUserId(this);

                //for every id make a request to add him as friend
                for(String friendId : friendIds){
                    if(!friendId.equals("")){
                        new AddFriend().execute("/users/" + userId + "/friends/" + friendId + "?notify=true");
                    }
                }
            }
        }


    }

    private class AddFriend extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            return Request.put(context, strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(context, "There was an Error exchanging your locations", Toast.LENGTH_LONG).show();
            } else if (result.equals("401")) {
                //unauthorized -> we need new token -> redirect to Login Activity
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            } else {
                Log.d("JSON response: ", result);
                Toast.makeText(context, "You have successfully added your friend from earlier", Toast.LENGTH_SHORT).show();
                //reset friend ids to null
                Preferences.saveFriendId(context, null);
            }
        }

    }

}
