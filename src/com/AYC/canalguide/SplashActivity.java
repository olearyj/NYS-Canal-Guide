package com.AYC.canalguide;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * This activity will be the splash screen (loading screen). If the last downloaded data
 * isn't too old, it will load that marker data. Else it will download data from the NYS canal
 * web site in an AsyncTask and will save that data. This splash screen will always be up for 
 * at least a minimum amount of time. If there exists saved data and the data isn't downloaded 
 * in that time, a button will then pop-up allowing the choice to load from storage.
 * 
 * @author James O'Leary
 *
 */
public class SplashActivity extends Activity {

	// This boolean will be used in all classes to choose whether to use logs or not
	public static final boolean LOG_ENABLED = true;
	
	// This is the minimum amount of time the splash screen will be up for (in milliseconds)
	private static final long MINIMUM_SPLASH_TIME = 1000L;
	
	private static final String PREFS_NAME = "xmlStrings";
	
	private static final String DATA_LAST_SAVED_DATE_TAG = "Last saved date";
	private static final long DAY_IN_MILLISECONDS = 86400000;
	private static final long DATA_VALID_TIME = 7 * DAY_IN_MILLISECONDS;	// Data valid for 7 days
	
	// Default permissions for this because MainActivity uses this
	public static final String[] URLs = {"http://www.canals.ny.gov/xml/locks.xml", 
			"http://www.canals.ny.gov/xml/marinas.xml", "http://www.canals.ny.gov/xml/canalwatertrail.xml", 
			"http://www.canals.ny.gov/xml/liftbridges.xml", "http://www.canals.ny.gov/xml/guardgates.xml",
			"http://www.canals.ny.gov/xml/boatsforhire.xml"};
	
	private Handler handler;
	private CountDownLatch countDownLatch;
	
	// Use this to cancel if needed
	private LoadAsyncTask downloadMarkersAsyncTask;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        log("Created Splash Activity");

        handler = new Handler();
        
