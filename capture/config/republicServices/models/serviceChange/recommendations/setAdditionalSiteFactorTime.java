//Test with: 11758326
//Get Site Factors for each of the options selected in Small Container configurator
//Accumulate each of the site factors selected
//Query Site Factors table
//Iterate on each of the site factor & sum 
totalSiteFactorTime = 0.0;
rollOutSequence = 0;
rolloutFactor = 0.0;
rollOutFactorExtraFeet = 0.0;
DELIM = "@_@";
enclosureFactor = 0.0;
lockFactor = 0.0;
allocationFactor = 1.0;
allocationDaysDict = dict("string[]"); //Key: waste type, value: allocation days
allocationFactorDict = dict("float");
wasteCategory = "";
accountNumber = "";
lOBCategoryDerived = "Commercial"; //This has to be set in future based on Small / Large container selection
onsiteTimeInMins = 0.0; //This should be coming from Account_status table
debug = false; //REMEMBER TO CHANGE THIS TO FALSE AS IT DEFAULTS STATIC VALUES FOR COMMERCE ATTRIBUTES

siteFactorsRS = bmql("SELECT SiteCharacteristic, Factor FROM Site_Char_Factors");

for eachRecord in siteFactorsRS{
	characteristic = get(eachRecord, "SiteCharacteristic");
	factor = getFloat(eachRecord, "Factor");
	
	//ADd Enclosure site factor
	if(isEnclosure AND characteristic == "Enclosure"){
		totalSiteFactorTime = totalSiteFactorTime + factor;
		enclosureFactor = factor;
	}
	
	//ADd Casters site factor
	if(casters AND rolloutFeet <> "" AND rolloutFeet <> "None"){
		if(characteristic == "Roll Out"){
			rolloutFactor = factor;
		}elif(characteristic == "Roll Out Extra Feet"){
			rollOutFactorExtraFeet = factor;
		}
	}
	//ADd Lock site factor
	if(lock <> "" AND characteristic == "Lock"){
		totalSiteFactorTime = totalSiteFactorTime + factor;
		lockFactor = factor;
	}
}

//Rollout
if(casters AND rolloutFeet <> "" AND rolloutFeet <> "None"){
	rolloutSequenceRS = bmql("SELECT sequence FROM rollout_factors WHERE value = $rolloutFeet");	
	for eachRecord in rolloutSequenceRS{
		rollOutSequence = getInt(eachRecord, "sequence");
	}
	totalSiteFactorTime = totalSiteFactorTime + (rolloutFactor + ((rollOutSequence - 1) * rollOutFactorExtraFeet));
}

