package com.interactivemedia.backpacker.fragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.activities.LoginActivity;
import com.interactivemedia.backpacker.helpers.Preferences;
import com.interactivemedia.backpacker.helpers.Request;
import com.interactivemedia.backpacker.models.Location;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationDetailsFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LOCATION_ID = "locationId";
    private static final String ARG_USER_ID = "userId";
    private static final String ARG_USER_NAME = "userName";
    private String locationGoogleId;
    private String userId;
    private String userName;
    private ProgressBar progressBar;

    private String myUserId;
    private String locationId;
    private Button btnRemoveLocation;

    public LocationDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment LocationDetailsFragment.
     */
    public static LocationDetailsFragment newInstance(String locationGoogleId, String userId, String userName) {
        LocationDetailsFragment fragment = new LocationDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LOCATION_ID, locationGoogleId);
        args.putString(ARG_USER_ID, userId);
        args.putString(ARG_USER_NAME, userName);

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            locationGoogleId = getArguments().getString(ARG_LOCATION_ID);
            userId = getArguments().getString(ARG_USER_ID);
            userName = getArguments().getString(ARG_USER_NAME);
        }

        myUserId = Preferences.getUserId(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location_details, container, false);

        //perform get request to get location data
        new GetLocation().execute("/locations?users[]=" + userId + "&googleId=" + locationGoogleId);

        //show user data
        //get text view
        TextView textViewUser = view.findViewById(R.id.text_view_user);
        //and set the user info if available, userName might be null when coming from my locations fragment
        if (userName != null) {
            String text = "Saved by " + userName;
            textViewUser.setText(text);
        }

        progressBar = view.findViewById(R.id.progress_bar);

        return view;
    }


    /**
     * this AsyncTask makes a call to our API to get the location passed through the intent
     */
    @SuppressLint("StaticFieldLeak")
    private class GetLocation extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            return Request.get(getContext(), strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Log.d("Error: ", "Error in GET Request");
                Toast.makeText(getContext(), "There was an Error loading the location", Toast.LENGTH_LONG).show();
                getActivity().finish();
            } else if (result.equals("401")) {
                //unauthorized -> we need new token -> redirect to Login Activity
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            } else {
                //hide progress bar
                progressBar.setVisibility(View.GONE);

                Log.d("JSON response: ", result);
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

                //define an array, because the server will respond with a json array
                //BUT! It will definitely be only one entry, unless the user has added the same location twice, but then fuck him!
                Location[] locationArray = gson.fromJson(result, Location[].class);
                if (locationArray.length == 0) {
                    Toast.makeText(getContext(), "There was an Error loading the location", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                } else {
                    Location location = locationArray[0];

                locationId=location.get_id();


                    View view = getView();
                    if (view != null) {

                        //get text views
                        TextView textViewTitle = view.findViewById(R.id.text_view_title);
                        TextView textViewCategories = view.findViewById(R.id.text_view_categories);
                        TextView textViewDescription = view.findViewById(R.id.text_view_description);
                        //and show location info
                        textViewTitle.setText(location.getName());
                        textViewCategories.setText(location.categoriesToString());
                        textViewDescription.setText(location.getDescription());

                        //add images to horizontal scroll view
                        //foreach image url we create a new image view, into which we will load the image with glide
                        String imageUri;
                        if (location.getImages().length != 0) {

                            //hide placeholder image
                            view.findViewById(R.id.image_view_placeholder).setVisibility(View.GONE);
                            //show horizontal scroll view with images
                            view.findViewById(R.id.image_scroll_view).setVisibility(View.VISIBLE);

                            for (String imageId : location.getImages()) {
                                //create new view
                                LinearLayout layout = view.findViewById(R.id.image_layout);
                                ImageView imageView = new ImageView(getContext());
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                layoutParams.setMargins(0, 0, 20, 0);
                                imageView.setLayoutParams(layoutParams);
                                layout.addView(imageView);

                                //load image into image view
                                imageUri = Request.IMAGES_URL + "/location/" + imageId + ".jpg";
                                Glide.with(getContext()).load(imageUri).into(imageView);
                            }
                        }
                    }
                }


                //if user has the same id as signed in user
                if(myUserId.equals(userId)){
                    btnRemoveLocation.setVisibility(View.VISIBLE);
                    btnRemoveLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertMessage(locationId);
                        }
                    });
                }


            }

        }

        private void alertMessage(final String locationId) {
              DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                // Yes button clicked
                                deleteLocation(locationId);
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

                //Show "warning" Dialog, if user is sure about deleting location.
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        }

        //Delete Location
    private void deleteLocation(String locationId) {
        new DeleteLocation().execute("/locations/" + locationId );
    }

    @SuppressLint("StaticFieldLeak")
    private class DeleteLocation extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.i("doInBackground", "deleteFriend");
            return Request.delete(getContext(), strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Log.d("Error: ", "Error in DELETE Request");
                Toast.makeText(getContext(), "There was an error deleting this location", Toast.LENGTH_LONG).show();
            } else if (result.equals("401") || result.equals("403")) {
                //unauthorized -> we need new token -> redirect to Login Activity
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            } else {
                Log.i("JSON response: ", result);
                Toast.makeText(getContext(), "Location deleted successfully", Toast.LENGTH_LONG).show();
            }
        }
    }

}
