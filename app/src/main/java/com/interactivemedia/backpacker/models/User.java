package com.interactivemedia.backpacker.models;

import java.util.ArrayList;

/**
 * This is the model for users. We will use this to parse our JSON to objects via GSON library.
 */

public class User {
    private String _id;
    private String firstName;
    private String lastName;
    private ArrayList<Location> locations;
    private String avatar;

    public User(String _id, String firstName, String lastName, ArrayList<Location> locations, String avatar) {
        this._id = _id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.locations = locations;
        this.avatar = avatar;
    }

    public String getId() {
        return _id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAvatar(){
        return avatar;
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }



}
