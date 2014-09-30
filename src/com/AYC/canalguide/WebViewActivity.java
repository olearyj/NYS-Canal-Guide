package com.AYC.canalguide;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * This activity will receive a markers url and display the website. It will allow
 * pinch-to-zoom.
 * 
 * @author James O'Leary
 *
 */
public class WebViewActivity extends Activity {

	private WebView webView;
	private String url;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.activity_webview);
	      
	      // TODO - Implement this in later version of this app
	      //ActionBar actionBar = getActionBar();
	      //actionBar.setDisplayHomeAsUpEnabled(true);
	      
	      url = getIntent().getStringExtra("url");
	      log("URL = " + url);
	      
	      webView = (WebView) findViewById(R.id.webView);
	      webView.getSettings().setJavaScriptEnabled(true);	// Not sure if i need this
	      webView.getSettings().setBuiltInZoomControls(true);
	      webView.setWebViewClient(new CanalWebViewClient());
	      webView.loadUrl(url);

	}
	
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

	private void log(String msg) {
		if(SplashActivity.LOG_ENABLED)
    		Log.i("WebViewActivity", msg);		
	}
	
	private class CanalWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        view.loadUrl(url);
	        return true;
	    }
	}

}

