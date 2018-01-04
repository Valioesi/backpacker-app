package com.interactivemedia.backpacker.helpers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.fragments.MyListFragment;
import com.interactivemedia.backpacker.models.Location;
import com.interactivemedia.backpacker.models.User;

/**
 * Adapter fills ListView with ListViewItems
 * Created by Rebecca Durm on 04.01.2018.
 */

public class FillListAdapter extends ArrayAdapter<Location>{
    private Context context;
    private int layoutResourceId;
    private Location[] locations;

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


    @Override @NonNull
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        LocationsHolder holder;



        if (view==null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            view = inflater.inflate(layoutResourceId, viewGroup, false);

            holder = new LocationsHolder();

            holder.nameLocation=(TextView) view.findViewById(R.id.tv_locationName);
            holder.nameCountry=(TextView) view.findViewById(R.id.tv_countryName);
            holder.nameCity= (TextView) view.findViewById(R.id.tv_cityName);
            holder.btn_favorite=(ImageButton) view.findViewById(R.id.btn_favorite);
            view.setTag(holder);

        } else {
            holder = (LocationsHolder) view.getTag();
        }

        Location location = locations[position];
        String nameLocation = location.getName() + " ";
        String nameCity=location.getCity() + "," ;
        String nameCountry=location.getCountry();


        holder.nameLocation.setText(nameLocation);
        holder.nameCity.setText(nameCity);
        holder.nameCountry.setText(nameCountry);



        return view;
    }


    static class LocationsHolder
    {
        TextView nameLocation;
        TextView nameCountry;
        TextView nameCity;
        ImageButton btn_favorite;
    }


    public void setLocations(Location[] locations){
        this.locations = locations;
    }

}