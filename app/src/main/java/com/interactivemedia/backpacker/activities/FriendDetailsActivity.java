package com.interactivemedia.backpacker.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.adapters.FillLocationListsAdapter;
import com.interactivemedia.backpacker.helpers.Preferences;
import com.interactivemedia.backpacker.helpers.Request;
import com.interactivemedia.backpacker.models.Location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * This activity shows name and profile picture of a friend. In addition a list of
 * his or her favorite locations are shown. They can be filtered depending on a chosen country.
 * Via a click on a button and a popup, which asks the user to confirm, the user is able
 * to delete the friend.
 */
public class FriendDetailsActivity extends AppCompatActivity {

    private ArrayList<Location> friendsLocations;
    private FillLocationListsAdapter adapter;
    private ArrayAdapter<String> countryAdapter;
    private ListView lv_favoritePlaces;
    private ArrayList<String> countries;
    private Spinner spinnerCountry;
    private String country;


    private ImageView profilePicture;
    private TextView noLocations;
    private ConstraintLayout locationInfo;

    TreeSet sortedCountryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_details);

        //Get information out of IntentExtras
        Intent intent = getIntent();
        final String friendId = intent.getStringExtra("userId");
        String firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");
        final String friendName = firstName + " " + lastName;
        String imageUri = intent.getStringExtra("avatar");


        //Set Text in TextView
        TextView tv_friendName = findViewById(R.id.tv_friendName);
        tv_friendName.setText(friendName);

        //Find ImageView
        profilePicture = findViewById(R.id.iv_avatar);

        //Set profile picture of friend.
        if (imageUri != null) {
            Glide.with(getApplicationContext()).load(Request.DOMAIN_URL + imageUri).into(profilePicture);
        }


        friendsLocations = new ArrayList<>();

        //find list view, create adapter containing friend list and set adapter of list view
        lv_favoritePlaces = findViewById(R.id.lvFavoritePlaces);
        String adapterCallSource = "FriendDetailsActivity";
        adapter = new FillLocationListsAdapter(getApplicationContext(), R.layout.listitem_locations, friendsLocations, adapterCallSource);
        lv_favoritePlaces.setAdapter(adapter);

        //set on click listener to direct to LocationDetailsActivity
        lv_favoritePlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //we need to get the locations from the adapter, because they might have been filtered
                //therefore we can not us friendsLocations from this activity
                Location location = adapter.getLocations().get(i);
                //start details activity
                Intent intent = new Intent(getApplicationContext(), LocationDetailsActivity.class);
                intent.putExtra("locationGoogleId", location.getGoogleId());
                intent.putExtra("userId", friendId);
                intent.putExtra("userName", friendName);
                startActivity(intent);
            }
        });

        //Initializes Load of Location information from the backend
        loadLocations(friendId);


        //find Spinner
        spinnerCountry = findViewById(R.id.SpinnerCountry);


        //create "remove friend" button with onClickListener
        Button btn = findViewById(R.id.btnRemoveFriend);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                alertMessage(friendId);
            }
        });

    }


    /**
     * This function opens a dialog to confirm the removal of the friend
     * @param friendId id of the friend that is to bo deleted
     */
    public void alertMessage(final String friendId) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        deleteFriend(friendId);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing
                        // Toast.makeText(FriendDetailsActivity.this, "No Clicked",
                        //        Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        //Show "warning" Dialog, if user is sure about deleting friend.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }



    /**
     * This method calls {@link GetLocationOfFriend} to get the locations of the friend.
     * @param friendId id of friend
     */
    private void loadLocations(String friendId) {
        new GetLocationOfFriend().execute("/locations?users=" + friendId);
    }


    /**
     * This {@link AsyncTask} makes an API request to get the locations of the friend.
     */
    @SuppressLint("StaticFieldLeak")
    private class GetLocationOfFriend extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            Log.i("doInBackground", "getLocationOfFriend");
            return Request.get(getApplicationContext(), strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Log.d("Error: ", "Error in GET Request");
                Toast.makeText(getApplicationContext(), "There was an error loading your friend's locations", Toast.LENGTH_LONG).show();
            } else if (result.equals("401")) {
                //unauthorized -> we need new token -> redirect to Login Activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            } else {
                Log.i("JSON response: ", result);
                //we need to handle the conversion from json string to User Object, because user in this json is in format
                //user : { id: ..., firstName: ..., lastName:..}    instead of just user: id
                //therefore we want to ignore user in the deserialization process
                //IMPORTANT! this might need to be changed later to a different approach, if the user data is needed here
                //a different approach could be a second Location class

                //we use the gson builder to add an exclusion strategy, which leads to gson excluding the field user
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.addDeserializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getName().equals("user");      //exclusion happening here!
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                });
                Gson gson = gsonBuilder.create();
                //type token is used to load into array list, see: https://stackoverflow.com/questions/12384064/gson-convert-from-json-to-a-typed-arraylist
                ArrayList<Location> locations = gson.fromJson(result, new TypeToken<ArrayList<Location>>() {
                }.getType());

                //only show favorite locations
                for(Location location : locations){
                    if(location.isFavorite()){
                        friendsLocations.add(location);
                    }
                }

                adapter.setLocations(friendsLocations);
                adapter.notifyDataSetChanged();

                //If friend has locations, fill the spinner with information
                if (friendsLocations != null && friendsLocations.size() > 0) {
                    fillSpinner();
                }

                //If friend has no locations saved, call function to set different layouts visible
                else {
                    setLayouts();
                }

            }
        }

    }


    /**
     * This method sets the layout, which contains to {@link Spinner}, to be invisible.
     */
    private void setLayouts() {
        locationInfo = findViewById(R.id.layout_locationInfo);
        noLocations = findViewById(R.id.noLocations);

        locationInfo.setVisibility(View.GONE);
        noLocations.setVisibility(View.VISIBLE);

    }


    /**
     * This function fills the {@link Spinner} with the values of the friend's location.
     */
    private void fillSpinner() {
        countries = new ArrayList<>();
        HashSet hashCountryList = new HashSet<>();
        sortedCountryList = new TreeSet();

        for (int i = 0; i < friendsLocations.size(); i++) {
            for (Location singleLocation : friendsLocations) {
                country = singleLocation.getCountry();
                Log.e("ForEach Location", country);
                hashCountryList.add(country);
            }
            hashCountryList.add("- All locations -");
        }

        //Hashset into TreeSet to sort it.
        sortedCountryList.addAll(hashCountryList);


        //countries = FillLocationListsAdapter.sortedCountries;
        countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(countryAdapter);

        countries.clear();
        countries.addAll(sortedCountryList);
        countryAdapter.notifyDataSetChanged();


        //what happens when user a country in the spinner is chosen --> Start Filtering friends location
        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.setLocations(friendsLocations);
                //Log.d("onitem", String.valueOf(friendsLocations));
                Log.d("ONITMEClick", countries.get(position));
                adapter.getFilter().filter(countries.get(position), new Filter.FilterListener() {

                    @Override
                    public void onFilterComplete(int count) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    /**
     * This function calls {@link DeleteFriend} after click on "delete friend" button.
     * @param friendId id of riend to be deleted
     */
    private void deleteFriend(String friendId) {
        String userId = Preferences.getUserId(this);
        //String meId = "5a323b82654ba50ef8d2b8c2";
        new DeleteFriend(adapter).execute("/users/" + userId + "/friends/" + friendId);
    }

    /**
     * This {@link AsyncTask} performs an API request to delete the friend.
     */
    @SuppressLint("StaticFieldLeak")
    private class DeleteFriend extends AsyncTask<String, Integer, String> {

        FillLocationListsAdapter adapter;

        DeleteFriend(FillLocationListsAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.i("doInBackground", "deleteFriend");
            return Request.delete(getApplicationContext(), strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Log.d("Error: ", "Error in DELETE Request");
                Toast.makeText(getApplicationContext(), "There was an error deleting your friend", Toast.LENGTH_LONG).show();
            } else if (result.equals("401")) {
                //unauthorized -> we need new token -> redirect to Login Activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            } else {
                Log.i("JSON response: ", result);
                Toast.makeText(getApplicationContext(), "Friend deleted successfully", Toast.LENGTH_LONG).show();
                //return to to home activity
                finish();
            }
        }
    }
}


