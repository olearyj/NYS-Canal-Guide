package com.AYC.canalguide;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class ThreadPoolDownloadService extends Service {

    private static final int MAX_THREADS = 4;

    /**
     * The key used to store/retrieve a Messenger extra from a Bundle.
     */
	public static final String MESSENGER_KEY = "MESSENGER";

	/**
     * The key used to store/retrieve a xmlString extra from a Bundle.
     */
	public static final String XMLSTRING_KEY = "XMLSTRING";
    
	// The ExecutorService that references a ThreadPool.
    private ExecutorService mExecutor;
    
    @Override
	public void onCreate() {
    	super.onCreate();
        mExecutor = Executors.newFixedThreadPool(MAX_THREADS);
    }
    
    private class DownloadXmlStringRunnable implements Runnable {
    	
    	Messenger messenger;
    	String URL;
    	
    	public DownloadXmlStringRunnable(Messenger messenger, String URL){
    		this.messenger = messenger;
    		this.URL = URL;
    	}
    	
    	@Override
    	public void run(){
    		String xmlString = downloadXmlFile(URL);
			sendXmlString(xmlString, messenger);
    	}
    }
    
    @Override
	public int onStartCommand(final Intent intent, int flags, int startId) {

		Messenger messenger = (Messenger) intent.getExtras().get(MESSENGER_KEY);
        
        for(int i=0; i<SplashActivity.navInfoURLs.length; i++)
        	mExecutor.execute(new DownloadXmlStringRunnable(messenger, SplashActivity.navInfoURLs[i]));
        
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
		// IOException catch will attempt to load from storage then open mainActivity on success
		} catch (IOException e) {
			log("Error: IOException because network not connected");
			/*
			// If there is saved data
			if(loadDataLastSavedDate() != -1){
				final HashMap<String, String> xmlStrings = loadXmlString();
				downloadMarkersAsyncTask.cancel(true);
				
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
				return null;
			}
			*/
		} 
		
		// There are 3 strange characters in the beginning of the some
		// strings that need to be taken out
		xmlString = xmlString.substring(xmlString.indexOf("<?xml version=\"1.0\" encoding=\"utf-8\"?>"));
		
		return xmlString;
	}
	
    public void sendXmlString(String xmlString, Messenger messenger) {
		Message msg = Message.obtain();
		Bundle data = new Bundle();
		data.putString(XMLSTRING_KEY, xmlString);
		
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
                                    Handler handler,
                                    String uri) {
    	// TODO - You fill in here, by replacing null with an
        // invocation of the appropriate factory method in
        // DownloadUtils that makes a MessengerIntent.
        // return DownloadUtils.makeMessengerIntent(context, ThreadPoolDownloadService.class, handler, uri);

    	Messenger messenger = new Messenger(handler);
    	Intent intent = new Intent(context, ThreadPoolDownloadService.class);
    	intent.putExtra(MESSENGER_KEY, messenger);
    	intent.setData(Uri.parse(uri));
    	
    	return intent;
    }
	
	private void log(String msg){
    	if(SplashActivity.LOG_ENABLED)
    		Log.i("ThreadPoolDownloadService", msg);
    }
	
}
