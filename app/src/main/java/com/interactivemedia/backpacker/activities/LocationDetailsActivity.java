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
import android.util.Log;
import android.view.View;

import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.fragments.LocationDetailsFragment;

import java.util.ArrayList;

public class LocationDetailsActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private String locationGoogleId;

    private ArrayList<String> userIds;

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
            ArrayList<String> userNames = intent.getStringArrayListExtra("userNameArray");

            if(intent.getStringArrayListExtra("userIdArray").size() > 1){
                //add a tab for every user
                for(String name: userNames){
                    tabLayout.addTab(tabLayout.newTab().setText(name));
                }
            }
        } else {
            //in this case we only have one user, so we want to hide the tabs
            tabLayout.setVisibility(View.GONE);

            userIds = new ArrayList<>();
            //because in this case we only have one user we add it to the array list
            userIds.add(intent.getStringExtra("userId"));
        }



        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());



        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));




    }




    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a LocationDetailsFragment
            return LocationDetailsFragment.newInstance(locationGoogleId, userIds.get(position));
        }

        @Override
        public int getCount() {
            // Show as many pages as there are locations.
            return userIds.size();
        }
    }


}