if(scoutRoute){ 
	//Get Account Specific data from Account_Status table
	routeTypeDervied = "";
	Lift_Days = "";
	compactorStr = "0";
	frequency = "";
	
	//Get commerce attributes 
	division = "";
	pickupDaysStr = "";
	commerceRS = bmql("SELECT smallContainerTentativePickupDaysSelected_quote, division_quote, _customer_id FROM commerce.quote_process");
	
	for eachRecord in commerceRS{
		pickupDaysStr = get(eachRecord, "smallContainerTentativePickupDaysSelected_quote");
		division = get(eachRecord, "division_quote");
		accountNumber = get(eachRecord, "_customer_id");
	}
	if(debug){
		division = "4800";
		accountNumber = "4800-8336";
	}
	
	
	accountStatusRS = bmql("SELECT Container_Cd, Lift_Days, has_Compactor  FROM Account_Status WHERE infopro_acct_nbr = $accountNumber AND Container_Grp_Nbr = $containerGroup_config");
	
	if(debug){
		print accountStatusRS;
	}
	for eachRecord in accountStatusRS{	
		routeTypeDervied = get(eachRecord, "Container_Cd");
		Lift_Days = get(eachRecord, "Lift_Days");
		compactorStr = get(eachRecord, "has_Compactor");
	}
	
	if(wasteType_readOnly == "Solid Waste"){
		wasteCategory = "Solid Waste";
	}elif(wasteType_readOnly == "All in One - Single stream"){ //correct this value
		wasteCategory = "Recycling";
	}
	
	wasteType = wasteType_readOnly;
	if(wasteType_sc <> "No Change"){
		wasteType = wasteType_sc;
	}
	quantity = quantity_readOnly;
	if(quantity_sc <> quantity_readOnly){
		quantity = quantity_sc;
	}
	containerSize = "0";
	
	if(containerSize_sc <> "No Change"){
		if(isnumber(containerSize_sc)){
			containerSize = string(atoi(containerSize_sc));
		}
	}else{
		if(isnumber(containerSize_readOnly)){
			containerSize = string(Integer(atof(containerSize_readOnly)));	//Could be better, change it later to better conversion
		}
	}
	
	liftsPerWeek = liftsPerContainer_sc;
	if(liftsPerContainer_sc == "No Change"){
		liftsPerWeekStr = Lift_Days;
		//Check if pick up days were selected earlier
		liftsPerWeekStrArr = split(liftsPerWeekStr, "");
		daysArray = string[];
		for eachDay in liftsPerWeekStrArr{
			if(eachDay <> "" AND eachDay <> "-"){
				append(daysArray, eachDay);
			}
		}
		if(sizeofarray(daysArray) > 0){
			liftsPerWeek = join(daysArray, ",");
		}else{
			liftsPerWeek = liftsPerContainer_readOnly;
			frequency = util.getServiceConversion("liftsPerCont_to_freq", liftsPerWeek);
		}
	}
	
	if(debug){
		print pickupDaysStr;
	}
	wasteTypesArray = string[];
	allocationFactorArray = split(pickupDaysStr, DELIM);
	
	for each in allocationFactorArray{
		if(each <> ""){
			eachArr = split(each, ":");
			wasteType = "";
			pickupDays = "";
			if(sizeofarray(eachArr) >=2){
				wasteType = eachArr[0];
				pickupDays = eachArr[1];
			}
			if(findinarray(wasteTypesArray, wasteType) == -1){
				append(wasteTypesArray, wasteType);
			}
			tempArray = string[];
			if(containskey(allocationDaysDict, wasteType)){
				tempArray = get(allocationDaysDict, wasteType);
			}
			append(tempArray, pickupDays);
			put(allocationDaysDict, wasteType, tempArray);
		}
	}
	//End get commerce values for existing containers
	//Add current selections 
	
	if(findinarray(wasteTypesArray, wasteCategory) == -1){
		append(wasteTypesArray, wasteCategory);
	}	
	tempArray = string[];
	if(pickupDaysStr <> ""){
		if(containskey(allocationDaysDict, wasteCategory)){
			tempArray = get(allocationDaysDict, wasteCategory);
		}
		append(tempArray, pickupDaysStr);
	}else{
		if(liftsPerContainer_sc == "No Change"){
			frequency = util.getServiceConversion("liftsPerCont_to_freq", liftsPerContainer_readOnly);
		}else{
			frequency = util.getServiceConversion("liftsPerCont_to_freq", liftsPerContainer_sc);	
		}
		append(tempArray, frequency);
	}
	put(allocationDaysDict, wasteCategory, tempArray);
	
	for eachWasteType in wasteTypesArray{
		if(containskey(allocationDaysDict, eachWasteType)){
			//Get allocation days for current waste type - expect repetition in values as they come from different model configurations
			//allocationDays can either be week name of the day or #lifts per container value
			allocationsDaysArray = get(allocationDaysDict, eachWasteType);
			uniquePickupDaysArray = string[];
			uniquePickUpsArray = float[];
			pickUpDaysArray = string[];
			i = 0;
			
			//pickup days in config is a multi select menu, split the values to get each week name and count of pickups scheduled
			for eachDay in allocationsDaysArray{
				//If eachDay is name of the week add it to pickUpDaysArray
				if(eachDay <> "" AND not(isnumber(eachDay))){
					splitDaysArray = split(eachDay, ",");
					for eachItem in splitDaysArray{
						append(pickUpDaysArray, eachItem);	//get all tentative pick up days selected
					}
				}elif(isnumber(eachDay)){
					uniquePickUpsArray[i] = atof(eachDay); //get frequency of pick ups selected
					i = i + 1;
				}
			}
			
			//Initially set totalTrips to the # of week names selected in configurator for all model configurations
			totalTrips = sizeofarray(pickUpDaysArray);
			
			//Get all unique week names to arrive at best optimum pickup options
			for eachItem in pickUpDaysArray{
				if(findinarray(uniquePickupDaysArray, eachItem) == -1){
					append(uniquePickupDaysArray, eachItem);	
				}
			}
			
			//Get max frequency from uniquePickUpsArray - consider it as # unique trips required - this best possible pick up to coincide all pick ups 
			uniqueTrips = 0.0;
			if(sizeofarray(uniquePickUpsArray) > 0){
				uniqueTrips = max(uniquePickUpsArray);
				//If pickup days selected are greater than max # of lifts per week - set # unique trips required as total # of pickup days selected
				//Minimum number of unique trips required will be the highest pick up frequency of all pick ups
				if(totalTrips > uniqueTrips){ 
					uniqueTrips = totalTrips;
				}
			}else{
				//Initialize uniqueTrips with unique pick up days - to start with
				uniqueTrips = sizeofarray(uniquePickupDaysArray) * 1.0;
			}
			
			//Update totalTrips adding # of lifts per week to existing week days count
			//This is another container group where pick up days were not selected
			for eachPickUp in uniquePickUpsArray{	
				totalTrips = totalTrips + eachPickUp;
			}
			//Calculate allocation factor 
			if(totalTrips > 0){
				allocationFactor = 	uniqueTrips/ totalTrips;
			}
			put(allocationFactorDict, eachWasteType, allocationFactor);
		}	
	}//Iterate on all waste types selected
	
	thisWasteAllocationFactor = get(allocationFactorDict, wasteCategory);
	tripsPerMonth = 0.0;
	if(isnumber(frequency)){
		tripsPerMonth = atof(frequency) * (52.0 / 12.0) * thisWasteAllocationFactor; //52 weeks / 12 months
	}
	
	liftsPerMonth = quantity * tripsPerMonth;
	if(debug){
		print "tripsPerMonth=="+string(tripsPerMonth);
		print "liftsPerMonth=="+string(liftsPerMonth);
	}
	
	//Minutes per lift
	minutesPerLift = 0.0;
	divisionKPIRecordSet = bmql("SELECT minutes_per_lift FROM tbl_division_kpi WHERE div_nbr = $division AND waste_type = $wasteCategory");
	for eachRecord in divisionKPIRecordSet{
		minutesPerLift	= getFloat(eachRecord, "minutes_per_lift");
	}
	
	compactor_additional_site_time = 0.0;
	if(debug){
		print "containerSize=="+containerSize;
	}
	
	partsRecordSet = bmql("SELECT part_number, custom_field5  FROM _parts WHERE custom_field9 = $routeTypeDervied AND custom_field10 = $containerSize AND custom_field11 = $compactorStr AND custom_field12 = $lOBCategoryDerived");  
	for eachPart in partsRecordSet{
		compactor_additional_site_time = getFloat(eachPart, "custom_field5");
	}
	if(debug){
		print partsRecordSet;
	}
	lockHasp = 0.0	;
	if(lock == "Lock & Hasp"){
		lockHasp = lockFactor;
	}
	siteTime = (tripsPerMonth * minutesPerLift) + ((liftsPerMonth - tripsPerMonth) * minutesPerLift * 0.5)
				+ (tripsPerMonth * (lockHasp + enclosureFactor + 
				(quantity * (rollOutSequence) * (rolloutFactor + ((rollOutSequence - 1) * rollOutFactorExtraFeet))) + 
				//customer_site_other_obs * site_fac_other +
				onsiteTimeInMins + compactor_additional_site_time));
	scoutRouteTime = siteTime * 0.5;
	totalSiteFactorTime = totalSiteFactorTime + scoutRouteTime;
}
return totalSiteFactorTime;