package com.AYC.canalguide.NearbyPlaces;

public class Place {

	private String name;
    private String vicinity;
    private double latitude;
    private double longitude;
    
    public Place(String name, String vicinity, double lat, double lng){
    	this.name = name;
    	this.vicinity = vicinity;
    	this.latitude = lat;
    	this.longitude = lng;
    }
	
    public String getName(){
    	return name;
    }
    public String getVicinity(){
    	return vicinity;
    }
    
    public double getLatitude(){
    	return latitude;
    }
    
    public double getLongitude(){
    	return longitude;
    }
    
}
