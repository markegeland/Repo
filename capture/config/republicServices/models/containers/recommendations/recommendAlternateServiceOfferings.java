//30336732 - TC-0505
ROW_START = "<tr  class='rs_tr_td_style'>";
ROW_END = "</tr>";
COL_START = "<td>";
COL_START_CURRENCY = "<td align='right' width='15%'>";
COL_END = "</td>";
guardrailOutputDict = dict("string");
guardrailInputDict_current_offering = dict("string");
guardrailInputDict_alt_offering1 = dict("string");
guardrailInputDict_alt_offering2 = dict("string");
targetPrice = 0.0;
roundingFactor = 1.0;
DEBUG = false;

/* START - Get the part number for small container*/
// Change the Compactor from Boolean to String.
compactorStr = "0";
if(compactor){
	compactorStr = "1";//Parts DB has 0 for compactor part and "1" for non-compactor part
}

//Round guard rail prices except floor to the rounding increment values as defined in roundingIncrements data table
smallContainerRoundingFactor = 1.0; //Initialize rounding factor to 0
largeContainerRoundingFactor = 1.0; //Initialize rounding factor to 0
containerTypesArray = string[];
append(containerTypesArray, "Small");
if(sizeofarray(containerTypesArray) > 0){
	roundingFactors = bmql("SELECT value FROM miscConfigData WHERE attribute = 'rounding_com_rate'");
	for record in roundingFactors{
		roundingFactor  = getFloat(record, "value");
	}
}

partNumber = "";
partsRecordSet = bmql("SELECT part_number FROM _parts WHERE custom_field9 = $routeTypeDervied AND custom_field10 = $containerSize AND custom_field11 = $compactorStr AND  custom_field12 = $lOBCategoryDerived");
for eachRec in partsRecordSet{
	partNumber = get(eachRec, "part_number");
}

/* END - Get the part number for small container*/

difference = "";
allocationFactor = 1.0;
containerSizeVal = 0.0;
frequency = 0.0;
if(isnumber(containerSize)){
	containerSizeVal = atof(containerSize);
}
qty = quantity;
frequencyStr = util.getServiceConversion("liftsPerCont_to_freq", liftsPerContainer_s);
if(isnumber(frequencyStr)){
	frequency = atof(frequencyStr);
}

allocationFactorStr = "";
wasteTypeAllocationDict = dict("float");

includeERF = "No";
includeFRF = "No";

if(find(feesToCharge_s, "ERF") > -1){
	includeERF = "Yes";
}
if(find(feesToCharge_s, "FRF") > -1){
	includeFRF = "Yes";
}

/*
	put(guardrailInputDict, "includeERF", includeERF_quote);
			put(guardrailInputDict, "includeFRF", includeFRF_quote);
			put(guardrailInputDict, "erfOnFrfRate", "1");
			put(guardrailInputDict, "siteName", siteName); 
			put(guardrailInputDict, "wasteType", wasteType); 
			put(guardrailInputDict, "division", division_quote); 
			put(guardrailInputDict, "costToServeMonth", get(outputDict, "costToServeMonth")); 
			put(guardrailInputDict, "billingType", billingType);
			put(guardrailInputDict, "driverCost", get(outputDict, "driverCost")); 
			put(guardrailInputDict, "truckCost", get(outputDict, "truckCost"));
			put(guardrailInputDict, "truckROA", get(outputDict, "truckROA"));
			put(guardrailInputDict, "commission", get(outputDict, "commission"));
			put(guardrailInputDict, "tonsPerHaul", getconfigattrvalue(line._parent_doc_number, "estTonsHaul_l"));
			put(guardrailInputDict, "haulsPerMonth", estHaulsPerMonthStr);
			put(guardrailInputDict, "accountType", accountType_quote);
			put(guardrailInputDict, "commissionRate", ".75");
			put(guardrailInputDict, "flatRateCommission", "15");
			put(guardrailInputDict, "competitorFactor", string(competitorFactor));
			put(guardrailInputDict, "priceType", priceType);
			put(guardrailInputDict, "wasteCategory", wasteCategory);
			put(guardrailInputDict, "truckDepreciation", get(outputDict, "truckDepreciation"));
			put(guardrailInputDict, "yardsPerMonth", string(yardsPerMonth));
			put(guardrailInputDict, "salesActivity_quote", salesActivity_quote);
			put(guardrailInputDict, "cr_new_business", "1"); //This is new business flag
			put(guardrailInputDict, "initialTerm_quote", initialTerm_quote);
			put(guardrailInputDict, "customer_zip", _quote_process_siteAddress_quote_zip);
*/

