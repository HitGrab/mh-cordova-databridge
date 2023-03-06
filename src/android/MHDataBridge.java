package com.hitgrab.android.mousehunt;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.helpshift.Helpshift;
import com.helpshift.UnsupportedOSVersionException;
import com.hitgrab.android.mousehunt.widget.MouseHuntWidgetProvider;
import com.hitgrab.android.mousehunt.widget.WidgetController;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;

import android.util.Log;
import org.apache.cordova.PluginResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

				Context context = this.cordova.getActivity().getApplicationContext();

				Intent intent = new Intent(context, MouseHuntWidgetProvider.class);
				intent.setAction(WidgetController.ACTION_SEED);
				payloadCache = raw;
				intent.putExtra("payload", raw);
				// Send an intent to MouseHuntWidgetProvider where it listens for broadcasts, save user data
				context.sendBroadcast(intent);

				// Return new PluginResult(PluginResult.Status.OK);
				callbackContext.success();
			} else {
				callbackContext.error("");
			}

			return true;

		} else if (action.equals("helpshiftInstall")) {

//         	String apiKey = args.getString(0);
			String domainName = args.getString(1);
			String appID = args.getString(2);

			Map<String, Object> config = Collections.emptyMap();

			try {
				Helpshift.install(cordova.getActivity().getApplication(),
						appID,
						domainName,
						config);

				callbackContext.sendPluginResult( new PluginResult(PluginResult.Status.OK, ""));
			} catch (UnsupportedOSVersionException e) {
				// Android OS versions prior to Lollipop (< SDK 21) are not supported.
				Log.e("MHDataBridge", "Android OS versions prior to Lollipop (< SDK 21) are not supported : ", e);
				callbackContext.error("");
			}

		} else if (action.equals("helpshiftShowFAQs")) {

			HashMap<String, Object> config = new HashMap<>();
			config.put("customMetadata", args.getJSONObject(0));

			Helpshift.showFAQs(cordova.getActivity(), config);
			callbackContext.sendPluginResult( new PluginResult(PluginResult.Status.OK, ""));

		} else if (action.equals("helpshiftShowConversation")) {

			// Pre-fill text Currently unsupported by SDK X
//			String prefillText = args.getString(1);

			HashMap<String, Object> config = new HashMap<>();
			config.put("customMetadata", args.getJSONObject(0));

			Helpshift.showConversation(cordova.getActivity(), config);
			callbackContext.sendPluginResult( new PluginResult(PluginResult.Status.OK, ""));

		} else if (action.equals("helpshiftLogin")) {

			Map<String, String> userData = new HashMap<>();
			userData.put("userId", args.getString(0));
			userData.put("userEmail", "");
			userData.put("userName", "");
			userData.put("userAuthToken", "");

			Helpshift.login(userData);

			callbackContext.sendPluginResult( new PluginResult(PluginResult.Status.OK, ""));

		} else if (action.equals("helpshiftLogout")) {

			Helpshift.logout();

		} else if (action.equals("helpshiftRegisterDeviceToken")) {

			String regId = args.getString(0);

			// Send registrationId to Helpshift
			Helpshift.registerPushToken(regId);

		} else {
			// Return new PluginResult(PluginResult.Status.INVALID_ACTION);
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
		if (ident.equals("isAppNative")) {
			return "true";
		} if (ident.equals("operatingSystem")) {
			return "android";
		} if (ident.equals("appStore")) {
			return "google";
		} if (ident.equals("clientVersion")) {
			return getClientVersion();
		} if (ident.equals("isEmulator")) {
			return Build.FINGERPRINT.startsWith("generic") ? "true" : "false";
		} if (ident.equals("numNativeBootups")) {
			return getNumBootups();
		} if (ident.equals("packageName")) {
			Context context = this.cordova.getActivity();
			return context.getPackageName();
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


}
