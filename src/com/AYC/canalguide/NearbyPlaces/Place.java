package com.AYC.canalguide.NearbyPlaces;

import com.AYC.canalguide.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
    
	public MarkerOptions getMarkerOptions() {
			return new MarkerOptions()
				.title(name)
				.position(new LatLng(latitude, longitude))
				.snippet(vicinity)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.mmi_red_buoy));
	}
    
}
