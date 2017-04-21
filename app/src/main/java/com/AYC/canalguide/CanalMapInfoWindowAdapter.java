package com.AYC.canalguide;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

/**
 * This adapter will make a customize the content of the info window 
 * for when a marker is clicked
 * 
 * @author James O'Leary
 *
 */
public class CanalMapInfoWindowAdapter implements InfoWindowAdapter{

	private Activity activity;
	
	public CanalMapInfoWindowAdapter(Activity activity){
		super();
		this.activity = activity;
	}
	
	// Returns a custom view to show custom context
	@Override
	public View getInfoContents(Marker marker) {
		
		// Getting view from the layout file layout_infowindow
        View view = activity.getLayoutInflater().inflate(R.layout.layout_infowindow, null);
        
	        view = setTitleAndSnippet(view, marker);
		
		return view;
	}
	
	private View setTitleAndSnippet(View view, Marker marker){
		TextView title = (TextView) view.findViewById(R.id.infoWindowTitle);
        TextView snippet = (TextView) view.findViewById(R.id.infoWindowSnippet);
        
        // This is used because there is UTF-8 characters in the LockMarker's title
        try {
			title.setText(new String(marker.getTitle().getBytes("ISO-8859-1"), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			title.setText(marker.getTitle());
		}
        snippet.setText(marker.getSnippet());
        return view;
	}

	// Returns null to use the default window frame
	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}
	
}