package com.interactivemedia.backpacker.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.models.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 * Created by Rebecca Durm on 13.01.2018.
 */

public class FillSpinnerAdapter extends ArrayAdapter<Location> {
    private Context context;
    private int layoutResourceId;
    private Location[] locations;
    private Spinner spinnerCountry;
    private ArrayList countriesOfLocation;
    private HashSet hashCountryList;
    public ArrayList sortedCountries;

    public FillSpinnerAdapter(@NonNull Context context, int layoutResourceId, Location[] friendsLocations) {
        super(context, layoutResourceId);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.locations = friendsLocations;
    }

    @Override
    public int getCount() {
        if (locations != null) {
            return locations.length;
        }
        return 0;
    }


    @SuppressLint("CutPasteId")
    @Override
    @NonNull
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;


            //LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            //This layout inflater works no matter whether we come from an activity or a fragment
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layoutResourceId, viewGroup, false);

            spinnerCountry = view.findViewById(R.id.spinner);

            Log.e("InFillSpinnerAdapter", "IM HERE");

            countriesOfLocation = new ArrayList<>();
            hashCountryList = new HashSet<>();


            for (Location singleLocation : locations) {
                String country = singleLocation.getCountry();
                Log.e("ForEach Location", country);
                hashCountryList.add(country);
            }
            countriesOfLocation.clear();
            countriesOfLocation.addAll(hashCountryList);

            Collections.sort(countriesOfLocation);

            sortedCountries = countriesOfLocation;



        return view;
    }

    public void setLocations(Location[] locations){
        this.locations = locations;
    }


}