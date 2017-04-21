package com.AYC.canalguide;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

// TODO document this class properly
public class ThreadPoolDownloadService extends Service {

    private static int MAX_THREADS;

    /**
     * The key used to store/retrieve a Messenger extra from a Bundle.
     */
	public static final String MESSENGER_KEY = "MESSENGER";

	/**
     * The key used to store/retrieve a xmlString extra from a Bundle.
     */
	public static final String XMLSTRING_KEY = "XMLSTRING";
	
	public static final String NAV_INFO_DATA_LAST_SAVED_DATE_TAG = "nav info last saved date";
	public static final String URL_KEY = "URL";
	public static final String DONEDOWNLOADING_KEY = "Done downloading";
	
	private HashMap<String, String> navInfoXmlStrings;
    
	// The ExecutorService that references a ThreadPool.
    private ExecutorService mExecutor;
    private CountDownLatch countDownLatch;
    
    private Messenger messenger;
    
    @Override
	public void onCreate() {
    	super.onCreate();
        navInfoXmlStrings = new HashMap<String, String>();
        
        // We want to use the maximum amount of worker threads to get the job done the quickest
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        MAX_THREADS = availableProcessors > 1 ? availableProcessors - 1 : 1 ;
        mExecutor = Executors.newFixedThreadPool(MAX_THREADS);
        
        log("Created ThreadPoolDownloadService with " + MAX_THREADS + " threads");
    }
    
    private class DownloadXmlStringRunnable implements Runnable {
    	
    	String URL;
    	
    	public DownloadXmlStringRunnable(String URL){
    		this.URL = URL;
    	}
    	
    	@Override
    	public void run(){
    		String xmlString = downloadXmlFile(URL);
    		navInfoXmlStrings.put(URL, xmlString);
			sendXmlStringUrl(URL);
			countDownLatch.countDown();
    	}
    }
    
    @Override
	public int onStartCommand(final Intent intent, int flags, int startId) {

    	Toast.makeText(getApplicationContext(), "Starting to download data for buoys", Toast.LENGTH_SHORT).show();
    	
		messenger = (Messenger) intent.getExtras().get(MESSENGER_KEY);
		countDownLatch = new CountDownLatch(SplashActivity.navInfoURLs.length);
		
        for(int i=0; i<SplashActivity.navInfoURLs.length; i++)
        	mExecutor.execute(new DownloadXmlStringRunnable(SplashActivity.navInfoURLs[i]));
        
        mExecutor.execute(new Runnable(){
			@Override
			public void run() {
				log("countDownLatch will start awaiting");
				try {
					countDownLatch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				stopSelf();
			}
        });
        
        // Tell the Android framework how to behave if this service is
        // interrupted.  In our case, we want to restart the service
        // then re-deliver the intent so that all files are eventually
        // downloaded.
        return START_REDELIVER_INTENT;
    }
    
    /**
     * Called when the service is destroyed, which is the last call
     * the Service receives informing it to clean up any resources it
     * holds.
     */
    @Override
	public void onDestroy() {
    	// Ensure that the threads used by the ThreadPoolExecutor
    	// complete and are reclaimed by the system.
    	
    	log("Destroying service and saving navInfoXmlStrings");
    	saveXmlStrings();
    	sendFinalMessage();
    	
        mExecutor.shutdown();
    }
    
    /**
     * Return null since this class does not implement a Bound
     * Service.
     */
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	/**
	 * 
	 * @param URL
	 * @return
	 */
	private String downloadXmlFile(String URL){
		String xmlString = "";
		
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
		} catch (IOException e) {
			log("Error: IOException because network not connected");
			e.printStackTrace();
		} 
		
		// There are 3 strange characters in the beginning of the some
		// strings that need to be taken out
		xmlString = xmlString.substring(xmlString.indexOf("<?xml version=\"1.0\" encoding=\"utf-8\"?>"));
		
		return xmlString;
	}
	
    public void sendXmlStringUrl(String URL) {
		Message msg = Message.obtain();
		Bundle data = new Bundle();
		data.putString(URL_KEY, URL);
		
		// Make the Bundle the "data" of the Message.
		msg.setData(data);
		
		try {
			// Send the Message back to the client Activity.
			messenger.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    }

    public void sendFinalMessage() {
		Message msg = Message.obtain();
		Bundle data = new Bundle();
		data.putString(DONEDOWNLOADING_KEY, DONEDOWNLOADING_KEY);
		
		// Make the Bundle the "data" of the Message.
		msg.setData(data);
		
		try {
			// Send the Message back to the client Activity.
			messenger.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Make an intent that will start this service if supplied to
     * startService() as a parameter.
     * 
     * @param context		The context of the calling component.
     * @param handler		The handler that the service should
     *                          use to respond with a result  
     * @param uri               The web URL of a file to download
     * 
     * This method utilizes the Factory Method makeMessengerIntent()
     * from the DownloadUtils class.  The returned intent is a Command
     * in the Command Processor Pattern. The intent contains a
     * messenger, which plays the role of Proxy in the Active Object
     * Pattern.
     */
    public static Intent makeIntent(Context context,
                                    Handler handler) {

    	Messenger messenger = new Messenger(handler);
    	Intent intent = new Intent(context, ThreadPoolDownloadService.class);
    	intent.putExtra(MESSENGER_KEY, messenger);
    	
    	return intent;
    }
    
    /**
	 * This method saves the navInfoXmlStrings using SharedPreferences when downloaded.
	 * 
	 * @param xmlStrings The navInfoXmlStrings that will be saved
	 */
	private void saveXmlStrings(){
		log("Saving navInfoXmlStrings");
		// We need an Editor object to make preference changes
	    SharedPreferences xmlStringsPref = getSharedPreferences(SplashActivity.PREFS_NAME, SplashActivity.PREFS_MODE);
	    SharedPreferences.Editor editor = xmlStringsPref.edit();
	    
	    Iterator<Map.Entry<String, String>> iterator = navInfoXmlStrings.entrySet().iterator();
	    while (iterator.hasNext()) {
	        Map.Entry<String, String> pairs = (Map.Entry<String, String>) iterator.next();
	        editor.putString((String) pairs.getKey(), (String) pairs.getValue());
	        iterator.remove(); // Avoids a ConcurrentModificationException
	    }
	    
	    // Save the the date that the data was downloaded
	    Calendar calendar = Calendar.getInstance();
	    editor.putLong(NAV_INFO_DATA_LAST_SAVED_DATE_TAG, calendar.getTimeInMillis());
	    
	    editor.commit();	// Commit the edits!
	}
	
	private void log(String msg){
    	if(SplashActivity.LOG_ENABLED)
    		Log.i("ThreadPoolDownloadService", msg);
    }
	
}