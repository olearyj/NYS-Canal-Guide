package com.AYC.canalguide;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import Tools.MyTimer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.AYC.canalguide.canalparser.CanalGuideXmlParser;
import com.AYC.canalguide.canalparser.MapMarker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CanalMapFragment extends MapFragment {

	public static final LatLng saratogaSprings = new LatLng(43.0616419,-73.7719178);
	public static final LatLng startLocation = saratogaSprings;
	public static final float startZoom = 8.0f;	// 8.0f is perfect
	
	private HashMap<String, String> xmlStrings, navInfoXmlStrings;
	private Activity activity;
	private Context context;
	
	private GoogleMap mMap;
	private PoiAdapter poiAdapter;
	
	private CameraPosition lastCameraPosition;
	
	public CanalMapFragment(){
		super();
		poiAdapter = new PoiAdapter();
	}
	
	public CanalMapFragment(HashMap<String, String> xmlStrings, Context context){
		super();
		this.xmlStrings = xmlStrings;
		this.context = context;
	}
	
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		this.activity = getActivity();
		this.xmlStrings = ((MainActivity) getActivity()).getXmlStrings();
		this.context = getActivity().getApplicationContext();

		initMap();
		return view;
	}    
    
    /**
     * When the app resumes, it will set the camera position
     */
    @Override
    public void onResume() {
        super.onResume();

        mMap = getMap();
        // If the app was never paused(freshly opened) move camera to default position
        if(lastCameraPosition == null)
	        // Move camera to area that includes POIs (hudson river)
	        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
	        		startLocation, startZoom));
        else
        	// Move camera to the position when the app was paused
        	mMap.moveCamera(CameraUpdateFactory.newCameraPosition(lastCameraPosition));
    }
    
    /**
     * When the application is paused, save the last camera position so when
     * this is resumed, the map will be shown with the same camera position
     */
    @Override
    public void onPause() {
        super.onPause();
        
        lastCameraPosition = mMap.getCameraPosition();
    }
    
    protected void setCameraPositionToDefault(){
    	lastCameraPosition = null;
    }
    
    private void initMap(){
    	
    	mMap = getMap();
    	
    	// Initialize map options
        mMap.setMyLocationEnabled(true);
        
        // Set map type to what the user selected in the options
    	OptionsFragment optFrag = (OptionsFragment) ((MainActivity) activity).getOptionsFragment();
    	log("init setting MAP TYPE = " + optFrag.getMapType());
        mMap.setMapType(optFrag.getMapType());
        
        // Setting a custom info window adapter for the google map
        mMap.setInfoWindowAdapter(new CanalMapInfoWindowAdapter((MainActivity) activity));
        
        // Setting click listener so if a user clicks on a markers pop-up window, it will do something
        mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
			@Override
			public void onInfoWindowClick(Marker marker) {
				MapMarker mapMarker = poiAdapter.getMapMarker(marker);
				log("MapMarker InfoWindow clicked: " + mapMarker.getName());
				
	            log("Creating the Marker Info Activity");
	            Intent intent = new Intent(context, MarkerInfoActivity.class);
	            intent.putExtra("MapMarker", mapMarker.cloneWithoutMarker());
	            startActivity(intent);
			}
		});

        // TODO - dont always parse then add, only do when poiAdapter is empty??
        if(poiAdapter.getCount() != 0)
        	addExistingMarkersToMap();
        else
        	parseXmlStringsAndAddMarkersToMap(xmlStrings);
        /*if(markersNotFilteredOut("navinfo")){
        	if(navInfoXmlStrings == null)
        		((MainActivity) getActivity()).startDownloadThreadPoolService();
        	else
        		parseXmlStringsAndAddMarkersToMap(navInfoXmlStrings);
        }*/
    }
    
    /**
     * 
     * @param xmlStrings
     */
    protected void parseXmlStringsAndAddMarkersToMap(HashMap<String, String> xmlStrings){
    	List<MapMarker> markerDataList = new ArrayList<MapMarker>();
		// For each xml document, get the data
    	Iterator<Map.Entry<String, String>> iterator = xmlStrings.entrySet().iterator();
	    while (iterator.hasNext()) {
	        Map.Entry<String, String> pairs = (Map.Entry<String, String>) iterator.next();
	        String currentURL = pairs.getKey();
	        String currentXmlString = pairs.getValue();
	        //iterator.remove(); // Avoids a ConcurrentModificationException
	        
			String currentXmlDocName = currentURL
					.replace("http://www.canals.ny.gov/xml/", "").replace(".xml", "");
			
				if(markersNotFilteredOut(currentXmlDocName)){
				
				try {
					markerDataList = new CanalGuideXmlParser().parse(new StringReader(
							currentXmlString));
					log("Completed parsing for " + currentURL);
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				log("for url: " + currentURL + "size() = " + markerDataList.size());
				
				// Create each MapMarker that came from the xmlString from one URL and
				// add the marker to the map
				Marker marker;
				MarkerOptions markerOptions;
				
				for(MapMarker mapMarker : markerDataList){
					markerOptions = mapMarker.getMarkerOptions();
					if(markerOptions != null && mapMarker != null){
						marker = mMap.addMarker(markerOptions);	// Will get an error at this line on emulator
						
						mapMarker.setMarker(marker);
						poiAdapter.addItem(mapMarker);
					}
				}
			}
		}
    }
    
    private void addExistingMarkersToMap(){
    	log("Adding existing markers to the map. poiAdapter size = " + poiAdapter.getCount());
    	Marker marker;
		MarkerOptions markerOptions;
		MyTimer timer = new MyTimer();
		
    	for(MapMarker mapMarker : poiAdapter){
    		if(markersNotFilteredOut(mapMarker)){
    			markerOptions = mapMarker.getMarkerOptions();
				
	    		if(markerOptions != null && mapMarker != null){
					timer.startTimer();
					marker = mMap.addMarker(markerOptions);	// Will get an error at this line on emulator
					timer.endTimer();
		    		
		    		mapMarker.setMarker(marker);
	    		}
    		}
    	}
    	timer.printTimeStats("marker = mMap.addMarker(markerOptions);");
    }
    
    private boolean markersNotFilteredOut(MapMarker mapMarker){
    	return markersNotFilteredOut(MapMarker.urlDocName(mapMarker));
    }
    
    private boolean markersNotFilteredOut(String urlDocName){
    	OptionsFragment optFrag = (OptionsFragment) ((MainActivity) activity).getOptionsFragment();
    	boolean[] switchValues = optFrag.getFilterData();
    	
    	if(switchValues == null){
    		if(urlDocName.contains("navinfo"))
    			return false;
    		else
    			return true;
    	}
    	else{
    		if(urlDocName.equals("locks"))
    			return switchValues[0];
    		else if(urlDocName.equals("marinas"))
    			return switchValues[1];
    		else if(urlDocName.equals("canalwatertrail"))
    			return switchValues[2];
    		else if(urlDocName.equals("liftbridges") || urlDocName.equals("guardgates"))
    			return switchValues[3];
    		else if(urlDocName.equals("boatsforhire"))
    			return switchValues[4];
    		if(urlDocName.contains("navinfo"))
    			return switchValues[5];
    		
    	}
    	return true;
    }
    
    private boolean switchValuesDefault(boolean[] switchValues){
    	for(int i=0; i<switchValues.length-1; i++)
    		if(switchValues[i] == false)
    			return false;
    	return true;
    }
    
    private void log(String msg){
    	if(SplashActivity.LOG_ENABLED)
    		Log.i("CanalMapFragment", msg);
    }
    
}