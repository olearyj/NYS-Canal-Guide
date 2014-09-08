package com.AYC.canalguide;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.AYC.canalguide.canalparser.CanalGuideXmlParser;
import com.AYC.canalguide.canalparser.LockMarker;
import com.AYC.canalguide.canalparser.MapMarker;
import com.AYC.canalguide.canalparser.MarinaMarker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * This is the main activity which will include the map. It will retrieve the map of xmlStrings,
 * then use the CanalGuideXmlParser to get the markers.
 * 
 * @author James O'Leary
 *
 */
public class OldMainActivity extends FragmentActivity {

	public static final LatLng saratogaSprings = new LatLng(43.0616419,-73.7719178);
	public static final LatLng startLocation = saratogaSprings;
	public static final float startZoom = 8.0f;
	
	private GoogleMap mMap;
    private PoiAdapter poiAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        log("IN METHOD ONCREATE()");
  
		setUpMapIfNeeded();
        
        poiAdapter = new PoiAdapter();
        
        // Get xmlStrings list received from splash activity
		Intent intent = getIntent();
		HashMap<String, String> markerXmlHashMap = (HashMap<String, String>) intent.getSerializableExtra("map");	// HashMap<(url), (xmlString)>
		
		// TODO Parse in new thread then add to map in UI thread
		List<MapMarker> markerDataList = new ArrayList<MapMarker>();
		// For each xml document, get the data
		for(int i=0; i<markerXmlHashMap.size(); i++){
			try {
				markerDataList = new CanalGuideXmlParser().parse(new StringReader(
						markerXmlHashMap.get(SplashActivity.URLs[i])));
				log("Completed parsing for " + SplashActivity.URLs[i]);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			log("for url: " + SplashActivity.URLs[i] + "size() = " + markerDataList.size());
			
			// Create each MapMarker that came from the xmlString from one URL and
			// add the marker to the map
			Marker marker;
			MarkerOptions markerOptions;
			for(MapMarker mapMarker : markerDataList){
				//if(mapMarker.getBodyOfWater() == "hudson")	// TODO Correct this to only show hudson markers
				markerOptions = mapMarker.getMarkerOptions();
				if(markerOptions != null){
					marker = mMap.addMarker(markerOptions);	// Will get an error at this line on emulator
					
					mapMarker.setMarker(marker);
					poiAdapter.addItem(mapMarker);
				}
			}	
		}
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        // Move camera to area that includes POIs
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
        		saratogaSprings, startZoom));	
    }
    
    private void setUpMapIfNeeded() {
        if (mMap != null) {
            return;
        }
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        if (mMap == null) {
            return;
        }
        
        // Initialize map options
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        
        // Setting a custom info window adapter for the google map
        mMap.setInfoWindowAdapter(new CanalMapInfoWindowAdapter(this));
        
        // Setting click listener so if a user clicks on a markers pop-up window, it will do something
        mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
			@Override
			public void onInfoWindowClick(Marker marker) {
				MapMarker mapMarker = poiAdapter.getMapMarker(marker);
				log("mapMarker InfoWindow clicked: " + mapMarker.getName());
				
				if(mapMarker instanceof LockMarker || mapMarker instanceof MarinaMarker){
		            log("Creating the Marker Info Activity");
		            Intent intent = new Intent(OldMainActivity.this, MarkerInfoActivity.class);
		            intent.putExtra("MapMarker", mapMarker.cloneWithoutMarker());
		            startActivity(intent);			
				}
				else if(true){
					// TODO get url
					String url = "";
					Intent intent = new Intent(OldMainActivity.this, WebViewActivity.class);
					intent.putExtra("url", url);
					startActivity(intent);
				}
			}
		});
        
    }
        
    private void log(String msg){
    	Log.i("MainActivity", msg);
    }
    
}