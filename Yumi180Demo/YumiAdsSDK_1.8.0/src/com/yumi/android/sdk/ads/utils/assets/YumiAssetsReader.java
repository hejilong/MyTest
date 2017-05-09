package com.yumi.android.sdk.ads.utils.assets;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.content.Context;

public final class YumiAssetsReader {

	private static final String TAG = "YumiAssetsReader";
	private static final boolean onoff = true;
	public final static String getFromAssets(Context context, String fileName){ 
		BufferedReader bufReader = null;
		String line = "";
		String result = null;
        try { 
            InputStreamReader inputReader = new InputStreamReader( context.getApplicationContext().getResources().getAssets().open(fileName) ); 
            bufReader =  new BufferedReader(inputReader);
            while((line = bufReader.readLine()) != null){
            	result += line;
            }
        } catch (Exception e) { 
        	ZplayDebug.e(TAG, "", e, onoff);
        } finally{
        	try {
        		if (bufReader != null) {
        			bufReader.close();
        		}
			} catch (Exception e) {
			}
        }
        return result;
} 
	
}
