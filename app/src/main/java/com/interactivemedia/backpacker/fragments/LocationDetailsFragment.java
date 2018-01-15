package com.interactivemedia.backpacker.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.helpers.Request;
import com.interactivemedia.backpacker.models.Location;
import com.interactivemedia.backpacker.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationDetailsFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LOCATION_ID = "locationId";
    private String locationId;


    public LocationDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment LocationDetailsFragment.
     */
    public static LocationDetailsFragment newInstance(String locationId) {
        LocationDetailsFragment fragment = new LocationDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LOCATION_ID, locationId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            locationId = getArguments().getString(ARG_LOCATION_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location_details, container, false);

        //perform get request to get location data
        new GetLocation().execute("/locations/" + locationId);

        return view;
    }



    /**
     * this AsyncTask makes a call to our API to get the location passed through the intent
     */
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
            } else {
                Log.d("JSON response: ", result);
                Gson gson = new Gson();
                Location location = gson.fromJson(result, Location.class);

            //    new GetUser().execute("/users/" + location.getUserId());

                View view = getView();
                if(view != null){

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
                    for(String imageId: location.getImages()){
                        //create new view
                        LinearLayout layout = getView().findViewById(R.id.image_layout);
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
    }



    /**
     * this AsyncTask makes a call to our API to get the user of the location
     */
    private class GetUser extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            return Request.get(getContext(), strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Log.d("Error: ", "Error in GET Request");
                Toast.makeText(getContext(), "There was an Error loading the location", Toast.LENGTH_LONG).show();
            } else {
                Log.d("JSON response: ", result);
                Gson gson = new Gson();
                User user = gson.fromJson(result, User.class);

                View view = getView();
                if(view != null){
                    //get text view
                    TextView textViewUser = view.findViewById(R.id.text_view_user);
                    //and set the user info
                    String text = "Saved by " + user.getFirstName() + " " + user.getLastName();
                    textViewUser.setText(text);
                }


            }

        }
    }


}
