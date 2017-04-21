package com.AYC.canalguide.NearbyPlaces;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONObject;

import com.AYC.canalguide.SplashActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.AsyncTask;
import android.util.Log;

/**
 * This task will download the jsonData that includes nearby places data. It will then parse
 * the places data and add them to the maps.
 * 
 * @author James O'Leary
 *
 */
public class AddNearbyPlacesToMapTask extends AsyncTask<Void, Integer, String> {
	
    private static final String types = "airport|amusement_park|aquarium|art_gallery|bakery|bank|bar" +
    		"|beauty_salon|book_store|bowling_alley|bus_station|cafe|casino|department_store|food" +
    		"|gas_station|grocery_or_supermarket|hair_care|hardware_store|laundry|library" +
    		"|liquor_store|lodging|meal_delivery|meal_takeaway|movie_theater|museum|park|pet_store" +
    		"|pharmacy|post_office|restaurant|shoe_store|shopping_mall|spa|stadium|store" +
    		"|subway_station|taxi_stand|train_station|zoo";
    //private static final String types1 = "food|bar|store|museum|art_gallery";
	private static final int radius = 1000;

	private GoogleMap googleMap;
	private String placesSearchUrl;
	
	/**
	 * Constructs the task
	 * 
	 * @param googleMap in order to add the nearby places to it on completion
	 * @param latLng
	 * Location to get near places
	 */
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
	
    /**
     *  Invoked by execute() method of this object
     *  Starts to download in the background
     */
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
 
    /**
     *  Executed after the complete execution of doInBackground() method
     *  Will then start the parser task to parse the downloaded jsonString
     */
    @Override
    protected void onPostExecute(String result){
        ParserTask parserTask = new ParserTask();
 
        // Start parsing the Google places in JSON format
        // Invokes the "doInBackground()" method of the class ParseTask
        parserTask.execute(result);
    }
 
    /**
     * Downloads url to a string
     * 
     * @param strUrl
     * @return downloaded page as a string
     * @throws IOException
     */
    private String downloadUrl(String strUrl) throws IOException{
        String dataString = "";
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

            String line = "";
            while( ( line = br.readLine())  != null){
                dataString += line;
            }
 
            br.close();
 
        }catch(Exception e){
            log("Exception while downloading url");
            e.printStackTrace();
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
 
        return dataString;
    }

    /**
     * URL string to get the nearby places in json format
     * 
     * @param latitude
     * @param longitude
     * @return URL string
     */
    public String setPlacesSearchUrl(double lat, double lng){
		return "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
			    "json?location=" + lat + "," + lng +
			    "&radius=" + radius + 
			    "&sensor=true" +
			    "&types=" + types +
			    "&key=AIzaSyAAepbo8NVMqtqz_TQczV7YLPwSu6yhj5g";
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
                jObject = new JSONObject(jsonString[0]);
 
                // Getting the parsed data as a List construct
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