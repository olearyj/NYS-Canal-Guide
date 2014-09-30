package com.AYC.canalguide;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * This activity will receive a url and display the website. It will allow
 * the pinch-to-zoom feature.
 * 
 * @author James O'Leary
 *
 */
public class WebViewActivity extends Activity {

	private WebView webView;
	private ProgressBar progressBar;
	private String url;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.activity_webview);
	      
	      // TODO - Implement this in later version of this app
	      ActionBar actionBar = getActionBar();
	      actionBar.setDisplayHomeAsUpEnabled(true);
	      
	      url = getIntent().getStringExtra("url");
	      log("URL = " + url);
	      
	      progressBar = (ProgressBar) findViewById(R.id.progressBar);
	      
	      webView = (WebView) findViewById(R.id.webView);
	      webView.getSettings().setJavaScriptEnabled(true);	// Not sure if i need this
	      webView.getSettings().setBuiltInZoomControls(true);
	      
	      webView.setWebViewClient(new CanalWebViewClient());
	      webView.setWebChromeClient(new CanalWebChromeClient());
	      
	      webView.loadUrl(url);

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
	 * This method is overridden so that when the back key is pressed, it will
	 * go back a page rather than finishing the activity. If we can't go back
	 * a page, we will finish the activity.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
	        webView.goBack();
	        return true;
	    }
	    else
	        finish();
	    return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * This class will control the visibility of the progress bar.
	 * Also, when a link is clicked, it will be overridden to load in this webview.
	 * 
	 * @author James O'Leary
	 *
	 */
	private class CanalWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        view.loadUrl(url);
	        return true;
	    }
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			progressBar.setVisibility(ProgressBar.VISIBLE);
		}
		@Override
		public void onPageFinished(WebView view, String url) {
			progressBar.setVisibility(ProgressBar.GONE);
		}
	}
	
	/**
	 * This class will update the progress bar when the progress of the
	 * loading web page is changed
	 * 
	 * @author James O'Leary
	 *
	 */
	private class CanalWebChromeClient extends WebChromeClient {	
		@Override
		public void onProgressChanged(WebView view, int newProgress) {			
			progressBar.setProgress(newProgress);
			super.onProgressChanged(view, newProgress);
		}
	}
	
	private void log(String msg) {
		if(SplashActivity.LOG_ENABLED)
    		Log.i("WebViewActivity", msg);		
	}

}