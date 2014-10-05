package Tools;

import android.util.Log;

import com.AYC.canalguide.SplashActivity;

/**
 * This class can be used to time how long it takes code to run.
 * It can also be used to see how the average time it takes for code
 * in a loop to run along with the minimum time and maximum time.
 * This will be great for debugging code that is taking too much time.
 * This uses System.currentTimeMillis() to keep track of time.
 */
public class MyTimer {
	
	private long startTime, finishTime;
    private int count, total, min, max;
    
    public MyTimer(){
    	startTime = 0;
    	finishTime = 0;
    	count = 0;
    	total = 0;
    	min = 9999999;
    	max = 0;
    }
    
    public long startTimer(){
    	startTime = System.currentTimeMillis();
    	return startTime;
    }
    
    public long endTimer(){
    	finishTime = System.currentTimeMillis();
    	count++;
    	int diff = (int) (finishTime - startTime);
    	total += diff;
    	if(min > diff)
    		min = diff;
    	if(max < diff)
    		max = diff;
    	return finishTime;
    }
    
    /**
     * This will return the time it took from startTime to endTime in milliseconds
     * 
     * @return startTime - finishTime
     */
    public long getTimeDifference(){
    	return startTime - finishTime;
    }
    
    public void logTimeDiff(String codeDesc){
    	log("\"" + codeDesc + "\": " + (finishTime - startTime) + "ms");
    }
    
    /**
     * This will log.i the average, minimum and maximum time in milliseconds
     * 
     * @param codeDesc
     * Description of the code that was timed
     */
    public void logTimeStats(String codeDesc){
    	log("\"" + codeDesc + "\": Average=" + total/count + "ms");
    	log("\"" + codeDesc + "\": " + "min=" + min + "ms" + " max=" + max + "ms");
    }

    private void log(String msg){
    	if(SplashActivity.LOG_ENABLED)
    		Log.i("MyTimer", msg);
    }
    
}
