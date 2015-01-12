/**
* @namespace
*
* @name commerce_ids
* @description A module used to grab common ids out of the source of a BigMachines quote
**/
define([],function() {
	var commerce_ids = {};

	/**
* Grab a hash of common ids
*
* @return ids {Object}
* @return ids.document_id {String}
* @return ids.open_action_id {String}
* @return ids.version_id {String}
* @return ids.document_number {String}
* @return ids.bsid {String}
*/
	commerce_ids.get_ids = function() {
		if(typeof jQuery !== "function") {
			throw new Error("jQuery is not available to load commerce ids. This function is only expected to be called from a commerce document, where jQuery should already be available.");
		}
		return {
			document_id: jQuery("input[name='document_id']").val(),
			open_action_id: "4654396",
			version_id: jQuery("input[name='version_id']").val(),
			document_number: "-1",
			bsid: jQuery("input[name='id']").val()
		};
	}

	return commerce_ids;
});