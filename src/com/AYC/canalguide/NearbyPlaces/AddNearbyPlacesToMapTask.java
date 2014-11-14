package com.AYC.canalguide.NearbyPlaces;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.AYC.canalguide.SplashActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.AsyncTask;
import android.util.Log;

public class AddNearbyPlacesToMapTask extends AsyncTask<Void, Integer, String> {
	
	private static final int radius = 1000;

	private GoogleMap googleMap;
	private String placesSearchUrl;
	
	public AddNearbyPlacesToMapTask(GoogleMap googleMap, LatLng latLng){
		super();
		this.googleMap = googleMap;
		placesSearchUrl = setPlacesSearchUrl(latLng.latitude, latLng.longitude);
	}
	
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
            e.printStackTrace();
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
    
    /*
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
    }*/
    
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
 
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
 
            // Connecting to url
            urlConnection.connect();
 
            // Reading data from url
            iStream = urlConnection.getInputStream();
 
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
 
            StringBuffer sb  = new StringBuffer();
 
            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }
 
            data = sb.toString();
 
            br.close();
 
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
 
        return data;
    }

    public String setPlacesSearchUrl(double lat, double lng){
		return "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
			    "json?location=" + lat + "," + lng +
			    "&radius=" + radius + 
			    "&sensor=true" +
			    "&types=food|bar|store|museum|art_gallery"+
			    "&key=AIzaSyAAepbo8NVMqtqz_TQczV7YLPwSu6yhj5g";
		// TODO I will need to change the key here if i ever change the api_key
	}

    /** 
     * A class to parse the Google Places in JSON format 
     */
    private class ParserTask extends AsyncTask<String, Integer, List<Place>>{
 
        JSONObject jObject;
 
        // Invoked by execute() method of this object
        @Override
        protected List<Place> doInBackground(String... jsonString) {
 
            List<Place> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();
 
            try{
            	log("jsonString = " + jsonString[0]);
            	if(jsonString[0] == null)
                	log("jsonString == null!!!");
                
                jObject = new JSONObject(jsonString[0]);
 
                if(jObject == null)
                	log("jObj == null!!!");
                
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
 
            for(int i=0; i<list.size(); i++){
                // Creating a marker
                MarkerOptions markerOptions = list.get(i).getMarkerOptions();
 
                // Add the marker
                googleMap.addMarker(markerOptions);
            }
            log("Added " + list.size() + " nearby places to the map");
        }
    }
    
    private void log(String message) {
		if(SplashActivity.LOG_ENABLED)
    		Log.i("AddNearbyPlacesToMapTask", message);
	}

}