//The above guardrailInoutDict in the comment is the one that should be sent as a parameter to calculateGuardrails util. The above is an extract from pre-pricing formulas. Instead of creating config attributes to read the commerce values, some of the commerce attributes have been directly queried here as below. Some are read into config attributes

if(allocationFactorString_s <> ""){
	allocationFactorArr = split(allocationFactorStr,"@_@");
	for eachEle in allocationFactorArr{
		wasteTypeAllocation = split(eachEle,"#_#" );
		if(sizeofarray(wasteTypeAllocation) == 2){
			put(wasteTypeAllocationDict, wasteTypeAllocation[0],atof(wasteTypeAllocation[1]));
		}
	}
}
if(containskey(wasteTypeAllocationDict, wasteType)){
	allocationFactor = get(wasteTypeAllocationDict, wasteType);
}
stringDict = dict("string");

//Populate the string dictionary required as input for calling the small container pricing util function
put(stringDict, "wasteCategory", wasteCategory);
put(stringDict, "routeTypeDerived", routeTypeDervied);
put(stringDict, "containerQuantity", string(quantity));
put(stringDict, "containerSize", containerSize);
put(stringDict, "frequency", string(frequency));
put(stringDict, "compactor", string(compactor));
put(stringDict, "accountType", accountType); 
put(stringDict, "division_quote", division_config);
put(stringDict, "region_quote", region); 
put(stringDict, "partNumber", partNumber); 
put(stringDict, "initialTerm_quote", initialTerm_l); 
put(stringDict, "disposalCostPerTon", string(disposalSiteCostCommercial_config));//Derived from the corresponding commerce attribute
//dsp_xfer_price_per_ton is not required in config because this is not used for guardrail calculations, only for "Financial summary" table
put(stringDict, "allocationFactor", string(allocationFactor)); 
put(stringDict, "industry", industry);
put(stringDict, "additionalSmallContainerSiteTime", string(additionalSiteFactorTime_s));
put(stringDict, "lock", lock);
put(stringDict, "isEnclosure", string(isEnclosure));
put(stringDict, "rolloutFeet", rolloutFeet);
put(stringDict, "casters", string(casters));
put(stringDict, "scoutRoute", string(scoutRoute));
put(stringDict, "onsiteTimeInMins", string(onsiteTimeInMins)); 
put(stringDict, "isCustomerOwned", string(isCustomerOwned));
put(stringDict, "newCustomerConfig", "1"); //0 - Existing customer; 1 - New Customer			

if(disposalSiteCostCommercial_config == 0.0){
	//Get disposal cost for site
	disposalSiteCostCommercialDict = util.getDispSiteAndCostFromZip(infoProDivision, division_config, string(zipcode), "Solid Waste", "", false);

	disposalSiteCostCommercialStr = "";
	disposalSiteCostCommercial = 0.0;
	if(containskey(disposalSiteCostCommercialDict, "disposalCost")){
		disposalSiteCostCommercialStr = get(disposalSiteCostCommercialDict, "disposalCost");
	}
	if(disposalSiteCostCommercialStr <> "" AND isnumber(disposalSiteCostCommercialStr)){
		disposalSiteCostCommercial = atof(disposalSiteCostCommercialStr);
	}
	put(stringDict, "disposalCostPerTon", string(disposalSiteCostCommercial));
}

//put(stringDict, "siteName", siteName);
if(DEBUG){
	print "--Current input to small container util from rec rule--"; print stringDict;
}

//Call the util function smallContainerPricing to get the Monthly Yards and Current Offering
outputDict = util.smallContainerPricing(stringDict);

if(DEBUG){
	print "Current Small Container output";
	print outputDict;
}
	
