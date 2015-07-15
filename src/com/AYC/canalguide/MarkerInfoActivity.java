package com.AYC.canalguide;

import com.AYC.canalguide.NearbyPlaces.AddNearbyPlacesToMapTask;
import com.AYC.canalguide.canalparser.BoatsForHireMarker;
import com.AYC.canalguide.canalparser.BridgeGateMarker;
import com.AYC.canalguide.canalparser.LaunchMarker;
import com.AYC.canalguide.canalparser.LockMarker;
import com.AYC.canalguide.canalparser.MapMarker;
import com.AYC.canalguide.canalparser.MarinaMarker;
import com.AYC.canalguide.canalparser.NavInfoMarker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class is an activity that is opened when a marker info window is clicked.
 * It will display data from the mapMarker along with two clickable icons to call or
 * visit webpage and a map at the bottom of the screen
 * 
 * @author James O'Leary
 *
 */
public class MarkerInfoActivity extends Activity implements OnClickListener {

	private static final int NOAA_SITE_ID = 234;
	
	private MapMarker mapMarker;
	
	private GoogleMap mMap;
	private LinearLayout scrollView;
	
	// This is a counter variable for the method addTextView
	private int textSizeCount = 0;
	
	// The following ten variables will make the onCreate method much easier and more readable
	private boolean isLock;
	private boolean isMarina;
    private boolean isLaunch;
	private boolean isBridge;
    private boolean isBoatsForHire;
    private boolean isNavInfo;
    
    private LockMarker lock;
    private MarinaMarker marina;
    private LaunchMarker launch;
    private BridgeGateMarker bridge;
    private BoatsForHireMarker boats;
    private NavInfoMarker navInfo;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	     super.onCreate(savedInstanceState);
	     setContentView(R.layout.layout_markerinfo);
	     
	      ActionBar actionBar = getActionBar();
	      // This will display the arrow on the left in the actionbar
	      actionBar.setDisplayHomeAsUpEnabled(true);
	     
	     // Get MapMarker from main activity
	     Intent intent = getIntent();
	     mapMarker = (MapMarker) intent.getSerializableExtra("MapMarker");
	     
	     isLock = mapMarker instanceof LockMarker;
	     isMarina = mapMarker instanceof MarinaMarker;
	     isLaunch = mapMarker instanceof LaunchMarker;
	     isBridge = mapMarker instanceof BridgeGateMarker;
	     isBoatsForHire = mapMarker instanceof BoatsForHireMarker;
	     isNavInfo = mapMarker instanceof NavInfoMarker;
	       
	     lock = isLock ? (LockMarker) mapMarker : null;
	     marina = isMarina ? (MarinaMarker) mapMarker : null;
	     launch = isLaunch ? (LaunchMarker) mapMarker : null;
	     bridge = isBridge ? (BridgeGateMarker) mapMarker : null;
	     boats = isBoatsForHire ? (BoatsForHireMarker) mapMarker : null;
	     navInfo = isNavInfo ? (NavInfoMarker) mapMarker : null;
	     
	     setUpCallAndWebsiteIcons();
	        
	     scrollView = (LinearLayout) findViewById(R.id.scrollViewLinearLayout);
	         
	     if(isLock){
	    	 addTextView(lock.getTitle());
	    	 addTextView(lock.getSnippet());
	     	 createLockTextViews();
	     }
	     
	     else if(isMarina){
	    	 addTextView(marina.getTitle());
	    	 addTextView(marina.getSnippet());
	     	 createMarinaTextViews();
	     }
	      
	     else if(isLaunch){
	    	 addTextView(launch.getTitle());
	    	 addTextView(launch.getSnippet());
	    	 createLaunchTextViews();
	     }
	        
	     else if(isBridge){
	    	 addTextView(bridge.getTitle());
	    	 addTextView(bridge.getSnippet());
	     	 createBridgeTextViews();
	     }
	        
	     else if(isBoatsForHire){
	    	 addTextView(boats.getTitle());
	    	 addTextView(boats.getSnippet());
	     	 createBoatsForHireTextViews();
	     }
	     
	     else if(isNavInfo){
	    	 addTextView(navInfo.getTitle());
	    	 addTextView(navInfo.getSnippet().replaceAll(", ", "\n"));
	    	 createNavInfoTextViews();
	     }
	     
