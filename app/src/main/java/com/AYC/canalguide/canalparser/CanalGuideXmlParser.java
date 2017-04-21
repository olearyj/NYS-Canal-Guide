package com.AYC.canalguide.canalparser;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.AYC.canalguide.SplashActivity;

import android.util.Log;
import android.util.Xml;

public class CanalGuideXmlParser {

    private XmlPullParser parser;
    private int URL;
	
	public CanalGuideXmlParser(String URL){
        parser = Xml.newPullParser();
        if(URL.contains("navinfo")){
	        for(int i = 0; i<SplashActivity.navInfoURLs.length; i++)
	        	if(SplashActivity.navInfoURLs[i].equals(URL))
	        		this.URL = i;
        }
        else 
        	this.URL = 0;
	}
	
    public List<MapMarker> parse(Reader reader) throws XmlPullParserException, IOException {
        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(reader);
            parser.nextTag();
            return read();
        } finally {
            reader.close();
        }
    }
	
    private List<MapMarker> read() throws XmlPullParserException, IOException {
        
    	List<MapMarker> mapMarkers = new ArrayList<MapMarker>();
    	
    	int event = parser.getEventType();
        while(event != XmlPullParser.END_DOCUMENT){
            String name = parser.getName();
            
            if(event == XmlPullParser.START_TAG)
            	   if(name.equals("locks")){
            		   mapMarkers = LockMarker.readMarker(parser);
            	   } 
            	   else if(name.equals("marinas")){
            		   mapMarkers = MarinaMarker.readMarker(parser);
            	   } 
            	   else if(name.equals("boatlaunches")){
            		   mapMarkers = LaunchMarker.readMarker(parser);            		
            	   }
            	   else if(name.equals("cruises")){
            		   mapMarkers = BoatsForHireMarker.readMarker(parser);            		
            	   }
            	   else if(name.equals("guardgates") || name.equals("liftbridges")){
            		   mapMarkers = BridgeGateMarker.readMarker(parser); 
            	   }
            	   else if(name.equals("navigationinfo")){
            		   mapMarkers = NavInfoMarker.readMarker(parser, URL);
            	   }
            	   else {
            		   log("ERROR: name of markers not found: \"" + name + "\"");
            	   }
            
            event = parser.next();   
        }
        
        return mapMarkers;
    }

	private void log(String msg) {
		if(SplashActivity.LOG_ENABLED)
    		Log.i("CanalGuideXmlParser", msg);		
	}

}