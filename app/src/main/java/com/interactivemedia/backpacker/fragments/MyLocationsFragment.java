package com.interactivemedia.backpacker.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.activities.AddLocationActivity;
import com.interactivemedia.backpacker.activities.LoginActivity;
import com.interactivemedia.backpacker.adapters.FillLocationListsAdapter;
import com.interactivemedia.backpacker.activities.LocationDetailsActivity;
import com.interactivemedia.backpacker.helpers.Preferences;
import com.interactivemedia.backpacker.helpers.Request;
import com.interactivemedia.backpacker.models.Location;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MyLocationsFragment extends Fragment {


    private ArrayList<Location> mylocations;
    private FillLocationListsAdapter fillListAdapter;
    private String adapterCallSource = "MyLocationsFragment";
    private String userId;
    private ListView lvMyLocations;
    private TextView tv_noOwnLocations;
    private Context context;

    public MyLocationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment MyLocationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyLocationsFragment newInstance() {
        return new MyLocationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_list, container, false);

        context = getContext();

        //find Layout Components
        tv_noOwnLocations=view.findViewById(R.id.noOwnLocations);
        lvMyLocations = view.findViewById(R.id.lv_myloc);

        mylocations = new ArrayList<>();

        userId = Preferences.getUserId(context);
        //userId = "5a46519c6de6a50f3c46efba";

        //Create Adapter containing location list
        String adapterCallSource = "MyLocationsFragment";
        fillListAdapter = new FillLocationListsAdapter(context, R.layout.listitem_locations, mylocations, adapterCallSource);
        lvMyLocations.setAdapter(fillListAdapter);


        //check, if user is online
        if(Request.hasInternetConnection(context)){
            //Loads locations by calling AsyncTask
            loadLocations();
        } else {
            //show sad backpack
            view.findViewById(R.id.main_layout).setVisibility(View.GONE);
            view.findViewById(R.id.no_internet).setVisibility(View.VISIBLE);
        }


        //find add location button and set on clock method to open new activity
        final ImageButton addLocationButton = view.findViewById(R.id.add_location_button);
        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddLocationActivity();
            }
        });

        //show Location Details after choosing an element
        lvMyLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //start details activity
                Intent intent = new Intent(context, LocationDetailsActivity.class);
                intent.putExtra("locationGoogleId", mylocations.get(i).getGoogleId());
                intent.putExtra("userId", userId);

                startActivity(intent);
            }
        });



        return view;
    }

    //use the onResume method to load for first time or reload locations(e.g. after one has been added)
    @Override
    public void onResume() {
        super.onResume();
        //check, if user is online
        if(Request.hasInternetConnection(context)){
            mylocations.clear();
            //Loads locations by calling AsyncTask
            loadLocations();
        } else {
            //show sad backpack
            getView().findViewById(R.id.main_layout).setVisibility(View.GONE);
            getView().findViewById(R.id.no_internet).setVisibility(View.VISIBLE);
        }

    }

    /**
     * This function calls {@link GetLocations} to load the user's locations.
     */
    private void loadLocations() {
        //call AsycTask to the locations of one user to show from server
        new GetLocations(fillListAdapter).execute("/locations?users=" + userId);
    }




    /**
     * This AsyncTask makes a call to our API to get locations, which will then
     * be used to fill the list.
     */
    @SuppressLint("StaticFieldLeak")
    private class GetLocations extends AsyncTask<String, Integer, String> {

        FillLocationListsAdapter adapter;

        GetLocations(FillLocationListsAdapter adapter) {
            this.adapter = adapter;
        }


        @Override
        protected String doInBackground(String... strings) {
            Log.i("DoInBackground", "Started in Class GetLocations");
            return Request.get(context, strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Log.d("Error: ", "Error in GET Request");
                Toast.makeText(context, "There was an Error loading your locations", Toast.LENGTH_LONG).show();
            } else if (result.equals("401")){
                //unauthorized -> we need new token -> redirect to Login Activity
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            }else {
                Log.i("JSON locations: ", result);
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
                mylocations = gson.fromJson(result, new TypeToken<ArrayList<Location>>() {
                }.getType());

                fillListAdapter.setLocations(mylocations);
                fillListAdapter.notifyDataSetChanged();

                //check if locations are empty and set different Layout components
                if (mylocations == null || mylocations.size() == 0) {
                    setLayout();
                }
            }

        }
    }
    /**
     * This opens {@link AddLocationActivity}.
     */
    private void openAddLocationActivity(){
        Intent intent = new Intent(context, AddLocationActivity.class);
        startActivity(intent);
    }


    /**
     * This function makes the list invisible.
     */
    private void setLayout() {
        //set visibility of ListView gone and of TextView visible to see the message
        lvMyLocations.setVisibility(View.GONE);

        tv_noOwnLocations.setVisibility(View.VISIBLE);
    }
    }
