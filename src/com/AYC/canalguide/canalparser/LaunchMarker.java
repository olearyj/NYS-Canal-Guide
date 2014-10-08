package com.AYC.canalguide.canalparser;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LaunchMarker extends MapMarker implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String waterway;
	private String id;
	private String municipality;
	private String launchType;
	private String parking;
	private String overnightParking;
	private String camping;
	private String potableWater;
	private String restrooms;
	private String dayUseAmenities;
	private String portageDistance;
	private String shore;
	
	public LaunchMarker(String siteName, String waterway, String id, String municipality, String launchType, 
			String parking, String overnightParking, String camping, String potableWater, String restrooms, 
			String dayUseAmenities, String portageDistance, LatLng latLng, double mile, String shore, 
			String bodyOfWater){
		
		super(latLng, siteName, bodyOfWater, mile);
		this.waterway = waterway;
		this.id = id;
		this.municipality = municipality;
		this.launchType = launchType;
		this.parking = parking;
		this.overnightParking = overnightParking;
		this.camping = camping;
		this.potableWater = potableWater;
		this.restrooms = restrooms;
		this.dayUseAmenities = dayUseAmenities;
		this.portageDistance = portageDistance;
		this.shore = shore;
	}
	
	public String getWaterway(){
		return waterway;
	}

	public String getId(){
		return id;
	}

	public String getMunicipality(){
		return municipality;
	}

	public String getLaunchType(){
		return launchType;
	}

	public String getParking(){
		return parking;
	}

	public String getOvernightParking(){
		return overnightParking;
	}
	
	public String getCamping(){
		return camping;
	}
	
	public String getPotableWater(){
		return potableWater;
	}
	
	public String getRestrooms(){
		return restrooms;
	}
	
	public String getDayUseAmenities(){
		return dayUseAmenities;
	}
	
	public String getPortageDistance(){
		return portageDistance;
	}
	
	public String getShore(){
		return shore;
	}
	
	@Override
	public MarkerOptions getMarkerOptions() {
		return new MarkerOptions()
				.title(getTitle())
				.position(new LatLng(lat, lng))
				.snippet(getSnippet())
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
	}
	
	@Override
	public String getTitle() {
		return "Launch - " + name;
	}

	@Override
	public String getSnippet() {
		return bodyOfWater + ", mile " + mile;
	}
	
	@Override
	public MapMarker cloneWithoutMarker(){
		return new LaunchMarker(name, waterway, id, municipality, launchType, 
				parking, overnightParking, camping, potableWater, restrooms, 
				dayUseAmenities, portageDistance, new LatLng(lat, lng), mile, shore, 
				bodyOfWater);
	}
	
	public static List<MapMarker> readMarker(XmlPullParser parser) 
			throws XmlPullParserException, IOException {
        
    	List<MapMarker> mapMarkers = new ArrayList<MapMarker>();
		
        double lat = 0, lng = 0;
        String name = null;
		double mile = 0;
		String bodyOfWater = null;

    	String waterway = null;
    	String id = null;
    	String municipality = null;
    	String launchType = null;
    	String parking = null;
    	String overnightParking = null;
    	String camping = null;
    	String potableWater = null;
    	String restrooms = null;
    	String dayUseAmenities = null;
    	String portageDistance = null;
    	String shore = null;

		 try{
		 String tag;
		 int event = parser.getEventType();
		 while(event != XmlPullParser.END_DOCUMENT){
			try{
			parser.nextTag();
			} catch(XmlPullParserException e){
				log("Returning " + mapMarkers.size() + " LaunchMarkers from catch");
				return mapMarkers;
			}
			tag = parser.getName();
		    if (tag.equals("boatlaunch")) {
		    	
		    	name = parser.getAttributeValue(null, "site_name");
				waterway = parser.getAttributeValue(null, "waterway");
				id = parser.getAttributeValue(null, "id");
				municipality = parser.getAttributeValue(null, "municipality");
				launchType = parser.getAttributeValue(null, "launch_type");
				parking = parser.getAttributeValue(null, "parking");
				overnightParking = parser.getAttributeValue(null, "overnight_parking");
				camping = parser.getAttributeValue(null, "camping");
				potableWater = parser.getAttributeValue(null, "potable_water");
				restrooms = parser.getAttributeValue(null, "restrooms");
				dayUseAmenities = parser.getAttributeValue(null, "day_use_amenities");
				portageDistance = parser.getAttributeValue(null, "portage_distance");
		    	lat = parseDouble(parser.getAttributeValue(null, "latitude"));
		    	lng = parseDouble(parser.getAttributeValue(null, "longitude"));
		    	mile = parseDouble(parser.getAttributeValue(null, "mile").replace("*", ""));
				shore = parser.getAttributeValue(null, "shore");
		    	bodyOfWater = parser.getAttributeValue(null, "bodyofwater");
				

		    	if(lat != -1 || lng != -1)
		    		mapMarkers.add(new LaunchMarker(name, waterway, id, municipality, launchType, 
	        				parking, overnightParking, camping, potableWater, restrooms, 
	        				dayUseAmenities, portageDistance, new LatLng(lat, lng), mile, shore, 
	        				bodyOfWater));	
	            
	            event = parser.next();   
		    }
		 }
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 
		 log("Returning " + mapMarkers.size() + " LaunchMarkers");
		 return mapMarkers;
	}

	public String toString(){
		return super.toString() + " " + name + " " + waterway + " " + id + " " + municipality + " " + launchType + " " + 
				parking + " " + overnightParking + " " + camping + " " + potableWater + " " + restrooms + " " + 
				dayUseAmenities + " " + portageDistance + " " + lat + " " + lng + " " + mile + " " + shore + " " + 
				bodyOfWater;
	}

	private static void log(String msg){
		log("LaunchMarker", msg);
    }
	
}
