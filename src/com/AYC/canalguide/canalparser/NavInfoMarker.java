package com.AYC.canalguide.canalparser;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * This class holds marker information for buoys, beacons and some other navigation information
 * 
 * @author James O'Leary
 *
 */
public class NavInfoMarker extends MapMarker implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String shore;
	private String featureUrl;
	private String featureColor;
	private int channelWidth;
	private int southWestDepth;
	private int middleDepth;
	private String middleDepthUrl;
	private int northEastDepth;
	private String overheadClearance;
	private String noaaPage;
	private String noaaPageUrl;
	
	public NavInfoMarker(LatLng latLng, String feature, double mile, String shore, 
			String featureUrl, String featureColor, int channelWidth, int southWestDepth, 
			int middleDepth, String middleDepthUrl, int northEastDepth, String overheadClearance, 
			String noaaPage, String noaaPageUrl){
		super(latLng, feature, "", mile);
		this.shore = shore;
		this.featureUrl = featureUrl;
		this.featureColor = featureColor;
		this.channelWidth = channelWidth;
		this.southWestDepth = southWestDepth;
		this.middleDepth = middleDepth;
		this.middleDepthUrl = middleDepthUrl;
		this.northEastDepth = northEastDepth;
		this.overheadClearance = overheadClearance;
		this.noaaPage = noaaPage;
		this.noaaPageUrl = noaaPageUrl;		 
	}
	

	public String getShore(){
		return shore;
	}

	public String getFeatureUrl(){
		return featureUrl;
	}

	public String getFeatureColor(){
		return featureColor;
	}

	public int getChannelWidth(){
		return channelWidth;
	}

	public int getSouthWestDepth(){
		return southWestDepth;
	}

	public int getMiddleDepth(){
		return middleDepth;
	}
	
	public String getMiddleDepthUrl(){
		return middleDepthUrl;
	}
	
	public int getNorthEastDepth(){
		return northEastDepth;
	}
	
	public String getOverheadClearance(){
		return overheadClearance;
	}
	
	public String getNoaaPage(){
		return noaaPage;
	}

	public String getNoaaPageUrl(){
		return noaaPageUrl;
	}


	@Override
	public MarkerOptions getMarkerOptions() {
		return new MarkerOptions()
		.title(name)
		.position(new LatLng(lat, lng))
		.snippet(bodyOfWater + ", mile " + mile)
		.icon(getBitmapDescriptor());
	}
	
	// TODO
	public String getTitle(){
		return "";
	}
	
	// TODO
	public String getSnippet(){
		return "";
	}
	
	// TODO
	public BitmapDescriptor getBitmapDescriptor(){
		if(featureColor.equalsIgnoreCase("green"))
			return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
		else if(featureColor.equalsIgnoreCase("red"))
			return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
		else if(featureColor.equalsIgnoreCase(""))
			return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
		else
			return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
	}
		
	@Override
	public MapMarker cloneWithoutMarker(){
		return new NavInfoMarker(new LatLng(lat, lng), name, mile, shore, featureUrl, 
				featureColor, channelWidth, southWestDepth, middleDepth, middleDepthUrl, 
				northEastDepth, overheadClearance, noaaPage, noaaPageUrl);
	}
	
	public static List<MapMarker> readMarker(XmlPullParser parser)
			throws XmlPullParserException, IOException {
        
    	List<MapMarker> mapMarkers = new ArrayList<MapMarker>();
		
        double lat = 0, lng = 0;
        String name = null;	
     	double mile = 0;
    	String shore = null;
    	String featureUrl = null;
    	String featureColor = null;
    	int channelWidth = 0;
    	int southWestDepth = 0;
    	int middleDepth = 0;
    	String middleDepthUrl = null;
    	int northEastDepth = 0;
    	String overheadClearance = null;
    	String noaaPage = null;
    	String noaaPageUrl = null;

		 try{
		 String tag;
		 int event = parser.getEventType();
		 while(event != XmlPullParser.END_DOCUMENT){
			try{
				while(parser.getEventType() != XmlPullParser.END_TAG)
					parser.next();
				parser.nextTag();
			} catch(XmlPullParserException e){
				e.printStackTrace();
				log("Returning " + mapMarkers.size() + " NavInfoMarkers from catch");
				return mapMarkers;
			}
		    tag = parser.getName();
		    if (tag.equals("channelinfo")) {
		    	lat = parseDouble(parser.getAttributeValue(null, "latitude"));
		    	lng = parseDouble(parser.getAttributeValue(null, "longitude"));
		    	name = parser.getAttributeValue(null, "feature");
		    	mile = Double.parseDouble(parser.getAttributeValue(null, "mile"));
		    	shore = parser.getAttributeValue(null, "shore");
		    	featureUrl = parser.getAttributeValue(null, "feature_url");
		    	featureColor = parser.getAttributeValue(null, "feature_color");
		    	channelWidth = parseInt(parser.getAttributeValue(null, "channel_width"));
		    	southWestDepth = parseInt(parser.getAttributeValue(null, "south_west_depth"));
		    	middleDepth = parseInt(parser.getAttributeValue(null, "middle_depth"));
		    	middleDepthUrl = parser.getAttributeValue(null, "middle_depth_url");
		    	northEastDepth = parseInt(parser.getAttributeValue(null, "north_east_depth"));
		    	overheadClearance = parser.getAttributeValue(null, "overhead_clearance");
		    	noaaPage = parser.getAttributeValue(null, "noaa_page");
		    	noaaPageUrl = parser.getAttributeValue(null, "noaa_page_url");
		    
		    	if(lat != -1 || lng != -1)
		    		mapMarkers.add(new NavInfoMarker(new LatLng(lat, lng), name, mile, shore, featureUrl, 
	        				featureColor, channelWidth, southWestDepth, middleDepth, middleDepthUrl, 
	        				northEastDepth, overheadClearance, noaaPage, noaaPageUrl));	
	            
	            event = parser.next();   
		    }
		 }
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 
		 log("Returning " + mapMarkers.size() + " NavInfoMarkers");
		 return mapMarkers;
	}
	
	private static int parseInt(String string){
		if(string.equals(""))
			return -1;
		else
			return Integer.parseInt(string);
	}
	
	private static double parseDouble(String string){
		if(string.equals(""))
			return -1;
		else
			return Double.parseDouble(string);
	}
	
	public String toString(){
		return super.toString() + shore + " " + featureUrl + " " + featureColor + " " + 
				channelWidth + " " + southWestDepth + " " + middleDepth + " " + middleDepthUrl + 
				" " + northEastDepth + " " + overheadClearance + " " + noaaPage + " " + noaaPageUrl;
	}
	
	private static void log(String msg){
		log("NavInfoMarker", msg);
    }

}
