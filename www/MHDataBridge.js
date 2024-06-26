var MHDataBridge = function() {};


MHDataBridge.prototype = {

	logRevenueEvent: function (externalSku, itemCategory, revenue) {
		var nothing = function(){};
		cordova.exec(nothing, nothing, "MHDataBridge", "logRevenueEvent", [externalSku, itemCategory, revenue]);
	},

	showRewardedVideoAd: function (success, failure) {
		//var nothing = function(){};
		cordova.exec(success, failure, "MHDataBridge", "showRewardedVideoAd", []);
	},

	showAdMob: function(success, failure) {
		cordova.exec(success, failure, "MHDataBridge", "showAdMob", []);
	},

	/**
	 * Fetches init data critical for startup, like platform (android|ios)
	 * store type (google|apple), etc.
	 *
	 * @param idents (Array)	MUST be an array. Android respects the contents
	 *							of this parameter, but ios does not.
	 */
	fetch: function (idents, callback) {
		cordova.exec(callback, callback, "MHDataBridge", "fetch", idents);
	},

	/**
	 * Fetches an array of messages as objects.
	 * Children expect these message objects to have
	 * the following keys defined
	 * - command
	 * - payload
	 *
	 * Sorry for the similar names, fetch came first and fetchMessages
	 * was patched in later.
	 */
	fetchMessages: function(callback) {
		cordova.exec(callback, callback, "MHDataBridge", "fetchMessages", []);
	},

	/**
	 * Sends up seed data (login tokens, etc) for the widget
	 */
	seedWidget: function(data, callback) {
		if(_.isUndefined(callback)) {
			callback = function() {};
		}
		cordova.exec(callback, callback, "MHDataBridge", "seedWidget", [JSON.stringify(data)]);
	},

	/**
	 * Helpshift - Installs the Helpshift SDK.
	 */
	helpshiftInstall: function (apiKey, domainName, appId, options) {
	    if (options && typeof options === "object") {
	    	cordova.exec (null, null, "MHDataBridge", "helpshiftInstall", [apiKey, domainName, appId, options]);
	    } else {
	      cordova.exec (null, null, "MHDataBridge", "helpshiftInstall", [apiKey, domainName, appId]);
	    }
	},

	/**
	 * Helpshift - Shows FAQs. This will show list of sections with search.
	 */
	helpshiftShowFAQs: function (options) {
	    if (options && typeof options === "object") {
	    	cordova.exec (null, null, "MHDataBridge", "helpshiftShowFAQs", [options]);
	    } else {
	    	cordova.exec (null, null, "MHDataBridge", "helpshiftShowFAQs", []);
	    }
	},

	/**
	 * Helpshift - Show the Helpshift conversation screen.
	 * @param  Object options
	 * @param  String prefillText optional
	 * @return Void
	 */
	helpshiftShowConversation: function (options, prefillText="") {
	    if (options && typeof options === "object") {
	    	cordova.exec (null, null, "MHDataBridge", "helpshiftShowConversation", [options, prefillText]);
	    } else {
	    	cordova.exec (null, null, "MHDataBridge", "helpshiftShowConversation", []);
	    }
	},

	/**
	 * Helpshift - Login. setUserIdentifier() is now deprecated and replaced by the new Login() and Logout() methods.
	 */
	helpshiftLogin: function (userIdentifier) {
		if (userIdentifier && typeof userIdentifier === "string") {
			cordova.exec (null, null, "MHDataBridge", "helpshiftLogin", [userIdentifier]);
		}
	},

	/**
	 * Helpshift - Logout.
	 */
	helpshiftLogout: function () {
		cordova.exec (null, null, "MHDataBridge", "helpshiftLogout", []);
	},

	/**
	 * Helpshift - Enable Push Notifications
	 */
	helpshiftRegisterDeviceToken: function(regstrationID) {
		if (regstrationID && typeof regstrationID === "string") {
	    	cordova.exec (null, null, "MHDataBridge", "helpshiftRegisterDeviceToken", [regstrationID]);
		}
	},

};

module.exports = new MHDataBridge();
