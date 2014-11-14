package com.AYC.canalguide.NearbyPlaces;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.AsyncTask;
import android.util.Log;

public class AddNearbyPlacesToMapTask extends AsyncTask<Void, Integer, String> {
	
	private static final int radius = 1000;

	private GoogleMap googleMap;
	private String placesSearchUrl;
	
	public AddNearbyPlacesToMapTask(GoogleMap googleMap, double lat, double lng){
		super();
		this.googleMap = googleMap;
		placesSearchUrl = setPlacesSearchUrl(lat, lng);
	}
	 
    // Invoked by execute() method of this object
    @Override
    protected String doInBackground(Void... v) {
        String jsonString = null;
    	try{
            jsonString = downloadUrl(placesSearchUrl);
        }catch(Exception e){
            Log.d("Background Task",e.toString());
        }
    	
        return jsonString;
    }
 
    // Executed after the complete execution of doInBackground() method
    @Override
    protected void onPostExecute(String result){
        ParserTask parserTask = new ParserTask();
 
        // Start parsing the Google places in JSON format
        // Invokes the "doInBackground()" method of the class ParseTask
        parserTask.execute(result);
    }
    
    /** 
     * A class to parse the Google Places in JSON format 
     */
    private class ParserTask extends AsyncTask<String, Integer, List<Place>>{
 
        JSONObject jObject;
 
        // Invoked by execute() method of this object
        @Override
        protected List<Place> doInBackground(String... jsonData) {
 
            List<Place> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();
 
            try{
                jObject = new JSONObject(jsonData[0]);
 
                /// Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);
 
            }catch(Exception e){
                e.printStackTrace();
            }
            return places;
        }
 
        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<Place> list){
 
            for(int i=0;i<list.size();i++){
                // Creating a marker
                MarkerOptions markerOptions = list.get(i).getMarkerOptions();
 
                // Add the marker
                googleMap.addMarker(markerOptions);
            }
        }
    }
    
    public String downloadUrl(String URL){
    	try {		
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(URL);
 
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			return EntityUtils.toString(httpEntity); 
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return null;
    }

    public String setPlacesSearchUrl(double lat, double lng){
		return "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
			    "json?location=" + lat + "," + lng +
			    "&radius=" + radius + 
			    "&sensor=true" +
			    "&types=food|bar|store|museum|art_gallery"+
			    "&key=AIzaSyDixjYiealXZvS6rZk22UWmd9HLk6LqhsU";
		// TODO I will need to change the key here if i ever change the api_key
	}

}