//The following are commerce attributes
put(guardrailInputDict_current_offering, "includeERF", includeERF);
put(guardrailInputDict_current_offering, "includeFRF", includeFRF);
put(guardrailInputDict_current_offering, "competitorFactor", competitorFactor_s);
put(guardrailInputDict_current_offering, "customer_zip", string(zipcode));
put(guardrailInputDict_current_offering, "division", division_config);
put(guardrailInputDict_current_offering, "initialTerm_quote", initialTerm_l);
put(guardrailInputDict_current_offering, "salesActivity_quote", salesActivity_config);
put(guardrailInputDict_current_offering, "accountType", accountType);
put(guardrailInputDict_current_offering, "segment", segmentForContainer);
put(guardrailInputDict_current_offering, "industry", industry);
put(guardrailInputDict_current_offering, "infoProDivNumber", infoProDivision);

//The following are static values to be added to guardrailInputDict_current_offering
put(guardrailInputDict_current_offering, "commissionRate", ".75");
put(guardrailInputDict_current_offering, "flatRateCommission", "15");
put(guardrailInputDict_current_offering, "cr_new_business", "1");
put(guardrailInputDict_current_offering, "priceType", "Containers");
put(guardrailInputDict_current_offering, "erfOnFrfRate", "1");
put(guardrailInputDict_current_offering, "wasteType", wasteType);
put(guardrailInputDict_current_offering, "wasteCategory", wasteCategory);
put(guardrailInputDict_current_offering, "newCustomerConfig", "1");
put(guardrailInputDict_current_offering, "quantity", string(quantity));

//The following must be taken from output dictionary which is a result from smallContainerPricing util
put(guardrailInputDict_current_offering, "costToServeMonth", get(outputDict, "costToServeMonth"));
put(guardrailInputDict_current_offering, "driverCost", get(outputDict, "driverCost")); 
put(guardrailInputDict_current_offering, "truckCost", get(outputDict, "truckCost"));
put(guardrailInputDict_current_offering, "truckROA", get(outputDict, "truckROA"));
put(guardrailInputDict_current_offering, "commission", get(outputDict, "commission"));
put(guardrailInputDict_current_offering, "truckDepreciation", get(outputDict, "truckDepreciation"));

//The following from smallContainerutil must be rounded
monthlyYards = 0.0;
currentMonthlyYards = 0.0;
currentYardsPerMonth = 0.0;
if(isnumber(get(outputDict,"yardsPerMonth"))){
	currentMonthlyYards = round(atof(get(outputDict,"yardsPerMonth")), 2);
	currentYardsPerMonth = round(atof(get(outputDict,"yardsPerMonth")), 4);
}
//Alternate Service offerings
yardsPerMonth = 0.0;


put(guardrailInputDict_current_offering, "yardsPerMonth", string(currentYardsPerMonth));

//FeesToCharge dummy variable for config
put(guardrailInputDict_current_offering, "feesToCharge", "Config");

if(DEBUG){
	print "--Current Guardrails input dict--"; print guardrailInputDict_current_offering;
}
guardrailOutputDict = util.calculateGuardrails(guardrailInputDict_current_offering);
if(DEBUG){
	print "--Current Guardrails output dict--"; print guardrailOutputDict;
}

currentOfferingTargetPrice = 0.0;
if(isnumber(get(guardrailOutputDict,"haulTarget"))){
	currentOfferingTargetPrice = round(atof(get(guardrailOutputDict,"haulTarget")), 2);
}
if(DEBUG){
	print "currentOfferingTargetPrice before rounding --"; print currentOfferingTargetPrice;
}
currentOfferingTargetPrice = ceil(currentOfferingTargetPrice/roundingFactor) * roundingFactor;

if(DEBUG){
	print "--roundingFactor--"; print roundingFactor;
	print "currentOfferingTargetPrice after rounding --"; print currentOfferingTargetPrice;
}
 
//Build HTML table	
retStr = "<style type='text/css'>.altServiceOfferings tr:nth-child(even){background: #C0D9D9;} .altServiceOfferings td {padding: 5px;}</style>";

