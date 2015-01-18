package com.AYC.canalguide.canalparser;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.AYC.canalguide.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class BridgeGateMarker extends MapMarker implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final BitmapDescriptor yellowMarker = 
			BitmapDescriptorFactory.fromResource(R.drawable.mmi_yellow_marker);
	
	private String location;
	private String phoneNumber;
	private double clearanceClosed;
	private double clearanceOpened;
	
	public BridgeGateMarker(LatLng latLng, String name, String location, double mile, 
			String bodyOfWater, String phoneNumber, double clearanceClosed, double clearanceOpened){
		
		 super(latLng, name, bodyOfWater, mile);
		 this.location = location;
		 this.phoneNumber = phoneNumber;
		 this.clearanceClosed = clearanceClosed;
		 this.clearanceOpened = clearanceOpened;
	}
	
	public String getLocation(){
		return location;
	}

	public String getPhoneNumber(){
		return phoneNumber;
	}
	
	public double getClearanceClosed(){
		return clearanceClosed;
	}
	
	public double getClearanceOpened(){
		return clearanceOpened;
	}
	
	@Override
	public MarkerOptions getMarkerOptions() {
		return new MarkerOptions()
				.title(getTitle())
				.position(new LatLng(lat, lng))
				.snippet(getSnippet())
				.icon(yellowMarker);	//BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
	}

	@Override
	public String getTitle() {
		return name;
	}

	@Override
	public String getSnippet() {
		return bodyOfWater + ", mile " + mile;
	}
	
	@Override
	public MapMarker cloneWithoutMarker(){
		return new BridgeGateMarker(new LatLng(lat, lng), name, location, mile, bodyOfWater, phoneNumber,
				clearanceClosed, clearanceOpened);
	}
	
	public static List<MapMarker> readMarker(XmlPullParser parser) 
			throws XmlPullParserException, IOException {
        
    	List<MapMarker> mapMarkers = new ArrayList<MapMarker>();
		
         double lat = 0, lng = 0;
         String name = null;
		 String location = null;
		 double mile = 0;
		 String bodyOfWater = null;
		 String phoneNumber = null;
		 double clearanceClosed = 0;
		 double clearanceOpened = 0;

		 try{
		 String tag;
		 int event = parser.getEventType();
		 while(event != XmlPullParser.END_DOCUMENT){
			try{
			parser.nextTag();
			} catch(XmlPullParserException e){
				log("Returning " + mapMarkers.size() + " BridgeGateMarkers from catch");
				return mapMarkers;
			}
		    tag = parser.getName();
		    if (tag.equals("liftbridge") || tag.equals("guardgate")) {
		    	lat = parseDouble(parser.getAttributeValue(null, "latitude"));
		    	lng = parseDouble(parser.getAttributeValue(null, "longitude"));
		    	name = parser.getAttributeValue(null, "name");
		    	location = parser.getAttributeValue(null, "location");
		    	mile = parseDouble(parser.getAttributeValue(null, "mile").replace("*", ""));
		    	bodyOfWater = parser.getAttributeValue(null, "bodyofwater");
		    	phoneNumber = parser.getAttributeValue(null, "phonenumber");
		    	clearanceClosed = parseDouble(parser.getAttributeValue(null, "clearance_closed").replace("unlimited", "999"));
		    	clearanceOpened = parseDouble(parser.getAttributeValue(null, "clearance_opened").replace("unlimited", "999"));

		    	if(lat != -1 || lng != -1)
		    		mapMarkers.add(new BridgeGateMarker(new LatLng(lat, lng), name, location, 
		    				mile, bodyOfWater, phoneNumber, clearanceClosed, clearanceOpened));	
	            
	            event = parser.next();   
		    }
		 }
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 
		 log("Returning " + mapMarkers.size() + " BridgeGateMarkers");
		 return mapMarkers;
	}

	public String toString(){
		return super.toString() + " " + location + " " + phoneNumber;
	}

	private static void log(String msg){
		log("BridgeMarker", msg);
    }
	
}
