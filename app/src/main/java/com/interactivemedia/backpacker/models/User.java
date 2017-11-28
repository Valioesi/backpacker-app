package com.interactivemedia.backpacker.models;

/**
 * Created by Vali on 26.11.2017.
 *
 * This is an example model for users. We will use this to parse our JSON to objects via GSON library
 */

public class User {
    private String  name;

    public User(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
