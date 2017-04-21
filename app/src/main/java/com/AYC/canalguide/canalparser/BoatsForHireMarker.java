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

public class BoatsForHireMarker extends MapMarker implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final BitmapDescriptor violetMarker = 
			BitmapDescriptorFactory.fromResource(R.drawable.mmi_violet_marker);
	
	private String type;
	private String address;
	private String url;
	private String city;
	private String state;
	private String zip;
	private String phoneNumber;
	private String vesseltypes;
	private String cruisetype;
	private String homeport;
	private String waterways;
	
	public BoatsForHireMarker(LatLng latLng, String name, String type, String address, String url,
			String city, String state, String zip, String phoneNumber, String vesseltypes, 
			String cruisetype, String homeport, String waterways){
		
		 super(latLng, name, null, -1);
		 this.type = type;
		 this.address = address;
		 this.url = url;
		 this.city = city;
		 this.state = state;
		 this.zip = zip;
		 this.phoneNumber = phoneNumber;
		 this.vesseltypes = vesseltypes;
		 this.cruisetype = cruisetype;
		 this.homeport = homeport;
		 this.waterways = waterways;
	}
	
	public String getType(){
		return type;
	}

	public String getAddress(){
		return address;
	}
	
	public String getUrl(){
		return url;
	}
	
	public String getCity(){
		return city;
	}

	public String getState(){
		return state;
	}
	
	public String getZip(){
		return zip;
	}

	public String getPhoneNumber(){
		return phoneNumber;
	}
	
	public String getVesselTypes(){
		return vesseltypes;
	}
	
	public String getCruiseType(){
		return cruisetype;
	}
	
	public String getHomeport(){
		return homeport;
	}
	
	public String getWaterways(){
		return waterways;
	}
	
	@Override
	public MarkerOptions getMarkerOptions() {
		return new MarkerOptions()
				.title(getTitle())
				.position(new LatLng(lat, lng))
				.snippet(getSnippet())
				.icon(violetMarker);	//BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
	}
	
	@Override
	public String getTitle() {
		return name;
	}

	@Override
	public String getSnippet(){
		if(getType().equals("rentals"))
        	return "Rentals: " + getVesselTypes();
        else if(getType().equals("cruises"))
        	return "Cruises: " + getCruiseType();

		log("Unexpected BoatsForHire Type");
		return "";
	}
	
	@Override
	public MapMarker cloneWithoutMarker(){
		return new BoatsForHireMarker(new LatLng(lat, lng), name, type, address, url,
				city, state, zip, phoneNumber, vesseltypes, cruisetype,	homeport, waterways);
	}
	
	public static List<MapMarker> readMarker(XmlPullParser parser) 
			throws XmlPullParserException, IOException {
        
    	 List<MapMarker> mapMarkers = new ArrayList<MapMarker>();
		
         double lat = 0, lng = 0;
         String name = null;
     	 String type = null;
    	 String address = null;
    	 String url = null;
    	 String city = null;
    	 String state = null;
    	 String zip = null;
    	 String phoneNumber = null;
    	 String vesseltypes = null;
    	 String cruisetype = null;
    	 String homeport = null;
    	 String waterways = null;

		 try{
		 String tag;
		 int event = parser.getEventType();
		 while(event != XmlPullParser.END_DOCUMENT){
			 try{
			 parser.nextTag();
			 } catch(XmlPullParserException e){
				 log("Returning " + mapMarkers.size() + " BoatsForHireMarkers from catch");
				 return mapMarkers;
			 }
		    tag = parser.getName();
		    if (tag.equals("cruise")) {
		    	lat = parseDouble(parser.getAttributeValue(null, "latitude"));
		    	lng = parseDouble(parser.getAttributeValue(null, "longitude"));
		    	name = parser.getAttributeValue(null, "company");
		    	type = parser.getAttributeValue(null, "type");
		    	address = parser.getAttributeValue(null, "address");
		    	url = parser.getAttributeValue(null, "company_url");
		    	city = parser.getAttributeValue(null, "city");
		    	state = parser.getAttributeValue(null, "state");
		    	zip = parser.getAttributeValue(null, "zip");
		    	phoneNumber = parser.getAttributeValue(null, "phonenumber");
		    	vesseltypes = parser.getAttributeValue(null, "vesseltypes");
		    	cruisetype = parser.getAttributeValue(null, "cruisetype");
		    	homeport = parser.getAttributeValue(null, "homeport");
		    	waterways = parser.getAttributeValue(null, "waterways");

		    	if(lat != -1 || lng != -1)
		    		mapMarkers.add(new BoatsForHireMarker(new LatLng(lat, lng), name, type, address, url,
		    				city, state, zip, phoneNumber, vesseltypes, cruisetype,	homeport, waterways));	
	            
	            event = parser.next();   
		    }
		 }
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 
		 log("Returning " + mapMarkers.size() + " BoatsForHireMarkers");
		 return mapMarkers;
	}

	public String toString(){
		return super.toString() + " " + type + " " + address + " " + url + " " + city + " " + 
				state + " " + zip + " " + phoneNumber + " " + vesseltypes + " " + cruisetype + " " + 
				homeport + " " + waterways;
	}
	
	private static void log(String msg){
		log("BoatsForHireMarker", msg);
    }
	
}
