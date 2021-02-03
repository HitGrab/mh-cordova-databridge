package com.hitgrab.android.mousehunt;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.helpshift.HelpshiftUser;
import com.helpshift.support.ApiConfig;
import com.helpshift.support.Metadata;
import com.helpshift.util.HSJSONUtils;
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
import org.apache.cordova.PluginResult;

import java.util.HashMap;

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

		} else if (action.equals("helpshiftInstall")) {

         	String apiKey = args.getString(0);
			String domainName = args.getString(1);
			String appID = args.getString(2);

			InstallConfig installConfig = new InstallConfig.Builder().build();

			Core.init(Support.getInstance());

			try {
				Core.install(cordova.getActivity().getApplication(), apiKey, domainName, appID, installConfig);

				callbackContext.sendPluginResult( new PluginResult(PluginResult.Status.OK, ""));

			} catch (InstallException e) {
				Log.e("MHDataBridge", "invalid install credentials : ", e);
				callbackContext.error("");
			}

		} else if (action.equals("helpshiftShowFAQs")) {

			HashMap<String,Object> config = new HashMap<String,Object>();
			config = HSJSONUtils.toMap(args.getJSONObject(0));

			Metadata metadata = new Metadata(config);

			ApiConfig apiConfig = new ApiConfig.Builder().setCustomMetadata(metadata).build();

			Support.showFAQs(cordova.getActivity(), apiConfig);

			callbackContext.sendPluginResult( new PluginResult(PluginResult.Status.OK, ""));

		} else if (action.equals("helpshiftShowConversation")) {

			HashMap<String,Object> config = new HashMap<String,Object>();
			config = HSJSONUtils.toMap(args.getJSONObject(0));

			Metadata metadata = new Metadata(config);

			ApiConfig apiConfig = new ApiConfig.Builder().setCustomMetadata(metadata).build();

			Support.showConversation(cordova.getActivity(), apiConfig);

			callbackContext.sendPluginResult( new PluginResult(PluginResult.Status.OK, ""));

		} else if (action.equals("helpshiftLogin")) {

			HelpshiftUser user = new HelpshiftUser.Builder(args.getString(0), "").build();
			Core.login(user);
			callbackContext.sendPluginResult( new PluginResult(PluginResult.Status.OK, ""));

		} else if (action.equals("helpshiftLogout")) {

			Core.logout();

		} else if (action.equals("helpshiftRegisterDeviceToken")) {

			Context context = this.cordova.getActivity().getApplicationContext();
			String regid = args.getString(0);

			// Send registrationId to Helpshift
			Core.registerDeviceToken(context, regid);

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
