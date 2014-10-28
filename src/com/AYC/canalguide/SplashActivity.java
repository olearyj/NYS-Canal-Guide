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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity will be the splash screen (loading screen). If the last downloaded data
 * isn't too old, it will load that marker data. Else it will download data from the NYS canal
 * web site in an AsyncTask and will save that data. This splash screen will always be up for 
 * at least a minimum amount of time. If there exists saved data and the data isn't downloaded 
 * in that time, a button will then pop-up allowing the choice to load from storage.
 * 
 * In logcat, you can remove certain tags by typing tag:^(?!(skia))
 * 
 * @author James O'Leary
 *
 */
public class SplashActivity extends Activity {

	// This boolean will be used in all classes to choose whether to use logs or not
	public static final boolean LOG_ENABLED = true;
	
	// This is the minimum amount of time the splash screen will be up for (in milliseconds)
	private static final long MINIMUM_SPLASH_TIME = 1000L;
	
	static final String PREFS_NAME = "xmlStrings";
	static final int PREFS_MODE = 0 | Context.MODE_MULTI_PROCESS;
	
	private static final String DATA_LAST_SAVED_DATE_TAG = "Last saved date";
	public static final long DAY_IN_MILLISECONDS = 86400000;
	private static long DATA_VALID_TIME;	// How long data is valid for
	
	// Default permissions for this because MainActivity uses this
	public static final String[] URLs = {"http://www.canals.ny.gov/xml/locks.xml", 
			"http://www.canals.ny.gov/xml/marinas.xml", "http://www.canals.ny.gov/xml/canalwatertrail.xml", 
			"http://www.canals.ny.gov/xml/liftbridges.xml", "http://www.canals.ny.gov/xml/guardgates.xml",
			"http://www.canals.ny.gov/xml/boatsforhire.xml"};
	
	public static final String[] navInfoURLs = {"http://www.canals.ny.gov/xml/navinfo-hudsonriver.xml",
		"http://www.canals.ny.gov/xml/navinfo-champlain.xml", "http://www.canals.ny.gov/xml/navinfo-fortedward.xml",
		"http://www.canals.ny.gov/xml/navinfo-erieeastern.xml", "http://www.canals.ny.gov/xml/navinfo-uticaharbor.xml",
		"http://www.canals.ny.gov/xml/navinfo-oswego.xml", "http://www.canals.ny.gov/xml/navinfo-eriecentral.xml",
		"http://www.canals.ny.gov/xml/navinfo-onondagalake.xml", "http://www.canals.ny.gov/xml/navinfo-cayugaseneca.xml",
		"http://www.canals.ny.gov/xml/navinfo-senecalake.xml", "http://www.canals.ny.gov/xml/navinfo-eriewestern.xml",
		"http://www.canals.ny.gov/xml/navinfo-geneseeriver.xml", "http://www.canals.ny.gov/xml/navinfo-ellicottcreek.xml",
		"http://www.canals.ny.gov/xml/navinfo-cayugalake.xml", "http://www.canals.ny.gov/xml/navinfo-frankfortharbor.xml"};
	
	private Handler handler;
	private CountDownLatch countDownLatch;
	
	private ProgressBar progressBar;
	private TextView tvLoading;
	
	// Use this to cancel if needed
	private LoadAsyncTask downloadMarkersAsyncTask;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        log("Created Splash Activity");

        DATA_VALID_TIME = getUpdateFrequency() * DAY_IN_MILLISECONDS;
        
        setLogoImageSize();
		
        handler = new Handler();
        // Latch is initialized with the parameter two because were waiting
        // for the one runnable that is post delayed to countDown the latch
    	// and to finish loading the data
        countDownLatch = new CountDownLatch(2);
        
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvLoading = (TextView) findViewById(R.id.tvLoading);
        
        // If the data has been downloaded before and isn't too old, load saved data
        // else, download data from the website
        if( isSavedDataValid() ){
        	loadFromStorageThread.start();
        }
        else
	        downloadMarkersAsyncTask = (LoadAsyncTask) new LoadAsyncTask().execute(URLs);
        
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
    
