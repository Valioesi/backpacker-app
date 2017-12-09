package com.interactivemedia.backpacker.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.activities.AddLocationActivity;
import com.interactivemedia.backpacker.helpers.CustomArrayAdapter;
import com.interactivemedia.backpacker.helpers.MarkerColors;
import com.interactivemedia.backpacker.helpers.Request;
import com.interactivemedia.backpacker.models.Location;
import com.interactivemedia.backpacker.models.User;

/**
 * A simple {@link Fragment} subclass.
 * <p>
 * This fragment included a MapView
 * The decision to use a MapView inside our own Fragment instead of using a MapFragment (which would have
 * already included all of the lifecycle methods) is based on flexibility in the future and the possibility to easily edit the xml file
 * and add functionality to the fragment.
 */
public class MapFragment extends Fragment {


    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private MapView mapView;
    private User[] users;
    private DrawerLayout drawer;
    private CustomArrayAdapter adapter;
    private GoogleMap map;
    private ListView listView;


    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //Vali: this is taken from https://github.com/googlemaps/android-samples/blob/master/ApiDemos/app/src/main/java/com/example/mapdemo/RawMapViewDemoActivity.java
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        //find mapView, call create function
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(mapViewBundle);

        users = new User[]{};

        //find list view, create adapter containing friend list and set adapter of list view
        listView = view.findViewById(R.id.filter_list);
        adapter = new CustomArrayAdapter(getContext(), R.layout.custom_list_item_multiple_choice, users);
        listView.setAdapter(adapter);

        //on item click listener will implement the functionality to filter the markers by user
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //mark checkboxes as checked or uncheck them
                CheckedTextView textView = view.findViewById(R.id.name);
                //mark it as the opposite of before
                textView.setChecked(!textView.isChecked());
                //remove all markers, before they can drawn again
                map.clear();
                //if there are no items checked we just render markers for every user
                if(listView.getCheckedItemCount() == 0){
                    addMarkersForAllUsers();
                }else{
                    //get all of the checked items
                    SparseBooleanArray booleanArray = listView.getCheckedItemPositions();
                    for (int index = 0; index < users.length; index++) {
                        //if the position is checked in the listview, we want to display the markers of this user
                        if (booleanArray.get(index)) {
                            for (Location location : users[index].getLocations()) {
                                map.addMarker(new MarkerOptions()
                                        .position(new LatLng(location.getCoordinates()[0], location.getCoordinates()[1]))
                                        .title(location.getName())
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(MarkerColors.MARKER_COLORS[index])));
                            }
                        }
                    }
                }


            }
        });

        drawer = view.findViewById(R.id.drawer_layout);

        //find filter button and add on click method to open drawer
        ImageButton filterButton = view.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.START);
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

        //getMapAsync is called, when the map is ready, the API call is fired
        //i have decided to it this way instead of first AsyncTask, then getMapAsync in onPostExecute (i had it like that earlier)
        //I think that the performance is better this way
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                //call AsycnTask to get users and their saved locations to show from server
                //this will be changed later, since we are only getting our friends!
                //TODO: uncomment the api call
           //     new GetLocations().execute("/users");
            }
        });
        return view;
    }

    /**
     * this function simply opens the addLocationActivity
     */
    private void openAddLocationActivity(){
        Intent intent = new Intent(getContext(), AddLocationActivity.class);
        startActivity(intent);
    }

    /**
     * this AsyncTask makes a call to our API to get locations, which will be rendered on the map
     */
    private class GetLocations extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            return Request.get(strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("JSON response: ", result);
            Gson gson = new Gson();
            users = gson.fromJson(result, User[].class);

            adapter.setUsers(users);
            adapter.notifyDataSetChanged();


            addMarkersForAllUsers();

        }
    }

    /**
     * this function loops through all users and adds their locations to the map
     */
    private void addMarkersForAllUsers(){
        for (int i = 0; i < users.length; i++) {
            for (Location location : users[i].getLocations()) {
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(location.getCoordinates()[0], location.getCoordinates()[1]))
                        .title(location.getName())
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(MarkerColors.computeColor(i))));
            }
        }
    }

    //we need to forward all lifecycle methods
    //see reference: https://developers.google.com/android/reference/com/google/android/gms/maps/MapView

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
