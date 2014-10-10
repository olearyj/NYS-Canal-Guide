package com.AYC.canalguide;

import java.util.Arrays;
import com.google.android.gms.maps.GoogleMap;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

public class OptionsFragment extends Fragment implements OnClickListener {

	public final static String PREFS_NAME = "NYS_Canal_Guide_Options";
	public final static String FILTER_DATA_KEY = "Filter Data";
	
	private final static int NUM_OF_SWITCHES = 6;
	
	private View view;
	private ViewStub viewStub;
	private Switch[] switches;
	
	private boolean[] switchValues;
	private int mapType;
	private int updateTime;
	
	/**
	 * This constructor will set default values
	 */
	public OptionsFragment(){
		super();
		switches = new Switch[NUM_OF_SWITCHES];
		switchValues = new boolean[NUM_OF_SWITCHES];
		Arrays.fill(switchValues, true);
		switchValues[5] = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
	    view = inflater.inflate(R.layout.fragment_options, container, false);

	    setUpMapTypeSpinner(view);
	    setUpDataValidSpinner(view);

		switches[0] = (Switch) view.findViewById(R.id.switch_lock);
		switches[1] = (Switch) view.findViewById(R.id.switch_marina);
		switches[2] = (Switch) view.findViewById(R.id.switch_launch);
		switches[3] = (Switch) view.findViewById(R.id.switch_bridge);
		switches[4] = (Switch) view.findViewById(R.id.switch_boatsforhire);
		switches[5] = (Switch) view.findViewById(R.id.switch_buoys);
		switches[5].setOnClickListener(this);

	    viewStub = (ViewStub) view.findViewById(R.id.navinfo_legend);
	    viewStub.inflate();

		TextView tv_tide = (TextView) view.findViewById(R.id.tv_tide);
		tv_tide.setOnClickListener(this);
		
		// TODO 
		/*
		if(savedInstanceState != null)
			restoreSavedInstanceState(savedInstanceState);
			*/
		
		return view;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		setViewStub();
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.tv_tide:
			Intent intent = new Intent(getActivity(), WebViewActivity.class);
			intent.putExtra("url", "http://ny.usharbors.com/");
			startActivity(intent);
			break;
		case R.id.switch_buoys:
			setViewStub();
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		log("Saving InstanceState");
		saveFilterData();
		outState.putBooleanArray(FILTER_DATA_KEY, switchValues);
		for(int i=0; i<switchValues.length; i++)
    		log("SIS - SV: switchValues[" + i + "] = " + switchValues[i]);
	}
	
	/**
	 * If the activity was reset because of screen orientation, use the
	 * information from savedInstanceState to restore the state. This method will
	 * restore the filter switch states.
	 * 
	 * @param savedInstanceState
	 */
	private void restoreSavedInstanceState(Bundle savedInstanceState){
	    if(savedInstanceState.getBooleanArray(FILTER_DATA_KEY) != null){
	    	switchValues = savedInstanceState.getBooleanArray(FILTER_DATA_KEY);
	    	for(int i=0; i<switchValues.length; i++)
	    		log("SV: switchValues[" + i + "] = " + switchValues[i]);
	    	for(int i=0; i<switches.length; i++)
	    		switches[i].setChecked(switchValues[i]);
	    	for(int i=0; i<switches.length; i++)
	    		log("SV: switches[" + i + "].isChecked() = " + switches[i].isChecked());
	    }
	}
	
	private void setViewStub(){
		if(!switches[5].isChecked())
			viewStub.setVisibility(ViewStub.GONE);
		else
			viewStub.setVisibility(ViewStub.VISIBLE);
	}
	
	@Override
	public void onStop(){
		saveFilterData();
		saveOptions();
		super.onStop();
	}
	
	private void saveFilterData(){
		boolean[] switchValues = new boolean[NUM_OF_SWITCHES];
		
		for(int i=0; i<switchValues.length; i++){
			switchValues[i] = switches[i].isChecked();
		}
		
		this.switchValues = switchValues;
	}
	
	public boolean[] getFilterData(){
		return switchValues;
	}
	
	/**
	 * This method allows the CanalMapFragent to get the user chosen map type
	 * 
	 * @return The map type
	 */
	public int getMapType(){
		return mapType;
	}
	
	/**
	 * This method is used by the main activity to set the map type after it's loaded
	 * 
	 * @param mapType
	 */
	public void setMapType(int mapType){
		this.mapType = mapType;
	}
	
	/**
	 * This method is used by the main activity to set the updateTime after it's loaded
	 * 
	 * @param updateTime
	 */
	public void setUpdateTime(int updateTime){
		this.updateTime = updateTime;
	}
	
	private void setUpMapTypeSpinner(View view){
		final int mapTypes[] = {GoogleMap.MAP_TYPE_NORMAL, GoogleMap.MAP_TYPE_HYBRID, 
				GoogleMap.MAP_TYPE_SATELLITE, GoogleMap.MAP_TYPE_TERRAIN};
		final String mapTypeStrings[] = {"Normal", "Hybrid", "Satellite", "Terrain"};
		
		final Spinner spinner = (Spinner) view.findViewById(R.id.spinnerMapType);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>
				(getActivity() , android.R.layout.simple_spinner_item, mapTypeStrings);
		
		spinner.setAdapter(adapter);
		
		// Set map type
		spinner.setSelection( intArrayIndexOf(mapTypes, mapType) );
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// On item select, set the map type variable
				mapType = mapTypes[spinner.getSelectedItemPosition()];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// Unimplemented
			}
		});
		
	}
	
	// TODO
	private void setUpDataValidSpinner(View view){
		final int updateTimes[] = {0, 1, 3, 7, 14};
		final String updateTimeStrings[] = {"Every time this app loads", "Every day", 
				"Every 3 days", "Every week", "Every 2 weeks"};
		
		final Spinner spinner = (Spinner) view.findViewById(R.id.spinnerDataValid);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>
				(getActivity() , android.R.layout.simple_spinner_item, updateTimeStrings);
		
		spinner.setAdapter(adapter);
		
		// Set update time
		spinner.setSelection( intArrayIndexOf(updateTimes, updateTime) );
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// On item select, set the map type variable
				updateTime = updateTimes[spinner.getSelectedItemPosition()];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// Unimplemented
			}
		});
		
	}
	
	private int intArrayIndexOf(int[] array, int value) {
	    for(int i=0; i<array.length; i++) 
	         if(array[i] == value)
	             return i;
	    return -1;
	}
	
	/**
	 * This method will save certain options(mapType) using SharedPreferences
	 */
	private void saveOptions(){
		log("Saving Options");
		// We need an Editor object to make preference changes
	    SharedPreferences sharedPref = getActivity().getSharedPreferences(PREFS_NAME, SplashActivity.PREFS_MODE);
	    SharedPreferences.Editor editor = sharedPref.edit();
	    
	    editor.putInt("MapType", mapType);
	    editor.putInt("UpdateTime", updateTime);
	    
	    editor.commit();	// Commit the edits!
	}
	
	private void log(String msg){
    	if(SplashActivity.LOG_ENABLED)
    		Log.i("OptionsFragment", msg);
    }
	
}
