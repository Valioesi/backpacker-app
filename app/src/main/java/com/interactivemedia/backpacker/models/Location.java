package com.interactivemedia.backpacker.models;

/**
 * This is the model for a location. We will use this to parse our JSON to objects via gson library.
 */

public class Location  {
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


    public void setImages(String[] images) {
        this.images = images;
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

    /**
     * this function transforms categories array into an appropriate string
     * @return categories formatted in one single String
     */
    public String categoriesToString(){
        //transform categories array into string
        StringBuilder builder = new StringBuilder();
        //this approach of using prefix is used to not have a trailing comma in the end
        String prefix = "";
        for (String category : this.getCategories()) {
            builder.append(prefix);
            prefix = ", ";
            builder.append(category);
        }
        return builder.toString();
    }
}
