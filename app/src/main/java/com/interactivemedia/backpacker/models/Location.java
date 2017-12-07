package com.interactivemedia.backpacker.models;

/**
 * Created by Vali on 01.12.2017.
 * This is model for a location. We will use this to parse our JSON to objects via GSON library.
 */

public class Location {
    private int id;
    private boolean favorite;
    private String name;
    private int[] coordinates;


    public Location(int id, boolean favorite, String name, int[] coordinates) {
        this.id = id;
        this.favorite = favorite;
        this.name = name;
        this.coordinates = coordinates;
    }

    public int getId() {
        return id;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public String getName() {
        return name;
    }

    public int[] getCoordinates() {
        return coordinates;
    }

}