	     // Because all information isn't always available, tell user to contact
	     if(!isNavInfo){
		     textSizeCount++;
		     addTextView("*For more information, please contact "  + mapMarker.getName());
	     }
	}
	
	/**
	 * This method is overridden to control the actions when the 
	 * action bar's Up/Home button is pressed
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        //NavUtils.navigateUpFromSameTask(this);	// Use this if i chose a parent activity in the manifest
	        finish();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	/**
	 * This method will remove the icon if there is no phone number or website for the marker.
	 * If there is, it will set the onClickListener for the call icon and the website icon
	 */
	private void setUpCallAndWebsiteIcons(){
		// Sets the onClick listeners for call and web site icon
		ImageView ivCall = (ImageView) findViewById(R.id.ivCall);
		ImageView ivWebsite = (ImageView) findViewById(R.id.ivWebsite);
		
		// If mapMarker has a phone number, set listener, else remove the call icon
		if(hasPhoneNumber())
			ivCall.setOnClickListener(this);
		else
			ivCall.setVisibility(ImageView.GONE);
		
		// If mapMarker has a website, set listener, else remove the website icon
		if(hasWebsite())
			ivWebsite.setOnClickListener(this);
		else
			ivWebsite.setVisibility(ImageView.GONE);
		

		if(isNavInfo && !isBlank(navInfo.getNoaaPageUrl()))
			addNavInfoSiteImage();
	}
	
	private void addNavInfoSiteImage(){
		ImageView ivNoaaWebsite = new ImageView(this);
		ivNoaaWebsite.setId(NOAA_SITE_ID);
		ivNoaaWebsite.setImageResource(R.drawable.ic_action_web_site);
		ivNoaaWebsite.setOnClickListener(this);
		ivNoaaWebsite.setPadding(10, 10, 10, 10);
		// Add the imageView to the layout
		LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayoutWithButtons);
		layout.addView(ivNoaaWebsite);
	}

	/**
	 * This method will create the lock TextViews using the data included in the marker object.
	 * This will add TextViews regarding the Address(or city) if available.
	 */
	private void createLockTextViews(){
		// If there is an address, display it, else display location
    	if( !isBlank(lock.getAddress()) && !isBlank(lock.getCity()) && !isBlank(lock.getZip()) ){
    		addTextView("Address");
    		addTextView( lock.getAddress() + "\n" + 
    				lock.getCity() + ", NY " + lock.getZip() );
    	}
    	else{
    		textSizeCount++;	// Use small text for this
        	addTextView("Location: " + lock.getLocation());
        }
	}

	/**
	 * This method will create the marina TextViews using the data included in the marker object.
	 * This will add TextViews regarding fuel, vhf, facilities and repair if available.
	 */
	private void createMarinaTextViews(){
		if( !isBlank(marina.getFuel()) ){
        	addTextView("Fuel");
        	addTextView(getFuelString(marina.getFuel()));
    	}
    	
    	if( !isBlank(marina.getVhf()) ){
        	addTextView("Vhf Channels");
        	addTextView(marina.getVhf().replaceAll(", *", ", "));
    	}
    	
    	if( !isBlank(marina.getFacilities()) ){
        	addTextView("Facilities");
        	addTextView(getFacilitiesString(marina.getFacilities()));
    	}
    	
    	if(  !isBlank(marina.getRepair()) ){
        	addTextView("Repair");
        	addTextView(getRepairString(marina.getRepair()));
    	}	
    }

	/**
	 * This method will create the launch TextViews using the data included in the marker object.
	 * This will add TextViews regarding launch type, parking, day use amenities, facilities/utilities 
	 * and other information(municipality and portage distance) if available.
	 */
	private void createLaunchTextViews(){
    	if( !isBlank(launch.getLaunchType()) ){
    		addTextView("Launch Type");
    		addTextView(launch.getLaunchType());
    	}
    	
    	if( !isBlank(launch.getParking()) ){
    		addTextView("Parking");
    		String text = launch.getParking();
    		
    		if( !isBlank(launch.getOvernightParking()) ){
    			
    			if(launch.getOvernightParking().equalsIgnoreCase("yes"))
    				text += "\n" + "Overnight Parking";
    			else if(launch.getOvernightParking().equalsIgnoreCase("Yes, call"))
    				text += "\n" + "Overnight Parking" + launch.getOvernightParking().substring(3);
    		}
    		addTextView(text);
    	}
    	
    	if( !isBlank(launch.getDayUseAmenities()) ){
    		addTextView("Day Use Amenities");
    		addTextView(launch.getDayUseAmenities().replace(", ", "\n"));
    	}
    	
    	if(launch.getRestrooms().contains("Yes") || launch.getPotableWater().contains("Yes") 
    			|| launch.getCamping().contains("Yes")){
        	addTextView("Facilities / Utilities");
        	
        	String text = "";
        	if( launch.getRestrooms().equalsIgnoreCase("Yes") )
    			text += "Restrooms";
        	else if( launch.getRestrooms().contains("Yes") )
        		text += "Restrooms" + launch.getRestrooms().substring(3);
    		
    		if( launch.getPotableWater().contains("Yes") )
    			text += (text.equals("") ? "" : "\n") + "Potable Water";
    		
    		if( launch.getCamping().contains("Yes") )
    			text += (text.equals("") ? "" : "\n") + "Camping";
    		addTextView(text);
    	}
    	
    	if( !isBlank(launch.getPortageDistance()) || !isBlank(launch.getMunicipality()) ){
    		addTextView("Other Information");
    		String text = "";
    		if( !isBlank(launch.getPortageDistance()) )
    			text += "Portage Distance: " + launch.getPortageDistance();
    		if( !isBlank(launch.getMunicipality()) )
    			text += (text.equals("") ? "" : "\n") + "Municipality: " + launch.getMunicipality();
    		addTextView(text);
    	}
	}

	/**
	 * This method will create the Bridge or guarded gate TextViews using the data included 
	 * in the marker object. This will add TextViews regarding the location if available.
	 */
	private void createBridgeTextViews(){
		textSizeCount++;
		if( !isBlank(bridge.getLocation()) )
			addTextView("Location: " + bridge.getLocation());
		
		double cc = bridge.getClearanceClosed(), co = bridge.getClearanceOpened();
		if(cc != -1 && co != -1){
			textSizeCount++;
			addTextView("Closed clearance: " + bridge.getClearanceClosed());
			textSizeCount++;
			addTextView("Opened clearance: " + 
					(bridge.getClearanceOpened() == 999 ? "Unlimited" : bridge.getClearanceOpened()));
		}
	}
	
	/**
	 * This method will create the boats for hire TextViews using the data included in the marker object.
	 * This will add TextViews regarding the waterways and address(or city) if available.
	 */
	private void createBoatsForHireTextViews(){
    	if( !isBlank(boats.getWaterways()) ){
    		textSizeCount++;
    		addTextView("Waterways: " + boats.getWaterways());
    	}
    	
    	if( !isBlank(boats.getAddress()) && !isBlank(boats.getCity()) && !isBlank(boats.getZip()) ){
    		addTextView("Address");
    		addTextView( boats.getAddress() + "\n" + 
    				boats.getCity() + ", NY " + boats.getZip() );
    	}
    	else if( !isBlank(boats.getCity()) ){
    		textSizeCount++;
    		addTextView("Location: " + boats.getCity());
    	}
	}
	
	private void createNavInfoTextViews(){
		// The following will print already because of the snippet
		
		// Erie and Ft Edward flows east and west, so the depths are north and south
		boolean ns = SplashActivity.navInfoURLs[navInfo.getUrlIndex()].contains("fortedward") || 
				SplashActivity.navInfoURLs[navInfo.getUrlIndex()].contains("eriewestern") ||
				SplashActivity.navInfoURLs[navInfo.getUrlIndex()].contains("erieeastern") ||
				SplashActivity.navInfoURLs[navInfo.getUrlIndex()].contains("eriecentral");
		
		if(navInfo.getNorthEastDepth() != -1){
			textSizeCount++;
			addTextView( (ns ? "North" : "East") + " Depth = " + navInfo.getNorthEastDepth());
		}
		if(navInfo.getSouthWestDepth() != -1){
			textSizeCount++;
			addTextView( (ns ? "South" : "West") + " Depth = " + navInfo.getSouthWestDepth());
		}
		
	}
	
	/**
	 * This onClickListener will get the phone number or website from the mapMarker.
	 * If the call icon was clicked, a dialog box will pop-up confirming the request to call.
	 * If the website icon was clicked, it will bring up a webview activity for the website.
	 */
	@Override
	public void onClick(View view) {
		
		String phoneNumber = "";
		String url = "";
		
		if(isLock)
			phoneNumber = lock.getPhoneNumber();
		
		else if(isMarina){
			phoneNumber = marina.getPhoneNumber();
			url = ((MarinaMarker) mapMarker).getUrl();	
		}
		else if(isBridge)
			phoneNumber = bridge.getPhoneNumber();
			
		else if(isBoatsForHire){
			phoneNumber = boats.getPhoneNumber();
			url = ((BoatsForHireMarker) mapMarker).getUrl();
		}
		else if(isNavInfo){
			url = ((NavInfoMarker) mapMarker).getFeatureUrl();
		}
		// Only use digits in phone number string
		phoneNumber = phoneNumber.replaceAll("\\D*", "");
		
		switch(view.getId()){
		
		case R.id.ivCall:
			log("Clicked Call Image: \"" + phoneNumber + "\"");
			// Creates a dialog box to confirm call
			ConfirmCallDialogFragment.newInstance(phoneNumber)
					.show(getFragmentManager(), "MarkerInfoActivity_CallConfirmation");
			break;
			
		case R.id.ivWebsite:
			log("Clicked Website Image" + url);
			Intent intent = new Intent(MarkerInfoActivity.this, WebViewActivity.class);
			intent.putExtra("url", url);
			startActivity(intent);
			break;
			
		case NOAA_SITE_ID:
			log("Clicked Website Noaa Image" + url);
			Intent noaaIntent = new Intent(MarkerInfoActivity.this, WebViewActivity.class);
			noaaIntent.putExtra("url", navInfo.getNoaaPageUrl());
			startActivity(noaaIntent);
			break;
		
		}
		
	}
	
	/**
	 * This method checks to see if the string parameter is null, empty, only a space, or N/A
	 * 
	 * @param string Any string
	 * 
	 * @return  True if the string parameter is null, empty, only a space, or N/A
	 */
	private boolean isBlank(String string){
		if(string == null || string.equals("") || string.equals(" ") || string.equalsIgnoreCase("N/A"))
			return true;
		else 
			return false;
	}
	
	/**
	 * Checks to see whether or not the this mapMarker has a phone number
	 * 
	 * @return True if this mapMarker has a phone number
	 */
	private boolean hasPhoneNumber(){
		if(!isLaunch)
			if(	(lock == null ? false : !isBlank(lock.getPhoneNumber())) || 
						(marina == null ? false : !isBlank(marina.getPhoneNumber())) ||
						(bridge == null ? false : !isBlank(bridge.getPhoneNumber())) || 
						(boats == null ? false : !isBlank(boats.getPhoneNumber())) ) 
				return true;
		return false;
	}
	
	/**
	 * Checks to see whether or not the this mapMarker has a website
	 * 
	 * @return True if this mapMarker has a website
	 */
	private boolean hasWebsite(){
		if(isMarina || isBoatsForHire)
			if( (marina == null ? false : !isBlank(marina.getUrl())) || 
		    			(boats == null ? false : !isBlank(boats.getUrl())) ||
		    			(navInfo == null ? false : !isBlank(navInfo.getFeatureUrl())) )
				return true;
		return false;
	}

	/**
	 * This method uses a global counter variable. If the variable is even, that means that
	 * the TextView is a header and will have medium test size appearance. If the variable is odd,
	 * the text appearance will be small and have padding.
	 * A TextView will be created with the parameter text and then added to the scrollView. 
	 * The global counter variable will now be incremented.
	 * 
	 * @param text Text to add to the scrollView
	 */
	private void addTextView(String text){
		TextView tv = new TextView(this);
		
		if(textSizeCount % 2 == 0){	// Header
			tv.setTextAppearance(this, android.R.style.TextAppearance_Medium);
			//tv.setTextColor(getResources().getColor(R.color.blue));
		}
		else{	// Subtext
			tv.setTextAppearance(this, android.R.style.TextAppearance_Small);
			tv.setPadding(15, 0, 0, 12);
		}
		
        tv.setText(text);
        scrollView.addView(tv);
        textSizeCount++;
	}
	
	/**
	 * Turns the String of letters into a user-friendly way to see what fuel is available
	 * 
	 * @param letters Letters representing what fuel is available
	 * 
	 * @return The String of what fuel is available
	 */
	private String getFuelString(String letters){
		String fuel = "";
		if(letters.contains("G"))
			fuel = "Gas";
		if(letters.contains("D"))
			fuel = "Diesel";
		if(letters.contains("G") && letters.contains("D"))
			fuel = "Gas & Diesel";
		return fuel;
	}

	/**
	 * Turns the String of letters into a user-friendly way to see what repairs are available
	 * 
	 * @param letters Letters representing the repairs available
	 * 
	 * @return The String of repairs available
	 */
	private String getRepairString(String letters){
		String repairString = "";
		if(letters.contains("E"))
			repairString += "Electrical\n";
		if(letters.contains("H"))
			repairString += "Hull\n";
		if(letters.contains("M"))
			repairString += "Mechanical\n";
		if(letters.contains("S"))
			repairString += "Mast Stepping\n";
		if(letters.contains("T"))
			repairString += "Towing\n";

		// return string - 1 to get rid of the extra new line character
		return repairString.substring(0, repairString.length() - 1);
	}
	
	/**
	 * Turns the String of letters into a user-friendly way to see what facilities are available
	 * 
	 * @param letters Letters representing what facilities are available
	 * 
	 * @return The String of what facilities are available
	 */
	private String getFacilitiesString(String letters){
		
		String facilitiesString = "";
		final char characters[] = {'E', 'W', 'P', 'R', 'S', 'L', 'I', 'C'};
		final String strings[] = {"Electrical", "Water", "Pumpout", "Restrooms", 
				"Showers", "Laundry", "Wi-Fi", "Cable"};
		
		for(int i=0; i<characters.length; i++){
			if(letters.indexOf(characters[i]) != -1)
				facilitiesString += strings[i] + "\n";
		}
		
		// return string - 1 to get rid of the extra new line character
		return facilitiesString.substring(0, facilitiesString.length() - 1);
	}
	
	/**
	 * This dialog will confirm that the user want to call. It will display the phone number.
	 * This dialog will have a call and cancel button.
	 * 
	 * @author James O'Leary
	 *
	 */
	public static class ConfirmCallDialogFragment extends DialogFragment {

		private static String phoneNumber;
		
		public static ConfirmCallDialogFragment newInstance(String phoneNum) {
			phoneNumber = phoneNum;
			return new ConfirmCallDialogFragment();
		}
		
		public static String phoneNumberWithDashes(String phoneNumber){
			if(phoneNumber.length() == 10){
				return "(" + phoneNumber.substring(0,3) + ")-" + 
						phoneNumber.substring(3,6) + "-" + phoneNumber.substring(6,10);
			}
			return phoneNumber;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			
			final String message = "Call: " + phoneNumberWithDashes(phoneNumber);
			
			return new AlertDialog.Builder(getActivity())
					//.setIcon(R.drawable.callicon)
					//.setTitle(R.string.title_dialogaddsettings)
					.setMessage(message)
					.setCancelable(false)
					.setPositiveButton(R.string.button_call, new DialogInterface.OnClickListener() {
			               @Override
			               public void onClick(DialogInterface dialog, int id) {
			            	   
			            	   try{
			            		   // If ACTION_CALL, it will call (make sure permission in manifest is set)
			            		   Intent intent = new Intent(Intent.ACTION_DIAL);
			            		   intent.setData(Uri.parse("tel:" + phoneNumber));
			            		   getActivity().startActivity(intent); 
			            	   } catch(ActivityNotFoundException e) {
			            		   Toast.makeText(getActivity(), "Error with call", Toast.LENGTH_LONG).show();
			            	   }
			            	   
			               }
					})
					.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								ConfirmCallDialogFragment.this.getDialog().cancel();
							}
					})
					.create();
		}
		
	}
	
	/**
	 * onResume will set up the map if needed and move the camera of the map to the mapMarkers location.
	 */
	@Override
	public void onResume(){
		super.onResume();
		setUpMapIfNeeded();
		
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
        		mapMarker.getlatLng(), 14.5f));
	}
	
	/**
	 * This method will get the map if variable mMap is null. If it isn't null, initialize the map by
	 * add the marker to the map
	 */
	private void setUpMapIfNeeded() {
        if (mMap != null) {
            return;
        }
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        if (mMap == null) {
            return;
        }
        
        // Initialize map options
        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.addMarker(mapMarker.getMarkerOptions());
        
        // Get and add nearby places to the map
        new AddNearbyPlacesToMapTask(mMap, mapMarker.getlatLng()).execute();
	}
	
	private void log(String message) {
		if(SplashActivity.LOG_ENABLED)
    		Log.i("MarkerInfoActivity", message);
	}
	
}
