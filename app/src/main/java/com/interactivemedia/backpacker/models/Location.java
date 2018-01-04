package com.interactivemedia.backpacker.models;

import java.util.ArrayList;

/**
 * Created by Vali on 01.12.2017.
 * This is model for a location. We will use this to parse our JSON to objects via GSON library.
 */

public class Location {
    private String _id;
    private String googleId;
    private String user;
    private boolean favorite;
    private String[] categories;
    private String[] images;
    private String description;
    private double[] coordinates;
    private String city;
    private String country;
    private String name;
    //this field is needed for our logic in map fragment to only download image once
    private transient boolean infoWindowAlreadyShown = false;     //transient, because gson should ignore it



    public Location(String googleId, String user, String name, boolean favorite,  String description, String[] categories, double[] coordinates, String city, String country) {
        this.googleId = googleId;
        this.user = user;
        this.name = name;
        this.favorite = favorite;
        this.description = description;
        this.categories = categories;
        this.coordinates = coordinates;
        this.city = city;
        this.country = country;
    }




    public String getId() {
        return _id;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String  getUserId() {
        return user;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public String getName() {
        return name;
    }

    public String[] getCategories() {
        return categories;
    }

    public String getDescription() {
        return description;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public String get_id() {
        return _id;
    }


    public String[] getImages() {
        return images;
    }

    public String getUser() {
        return user;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }


    public boolean wasInfoWindowAlreadyShown() {
        return infoWindowAlreadyShown;
    }

    public void setInfoWindowAlreadyShown(boolean infoWindowAlreadyShown) {
        this.infoWindowAlreadyShown = infoWindowAlreadyShown;
    }

}
