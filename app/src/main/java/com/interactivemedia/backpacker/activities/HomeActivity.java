package com.interactivemedia.backpacker.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.fragments.MyFriendsFragment;
import com.interactivemedia.backpacker.fragments.MyLocationsFragment;
import com.interactivemedia.backpacker.fragments.MyMapFragment;
import com.interactivemedia.backpacker.fragments.SettingsFragment;

public class HomeActivity extends AppCompatActivity {

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

        //this serves as an example of how to use Request helper class
        //can be deleted later
      /*  new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("Json Response",Request.get("/posts"));
                Log.i("Json Response",Request.post("/posts", "{ \"title\": \"what\", \"body\": \"blub\"}"));
            }
        }).start();*/
    }


}
