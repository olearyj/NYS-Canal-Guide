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

public class MarinaMarker extends MapMarker implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final BitmapDescriptor blueMarker = 
			BitmapDescriptorFactory.fromResource(R.drawable.mmi_blue_marker);
	
	private String shore;
	private String url;
	private String phoneNumber;
	private String vhf;
	private String fuel;
	private String repair;
	private String facilities;
	
	public MarinaMarker(LatLng latLng, String name, String bodyOfWater, double mile, String shore, 
			String url, String phoneNumber, String vhf, String fuel, String repair, String facilities){
		 super(latLng, name, bodyOfWater, mile);
		 this.shore = shore;
		 this.url = url;
		 this.phoneNumber = phoneNumber;
		 this.vhf = vhf;
		 this.fuel = fuel;
		 this.repair = repair;
		 this.facilities = facilities;		 
	}
	
	public String getShore(){
		return shore;
	}

	public String getUrl(){
		return url;
	}

	public String getPhoneNumber(){
		return phoneNumber;
	}

	public String getVhf(){
		return vhf;
	}

	public String getFuel(){
		return fuel;
	}

	public String getRepair(){
		return repair;
	}

	public String getFacilities(){
		return facilities;
	}

	@Override
	public MarkerOptions getMarkerOptions() {
		return new MarkerOptions()
		.title(getTitle())
		.position(new LatLng(lat, lng))
		.snippet(getSnippet())
		.icon(blueMarker);	//BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
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
		return new MarinaMarker(new LatLng(lat, lng), name, bodyOfWater, 
    			mile, shore, url, phoneNumber, vhf, fuel, repair, facilities);
	}
	
	public static List<MapMarker> readMarker(XmlPullParser parser)
			throws XmlPullParserException, IOException {
        
    	List<MapMarker> mapMarkers = new ArrayList<MapMarker>();
		
        double lat = 0, lng = 0;
        String name = null;	
        String bodyOfWater = null;
     	double mile = 0;
    	String shore = null;
    	String url = null;
    	String phoneNumber = null;
    	String vhf = null;
    	String fuel = null;
    	String repair = null;
    	String facilities = null;

		 try{
		 String tag;
		 int event = parser.getEventType();
		 while(event != XmlPullParser.END_DOCUMENT){
			 try{
			 parser.nextTag();
			 } catch(XmlPullParserException e){
				 log("Returning " + mapMarkers.size() + " MarinaMarkers from catch");
				 return mapMarkers;
			 }
			//parser.require(XmlPullParser.START_TAG, ns, "lock");
		    tag = parser.getName();
		    if (tag.equals("marina")) {
		    	lat = parseDouble(parser.getAttributeValue(null, "latitude"));
		    	lng = parseDouble(parser.getAttributeValue(null, "longitude"));
		    	name = parser.getAttributeValue(null, "marina");
		    	bodyOfWater = parser.getAttributeValue(null, "bodyofwater");
		    	mile = parseDouble(parser.getAttributeValue(null, "mile"));
		    	shore = parser.getAttributeValue(null, "shore");
		    	url = parser.getAttributeValue(null, "marina_url");
		    	phoneNumber = parser.getAttributeValue(null, "phonenumber");
		    	vhf = parser.getAttributeValue(null, "vhf");
		    	fuel = parser.getAttributeValue(null, "fuel");
		    	repair = parser.getAttributeValue(null, "repair");
		    	facilities = parser.getAttributeValue(null, "facilities");
		    

		    	if(lat != -1 || lng != -1)
		    		mapMarkers.add(new MarinaMarker(new LatLng(lat, lng), name, bodyOfWater, 
		    				mile, shore, url, phoneNumber, vhf, fuel, repair, facilities));	
	            
	            event = parser.next();   
		    }
		 }
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 
		 log("Returning " + mapMarkers.size() + " MarinaMarkers");
		 return mapMarkers;
	}
	
	public String toString(){
		return super.toString() + " " + shore + " " + url + " " + phoneNumber + " " + 
				vhf + " " + fuel + " " + repair + " " + facilities;
	}
	
	private static void log(String msg){
		log("MarinaMarker", msg);
    }
	
}