        // If the data has been downloaded before and isn't too old, load saved data
        // else, download data from the website
        if( isSavedDataValid() ){
        	createMainActivity( loadXmlStrings() );
        }
        else{
	        // Latch is initialized with the parameter two because were waiting
	        // for the one runnable that is post delayed to countDown the latch
        	// and to finish downloading the data
	        countDownLatch = new CountDownLatch(2);
	        
	        downloadMarkersAsyncTask = (LoadAsyncTask) new LoadAsyncTask().execute(URLs);
        }
    }
    
    /**
     * This extends AsychTask will load add app data (from data or storage)
     * It will then create the main activity and close this splash activity when finished 
     * 
     * @author James O'Leary
     *
     */
	private class LoadAsyncTask extends AsyncTask<String, Void, HashMap<String, String>>{

		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            log("Created LoadAsyncTask");
            
            handler.postDelayed(new Runnable(){
            	@Override
        		public void run() {
        			countDownLatch.countDown();
        			log("latch countDown: Time has passed minimum time of: " + 
        					MINIMUM_SPLASH_TIME + "ms for this splash screen");
        		    
            		// If there is saved data and if the data isn't finished downloading yet, 
        			// display a button allowing the option to load from previously stored data
            		if( loadDataLastSavedDate() != -1 && countDownLatch.getCount() != 0 )	
            			createLoadFromStorageButton();
            	}
            }, MINIMUM_SPLASH_TIME);
		}
		
		@Override
		protected HashMap<String, String> doInBackground(String... URLs) {
			HashMap<String, String> map = new HashMap<String, String>();
			String xmlString = "";
			
			for(String URL : URLs){
				try {		
		
					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(URL);
		 
					HttpResponse httpResponse = httpClient.execute(httpPost);
					HttpEntity httpEntity = httpResponse.getEntity();
					xmlString = EntityUtils.toString(httpEntity); 
					
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				// IOException catch will attempt to load from storage then open mainActivity on success
				} catch (IOException e) {
					log("Error: IOException because network not connected");
					
					// If there is saved data
					if(loadDataLastSavedDate() != -1){
						HashMap<String, String>	xmlStrings = loadXmlStrings();
						downloadMarkersAsyncTask.cancel(true);
						createMainActivity(xmlStrings);
						
						runOnUiThread(new Runnable(){
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(), "Couldn't download data from network, " + 
										"loaded from storage.", Toast.LENGTH_LONG).show();
							}
						});
						return null;
					}
					else{
						runOnUiThread(new Runnable(){
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(), "Couldn't download data from network, " + 
										"check your network connection and try again.", Toast.LENGTH_LONG).show();
							}
						});
						// Close Activity since loading from website failed and there is no saved data
						finish();
					}
				} 
				
				// There are 3 strange characters in the beginning of the some
				// strings that need to be taken out
				xmlString = xmlString.substring(xmlString.indexOf("<?xml version=\"1.0\" encoding=\"utf-8\"?>"));
				
				map.put(URL, xmlString);
			}
			
        	countDownLatch.countDown();
        	log("Latch countDown: Done loading data");
			
			// Wait for countDown before going to onPostExcecute and opening main activity
			try {
            	log("Latch awaiting");
				countDownLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return map;
		}
		
		/**
		 * After the xmlStrings are loaded, it will save the xmlStrings then create the main activity
		 */
		@Override
        protected void onPostExecute(HashMap<String, String> result) {
            super.onPostExecute(result);
            if(result != null){
	            // The map must be cloned because the save method removes entries from
	            // the map in order to pass an exception
	            saveXmlStrings((HashMap<String, String>) result.clone());
	            
	            createMainActivity(result);
            }
		}
		
		/**
		 * Simply for testing purposes
		 */
		@Override
		  protected void onCancelled(HashMap<String, String> result) {
			log("LoadAsyncTask.onCancelled() called");
		    super.onCancelled(result);
		  }
	}
	
	/**
	 * This method will create the load from storage button then set and create its onClickListener.
	 * When the button is clicked, it will attempt to load the xmlStrings. If it is successful, it
	 * will then create the loadAsyncTask will be canceled and the main activity will be created.
	 */
	public void createLoadFromStorageButton(){
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.relativeLayout);
		final Button loadFromStorageButton = new Button(getApplicationContext());
		loadFromStorageButton.setText("Load from phone storage");
		
		// Create params to position the ImageView
		RelativeLayout.LayoutParams params = 
				new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		layout.addView(loadFromStorageButton, params);
		
		loadFromStorageButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				log("Clicked on something");
				
				if(view == loadFromStorageButton){
					log("Clicked load from storage button");
					
					// Either successful(creates activity) or fails, we don't want the user to click again)
					view.setClickable(false);	view.setVisibility(View.GONE);
					
					HashMap<String, String>	xmlStrings = loadXmlStrings();
					// Cancel LoadAsyncTask if loadXmlStrings successful
					if(xmlStrings.containsKey(URLs[0])){
						downloadMarkersAsyncTask.cancel(true);
						createMainActivity(xmlStrings);
					}
					else
						Toast.makeText(getApplicationContext(), "Load from storage unsucessful: " + 
								"(You probably have never used this app before)", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		log("Added " + loadFromStorageButton.getText() + " loadFromStorageButton");	
	}
	
	/**
	 * This method will create the MainActivity activity and send it the xmlStrings map.
	 * It will then close this splash activity
	 * 
	 * @param xmlStringMap xmlStrings to be sent to the MainActivity
	 */
	public void createMainActivity(HashMap<String, String> xmlStringMap){
        log("Creating the Main Activity");
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra("map", xmlStringMap);
        startActivity(intent);

        // Close this activity
        finish();
	}
	
	/**
	 * This method saves the xmlStrings using SharedPreferences when downloaded.
	 * 
	 * @param xmlStrings The xmlStrings that will be saved
	 */
	private void saveXmlStrings(HashMap<String, String> xmlStrings){
		log("Saving xmlStrings");
		// We need an Editor object to make preference changes
	    SharedPreferences xmlStringsPref = getSharedPreferences(PREFS_NAME, 0);
	    SharedPreferences.Editor editor = xmlStringsPref.edit();
	    
	    Iterator<Map.Entry<String, String>> iterator = xmlStrings.entrySet().iterator();
	    while (iterator.hasNext()) {
	        Map.Entry<String, String> pairs = (Map.Entry<String, String>) iterator.next();
	        editor.putString((String) pairs.getKey(), (String) pairs.getValue());
	        iterator.remove(); // Avoids a ConcurrentModificationException
	    }
	    
	    // Save the the date that the data was downloaded
	    Calendar calendar = Calendar.getInstance();
	    editor.putLong(DATA_LAST_SAVED_DATE_TAG, calendar.getTimeInMillis());
	    
	    editor.commit();	// Commit the edits!
	}
	
	/**
	 * This method loads the xmlStrings from the last downloaded xmlStrings using SharedPreferences.
	 * 
	 * @return If successful, return xmlStrings, else returns xmlStrings with values of ""
	 */
	private HashMap<String, String> loadXmlStrings(){
		log("Loading xmlStrings");
	    SharedPreferences xmlStringsPref = getSharedPreferences(PREFS_NAME, 0);
		HashMap<String, String> xmlStrings = new HashMap<String, String>();
		
		for(String url : URLs){
			xmlStrings.put(url, xmlStringsPref.getString(url, ""));
			log(url + " = " + xmlStringsPref.getString(url, "").substring(0, 100));
		}
		
		return xmlStrings;
	}
	
	/**
	 * This method will use the date that the data was last downloaded to determine 
	 * whether the data is too old.
	 * 
	 * @return true if the data isn't too old
	 */
	private boolean isSavedDataValid(){
		Calendar calendar = Calendar.getInstance();
        Date lastValidDataDate = new Date(calendar.getTimeInMillis() - DATA_VALID_TIME);
        Date lastDownloadDataDate = new Date(loadDataLastSavedDate());
        
        log("Last valid data date = " + lastValidDataDate);
        log("Last download data date = " + lastDownloadDataDate);
        
        if(lastDownloadDataDate.after(lastValidDataDate)){
        	log("Saved data is valid");
        	return true;
        } else {
        	log("Saved data is valid");
        	return false;
        }
	}
	
	/**
	 * This will get the date that the data was last downloaded from the canal site
	 * 
	 * @return Date in milliseconds
	 */
	private long loadDataLastSavedDate(){
		log("Loading the date that the data was saved last");
	    SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, 0);
		
		return sharedPref.getLong(DATA_LAST_SAVED_DATE_TAG, -1);
	}
		
    private void log(String msg){
    	if(SplashActivity.LOG_ENABLED)
    		Log.i("SplashActivity", msg);
    }
	
}
