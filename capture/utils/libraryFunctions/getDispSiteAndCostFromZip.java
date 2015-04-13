retDict = dict("string");

// Disposal zip table supports 5-digit zip codes only

customerZipCode = zip;
if(len(customerZipCode) > 5) {
	customerZipCode = substring(customerZipCode, 0, 5);
}

if(debug) {
	print "customerZipCode: " + customerZipCode;
}

containerDisposalRecordSet = bmql("SELECT waste_type, disposal_cd, is_franchise, is_serviceable FROM small_cont_dsp_zip WHERE infopro_div_nbr = $infoproDivisionNumber AND division = $lawsonDivision AND zip = $customerZipCode");

if(debug) {
	print containerDisposalRecordSet;
}

waste_type = "";
disposal_cd = "";
is_franchise = "";
is_serviceable = "";
siteFound = false;

dsp_site_cost = "";
dsp_xfer_price = "";


for eachDisposalRecord in containerDisposalRecordSet {
	
	waste_type = get(eachDisposalRecord, "waste_type");
	disposal_cd = get(eachDisposalRecord, "disposal_cd");
	is_franchise = get(eachDisposalRecord, "is_franchise");
	is_serviceable = get(eachDisposalRecord, "is_serviceable");
	print disposal_cd;
	print len(disposal_cd);
	
	
	
	disposalCostRecordSet = bmql("SELECT dsp_site_cost, dsp_xfer_price FROM DisposalCosts_Comm WHERE division = $lawsonDivision AND infopro_div_nbr = $infoproDivisionNumber AND disposal_cd = $disposal_cd");
print 	disposalCostRecordSet;	
	for eachCostRecord in disposalCostRecordSet {
		siteFound = true;
		dsp_site_cost = get(eachCostRecord, "dsp_site_cost");
		dsp_xfer_price = get(eachCostRecord, "dsp_xfer_price");print "dspcost from table";print dsp_site_cost;
		break;
	}
	break;
}

if(not(siteFound)) {
	disposal_cd = "";
	waste_type = "Solid Waste";
	is_franchise = "0";
	is_serviceable = "1";
	defaultRecordSet = bmql("SELECT default_disposal_3p FROM tbl_division_kpi WHERE div_nbr = $lawsonDivision AND waste_type = $waste_type");
	//print defaultRecordSet;
	for eachDefaultRecord in defaultRecordSet {
		dsp_site_cost = get(eachDefaultRecord, "default_disposal_3p");
		dsp_xfer_price = get(eachDefaultRecord, "default_disposal_3p");
		print "sitenotFound_dspcost";print dsp_site_cost;
		break;
	}		
}

put(retDict, "disposal_cd", disposal_cd);
put(retDict, "is_serviceable", is_serviceable);
put(retDict, "is_franchise", is_franchise);
put(retDict, "disposalCost", dsp_site_cost);
put(retDict, "dsp_xfer_priceperton", dsp_xfer_price);

// put(retDict, "windingNumber", string(winding));


return retDict;