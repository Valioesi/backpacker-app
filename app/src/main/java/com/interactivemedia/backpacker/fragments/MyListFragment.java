package com.interactivemedia.backpacker.fragments;

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

import com.google.gson.Gson;
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

    private ImageButton imageButton;
    private Location[] mylocations;
    private FillListAdapter fillListAdapter;

//    private String testJsonObject=" {\n" +
//            "    \"_id\": \"5a4e2297c54f8939ec85f32c\",\n" +
//            "    \"user\": {\n" +
//            "      \"_id\": \"5a323b82654ba50ef8d2b8c2\",\n" +
//            "      \"firstName\": \"Peter\",\n" +
//            "      \"lastName\": \"Müller\"\n" +
//            "    },\n" +
//            "    \"googleId\": \"ChIJqe7nq7olv0cRUggn35PiWuU\",\n" +
//            "    \"city\": \"Köln\",\n" +
//            "    \"country\": \"Deutschland\",\n" +
//            "    \"name\": \"Kölner Dom\",\n" +
//            "    \"categories\": [],\n" +
//            "    \"images\": [],\n" +
//            "    \"coordinates\": [\n" +
//            "      50.941325,\n" +
//            "      6.9584\n" +
//            "    ],\n" +
//            "    \"favorite\": false,\n" +
//            "    \"description\": \"Sehr schön hier\"\n" +
//            "  },\n" +
//            "  {\n" +
//            "    \"_id\": \"5a4e22d6c54f8939ec85f32d\",\n" +
//            "    \"user\": {\n" +
//            "      \"_id\": \"5a323b82654ba50ef8d2b8c2\",\n" +
//            "      \"firstName\": \"Peter\",\n" +
//            "      \"lastName\": \"Müller\"\n" +
//            "    },\n" +
//            "    \"googleId\": \"ChIJ-yu74gMlv0cRpwJGueZgLUQ\",\n" +
//            "    \"city\": \"Köln\",\n" +
//            "    \"country\": \"Deutschland\",\n" +
//            "    \"name\": \"VOLKSTHEATER Millowitsch\",\n" +
//            "    \"categories\": [],\n" +
//            "    \"images\": [],\n" +
//            "    \"coordinates\": [\n" +
//            "      50.936548,\n" +
//            "      6.936866\n" +
//            "    ],\n" +
//            "    \"favorite\": false,\n" +
//            "    \"description\": \"schönes Volkstheater, hab kein Wort verstanden\"\n" +
//            "  }";


    static class ViewHolder {
        TextView nameLocation;
        TextView nameCity;
        TextView nameCountry;
        ImageButton btn_favorite;
    }

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


        ListView lvMyLocations = (ListView) view.findViewById(R.id.lv_myloc);
        mylocations = new Location[]{};

        //Create Adapter containing location list
        fillListAdapter = new FillListAdapter(getContext(), R.layout.listitem_mylist, mylocations);
        lvMyLocations.setAdapter(fillListAdapter);

        //Loads locations by calling AsyncTask
        loadLocations();


        //set OnItemClickListener to change the Image of the Button and set the location favorite
        lvMyLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                
            }
        });



        //find add location button and set on clock method to open new activity
        final ImageButton addLocationButton = view.findViewById(R.id.add_location_button);
        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddLocationActivity();
            }
        });


        return view;
    }


    private void loadLocations() {
        //call AsycTask to the locations of one user to show from server
        //TODO: instead of a defined userid we will insert the userid of the owner of the app
        new GetLocations(fillListAdapter).execute("/locations?users=5a323b82654ba50ef8d2b8c2");
    }

    /**
     * this AsyncTask makes a call to our API to get locations, which will be rendered on the map
     */
    private class GetLocations extends AsyncTask<String, Integer, String> {

        private FillListAdapter adapter;

        public GetLocations(FillListAdapter adapter) {
            this.adapter = adapter;
        }


        @Override
        protected String doInBackground(String... strings) {
            Log.i("DoInBackground", "Started in Class GetLocations");
            return Request.get(strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("JSON response: ", result);
            if (result.equals("error")) {
                Log.d("Error: ", "Error in GET Request");
                Toast.makeText(getContext(), "There was an Error loading your locations", Toast.LENGTH_LONG).show();
            } else {
                Gson gson = new Gson();
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
