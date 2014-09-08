package com.AYC.canalguide;

import java.util.ArrayList;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.AYC.canalguide.canalparser.MapMarker;
import com.google.android.gms.maps.model.Marker;

/**
 * This adapter will come in handy when i have different types of 
 * POI and i want to remove visibility of a group of POIs.
 * 
 * @author James O'Leary
 *
 */
public class PoiAdapter extends BaseAdapter {

	// List of MapMarkers that contains all of the data including the Marker
	private ArrayList<MapMarker> mapMarkerList;
	
	public PoiAdapter(){
		mapMarkerList = new ArrayList<MapMarker>();
	}
	
	/**
	 * Adds marker to the PoiAdapter's ArrayList
	 * 
	 * @param marker
	 * The marker you want to add to the PoiAdapter's ArrayList
	 */
	public void addItem(MapMarker mapMarker){
		mapMarkerList.add(mapMarker);
	}
	
	public MapMarker getMapMarker(Marker marker){
		
		for(MapMarker mapMarker: mapMarkerList)
			if(mapMarker.getMarker().equals(marker))
				return mapMarker;
		
		return null;
	}
	
	/**
	 * This method will get the count of the markerList
	 */
	@Override
	public int getCount() {
		return mapMarkerList.size();
	}

	/**
	 * This method will get a marker by index number
	 */
	@Override
	public MapMarker getItem(int idx) {
		return mapMarkerList.get(idx);
	}

	/**
	 * Unimplemented
	 */
	@Override
	public long getItemId(int position) {
		return 0;
	}

	/**
	 * Unimplemented
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

    private void log(String msg){
    	if(SplashActivity.LOG_ENABLED)
    		Log.i("PoiAdapter", msg);
    }
	
}