    /**
     * This thread will load data from storage, await for the minimum splash time to expire
     * and create the main activity
     */
    Thread loadFromStorageThread = new Thread(){
		public void run(){
			final HashMap<String, String> xmlStrings = loadXmlStrings();
			
			countDownLatch.countDown();
        	log("Latch countDown: Done loading data");
        	
        	// Wait for countDown before opening main activity
    		try {
    	    	log("Latch awaiting");
    			countDownLatch.await();
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    		
			runOnUiThread(new Runnable(){
				public void run(){
				createMainActivity(xmlStrings);
				}
			});
		}
	};
    
	/**
	 * This method will set the logo image of the splash screen to the 512px logo.
	 * The logo's height and width will be resized to 2/5 the width of the phones screen.
	 */
	private void setLogoImageSize(){
        DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		if(dm.widthPixels != 0){
			int maxSize = (int) (dm.widthPixels * 2/5);
		    ImageView ivLogo = (ImageView) findViewById(R.id.ivLogo);
	        ivLogo.setImageResource(R.drawable.ic_logo_512);
	        ivLogo.setMaxHeight(maxSize);
	        ivLogo.setMaxWidth(maxSize);
	        ivLogo.setMinimumHeight(maxSize);
	        ivLogo.setMinimumWidth(maxSize);
		}
	}
	
    /**
     * This extends AsychTask will load add app data (from data or storage)
     * It will then create the main activity and close this splash activity when finished 
     * 
     * @author James O'Leary
     *
     */
	private class LoadAsyncTask extends AsyncTask<String, Integer, HashMap<String, String>>{

		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            log("Created LoadAsyncTask");
            
            tvLoading.setText(R.string.tv_loadingdatabase);
		}
		
		@Override
		protected HashMap<String, String> doInBackground(String... URLs) {
			HashMap<String, String> map = new HashMap<String, String>();
			String xmlString = "";
			int downloadedCount = 0;
			
			for(String URL : URLs){
				try {		
					
					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(URL);
		 
					HttpResponse httpResponse = httpClient.execute(httpPost);
					HttpEntity httpEntity = httpResponse.getEntity();
					xmlString = EntityUtils.toString(httpEntity); 

					downloadedCount++;
					publishProgress((int) ((downloadedCount / (float) URLs.length) * 100));
					
					if(isCancelled()){
						log("LoadAsyncTask: is cancelled!! returning null");
						return null;
					}
					
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				// IOException catch will attempt to load from storage then open mainActivity on success
				} catch (IOException e) {
					log("Error: IOException because network not connected");
					
					// If there is saved data
					if(loadDataLastSavedDate() != -1){
						final HashMap<String, String> xmlStrings = loadXmlStrings();
						downloadMarkersAsyncTask.cancel(true);
						
						runOnUiThread(new Runnable(){
							@Override
							public void run() {
								createMainActivity(xmlStrings);
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
						return null;
					}
				} 
				
				// There are 3 strange characters in the beginning of the some
				// strings that need to be taken out
				xmlString = xmlString.substring(xmlString.indexOf("<?xml version=\"1.0\" encoding=\"utf-8\"?>"));
				
				map.put(URL, xmlString);
			}
			
			if(isCancelled()){
				log("LoadAsyncTask: is cancelled!! returning null");
				return null;
			}
			
			countDownLatch.countDown();
        	log("Latch countDown: Done loading data");
        	
        	// Wait for countDown before opening main activity
    		try {
    	    	log("Latch awaiting");
    			countDownLatch.await();
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
			
			return map;
		}
		
	    protected void onProgressUpdate(Integer... progress) {
	    	 progressBar.setProgress(progress[0]);
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
		final Button loadFromStorageButton = new Button(this, null, android.R.attr.buttonStyleSmall);
		loadFromStorageButton.setText("Load from phone storage");
		
		// Create params to position the ImageView
		RelativeLayout.LayoutParams params = 
				new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.ABOVE, R.id.tvLoading);
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
	    SharedPreferences xmlStringsPref = getSharedPreferences(PREFS_NAME, SplashActivity.PREFS_MODE);
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
	    SharedPreferences xmlStringsPref = getSharedPreferences(PREFS_NAME, SplashActivity.PREFS_MODE);
		HashMap<String, String> xmlStrings = new HashMap<String, String>();
		
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				tvLoading.setText(R.string.tv_loadingphone);
			}
		});
		
		progressBar.setProgress(0);
		
		// Use this method of posting progress so it increments until the MIN_SPLASH_TIME is over
		// since this method of loading is so fast
		Runnable updateProgressWithTimeRunnable = new Runnable(){
			@Override
			public void run() {
				int curProgress = progressBar.getProgress();
				progressBar.setProgress((int) (curProgress + ((1 / (float) URLs.length) * 100)));
			}
		};
		for(int i=1; i<URLs.length + 1; i++)
			handler.postDelayed(updateProgressWithTimeRunnable, i * (MINIMUM_SPLASH_TIME/URLs.length));
		
		for(String url : URLs)
			xmlStrings.put(url, xmlStringsPref.getString(url, ""));
		
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
        	log("Saved data is not valid");
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
	    SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, SplashActivity.PREFS_MODE);
		
		return sharedPref.getLong(DATA_LAST_SAVED_DATE_TAG, -1);
	}
	
	/**
     * This method will get the update frequency that was saved in the OptionsFragment
     * 
     * @return Update frequency in days
     */
    private int getUpdateFrequency(){
	    SharedPreferences sharedPref = getSharedPreferences(OptionsFragment.PREFS_NAME, SplashActivity.PREFS_MODE);
		return sharedPref.getInt(OptionsFragment.UPDATE_FREQ_KEY, 7);
    }
		
    private void log(String msg){
    	if(SplashActivity.LOG_ENABLED)
    		Log.i("SplashActivity", msg);
    }
	
}
