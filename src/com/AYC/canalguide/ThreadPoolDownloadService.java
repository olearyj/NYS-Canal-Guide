package com.AYC.canalguide;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ThreadPoolDownloadService extends Service {

    private static final int MAX_THREADS = 4;
	
	// The ExecutorService that references a ThreadPool.
    private ExecutorService mExecutor;
    
    @Override
	public void onCreate() {
    	super.onCreate();
        mExecutor = Executors.newFixedThreadPool(MAX_THREADS);
    }
    
    @Override
	public int onStartCommand(final Intent intent, int flags, int startId) {
    	
        Runnable downloadRunnable = new Runnable(){
			@Override
			public void run() {
				// TODO This is where i will download the a xml file
				//DownloadUtils.downloadAndRespond(getApplicationContext(), intent.getData(), 
				//		(Messenger) intent.getExtras().get(DownloadUtils.MESSENGER_KEY));
			}
        };
        mExecutor.execute(downloadRunnable);
        
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

}
