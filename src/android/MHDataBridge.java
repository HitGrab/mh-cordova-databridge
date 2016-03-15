package com.hitgrab.android.mousehunt;


import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;

/**
 * Fetch a String's worth of data. Booleans will be returned as encoded strings, 
 * eg literally "true" or "false"
 * 
 * @author Derek Cheung
 */
public class MHDataBridge extends CordovaPlugin {
	
	 @Override
     public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
         if (action.equals("fetch")) {
        	 
        	 JSONObject output = new JSONObject();
        	 for(int i=0;i<args.length();i++) {
        		 String ident = args.getString(i);
        		 String result = fetch(ident);
        		 output.put(ident, result);
        	 }
             
             if(output.length() != 0) {
            	 callbackContext.success(output.toString());
             } else {
            	 callbackContext.error("");            	 
             }
             
             return true;
         }
         return false;
     }
	 
	 private String fetch(String ident) {
		 
		 /**
		  * Developer's notes
		  * ------------------
		  * 
		  * If you add an "if" block here with a result greater than one line,
		  * make a new function for it below instead (see clientVersion for an example)
		  * 
		  * Booleans should be returned as full strings, eg. literally "true" or "false"
		  * (see isAppNative for an example)
		  * 
		  */
         if(ident.equals("isAppNative")) {
        	 return "true";
         } if(ident.equals("operatingSystem")) {
        	 return "android";
         } if(ident.equals("appStore")) {
        	 return "google";
         } if(ident.equals("clientVersion")) {
        	 return getClientVersion();     	 
         } if(ident.equals("isEmulator")) {
        	 return Build.FINGERPRINT.startsWith("generic") ? "true" : "false";
         } if(ident.equals("numNativeBootups")) {
        	 return getNumBootups();
         } else {
        	 return null;
         }
	 }
	 
	 /**
	  * Returns current client version in format x.x.x, or
	  * literally "0.0.0" 
	  * 
	  * @return String in form x.x.x
	  */
	 private String getClientVersion() {
	        try {
	        	Context context = this.cordova.getActivity();
	        	PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
	        	return pInfo.versionName;
	        } catch(Exception e) {
	        	return "0.0.0";
	        }
	 }
	 
	 private String getNumBootups() {
		return Integer.toString(this.cordova.getActivity().getPreferences(Context.MODE_PRIVATE).getInt("TOTAL_BOOTUPS", 0));
	 }

}
