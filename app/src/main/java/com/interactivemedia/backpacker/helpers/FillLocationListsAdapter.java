package com.interactivemedia.backpacker.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.models.Location;

import java.util.ArrayList;
import java.util.HashSet;


/**
 * Adapter fills ListView with ListViewItems
 * Created by Rebecca Durm on 04.01.2018.
 */

public class FillLocationListsAdapter extends ArrayAdapter<ArrayList<Location>> implements Filterable{
    private Context context;
    private int layoutResourceId;
    private ArrayList<Location> locations;
    private String adapterCallSource;
    private ArrayList countriesOfLocation;
    private HashSet hashCountryList;
    private ArrayList<String> sortedCountries;
    private String country;

    ImageView imageViewButton;
    private boolean isFavorite;



    public FillLocationListsAdapter(Context context, int layoutResourceId, ArrayList<Location> locations, String adapterCallSource){
        super(context, layoutResourceId);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.locations = locations;
        this.adapterCallSource = adapterCallSource;
    }

    @SuppressLint("CutPasteId")
    @Override @NonNull
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View row = convertView;
        LocationsHolder holder;



        if (row==null){
            //LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            //This layout inflater works no matter whether we come from an activity or a fragment
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, viewGroup, false);

            holder = new LocationsHolder();

            holder.nameLocation= row.findViewById(R.id.tv_firstName);
            holder.nameCountry= row.findViewById(R.id.tv_countryName);
            holder.nameCity=  row.findViewById(R.id.tv_cityName);
            holder.description=row.findViewById(R.id.tv_location_des);
            holder.iv_favorite= row.findViewById(R.id.iv_favorite);

            row.setTag(holder);


        } else {
            holder = (LocationsHolder) row.getTag();
        }

        Location location = locations.get(position);
        String nameLocation = location.getName() + " ";
        String nameCity=location.getCity() + "," ;
        String nameCountry=location.getCountry();
        String description=location.getDescription();
        final Boolean isFavoriteBackend=location.isFavorite();
        final String googleId=location.getGoogleId();
        final String locationid=location.get_id();

        holder.nameLocation.setText(nameLocation);
        holder.nameCity.setText(nameCity);
        holder.nameCountry.setText(nameCountry);
        holder.description.setText(description);





        //Only show and interact with FavoriteButton, if you come from the fragment "my list".
        if(adapterCallSource=="MyListFragment") {

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


//
//        //Only relevant for Activity FriendsDetailsActivity
//        if (adapterCallSource=="FriendsDetailsActivity"){
//            countriesOfLocation = new ArrayList<>();
//            hashCountryList = new HashSet<>();
//
//
//            for (Location singleLocation : locations){
//                country = singleLocation.getCountry();
//                Log.e ("ForEach Location", country);
//                hashCountryList.add(country);
//            }
//            countriesOfLocation.clear();
//            countriesOfLocation.addAll(hashCountryList);
//
//            Collections.sort(countriesOfLocation);
//
//            sortedCountries=sortCountries(countriesOfLocation);
//            Log.e("FillLocationListAdapter", String.valueOf(sortedCountries));
//        }
//

        return row;
    }


    @Override
    public int getCount(){
        if(locations != null){
            return locations.size();
        }
        return 0;
    }

    public void setLocations(ArrayList<Location> locations){
        this.locations = locations;
    }

//
//    public Filter getFilter(){
//        CountryFilter countryFilter = null;
//        if(countryFilter==null){
//            countryFilter = new CountryFilter();
//        }
//        return countryFilter;
//    }





    static class LocationsHolder    {
        TextView nameLocation;
        TextView nameCountry;
        TextView nameCity;
        TextView description;
        ImageView iv_favorite;
    }







    @SuppressLint("StaticFieldLeak")
    private class ChangeFavorite extends AsyncTask<String, Integer, String>{
        Boolean isFavorite;
        String googleId;

        /**Changes the attribute "favorite" in the Backend depending on the user's input. Click on image view changes
         * isFavorite value.
         * @param isFavorite Determines if location is a favorite Place or not. Is set by clicking the image view.
         * @param googleId GoogleID of the location. Is necessary for changing the location's attributes.
         */
        ChangeFavorite(boolean isFavorite, String googleId) {
            this.isFavorite=isFavorite;
            this.googleId=googleId;
        }

        @Override
        protected String doInBackground(String... strings) {
            //return Request.changeFavorites(strings[0], isFavorite, locationid, "PATCH");
            String json= "{\"googleId\":\"" +  googleId + "\",\"favorite\":"+ isFavorite + "}";
            Log.e("SendBack Json", json);
            return Request.patch(getContext(), strings[0], json);
        }



        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(getContext(), "There was an Error setting your location to favorite", Toast.LENGTH_LONG).show();
            } else {
                if (isFavorite) {
                    Toast.makeText(getContext(), "Location set as favorite", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Location unset as favorite", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

//
//    /**
//     * https://www.codeproject.com/Tips/894233/Using-Spinner-Control-for-Filtering-ListView-Andro
//     */
//    private class CountryFilter extends Filter {
//
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            FilterResults results=new FilterResults();
//
//            //checks match for the countryName and adds to the filterlist
//            if (country.length()>0){
//                ArrayList<Location> filterList = new ArrayList<>();
//                for (int i=0; i<locations.length; i++){
//
//                    if ((locations[i].getCountry()==country){
//                        Location location = locations[i];
//                        filterList.add(location);
//                    }
//                }
//
//                results.count=filterList.size();
//                results.values = filterList;
//            }
//            else {
//                results.count=sortedCountries.size();
//                results.values = sortedCountries;
//            }
//            return results;
//        }
//
//
//        //publishes the matches found
//
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//
//            locations = (Location[]) results.values;
//            notifyDataSetChanged();
//
//        }
//    }
}