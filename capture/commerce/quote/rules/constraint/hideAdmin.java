//Check divisionFeeRate table to see if Admin Fee Charged

ret = false;
if (_system_current_step_var == ("adjustPricing") OR (_system_current_step_var == ("selectServices"))){
recset = bmql("Select adminAmount from divisionFeeRate where divisionNumber = $division_quote AND infopro_div_nbr = $infoProNumberDisplayOnly_quote");
for rec in recset{
adminAmt = get(rec,"adminAmount");
	if (adminAmt == "0.0"){
		ret = true;
	}
	else {
		ret = false;
	}
}
}
return ret;