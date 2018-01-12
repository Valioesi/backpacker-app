package com.interactivemedia.backpacker.helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.models.Location;


/**
 * Adapter fills ListView with ListViewItems
 * Created by Rebecca Durm on 04.01.2018.
 */

public class FillListAdapter extends ArrayAdapter<Location>{
    private Context context;
    private int layoutResourceId;
    private Location[] locations;


    ImageView imageViewButton;
    private boolean isFavorite;



    public FillListAdapter(Context context, int layoutResourceId, Location[] locations){
        super(context, layoutResourceId);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.locations = locations;
    }


    @Override
    public int getCount(){
        if(locations != null){
            return locations.length;
        }
        return 0;
    }


    @SuppressLint("CutPasteId")
    @Override @NonNull
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        LocationsHolder holder;



        if (view==null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            view = inflater.inflate(layoutResourceId, viewGroup, false);

            holder = new LocationsHolder();

            holder.nameLocation= view.findViewById(R.id.tv_locationName);
            holder.nameCountry= view.findViewById(R.id.tv_countryName);
            holder.nameCity=  view.findViewById(R.id.tv_cityName);
            holder.description=view.findViewById(R.id.tv_location_des);
            holder.iv_favorite= view.findViewById(R.id.iv_favorite);
            view.setTag(holder);


        } else {
            holder = (LocationsHolder) view.getTag();
        }

        final Location location = locations[position];
        String nameLocation = location.getName() + " ";
        String nameCity=location.getCity() + "," ;
        String nameCountry=location.getCountry();
        String description=location.getDescription();
        Log.i("Description", description);
        final Boolean isFavoriteBackend=location.isFavorite();
        final String googleId=location.getGoogleId();
        final String locationid=location.get_id();

        holder.nameLocation.setText(nameLocation);
        holder.nameCity.setText(nameCity);
        holder.nameCountry.setText(nameCountry);
        holder.description.setText(description);


        //Get information about favorite location from Backend
        isFavorite=isFavoriteBackend;
        Log.e("IsFavoriteBackend", ": " + isFavorite);



        imageViewButton = view.findViewById(R.id.iv_favorite);

        //Set ImageView depending on variable of Backend
        if (isFavorite) {
            imageViewButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
            isFavorite =true;
        } else {
            imageViewButton.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
            isFavorite=false;
        }





        //Change the "isFavorite" value if button is clicked.
        imageViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstraintLayout vwParentRow=(ConstraintLayout) v.getParent();
                ImageView imageViewButton = (ImageView)vwParentRow.getChildAt(3);

                if (!isFavorite) {
                    imageViewButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                    isFavorite=true;
                    new ChangeFavorite(isFavorite, googleId).execute("/locations/"+locationid);
                } else {
                    imageViewButton.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                    isFavorite=false;
                    new ChangeFavorite(isFavorite, googleId).execute("/locations/"+locationid);
                }
            }
        });





        return view;
    }







    static class LocationsHolder
    {
        TextView nameLocation;
        TextView nameCountry;
        TextView nameCity;
        TextView description;
        ImageView iv_favorite;
    }


    public void setLocations(Location[] locations){
        this.locations = locations;
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
            Log.d("JSON response: ", result);
            if (result.equals("error")) {
                Toast.makeText(getContext(), "There was an Error setting your location to favorite", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Location set as favorite", Toast.LENGTH_LONG).show();
            }
        }
    }
}