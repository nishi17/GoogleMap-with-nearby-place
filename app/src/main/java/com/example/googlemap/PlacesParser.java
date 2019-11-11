package com.example.googlemap;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PlacesParser {

    public ArrayList<Place> parse(JSONObject jsonObject) {
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }

    private ArrayList<Place> getPlaces(JSONArray jsonArray) {
        int placesCount = jsonArray.length();
//        List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
        ArrayList<Place> placesList = new ArrayList<>();
        HashMap<String, String> placeMap = null;

        for (int i = 0; i < placesCount; i++) {
            try {
                Place p = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(p);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placesList;
    }

    private Place getPlace(JSONObject googlePlaceJson) {
        HashMap<String, String> googlePlaceMap = new HashMap<String, String>();
        Place place = new Place();
        String place_id="";
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";
        String icon = "";
        double rating = 0;
        boolean open_now = false;

        try {

            if (!googlePlaceJson.isNull("place_id")) {
                place_id = googlePlaceJson.getString("place_id");
                Log.e("place_id",";-"+place_id);
            }

            if (!googlePlaceJson.isNull("name")) {
                placeName = googlePlaceJson.getString("name");
                Log.e("Place Name",";-"+placeName);
            }


            if (!googlePlaceJson.isNull("icon")) {
                icon = googlePlaceJson.getString("icon");
                Log.e("Place icon",";-"+icon);
            }

            if (!googlePlaceJson.isNull("vicinity")) {
                vicinity = googlePlaceJson.getString("vicinity");
            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJson.getString("reference");

            if(!googlePlaceJson.isNull("opening_hours")){

                JSONObject jsonObject = googlePlaceJson.getJSONObject("opening_hours");

                if(!jsonObject.isNull("open_now")){
                    open_now = jsonObject.getBoolean("open_now");
                }

            }

            googlePlaceMap.put("icon",icon);
            googlePlaceMap.put("place_id", place_id);
            googlePlaceMap.put("place_name", placeName);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("reference", reference);
            googlePlaceMap.put("open_now",String.valueOf(open_now));

            if (!googlePlaceJson.isNull("rating")) {
                rating = googlePlaceJson.getDouble("rating");
                Log.e("Place rating",";-"+rating);
            }

            place.setPlace_id(place_id);
            place.setPlaceName(placeName);
            place.setIcon(icon);
            place.setLatitude(Double.parseDouble(latitude));
            place.setLongitude(Double.parseDouble(longitude));
            place.setAddress(vicinity);
            place.setRating(rating);
            place.setOpen(open_now);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return place;
    }
}