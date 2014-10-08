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

public class LockMarker extends MapMarker implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String location;
	private String lift;
	private String address;
	private String city;
	private String zip;
	private String phoneNumber;
	
	public LockMarker(LatLng latLng, String name, String location, String lift, String address, String city, 
			String zip, double mile, String bodyOfWater, String phoneNumber){
		
		 super(latLng, name, bodyOfWater, mile);
		 this.location = location;
		 this.lift = lift;
		 this.address = address;
		 this.city = city;
		 this.zip = zip;
		 this.phoneNumber = phoneNumber;
	}
	
	public String getLocation(){
		return location;
	}

	public String getLift(){
		return lift;
	}

	public String getAddress(){
		return address;
	}

	public String getCity(){
		return city;
	}

	public String getZip(){
		return zip;
	}

	public String getPhoneNumber(){
		return phoneNumber;
	}
	
	@Override
	public MarkerOptions getMarkerOptions() {
		return new MarkerOptions()
				.title(getTitle())
				.position(new LatLng(lat, lng))
				.snippet(bodyOfWater + ", mile " + mile)
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
	}

	@Override
	public String getTitle(){
		return "Lock - " + name.replaceAll(" Lock", "")	+ " " + lift;
	}

	@Override
	public String getSnippet() {
		return bodyOfWater + ", mile " + mile;
	}
	
	
	@Override
	public MapMarker cloneWithoutMarker(){
		return new LockMarker(new LatLng(lat, lng), name, location, lift, address, 
    			city, zip, mile, bodyOfWater, phoneNumber);
	}
	
	public static List<MapMarker> readMarker(XmlPullParser parser) 
			throws XmlPullParserException, IOException {
        
    	List<MapMarker> mapMarkers = new ArrayList<MapMarker>();
		
         double lat = 0, lng = 0;
         String name = null;
		 String location = null;
		 String lift = null;
		 String address = null;
		 String city = null;
		 String zip = null; 
		 double mile = 0;
		 String bodyOfWater = null;
		 String phoneNumber = null;

		 try{
		 String tag;
		 int event = parser.getEventType();
		 while(event != XmlPullParser.END_DOCUMENT){
			try{
			parser.nextTag();
			} catch(XmlPullParserException e){
				log("Returning " + mapMarkers.size() + " LockMarkers from catch");
				return mapMarkers;
			}
		    tag = parser.getName();
		    if (tag.equals("lock")) {
		    	lat = parseDouble(parser.getAttributeValue(null, "latitude"));
		    	lng = parseDouble(parser.getAttributeValue(null, "longitude"));
		    	name = parser.getAttributeValue(null, "name");
		    	location = parser.getAttributeValue(null, "location");
		    	lift = parser.getAttributeValue(null, "lift");
		    	address = parser.getAttributeValue(null, "address");
		    	city = parser.getAttributeValue(null, "city");
		    	zip = parser.getAttributeValue(null, "zip");
		    	mile = parseDouble(parser.getAttributeValue(null, "mile").replace("*", ""));
		    	bodyOfWater = parser.getAttributeValue(null, "bodyofwater");
		    	phoneNumber = parser.getAttributeValue(null, "phonenumber");

		    	if(lat != -1 || lng != -1)
		    		mapMarkers.add(new LockMarker(new LatLng(lat, lng), name, location, lift, address, 
		    				city, zip, mile, bodyOfWater, phoneNumber));	
	            
	            event = parser.next();   
		    }
		 }
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 
		 log("Returning " + mapMarkers.size() + " LockMarkers");
		 return mapMarkers;
	}

	public String toString(){
		return super.toString() + " " + location + " " + lift + " " + address + " " + city + " " + 
				zip + " " + phoneNumber;
	}
	
	private static void log(String msg){
		log("LockMarker", msg);
    }
	
}