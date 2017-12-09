package com.interactivemedia.backpacker.models;

import java.util.ArrayList;

/**
 * Created by Vali on 01.12.2017.
 * This is model for a location. We will use this to parse our JSON to objects via GSON library.
 */

public class Location {
    private int id;
    private String googleId;
    private int userId;
    private boolean favorite;
    private String name;
    private ArrayList<String> categories;
    private String description;
    private int[] coordinates;


    public Location(String googleId, int userId, String name, boolean favorite,  ArrayList<String> categories, String description, int[] coordinates) {
        this.googleId = googleId;
        this.userId = userId;
        this.favorite = favorite;
        this.name = name;
        this.categories = categories;
        this.description = description;
        this.coordinates = coordinates;
    }

    public Location(String googleId, String name, boolean favorite, ArrayList<String> categories, String description, int[] coordinates) {
        this.googleId = googleId;
        this.favorite = favorite;
        this.name = name;
        this.categories = categories;
        this.description = description;
        this.coordinates = coordinates;
    }

    public int getId() {
        return id;
    }

    public String getGoogleId() {
        return googleId;
    }

    public int getUserId() {
        return userId;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public String getDescription() {
        return description;
    }

    public int[] getCoordinates() {
        return coordinates;
    }
}
