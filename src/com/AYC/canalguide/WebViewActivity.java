package com.AYC.canalguide;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;

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
	private SearchView searchView;
	private String url;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
	      setContentView(R.layout.activity_webview);
	      
	      ActionBar actionBar = getActionBar();
	      // This will display the arrow on the left in the actionbar
	      actionBar.setDisplayHomeAsUpEnabled(true);
	      
	      if(url == null)
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
	 * Create the searchview icon in the actionbar
	 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchview_in_menu, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        return true;
    }
    
    /**
     * When the options menu is being prepared, change the searchview icon and
     * set its text to the url
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);    
        searchView = (SearchView) searchViewMenuItem.getActionView();
        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_action_edit); 
        //searchView.setOnQueryTextListener(this);
        searchView.setQuery(url, false);
        return super.onPrepareOptionsMenu(menu);
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
			searchView.setQuery(url, false);
	    	WebViewActivity.this.url = url;
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