package com.example.googlemap;

/**
 * Created by lg on 11/17/2016.
 */

public class Place {

    private String icon;
    private String place_id="";
    private double latitude;
    private double longitude;
    private String placeName;
    private double distanceFromCurrentLoc=0;
    private String address;
    private double rating = 0;
    private String phone_number="";
    private boolean isOpen =false;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistanceFromCurrentLoc() {
        return distanceFromCurrentLoc;
    }

    public void setDistanceFromCurrentLoc(double distanceFromCurrentLoc) {
        this.distanceFromCurrentLoc = distanceFromCurrentLoc;
    }

    public String getAddress() {
        return address;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }
}
