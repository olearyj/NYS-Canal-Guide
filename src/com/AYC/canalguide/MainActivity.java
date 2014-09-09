package com.AYC.canalguide;

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
import android.util.Log;

public class MainActivity extends Activity {

	private static final String MAP_TABSTRING = "Map";
	private static final String OPTIONS_TABSTRING = "Options";
	
	private MapFragment mapFragment;
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
		//mapFragment = new CanalMapFragment((HashMap<String, String>) getIntent().getSerializableExtra("map"), this);
		tabBar.addTab(tabBar.newTab().setText(MAP_TABSTRING)
				.setTabListener(new TabListener(mapFragment)));
		
		optionsFragment = new OptionsFragment();
		tabBar.addTab(tabBar.newTab().setText(OPTIONS_TABSTRING)
				.setTabListener(new TabListener(optionsFragment)));
		loadSavedOptions();
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
    
    /**
     * This method will load the options that were saved in the OptionsFragment
     */
    private void loadSavedOptions(){
		log("Loading Options");
	    SharedPreferences sharedPref = getSharedPreferences(OptionsFragment.PREFS_NAME, 0);
	    
		int mapType = sharedPref.getInt("MapType", GoogleMap.MAP_TYPE_NORMAL);
		((OptionsFragment) optionsFragment).setMapType(mapType);
    }
    
    private void log(String msg){
    	if(SplashActivity.LOG_ENABLED)
    		Log.i("MapFragment", msg);
    }
 
}