retStr = retStr + "<table width='100%' class='rs_table_style' cellpadding=\"3\" cellspacing=\"0\"  ><tbody> " 
		//Header Row
		+ "<tr class='rs_table_style'>" + "<td width='15%' align='center' style='border-bottom: thin solid #3D9BCB;color: #000059;'>" + "Offering" + COL_END + "<td width='10%' align='center' style='border-bottom: thin solid #3D9BCB;color: #000059;'>" + "Qty" + COL_END + "<td width='10%'  align='center' style='border-bottom: thin solid #3D9BCB;color: #000059;'>" + "Size" + COL_END + "<td width='20%' align='center' style='border-bottom: thin solid #3D9BCB;color: #000059;'>" + "Lifts Per Container" +COL_END + "<td width='10%' align='center' style='border-bottom: thin solid #3D9BCB;color: #000059;'>" + "Monthly Yards" + COL_END + "<td align='right' width='15%' style='border-bottom: thin solid #3D9BCB;color: #000059;'>" + "Target Price*" + COL_END + "<td align='right' width='15%' style='border-bottom: thin solid #3D9BCB;color: #000059;'>" + "Difference" + COL_END +  ROW_END	;
		

		
//Query the Alt_Service_Offering table to get the Alternate Offerings for the quantity and Original Frequency
altServiceOfferingsResults = bmql("SELECT Alternate1Quantity, Alternate1Size, Alternate1Frequency, Alternate2Quantity, Alternate2Size, Alternate2Frequency FROM Alt_Service_Offering WHERE OriginalQuantity = $qty AND OriginalSize = $containerSizeVal AND OriginalFrequency = $frequency");
print "qty=="+string(qty);
print "containerSizeVal=="+string(containerSizeVal);
print "frequency=="+string(frequency);
print "altServiceOfferingsResults";
print altServiceOfferingsResults;
alternateRetstr = "";

