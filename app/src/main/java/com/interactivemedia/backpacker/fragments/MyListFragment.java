package com.interactivemedia.backpacker.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.activities.AddLocationActivity;
import com.interactivemedia.backpacker.helpers.FillListAdapter;
import com.interactivemedia.backpacker.helpers.Request;
import com.interactivemedia.backpacker.models.Location;



/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MyListFragment extends Fragment {


    private Location[] mylocations;
    private FillListAdapter fillListAdapter;


    public MyListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment MyListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyListFragment newInstance() {
        return new MyListFragment();
    }

    /**
     * Fills Parts of listitem_mylist.xml with dummy texts from a hardCoded String Array
     * Sources: https://stackoverflow.com/questions/28772909/listview-with-custom-adapter-in-fragment
     *
     * @param savedInstanceState
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_list, container, false);


        ListView lvMyLocations = view.findViewById(R.id.lv_myloc);
        mylocations = new Location[]{};

        //Create Adapter containing location list
        fillListAdapter = new FillListAdapter(getContext(), R.layout.listitem_mylist, mylocations);
        lvMyLocations.setAdapter(fillListAdapter);

        //Loads locations by calling AsyncTask
        loadLocations();


        //find add location button and set on clock method to open new activity
        final ImageButton addLocationButton = view.findViewById(R.id.add_location_button);
        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddLocationActivity();
            }
        });

//        lvMyLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(), "Stop Clicking me", Toast.LENGTH_SHORT).show();
//                if (!isFavorite) {
//                    imageViewButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
//                    isFavorite =true;
//                } else {
//                    imageViewButton.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
//                    isFavorite=false;
//                }
//
//            }
//        });




        return view;
    }


    private void loadLocations() {
        //call AsycTask to the locations of one user to show from server
        //TODO: instead of a defined userid we will insert the userid of the owner of the app
        new GetLocations(fillListAdapter).execute("/locations?users=5a46519c6de6a50f3c46efba");
    }




    /**
     * this AsyncTask makes a call to our API to get locations, which will be rendered on the map
     */
    @SuppressLint("StaticFieldLeak")
    private class GetLocations extends AsyncTask<String, Integer, String> {

        FillListAdapter adapter;

        GetLocations(FillListAdapter adapter) {
            this.adapter = adapter;
        }


        @Override
        protected String doInBackground(String... strings) {
            Log.i("DoInBackground", "Started in Class GetLocations");
            return Request.get(getContext(), strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("JSON response: ", result);
            if (result.equals("error")) {
                Log.d("Error: ", "Error in GET Request");
                Toast.makeText(getContext(), "There was an Error loading your locations", Toast.LENGTH_LONG).show();
            } else {
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
                mylocations = gson.fromJson(result, Location[].class);

                fillListAdapter.setLocations(mylocations);
                fillListAdapter.notifyDataSetChanged();

                //TODO: Check if locations are empty.
                //check if friends are not empty
//                if (mylocations != null && mylocations.length != 0) {
//                    addMarkersForAllUsers();
//                    configureInfoWindow();
            }
        }

    }
    /**
     * this function simply opens the addLocationActivity
     */
    private void openAddLocationActivity(){
        Intent intent = new Intent(getContext(), AddLocationActivity.class);
        startActivity(intent);
    }





}
