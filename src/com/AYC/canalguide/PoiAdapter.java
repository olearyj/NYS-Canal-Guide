package com.AYC.canalguide;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.AYC.canalguide.canalparser.MapMarker;
import com.AYC.canalguide.canalparser.NavInfoMarker;
import com.google.android.gms.maps.model.Marker;

/**
 * This adapter will come in handy when i have different types of 
 * POI and i want to remove visibility of a group of POIs.
 * 
 * @author James O'Leary
 *
 */
public class PoiAdapter extends BaseAdapter implements Iterable<MapMarker>, Iterator<MapMarker> {

	// List of MapMarkers that contains all of the data including the Marker
	private ArrayList<MapMarker> mapMarkerList;
	private int index;
	
	public PoiAdapter(){
		mapMarkerList = new ArrayList<MapMarker>();
		index = 0;
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
	
	public boolean containsNavInfoMarkers(){
		for(int i = 0; i<mapMarkerList.size(); i++){
			MapMarker mm = mapMarkerList.get(i);
			if(mm instanceof NavInfoMarker && mm.getMarker() != null)
				return true;
		}
		return false;
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
	
	// Iterator and Iterable methods that are overridden

	/**
	 * Returns true if there is a next MapMarker
	 */
	@Override
	public boolean hasNext() {
		if(index < mapMarkerList.size())
			return true;
		return false;
	}

	/**
	 * Returns the next MapMarker in the list
	 * 
	 * Throws a NoSuchElementException if there is no more elements
	 */
	@Override
	public MapMarker next() {
		if(index == mapMarkerList.size())
			throw new NoSuchElementException();
		
		index++;
		return mapMarkerList.get(index-1);
	}

	/**
	 * Unimplemented method
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * This class implements Iterable. This method will set index to 0.
	 * 
	 * @return this
	 */
	@Override
	public Iterator<MapMarker> iterator() {
		index = 0;
		return this;
	}

    private void log(String msg){
    	if(SplashActivity.LOG_ENABLED)
    		Log.i("PoiAdapter", msg);
    }
	
}