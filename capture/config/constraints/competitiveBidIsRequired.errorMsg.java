/*Added the conditional for error message for large existing*/
ret = "";
if(salesActivity == "Service level change"){
	ret = ret + "Competitive Bid is a required field; must use “Service Change : Standard Change” if customer refuses to provide Competitive Bid amount.";
}
if(salesActivity == "Price adjustment"){
	ret = ret + "Competitive Bid is a required field; must use “Rollback of Current Rate(s)” if customer refuses to provide Competitive Bid amount.";
}
if(salesActivity == "Change of Owner"){
	ret = ret + "Competitive Bid is a required field; must use “Change of Owner : Standard Change” if customer refuses to provide Competitive Bid amount.";
}

return ret;