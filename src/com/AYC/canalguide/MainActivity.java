package com.AYC.canalguide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MainActivity extends Activity {

	private static final String MAP_TABSTRING = "Map";
	private static final String OPTIONS_TABSTRING = "Options";
	
	private MessengerHandler handler;
	
	private static MapFragment mapFragment;
	private Fragment optionsFragment;
	
	private HashMap<String, String> xmlStrings;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
	
		final ActionBar tabBar = getActionBar();
		tabBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		xmlStrings = (HashMap<String, String>) getIntent().getSerializableExtra("map");
		mapFragment = new CanalMapFragment();
		tabBar.addTab(tabBar.newTab().setText(MAP_TABSTRING)
				.setTabListener(new TabListener(mapFragment)));
		
		optionsFragment = new OptionsFragment();
		tabBar.addTab(tabBar.newTab().setText(OPTIONS_TABSTRING)
				.setTabListener(new TabListener(optionsFragment)));
		
		loadSavedOptions();
		
		handler = new MessengerHandler(this);
    }
    
	public static class TabListener implements ActionBar.TabListener {
		private final Fragment mFragment;

		public TabListener(Fragment fragment) {
			mFragment = fragment;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			if (null != mFragment) {
				ft.replace(R.id.fragment_container, mFragment);
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (null != mFragment)
				ft.remove(mFragment);
		}
	}

    /**
     * When the application is stopped, let the camera position to default so 
     * when the app is opened back up, it had the default position and zoomed out view
     */
    @Override
    public void onStop() {
        super.onStop();
        ((CanalMapFragment) mapFragment).setCameraPositionToDefault();
    }
    
    protected Fragment getOptionsFragment(){
    	return optionsFragment;
    }
    
    protected HashMap<String, String> getXmlStrings(){
    	return xmlStrings;
    }
    
    protected void startDownloadThreadPoolService(){
		startService(ThreadPoolDownloadService.makeIntent(this, handler));
    }
    
    /**
     * This method will load the options that were saved in the OptionsFragment
     */
    private void loadSavedOptions(){
		log("Loading Options");
	    SharedPreferences sharedPref = getSharedPreferences(OptionsFragment.PREFS_NAME, 0);
	    
		int mapType = sharedPref.getInt("MapType", GoogleMap.MAP_TYPE_NORMAL);
		((OptionsFragment) optionsFragment).setMapType(mapType);
		int updateTime = sharedPref.getInt("UpdateTime", 7);
		((OptionsFragment) optionsFragment).setUpdateTime(updateTime);
    }
    
    /**
     * This is the handler used for handling messages sent by a
     * Messenger.  It receives a message containing a pathname to an
     * image and displays that image in the ImageView.
     *
     * The handler plays several roles in the Active Object pattern,
     * including Proxy, Future, and Servant.
     * 
     * Please use displayBitmap() defined in DownloadBase
     */
    class MessengerHandler extends Handler {
	    
    	// A weak reference to the enclosing class
    	WeakReference<MainActivity> outerClass;
    	
    	ArrayList<String> downloadedURLs;
    	
    	/**
    	 * A constructor that gets a weak reference to the enclosing class.
    	 * We do this to avoid memory leaks during Java Garbage Collection.
    	 * 
    	 * @see https://groups.google.com/forum/#!msg/android-developers/1aPZXZG6kWk/lIYDavGYn5UJ
    	 */
    	public MessengerHandler(MainActivity outer) {
    		super();
            outerClass = new WeakReference<MainActivity>(outer);
            downloadedURLs = new ArrayList<String>(); 
    	}
    	
    	// Handle any messages that get sent to this Handler
    	@Override
		public void handleMessage(Message msg) {
    		
            // Get an actual reference to the DownloadActivity
            // from the WeakReference.
            final MainActivity activity = outerClass.get();
    		
            // If DownloadActivity hasn't been garbage collected
            // (closed by user), display the sent image.
            if (activity != null) {
            	Bundle bundle = msg.getData();
                if (bundle != null) {
                	String URL = bundle.getString(ThreadPoolDownloadService.URL_KEY);
                	if(URL != null){
                		downloadedURLs.add(URL);
                		log(downloadedURLs.size() + ") Recieved navInfo xmlString for " + URL);
                	}
                	else if(bundle.getString(ThreadPoolDownloadService.DONEDOWNLOADING_KEY)
                			.equals(ThreadPoolDownloadService.DONEDOWNLOADING_KEY)){
                		
                		log(downloadedURLs.size() + ") Last navInfoXmlFile received");
                		HashMap<String, String> navInfoXmlStrings = loadNavInfoXmlStrings();
                		((CanalMapFragment) mapFragment).parseXmlStringsAndAddMarkersToMap(navInfoXmlStrings);
                		
                	}
                }
            }
    	}    	

        private HashMap<String, String> loadNavInfoXmlStrings(){
    		log("Loading xmlStrings");
    	    SharedPreferences xmlStringsPref = getSharedPreferences(SplashActivity.PREFS_NAME, 0);
    		HashMap<String, String> xmlStrings = new HashMap<String, String>();
    		
    		int i = 0;
    		for(String url : SplashActivity.navInfoURLs){
    			xmlStrings.put(url, xmlStringsPref.getString(url, ""));
    			log(i++ + ") " + url + " = " + xmlStringsPref.getString(url, "").substring(0, 100));
    		}
    		
    		return xmlStrings;
    	}
    	
    	private void log(String msg){
        	if(SplashActivity.LOG_ENABLED)
        		Log.i("MainActivity-MessengerHandler", msg);
        }
    }
    
    private void log(String msg){
    	if(SplashActivity.LOG_ENABLED)
    		Log.i("MainActivity", msg);
    }
 
}
