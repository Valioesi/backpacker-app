package com.interactivemedia.backpacker.models;

import java.util.ArrayList;

/**
 * Created by Vali on 26.11.2017.
 *
 * This is an example model for users. We will use this to parse our JSON to objects via GSON library
 */

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private ArrayList<Location> locations;

    public User(int id, String firstName, String lastName, ArrayList<Location> locations) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.locations = locations;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }



}
