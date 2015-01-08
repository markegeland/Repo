/**
 * @param dependencies {Array} name of modules this code depends on. Can exclude ".js"
 * @param callback {Function} function containing this module's functionality.
 * @version Fri Feb 25 18:44:56 2011
 */
 
require(["return_to_quote_button", "jquery_cookie"],function(rtq) { 
	//Put all functions for homepage here
	//this function runs when the page loads
	require.ready(function() {
		rtq.add_button_to_homepage(); 
	});
});

require(["qs_homepage"]);