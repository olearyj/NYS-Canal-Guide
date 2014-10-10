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
	
	private static final BitmapDescriptor greenBuoyIcon = 
			BitmapDescriptorFactory.fromResource(R.drawable.mmi_green_buoy);
	
	private static final BitmapDescriptor redBuoyIcon = 
			BitmapDescriptorFactory.fromResource(R.drawable.mmi_red_buoy);
	
	private static final BitmapDescriptor greenBeaconIcon = 
			BitmapDescriptorFactory.fromResource(R.drawable.mmi_green_beacon);

	private static final BitmapDescriptor redBeaconIcon = 
			BitmapDescriptorFactory.fromResource(R.drawable.mmi_red_beacon);
	
	private static final BitmapDescriptor bridgeIcon = 
			BitmapDescriptorFactory.fromResource(R.drawable.mmi_bridge);
	
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
		BitmapDescriptor bd = getBitmapDescriptor();
		if(bd != null)
			return new MarkerOptions()
				.title(getTitle())
				.position(new LatLng(lat, lng))
				.snippet(getSnippet())
				.anchor(0.5f, 0.5f)
				.icon(bd);
		else
			return null;
	}
	
	@Override
	public String getTitle(){
		return name;
	}
	
	@Override
	public String getSnippet(){
		return "Mile " + mile + 
				(isNotBlank(southWestDepth) ? ", SW Depth=" + southWestDepth : "") + 
				(isNotBlank(middleDepth) ? ", Middle Depth=" + middleDepth : "") + 
				(isNotBlank(northEastDepth) ? ", NE Depth=" + northEastDepth : "") + 
				(isNotBlank(overheadClearance) ? ", Overhead Clearance=" + overheadClearance : "");
	}
	
	public BitmapDescriptor getBitmapDescriptor(){
		
		if(name.toLowerCase().contains("buoy")){
			if(featureColor.equalsIgnoreCase("green"))
				return greenBuoyIcon;	//BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
			else if(featureColor.equalsIgnoreCase("red"))
				return redBuoyIcon;	//BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
		}
		else if(name.toLowerCase().contains("beacon")){
			if(featureColor.equalsIgnoreCase("green"))
				return greenBeaconIcon;	//BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
			else if(featureColor.equalsIgnoreCase("red"))
				return redBeaconIcon;	//BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
		}
		else if(name.toLowerCase().contains("bridge"))
			return bridgeIcon;
		
		return null;
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
		
	public String toString(){
		return super.toString() + shore + " " + featureUrl + " " + featureColor + " " + 
				channelWidth + " " + southWestDepth + " " + middleDepth + " " + middleDepthUrl + 
				" " + northEastDepth + " " + overheadClearance + " " + noaaPage + " " + noaaPageUrl;
	}
	
	private static void log(String msg){
		log("NavInfoMarker", msg);
    }

}
