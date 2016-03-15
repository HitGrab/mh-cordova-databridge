var MHDataBridge = function() {};


MHDataBridge.prototype = {

	/**
	 * Logs an event of the given event name
	 *
	 * @param eventName (String)	String of the event name to log.
	 * @param data		(Object)	Object of extra data, may pass null
	 */
	logEvent: function (eventName, data) {

		var nothing = function(){};
		if(typeof data === 'undefined' || data == null) {
			data = {};
		}

		cordova.exec(nothing, nothing, "MHDataBridge", "logEvent", [eventName, data]);
	},

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

};

module.exports = new MHDataBridge();
