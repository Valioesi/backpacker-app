package com.interactivemedia.backpacker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.fragments.LocationDetailsFragment;

import java.util.ArrayList;

/**
 * This activity shows description, categories and images of a location.
 * If the (real) location was added by multiple friends, the user can navigate
 * via tabs and a {@link ViewPager} (swiping) between the different information.
 * The UI of a single location is implemented in the {@link LocationDetailsFragment}.
 */
public class LocationDetailsActivity extends AppCompatActivity {

    private String locationGoogleId;

    private ArrayList<String> userIds;

    private ArrayList<String> userNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = findViewById(R.id.tabs);

        Intent intent = getIntent();
        //get location id from intent
        locationGoogleId = intent.getStringExtra("locationGoogleId");

        //check if an array was passed or just a single id
        if(intent.hasExtra("userIdArray")){
            userIds = intent.getStringArrayListExtra("userIdArray");
            userNames = intent.getStringArrayListExtra("userNameArray");

            if(intent.getStringArrayListExtra("userIdArray").size() > 1){
                //add a tab for every user
                for(String name: userNames){
                    tabLayout.addTab(tabLayout.newTab().setText(name));
                }

            } else {
                //in this case we only have one user, so we want to hide the tabs
                tabLayout.setVisibility(View.GONE);
            }
        } else {
            //in this case we only have one user, so we want to hide the tabs
            tabLayout.setVisibility(View.GONE);

            userIds = new ArrayList<>();
            //because in this case we only have one user we add it to the array list
            userIds.add(intent.getStringExtra("userId"));

            //if has name add it to array
            userNames = new ArrayList<>();
            if(intent.hasExtra("userName")){
                userNames.add(intent.getStringExtra("userName"));
            }
        }



        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());



        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));




    }




    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a LocationDetailsFragment

            //check if we got names (in case of multiple users)
            String name = null;
            if(userNames != null && userNames.size() > 0){
                name = userNames.get(position);
            }
            return LocationDetailsFragment.newInstance(locationGoogleId, userIds.get(position), name);
        }

        @Override
        public int getCount() {
            // Show as many pages as there are locations.
            return userIds.size();
        }
    }


}