for rec in altServiceOfferingsResults{
	alt1Qty = getint(rec, "Alternate1Quantity");
	alt1Size = getFloat(rec, "Alternate1Size");
	alt1Frequency = get(rec, "Alternate1Frequency");
	alt2Qty = getint(rec, "Alternate2Quantity");
	alt2Size = getFloat(rec, "Alternate2Size");
	alt2Frequency = get(rec, "Alternate2Frequency");
	
	conversionType = "freq_to_liftsPerCont";
	alt1LiftsPerCont = util.getServiceConversion(conversionType, alt1Frequency);
	alt2LiftsPerCont = util.getServiceConversion(conversionType, alt2Frequency);
	
	if(alt1Qty <> 0 AND alt1Size <> 0.0){	
		
		/*if(containskey(allocationFactorDict, wasteType)){
			allocationFactor = get(allocationFactorDict, wasteType);
		}*/
			
		//Get the target stretch price from small container pricing for alternate site 1
		put(stringDict, "containerSize", string(alt1Size));
		put(stringDict, "frequency", alt1Frequency);
		put(stringDict, "containerQuantity", string(alt1Qty));
	
		outputDict = util.smallContainerPricing(stringDict);
		
	//The following are commerce attributes
	put(guardrailInputDict_alt_offering1, "includeERF", includeERF);
	put(guardrailInputDict_alt_offering1, "includeFRF", includeFRF);
	put(guardrailInputDict_alt_offering1, "competitorFactor", competitorFactor_s);
	put(guardrailInputDict_alt_offering1, "customer_zip", string(zipcode));
	put(guardrailInputDict_alt_offering1, "division", division_config);
	put(guardrailInputDict_alt_offering1, "initialTerm_quote", initialTerm_l);
	put(guardrailInputDict_alt_offering1, "salesActivity_quote", salesActivity_config);
	put(guardrailInputDict_alt_offering1, "accountType", accountType);
	put(guardrailInputDict_alt_offering1, "segment", segmentForContainer);
	put(guardrailInputDict_alt_offering1, "industry", industry);
	put(guardrailInputDict_alt_offering1, "infoProDivNumber", infoProDivision);
	
	//The following are static values to be added to guardrailInputDict_current_offering
	put(guardrailInputDict_alt_offering1, "commissionRate", ".75");
	put(guardrailInputDict_alt_offering1, "flatRateCommission", "15");
	put(guardrailInputDict_alt_offering1, "cr_new_business", "1");
	put(guardrailInputDict_alt_offering1, "priceType", "Containers");
	put(guardrailInputDict_alt_offering1, "erfOnFrfRate", "1");
	put(guardrailInputDict_alt_offering1, "wasteType", wasteType);
	put(guardrailInputDict_alt_offering1, "wasteCategory", wasteCategory);
	put(guardrailInputDict_alt_offering1, "newCustomerConfig", "1");
	put(guardrailInputDict_alt_offering1, "quantity", string(alt1Qty));

	//The following must be taken from output diction which is a result from smallContainerPricing util
	put(guardrailInputDict_alt_offering1, "costToServeMonth", get(outputDict, "costToServeMonth"));
	put(guardrailInputDict_alt_offering1, "driverCost", get(outputDict, "driverCost")); 
	put(guardrailInputDict_alt_offering1, "truckCost", get(outputDict, "truckCost"));
	put(guardrailInputDict_alt_offering1, "truckROA", get(outputDict, "truckROA"));
	put(guardrailInputDict_alt_offering1, "commission", get(outputDict, "commission"));
	put(guardrailInputDict_alt_offering1, "truckDepreciation", get(outputDict, "truckDepreciation"));
	if(DEBUG){
		print "Alternate Service Offering 1 - Small Container Input";
		print stringDict;
		print "Alternate Service Offering 1 - Small Container Output";
		print outputDict;
	}	
		if(isnumber(get(outputDict,"yardsPerMonth"))){
			monthlyYards = round(atof(get(outputDict,"yardsPerMonth")), 2);
		}
		yardsPerMonth = 0.0;
		yardsPerMonthStr = get(outputDict, "yardsPerMonth");
		if(isnumber(yardsPerMonthStr)){
			yardsPerMonth = atof(yardsPerMonthStr);
			yardsPerMonth = round(yardsPerMonth, 4);
		}
		
		//FeesToCharge dummy for config
		put(guardrailInputDict_alt_offering1, "feesToCharge", "Config");
		put(guardrailInputDict_alt_offering1, "yardsPerMonth", string(yardsPerMonth));
		
		guardrailOutputDict = util.calculateGuardrails(guardrailInputDict_alt_offering1);
		
		if(isnumber(get(guardrailOutputDict,"haulTarget"))){
			targetPrice = round(atof(get(guardrailOutputDict,"haulTarget")),2);
			targetPrice = ceil(targetPrice/roundingFactor) * roundingFactor;
			difference = formatascurrency((currentOfferingTargetPrice) - (targetPrice));
		}
		
		alternateRetstr = alternateRetstr 
			//Alternate 1 Service Offering Row
			+ ROW_START + "<td width='15%' align='center'>" + "Alternate 1" + COL_END + "<td width='10%' align='center'>" + string(alt1Qty) + COL_END + "<td width='10%'  align='center'>" + string(alt1Size) + COL_END + "<td width='20%' align='center'>" + alt1LiftsPerCont + COL_END +  "<td width='10%' align='center'>"  + string(monthlyYards) + COL_END + COL_START_CURRENCY + formatascurrency(targetPrice) + COL_END + COL_START_CURRENCY + difference + COL_END + ROW_END;
	
	}
	if(alt2Qty <> 0 AND alt2Size <> 0.0){
		//Get the target stretch price from small container pricing for alternate site 2
		put(stringDict, "containerSize", string(alt2Size));		
		put(stringDict, "frequency", alt2Frequency);
		put(stringDict, "containerQuantity", string(alt2Qty));
		
		outputDict = util.smallContainerPricing(stringDict);
		
		if(DEBUG){
			print "Alternate Service Offering 2 - Small Container Input";
			print stringDict;
			print "Alternate Service Offering 2 - Small Container Output";
			print outputDict;
		}	
		
		//The following are commerce attributes
		
		put(guardrailInputDict_alt_offering2, "includeERF", includeERF);
		put(guardrailInputDict_alt_offering2, "includeFRF", includeFRF);
		put(guardrailInputDict_alt_offering2, "competitorFactor", competitorFactor_s);
		put(guardrailInputDict_alt_offering2, "customer_zip", string(zipcode));
		put(guardrailInputDict_alt_offering2, "division", division_config);
		put(guardrailInputDict_alt_offering2, "initialTerm_quote", initialTerm_l);
		put(guardrailInputDict_alt_offering2, "salesActivity_quote", salesActivity_config);
		put(guardrailInputDict_alt_offering2, "accountType", accountType);
		put(guardrailInputDict_alt_offering2, "segment", segmentForContainer);
		put(guardrailInputDict_alt_offering2, "industry", industry);
		put(guardrailInputDict_alt_offering2, "infoProDivNumber", infoProDivision);
		
		//The following are static values to be added to guardrailInputDict_current_offering
		put(guardrailInputDict_alt_offering2, "commissionRate", ".75");
		put(guardrailInputDict_alt_offering2, "flatRateCommission", "15");
		put(guardrailInputDict_alt_offering2, "cr_new_business", "1");
		put(guardrailInputDict_alt_offering2, "priceType", "Containers");
		put(guardrailInputDict_alt_offering2, "erfOnFrfRate", "1");
		put(guardrailInputDict_alt_offering2, "wasteType", wasteType);
		put(guardrailInputDict_alt_offering2, "wasteCategory", wasteCategory);
		put(guardrailInputDict_alt_offering2, "newCustomerConfig", "1");
		put(guardrailInputDict_alt_offering2, "quantity", string(alt2Qty));
		
		//The following must be taken from output diction which is a result from smallContainerPricing util
		put(guardrailInputDict_alt_offering2, "costToServeMonth", get(outputDict, "costToServeMonth"));
		put(guardrailInputDict_alt_offering2, "driverCost", get(outputDict, "driverCost")); 
		put(guardrailInputDict_alt_offering2, "truckCost", get(outputDict, "truckCost"));
		put(guardrailInputDict_alt_offering2, "truckROA", get(outputDict, "truckROA"));
		put(guardrailInputDict_alt_offering2, "commission", get(outputDict, "commission"));
		put(guardrailInputDict_alt_offering2, "truckDepreciation", get(outputDict, "truckDepreciation"));
		
		if(isnumber(get(outputDict,"yardsPerMonth"))){
			monthlyYards = round(atof(get(outputDict,"yardsPerMonth")), 2);
		}
		put(outputDict, "priceType", "Containers");
		
		yardsPerMonth = 0.0;
		yardsPerMonthStr = get(outputDict, "yardsPerMonth");
		if(isnumber(yardsPerMonthStr)){
			yardsPerMonth = atof(yardsPerMonthStr);
			yardsPerMonth = round(yardsPerMonth, 4);
		}
		put(guardrailInputDict_alt_offering2, "yardsPerMonth", string(yardsPerMonth));
		
		//feesToCharge dummy for config
		put(guardrailInputDict_alt_offering2, "feesToCharge", "Config");
		
		guardrailOutputDict = util.calculateGuardrails(guardrailInputDict_alt_offering2);
		
		if(isnumber(get(guardrailOutputDict,"haulTarget"))){
			targetPrice = round(atof(get(guardrailOutputDict,"haulTarget")),2);
			targetPrice = ceil(targetPrice/roundingFactor) * roundingFactor;
			difference = formatascurrency((currentOfferingTargetPrice) - (targetPrice));
		}
		
		alternateRetstr = alternateRetstr
			//Alternate 2 Service Offering Row
			+ ROW_START + "<td width='15%' align='center'>" + "Alternate 2" + COL_END + "<td width='10%' align='center'>" + string(alt2Qty) + COL_END + "<td width='10%'  align='center'>" + string(alt2Size) + COL_END + "<td width='20%' align='center'>"  + alt2LiftsPerCont + COL_END + "<td width='10%' align='center'>"  + string(monthlyYards) + COL_END + COL_START_CURRENCY + formatascurrency(targetPrice )+ COL_END + COL_START_CURRENCY + difference + COL_END + ROW_END;	
	}
		
}
if(alternateRetstr == ""){
	retStr = retStr + "<tr><td colspan='7' align='center'>No Alternate Service Levels available for current selection</td></tr>";	
}
else{
		//Current Offering Row
	retStr = retStr + ROW_START + "<td width='15%' align='center'>" + "Current" + COL_END + "<td width='10%' align='center'>" + string(qty) + COL_END + "<td width='10%'  align='center'>" + containerSize + COL_END + "<td width='20%' align='center'>"  + liftsPerContainer_s + COL_END + "<td width='10%' align='center'>"  + string(currentMonthlyYards) + COL_END + COL_START_CURRENCY + formatascurrency(currentOfferingTargetPrice )+ COL_END + COL_START  + COL_END + ROW_END + alternateRetstr;
}

retStr = retStr + "</tbody></table>";

return retStr;