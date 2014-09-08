package com.AYC.canalguide;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

public class OptionsFragment extends Fragment {
	
	private final static int NUM_OF_SWITCHES = 5;
	private Switch[] switches;
	private boolean[] switchValues;
	
	/**
	 * This constructor is overridden in order to get the activity needed to find views
	 * 
	 * @param context
	 * The context of the parent activity
	 */
	public OptionsFragment(){
		super();
		switches = new Switch[NUM_OF_SWITCHES];
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
	    View view = inflater.inflate(R.layout.fragment_options, container, false);

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
	
}
