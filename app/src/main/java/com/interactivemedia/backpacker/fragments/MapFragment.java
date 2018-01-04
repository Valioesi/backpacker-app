package com.interactivemedia.backpacker.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.activities.AddLocationActivity;
import com.interactivemedia.backpacker.helpers.CustomArrayAdapter;
import com.interactivemedia.backpacker.helpers.MarkerColors;
import com.interactivemedia.backpacker.helpers.Request;
import com.interactivemedia.backpacker.models.Location;
import com.interactivemedia.backpacker.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private User[] friends;
    private DrawerLayout drawer;
    private CustomArrayAdapter adapter;
    private GoogleMap map;
    private ListView listView;
    private HashMap<String, ArrayList<User>> googleIdUsersMap;

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

        googleIdUsersMap = new HashMap<>();

        //find mapView, call create function
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(mapViewBundle);

        friends = new User[]{};

        //find list view, create adapter containing friend list and set adapter of list view
        listView = view.findViewById(R.id.filter_list);
        adapter = new CustomArrayAdapter(getContext(), R.layout.custom_list_item_multiple_choice, friends);
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
                //clear hash map
                googleIdUsersMap.clear();
                //if there are no items checked we just render markers for every user
                if (listView.getCheckedItemCount() == 0) {
                    addMarkersForAllUsers();
                } else {
                    //get all of the checked items
                    SparseBooleanArray booleanArray = listView.getCheckedItemPositions();
                    for (int index = 0; index < friends.length; index++) {
                        //if the position is checked in the listview, we want to display the markers of this user
                        if (booleanArray.get(index)) {
                            for (Location location : friends[index].getLocations()) {
                                //add user to google id -> to be able to later check, if there are multiple users having the same location
                                addUserToHashMap(location.getGoogleId(), friends[index]);
                                setMarker(location, i);
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
        ImageButton addLocationButton = view.findViewById(R.id.add_location_button);
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
                loadLocations();
            }
        });

        return view;
    }

    /**
     * this function simply opens the addLocationActivity
     */
    private void openAddLocationActivity() {
        Intent intent = new Intent(getContext(), AddLocationActivity.class);
        startActivity(intent);
    }

    /**
     * this function calls the async task to get locations from server
     */
    private void loadLocations() {
        //call AsycnTask to get users and their saved locations to show from server
        //this will be changed later, since we are only getting our friends!
        new GetLocations().execute("/users/5a4cb9154162d41ba096f01d/friends");
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
            if (result.equals("error")) {
                Log.d("Error: ", "Error in GET Request");
                Toast.makeText(getContext(), "There was an Error loading the locations of your friends", Toast.LENGTH_LONG).show();
            } else {
                Gson gson = new Gson();
                friends = gson.fromJson(result, User[].class);

                adapter.setUsers(friends);
                adapter.notifyDataSetChanged();

                //check if friends are not empty
                if (friends != null && friends.length != 0) {
                    addMarkersForAllUsers();
                    configureInfoWindow();
                }
            }

        }
    }


    /**
     * this function sets one marker on the map
     *
     * @param location the location object, which holds the data needed for the marker
     * @param index the index of the loop through the user's friends -> needed to compute color for marker
     */
    private void setMarker(Location location, int index){
        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(location.getCoordinates()[0], location.getCoordinates()[1]))
                .title(location.getName())
                .icon(BitmapDescriptorFactory
                        .defaultMarker(MarkerColors.computeColor(index)));
        Marker marker = map.addMarker(options);
        //set tag is important, so that we can use the data from location in our info window
        marker.setTag(location);
    }

    /**
     * this function loops through all users and adds their locations to the map
     */
    private void addMarkersForAllUsers() {
        for (int i = 0; i < friends.length; i++) {
            for (Location location : friends[i].getLocations()) {
                //add user to google id -> to be able to later check, if there are multiple users having the same location
                addUserToHashMap(location.getGoogleId(), friends[i]);
                setMarker(location, i);
            }
        }
    }

    /**
     * this function adds a user to the googleIdHashmap
     *
     * @param googleId the key, under which the user will be stored
     * @param user the user to be added (will be in array list in hash map)
     */
    private void addUserToHashMap(String googleId, User user){
        ArrayList<User> users = googleIdUsersMap.get(googleId);
        //create new array, if it does not exist, otherwise just add the user to the existing one (meaning, that there will be multiple users)
        if(users == null){
            users = new ArrayList<>();
        }
        users.add(user);
        //now put to updated array list to our map to the appropriate key (which is the google id)
        googleIdUsersMap.put(googleId, users);
    }
    /**
     * this function configures the info window for the markers
     */
    private void configureInfoWindow() {
        //set individual info window
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            //gets called first, if null, getInfoContents gets called
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            //this method only adjusts not the content of the window, not border or background -> therefore enough
            @Override @SuppressLint("InflateParams")
            public View getInfoContents(final Marker marker) {
                Log.d("check", "is this called only once?");
                View view = getLayoutInflater().inflate(R.layout.view_info_window, null);
                //get text views
                TextView textViewTitle = view.findViewById(R.id.text_view_title);
                TextView textViewCategories = view.findViewById(R.id.text_view_categories);
                TextView textViewDescription = view.findViewById(R.id.text_view_description);

                //get image view
                final ImageView imageView = view.findViewById(R.id.image_view_location);

                //we have already set the location as tag, when we added the marker
                //now we can get our needed data
                Location location = (Location) marker.getTag();
                //set texts
                if (location != null) {
                    textViewTitle.setText(location.getName());
                    textViewDescription.setText(location.getDescription());

                    if (location.getCategories().length == 0) {
                        textViewCategories.setText(R.string.no_categories);
                    } else {
                        //transform categories array into string
                        StringBuilder builder = new StringBuilder();
                        //this approach of using prefix is used to not have a trailing comma in the end
                        String prefix = "";
                        for (String category : location.getCategories()) {
                            builder.append(prefix);
                            prefix = ", ";
                            builder.append(category);
                        }
                        textViewCategories.setText(builder.toString());
                    }

                    //download first image of images array via Picasso library
                    //but only, if an image exists for this location
                    if (location.getImages().length != 0) {
                        String imageUri = Request.IMAGES_URL + "/location/" + location.getImages()[0] + ".jpg";
                        //we have to set a request listener to reload the info window (because it is not a live view, just an image)
                        //only set callback once
                        //therefore we check if the window was already shown, ergo this code has already been executed
                        if (!location.wasInfoWindowAlreadyShown()) {
                            Glide.with(getContext()).load(imageUri).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    //reload info window to show image!
                                    marker.showInfoWindow();
                                    return false;
                                }
                            }).into(imageView);
                            location.setInfoWindowAlreadyShown(true);
                        } else {
                            //this time around the image will be taken from cache or disk or whatever
                            Glide.with(getContext()).load(imageUri).into(imageView);
                        }
                    } else {
                        //if there are no images for this location, add a placeholder image
                        Drawable drawable = getResources().getDrawable(R.drawable.ic_place_black_100dp);
                        //set tint to accent color
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            drawable.setTint(getResources().getColor(R.color.colorAccent));
                        }
                        imageView.setImageDrawable(drawable);
                        //scale down image view a bit
                        imageView.getLayoutParams().height = 100;
                        imageView.getLayoutParams().width = 100;
                    }


                    //IMPORTANT! use hash map to check, if the location is used by more than one user
                    ArrayList<User> users = googleIdUsersMap.get(location.getGoogleId());
                    //if there are more than one users, we want to show it in the info window, otherwise not
                    if(users.size() > 1){
                        TextView textViewUsers = view.findViewById(R.id.text_view_users);
                        StringBuilder builder = new StringBuilder();
                        builder.append("Location saved by ");
                        //this approach of using prefix is used to not have a trailing comma in the end
                        String prefix = "";
                        for(User user: users){
                            builder.append(prefix);
                            prefix = ", ";
                            builder.append(user.getFirstName());
                        }
                        textViewUsers.setText(builder.toString());
                        textViewUsers.setVisibility(View.VISIBLE);
                    }

                }


                return view;
            }
        });
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
        //reload markers, because there might have been added one
        if (map != null) {
            map.clear();
            loadLocations();
        }
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
