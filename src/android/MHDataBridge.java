package com.hitgrab.android.mousehunt;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hitgrab.android.mousehunt.widget.WidgetService;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;

import android.util.Log;
import com.helpshift.Core;
import com.helpshift.support.Support;
import com.helpshift.InstallConfig;
import com.helpshift.exceptions.InstallException;

/**
 * Fetch a String's worth of data. Booleans will be returned as encoded strings,
 * eg literally "true" or "false"
 *
 * @author Derek Cheung
 */
public class MHDataBridge extends CordovaPlugin {

	public final static String ACTION_ADD_MESSAGE = "add_message";
	public final static String EXTRA_KEY_COMMAND = "command";
	public final static String EXTRA_KEY_PAYLOAD = "payload";

	public static String payloadCache;
	public static JSONArray messageStack = new JSONArray();

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
		} else if(action.equals("fetchMessages")) {

			JSONArray outputMessageStack = messageStack;
			messageStack = new JSONArray();
			//return new PluginResult(PluginResult.Status.OK, outputMessageStack);
			callbackContext.success(outputMessageStack);
			return true;
		} else if (action.equals("seedWidget")) {
			String raw = args.getString(0);
			if (raw != null && raw.length() > 0) {

				// Configure the service
				Context context = this.cordova.getActivity().getApplicationContext();
				Intent intent = new Intent(context, WidgetService.class);
				intent.setAction(WidgetService.ACTION_SEED);
				payloadCache = raw;
				intent.putExtra("payload", raw);
				context.startService(intent);

				// return new PluginResult(PluginResult.Status.OK);
				callbackContext.success();
			} else {
				callbackContext.error("");
			}

			return true;

		} else if (action.equals("install")) {
         	String apiKey = args.getString(0);
			String domainName = args.getString(1);
			String appID = args.getString(2);

			install(apiKey, domainName, appID);

         } else {
			//return new PluginResult(PluginResult.Status.INVALID_ACTION);
			callbackContext.error("");
			return false;
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


	public static void addMessage(String command, String payload) {

		if(command == null && payload == null) { return; }

		JSONObject pack = new JSONObject();

		try {
			pack.put(EXTRA_KEY_COMMAND, command);
			pack.put(EXTRA_KEY_PAYLOAD, payload);
			messageStack.put(pack);
		} catch (JSONException e) {
			// whatever
		}
	}

	private void install(String apiKey, String domainName, String appID) {

		InstallConfig installConfig = new InstallConfig.Builder().build();

		Core.init(Support.getInstance());
		
		try {
			Core.install(cordova.getActivity().getApplication(), apiKey, domainName, appID, installConfig);
		} catch (InstallException e) {
			Log.e("MHDataBridge", "invalid install credentials : ", e);
		}

	}

}
