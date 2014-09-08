package com.AYC.canalguide.canalparser;

import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * This is the abstract base class for all MapMarker objects that 
 * contain all data about each point of interest
 * 
 * @author James O'Leary
 *
 */
public abstract class MapMarker implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Marker will be added once it is known (not in the constructor)
	protected Marker marker;
	
	// ** LatLng and Marker classes aren't Serializable
	protected double lat, lng;
	protected String name;
	protected String bodyOfWater;
	protected double mile;
	
	public MapMarker(LatLng latLng, String name, String bodyOfWater, double mile){
		this.lat = latLng.latitude;
		this.lng = latLng.longitude;
		this.name = name;
		this.mile = mile;
		
		if(bodyOfWater != null && bodyOfWater.length() > 1){
	    	// Make first letter upper case for bodyOfWater
	    	char first = Character.toUpperCase(bodyOfWater.charAt(0));
			this.bodyOfWater = first + bodyOfWater.substring(1);
		}
		else
			this.bodyOfWater = bodyOfWater;
	}

	public abstract MarkerOptions getMarkerOptions();
	
	public abstract MapMarker cloneWithoutMarker();	
	
	public void setMarker(Marker marker){
		this.marker = marker;
	}
	
	public Marker getMarker(){
		return marker;
	}
	
	// I dont think this will work because it is a static method
	//public abstract LockMarker readMarker(XmlPullParser parser) throws XmlPullParserException, IOException;

	public LatLng getlatLng(){
		return new LatLng(lat, lng);
	}
	
	public String getName(){
		return name;
	}
	
	public String getBodyOfWater(){
		return bodyOfWater;
	}
	
	public double getMile(){
		return mile;
	}
	
	public String toString(){
		return name + " " + lat + " " + lng + " " + bodyOfWater + " " + mile;
	}

	
	
}
