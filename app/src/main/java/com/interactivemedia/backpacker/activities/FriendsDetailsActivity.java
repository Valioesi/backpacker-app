package com.interactivemedia.backpacker.activities;

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
import com.interactivemedia.backpacker.helpers.FillLocationListsAdapter;
import com.interactivemedia.backpacker.helpers.Request;
import com.interactivemedia.backpacker.models.Location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

public class FriendsDetailsActivity extends AppCompatActivity {

    //With the String adapterCallSource you can handle the functions in the Adapter, e.G. you can't see and interactive with the favorite button
    private String adapterCallSource = "FriendsDetailsActivity";
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
        String firstName=intent.getStringExtra("firstName");
        String lastName=intent.getStringExtra("lastName");
        String friendName = firstName + " " + lastName;
        String imageUri = intent.getStringExtra("avatar");


        //Set Text in TextView
        TextView tv_friendName = findViewById(R.id.tv_friendName);
        tv_friendName.setText(friendName);

        //Find ImageView
        profilePicture = findViewById(R.id.iv_avatar);

        //Set profile picture of friend.
        if(imageUri!= null){
            Glide.with(getApplicationContext()).load(Request.DOMAIN_URL + imageUri).into(profilePicture);
        }



        friendsLocations=new ArrayList<>();

        //find list view, create adapter containing friend list and set adapter of list view
        lv_favoritePlaces = findViewById(R.id.lvFavoritePlaces);
        adapter = new FillLocationListsAdapter(getApplicationContext(), R.layout.listitem_locations, friendsLocations, adapterCallSource);
        lv_favoritePlaces.setAdapter(adapter);


        //Initializes Load of Location information from the backend
        loadLocations(friendId);



        //find Spinner
        spinnerCountry = findViewById(R.id.SpinnerCountry);


        //create "remove friend" button with onClickListener
        Button btn = (Button) findViewById(R.id.btnRemoveFriend);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                alertMessage(friendId);
            }
        });

    }







    //remove friend confirmation dialog
    //taken from: http://www.androidhub4you.com/2012/09/alert-dialog-box-or-confirmation-box-in.html
    public void alertMessage(final String userId) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        deleteFriend(userId);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing
                        // Toast.makeText(FriendsDetailsActivity.this, "No Clicked",
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





    //initializes Request to load Locations of a friend.
    private void loadLocations(String userId) {
        new GetLocationOfFriend().execute("/locations?users=" + userId);
        //new GetLocationOfFriend().execute("/users/" + userId + "/friends");
    }



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
                Toast.makeText(getApplicationContext(), "There was an Error loading your locations", Toast.LENGTH_LONG).show();
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
                ArrayList<Location> locations = gson.fromJson(result, new TypeToken<ArrayList<Location>>(){}.getType());

                friendsLocations.addAll(locations);

                adapter.setLocations(friendsLocations);
                adapter.notifyDataSetChanged();


                //If friend has locations, fill the spinner with information
                if (friendsLocations!=null && friendsLocations.size()>0){
                    fillSpinner();
                }

                //If friend has no locations saved, call function to set different layouts visible
                else {
                    setLayouts();
                }


                //TODO: Check if locations are empty.
                //check if friends are not empty
        //                if (mylocations != null && mylocations.length != 0) {
        //                    addMarkersForAllUsers();
        //                    configureInfoWindow();
            }
        }

    }

    //set layout with spinner to invisible
    private void setLayouts() {
        ConstraintLayout locationInfo = findViewById(R.id.layout_locationInfo);
        noLocations = findViewById(R.id.noLocations);

        locationInfo.setVisibility(View.GONE);
        noLocations.setVisibility(View.VISIBLE);

    }

    //Fill Spinner with values from Friend's Location
    private void fillSpinner() {
        countries = new ArrayList<>();
        HashSet hashCountryList = new HashSet<>();
        sortedCountryList = new TreeSet();

        for (int i = 0; i < friendsLocations.size(); i++) {
            for (Location singleLocation : friendsLocations) {
                country = singleLocation.getCountry();
                Log.e ("ForEach Location", country);
                hashCountryList.add(country);
            }
            hashCountryList.add("- All locations -");
        }

        //Hashset into TreeSet to sort it.
        sortedCountryList.addAll(hashCountryList);



        //countries = FillLocationListsAdapter.sortedCountries;
        countryAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, countries);
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
                Log.d ("ONITMEClick", countries.get(position));
                adapter.getFilter().filter(countries.get(position), new Filter.FilterListener(){

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

    //delete friend after pushing the button --> Send request to backend.
    private void deleteFriend(String userId) {
        //TODO: here you have to exchange the value with the userId ("me")
        String meId = "5a323b82654ba50ef8d2b8c2";
        new DeleteFriend(adapter).execute("/" +meId+"/friends/"+userId);
    }

    private class DeleteFriend extends AsyncTask <String, Integer, String> {

        FillLocationListsAdapter adapter;

        public DeleteFriend(FillLocationListsAdapter adapter) {
            this.adapter=adapter;
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
                Toast.makeText(getApplicationContext(), "There was an Error deleting your friend", Toast.LENGTH_LONG).show();
            } else {
                Log.i("JSON response: ", result);
                Toast.makeText(getApplicationContext(), "Friend deleted successfully", Toast.LENGTH_LONG).show();
            }
        }
    }
}


