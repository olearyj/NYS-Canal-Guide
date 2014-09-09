package com.AYC.canalguide;

import java.util.Arrays;
import com.google.android.gms.maps.GoogleMap;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

public class OptionsFragment extends Fragment {

	public final static String PREFS_NAME = "NYS_Canal_Guide_Options";
	private final static int NUM_OF_SWITCHES = 5;
	
	private Switch[] switches;
	
	private boolean[] switchValues;
	private int mapType;
	
	/**
	 * This constructor will set default values
	 */
	public OptionsFragment(){
		super();
		switches = new Switch[NUM_OF_SWITCHES];
		switchValues = new boolean[NUM_OF_SWITCHES];
		Arrays.fill(switchValues, true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
	    View view = inflater.inflate(R.layout.fragment_options, container, false);

	    setUpMapTypeSpinner(view);

		switches[0] = (Switch) view.findViewById(R.id.switch_lock);
		switches[1] = (Switch) view.findViewById(R.id.switch_marina);
		switches[2] = (Switch) view.findViewById(R.id.switch_launch);
		switches[3] = (Switch) view.findViewById(R.id.switch_bridge);
		switches[4] = (Switch) view.findViewById(R.id.switch_boatsforhire);
		
		return view;
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
	    SharedPreferences sharedPref = getActivity().getSharedPreferences(PREFS_NAME, 0);
	    SharedPreferences.Editor editor = sharedPref.edit();
	    
	    editor.putInt("MapType", mapType);
	    
	    editor.commit();	// Commit the edits!
	}
	
	private void log(String msg){
    	if(SplashActivity.LOG_ENABLED)
    		Log.i("OptionsFragment", msg);
    }
	
}
