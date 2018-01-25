package com.interactivemedia.backpacker.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.activities.LoginActivity;
import com.interactivemedia.backpacker.helpers.Request;
import com.interactivemedia.backpacker.models.Location;
import com.interactivemedia.backpacker.models.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;


/**
 * Adapter fills ListView with ListViewItems
 */

public class FillLocationListsAdapter extends ArrayAdapter<Location> implements Filterable {
    private Context context;
    private int layoutResourceId;
    private ArrayList<Location> locations;
    private String adapterCallSource;
    private String selectedCountry;


    private ImageView imageViewButton;
    private boolean isFavorite;


    public FillLocationListsAdapter(Context context, int layoutResourceId, ArrayList<Location> locations, String adapterCallSource) {
        super(context, layoutResourceId);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.locations = locations;
        this.adapterCallSource = adapterCallSource;
    }

    @SuppressLint("CutPasteId")
    @Override
    @NonNull
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View row = convertView;
        LocationsHolder holder;


        if (row == null) {
            //LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            //This layout inflater works no matter whether we come from an activity or a fragment
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, viewGroup, false);

            holder = new LocationsHolder();

            holder.nameLocation = row.findViewById(R.id.tv_firstName);
            holder.nameCountry = row.findViewById(R.id.tv_countryName);
            holder.nameCity = row.findViewById(R.id.tv_cityName);
            //holder.description=row.findViewById(R.id.tv_location_des);
            holder.iv_favorite = row.findViewById(R.id.iv_favorite);

            row.setTag(holder);


        } else {
            holder = (LocationsHolder) row.getTag();
        }

        Location location = locations.get(position);
        String nameLocation = location.getName() + " ";
        String nameCity = location.getCity() + ",";
        String nameCountry = location.getCountry();
        //String description=location.getDescription();
        final Boolean isFavoriteBackend = location.isFavorite();
        final String googleId = location.getGoogleId();
        final String locationid = location.get_id();

        holder.nameLocation.setText(nameLocation);
        holder.nameCity.setText(nameCity);
        holder.nameCountry.setText(nameCountry);
        //holder.description.setText(description);


        //Only show and interact with FavoriteButton, if you come from the fragment "my list".
        if (adapterCallSource == "MyLocationsFragment") {

            //Get information about favorite location from Backend
            isFavorite = isFavoriteBackend;
            Log.e("IsFavoriteBackend", ": " + isFavorite);


            imageViewButton = row.findViewById(R.id.iv_favorite);

            //Set ImageView depending on variable of Backend
            if (isFavorite) {
                imageViewButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                isFavorite = true;
            } else {
                imageViewButton.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                isFavorite = false;
            }


            //Change the "isFavorite" value if button is clicked.
            imageViewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isFavorite) {
                        v.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                        isFavorite = true;
                        new ChangeFavorite(isFavorite, googleId).execute("/locations/" + locationid);
                    } else {
                        v.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                        isFavorite = false;
                        new ChangeFavorite(isFavorite, googleId).execute("/locations/" + locationid);
                    }
                }
            });

        }


        return row;
    }


    @Override
    public int getCount() {
        if (locations != null) {
            return locations.size();
        }
        return 0;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public ArrayList<Location> getLocations() {
        return this.locations;
    }


    static class LocationsHolder {
        TextView nameLocation;
        TextView nameCountry;
        TextView nameCity;
        //TextView description;
        ImageView iv_favorite;
    }


    /**
     * Changes the attribute "favorite" in the Backend depending on the user's input. Click on image view changes
     * isFavorite value.
     */
    @SuppressLint("StaticFieldLeak")
    private class ChangeFavorite extends AsyncTask<String, Integer, String> {
        Boolean isFavorite;
        String googleId;

        ChangeFavorite(boolean isFavorite, String googleId) {
            this.isFavorite = isFavorite;
            this.googleId = googleId;
        }

        @Override
        protected String doInBackground(String... strings) {
            //return Request.changeFavorites(strings[0], isFavorite, locationid, "PATCH");
            String json = "{\"googleId\":\"" + googleId + "\",\"favorite\":" + isFavorite + "}";
            Log.e("SendBack Json", json);
            return Request.patch(getContext(), strings[0], json);
        }


        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(getContext(), "There was an Error setting your location to favorite", Toast.LENGTH_LONG).show();
            } else if (result.equals("401")) {
                //unauthorized -> we need new token -> redirect to Login Activity
                Intent intent = new Intent(getContext(), LoginActivity.class);
                getContext().startActivity(intent);
            } else {
                if (isFavorite) {
                    Toast.makeText(getContext(), "Location set as favorite", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Location unset as favorite", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    /**
     * https://www.codeproject.com/Tips/894233/Using-Spinner-Control-for-Filtering-ListView-Andro
     */
    private class CountryFilter extends Filter {


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            String countryName = constraint.toString();
            Log.d("Filter", countryName);

            //checks match for the countryName and adds to the filtered list
            if (constraint != null || constraint.length() != 0) {
                ArrayList<Location> filteredFriendsLocations = new ArrayList<Location>();
                for (int i = 0; i < locations.size(); i++) {

                    if (locations.get(i).getCountry().equals(countryName)) {
                        Location location = locations.get(i);
                        Log.d("Location of Filter", String.valueOf(location));
                        filteredFriendsLocations.add(location);
                    }
                }

                results.count = filteredFriendsLocations.size();
                results.values = filteredFriendsLocations;
            }

            // There is always a value selected in the spinner. The value "**All locations**" is shown as first element.
            // all values are shown.
            if (countryName.equals("- All locations -")) {
                results.count = locations.size();
                results.values = locations;

            }


            return results;
        }


        //publishes the matches found

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            locations = (ArrayList<Location>) results.values;
            notifyDataSetChanged();

        }
    }


    public Filter getFilter() {
        CountryFilter countryFilter = null;
        if (countryFilter == null) {
            countryFilter = new CountryFilter();
        }
        return countryFilter;
    }


}