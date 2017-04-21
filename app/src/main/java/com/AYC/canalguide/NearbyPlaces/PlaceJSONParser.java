package com.AYC.canalguide.NearbyPlaces;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlaceJSONParser {
	 
    /** 
     * Receives a JSONObject and returns a list of Place objects
     * 
     * @param jObject
     * 
     * @return
     */
    public List<Place> parse(JSONObject jObject){
 
        JSONArray jPlaces = null;
        try {
            // Retrieves all the elements in the 'places' array
            jPlaces = jObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPlaces(jPlaces);
    }
 
    /**
     * Return the list of players
     * 
     * @param jPlaces
     * array of JSON places
     * 
     * @return list of places
     */
    private List<Place> getPlaces(JSONArray jPlaces){
        int placesCount = jPlaces.length();
        List<Place> placesList = new ArrayList<Place>();
        Place place = null;
 
        // Taking each place, parses and adds to list object
        for(int i=0; i<placesCount;i++){
            try {
                // Call getPlace with place JSON object to parse the place
                place = getPlace((JSONObject)jPlaces.get(i));
                placesList.add(place);
 
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
 
        return placesList;
    }
 
    /** 
     * Parsing the Place JSON object
     * 
     * @param jPlace
     * JSON place object
     * 
     * @return a Place object
     */
    private Place getPlace(JSONObject jPlace){
 
        Place place = null;
        String name = "-NA-";
        String vicinity = "-NA-";
        String lat="";
        String lng="";
 
        try {
 
        	if(!jPlace.isNull("name")){
                name = jPlace.getString("name");
            }
 
            if(!jPlace.isNull("vicinity")){
                vicinity = jPlace.getString("vicinity");
            }
 
            lat = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
            lng = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");
 
            place = new Place(name, vicinity, Double.parseDouble(lat), Double.parseDouble(lng));
 
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return place;
    }
}