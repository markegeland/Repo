/*
================================================================================
Name:   calculateFloorValue
Author:   Zach Schlieder
Create date:  11/6/13
Description:  Calculates the Floor price for a given configuration based on config and commerce attributes as well as data tables. Built in a util to allow for use in Config or Commerce.
        
Input:   	stringDict: String Dictionary - Contains values of Config and Commerce attributes used in calculations and table calls
                    
Output:  	String Dictionary - Contains attribute name and value pairs for use in Config or Commerce

Updates:	11/13/13 - Zach Schlieder - Update divisionKPI table call to handle new data format
									- Added container size group table call to determine correct average price per yard
									- Added compactorFactor logic, Updated yardsPerMonth calculation to include compactorFactor
									- Changed output from float to string dictionary to support passing container size group
			11/18/13 - Zach Schlieder - Updated tripMinutes calculation (tripMinutes calc should be: tripMinutesPerMonth = tripsPerMonth * minutesPerLift;  currently the calc is using liftsPerMonth which is inflating the site time across the board.)
									- Updated commission calculation to divide result by 36
									- Updated avgPricePerYard calculation to use new pricePerYard table rather than divisionKPI table
									- Updated ROI calculation to include container quantity
			11/25/13 - Zach Schlieder - Updated tripsPerWeek - removed Frequency Factor for use with Wave 2 requirements
									- Updated assetCost calculation
    
=====================================================================================================
*/

//=============================== START - Variable Initialization ===============================//
//Default variables
returnDict = dict("string");
floorValue = 0.0; 

//Variables from Config attributes found in the input string dictionary
wasteCategory = get(stringDict, "wasteCategory");
industry = get(stringDict, "industry");
siteName = get(stringDict, "siteName");
wasteType = get(stringDict, "wasteType");
LOB = get(stringDict, "lOBCategoryDerived");
routeType = get(stringDict, "routeType");
//Get float variables from input dictionary in string form
containerQuantityStr = get(stringDict, "containerQuantity");
containerSizeStr = get(stringDict, "containerSize");
frequencyStr = get(stringDict, "frequency");
additionalSiteMinutesStr = get(stringDict, "onsiteTimeInMins");
compactor = get(stringDict, "compactor");

//Convert necessary variables from string to float for use in calculations
containerQuantity = 0.0;
containerSize = 0.0;
frequency = 0.0;
additionalSiteMinutes = 0.0;

if(isnumber(containerQuantityStr)){
	containerQuantity = atof(containerQuantityStr);
}
if(isnumber(containerSizeStr)){
	containerSize = atof(containerSizeStr);
}
if(isnumber(frequencyStr)){
	frequency = atof(frequencyStr);
}
if(isnumber(additionalSiteMinutesStr)){
	additionalSiteMinutes = atof(additionalSiteMinutesStr);
}


//Get variables from Commerce attributes found in the input string dictionary
division = get(stringDict, "division_quote");
region = get(stringDict, "region_quote");
partNumber = get(stringDict, "partNumber");

//Initialize string variables set by data table results
//containerSizeMapping
containerSizeGroup = "";
//divisionKPI table
laborCostPerMinuteStr = "";
containerMaintPerLiftStr = "";
truckCostPerMinuteStr = "";
poundsPerYardStr = "";
minutesPerDispTonStr = "";
minutesPerLiftStr = "";
truckHoursPerMonthStr = "";
//pricePerYard table
averagePricePerYardStr = "";
//industries table
containerStr = "";
weightsStr = "";
//disposalSiteCosts table
disposalCostPerTonStr = "";
//trucks table
truckAssetsStr = "";
truckDepreciationPerMonthStr = "";
//parts database
containerValueStr = "";
depreciationPerMonthStr = "";

//=============================== END - Variable Initialization ===============================//

//=============================== START - Table Lookups ===============================//
//Get values of all necessary values for calculations from data tables or parts database

	//=============================== START - Get compactor factor ===============================//
	compactorFactor = 1.0;
	if(compactor == "true"){
		compactorFactorStr = "";
		additionalConfigInfoRecordSet = bmql("SELECT value FROM miscConfigData WHERE attribute = 'compactorFactor'");
		
		for eachRecord in additionalConfigInfoRecordSet{
			compactorFactorStr = get(eachRecord, "value");
			break;	//Only one result is desired
		}
		
		if(isnumber(compactorFactorStr)){
			compactorFactor = atof(compactorFactorStr);
		}
	}
	put(returnDict, "compactorFactor", string(compactorFactor));
	
	//=============================== END - Get compactor factor ===============================//

	//=============================== START - Get container size ===============================//

	//containerQuantity and containerSize come from Config
	yardsPerMonth = containerQuantity * containerSize * frequency * compactorFactor * (52.0 / 12.0); //52 weeks / 12 months
	put(returnDict, "yardsPerMonth", string(yardsPerMonth));
	
	containerSizeRecordSet = bmql("SELECT sizeGroup FROM containerSizeMapping WHERE minTotalMonthlyYards <= $yardsPerMonth AND maxTotalMonthlyYards >= $yardsPerMonth");
	
	for eachRecord in containerSizeRecordSet{
		containerSizeGroup = get(eachRecord, "sizeGroup");
		break;	//Only one result is desired
	}
	put(returnDict, "containerSizeGroup", containerSizeGroup);
	
	//=============================== END - Get container size ===============================//

	//=============================== START - Lookups on the divisionKPI table ===============================//
	//Set the waste type for lookup on the divisionKPI table
	wasteTypeCode = "";
	if(wasteCategory == "Solid Waste"){
		wasteTypeCode = "SW";
	}
	elif(wasteCategory == "Recycling"){
		wasteTypeCode = "RE";
	}
	print "--wasteTypeCode--"; print wasteTypeCode;
	//Get all necessary values from the divisionKPI table based 
	divisionKPIRecordSet = bmql("SELECT laborCostPerMinute, containerMntPerLift, truckCostPerMinute, poundsPerYard, minutesPerDispTon, minutesPerLift, truckHoursPerMonth FROM divisionKPI WHERE divisionNumber = $division AND wasteType = $wasteTypeCode");

	for eachRecord in divisionKPIRecordSet{
		laborCostPerMinuteStr = get(eachRecord, "laborCostPerMinute");
		
		containerMaintPerLiftStr = get(eachRecord, "containerMntPerLift");
		truckCostPerMinuteStr = get(eachRecord, "truckCostPerMinute");
		poundsPerYardStr = get(eachRecord, "poundsPerYard");
		minutesPerDispTonStr = get(eachRecord, "minutesPerDispTon");
		minutesPerLiftStr = get(eachRecord, "minutesPerLift");
		truckHoursPerMonthStr = get(eachRecord, "truckHoursPerMonth");

		break;	//Only one result is desired
	}

		//laborCostPerMinute - Found in divisionKPI, based on the division number. Used in calculations of disposalTripCost
		laborCostPerMinute = 0.0;
		if(isnumber(laborCostPerMinuteStr)){	//Convert the table result to a float for use in calculations
			laborCostPerMinute = atof(laborCostPerMinuteStr);
		}
		put(returnDict, "laborCostPerMinute", string(laborCostPerMinute));
		
		//containerMaintPerLift - Found in divisionKPI, based on the division number. Used in calculations of assetCost
		containerMaintPerLift = 0.0;
		if(isnumber(containerMaintPerLiftStr)){	//Convert the table result to a float for use in calculations
			containerMaintPerLift = atof(containerMaintPerLiftStr);
		}
		put(returnDict, "containerMaintPerLift", string(containerMaintPerLift));

		//truckCostPerMinute - Found in divisionKPI, based on the division number and waste type. Used in calculations of disposalTripCost
		truckCostPerMinute = 0.0;
		if(isnumber(truckCostPerMinuteStr)){	//Convert the table result to a float for use in calculations
			truckCostPerMinute = atof(truckCostPerMinuteStr);
		}
		put(returnDict, "truckCostPerMinute", string(truckCostPerMinute));
		
		//divisionPoundsPerYard - Found in divisionKPI, based on the division number and waste type. Used in calculations of disposalProcessingCost
		divisionPoundsPerYard = 0.0;
		if(isnumber(poundsPerYardStr)){	//Convert the table result to a float for use in calculations
			divisionPoundsPerYard = atof(poundsPerYardStr);
		}
		put(returnDict, "divisionPoundsPerYard", string(divisionPoundsPerYard));
		
		//disposalMinutesPerTon - Found in divisionKPI, based on the division number and waste type. Used in calculations of disposalTripCost, assetCost, and ROI
		disposalMinutesPerTon = 0.0;
		if(isnumber(minutesPerDispTonStr)){	//Convert the table result to a float for use in calculations
			disposalMinutesPerTon = atof(minutesPerDispTonStr);
		}
		put(returnDict, "disposalMinutesPerTon", string(disposalMinutesPerTon));
		
		//minutesPerLift  - Found in divisionKPI, based on the division number and waste type. Used in calculations of operatingCost, assetCost, and ROI
		minutesPerLift  = 0.0;
		if(isnumber(minutesPerLiftStr)){	//Convert the table result to a float for use in calculations
			minutesPerLift  = atof(minutesPerLiftStr);
		}
		put(returnDict, "minutesPerLift", string(minutesPerLift));
		
		//truckHoursPerMonth  - Found in divisionKPI, based on the division number and waste type. Used in calculations of assetCost and ROI
		truckHoursPerMonth = 0.0;
		if(isnumber(truckHoursPerMonthStr)){	//Convert the table result to a float for use in calculations
			truckHoursPerMonth = atof(truckHoursPerMonthStr);
		}
		put(returnDict, "truckHoursPerMonth", string(truckHoursPerMonth));
	
	//=============================== END - Lookups on the divisionKPI table ===============================//
	
	//=============================== START - Lookups on the pricePerYard table ===============================//
	pricePerYardRecordSet = bmql("SELECT pricePerYard FROM pricePerYard WHERE division = $division AND wasteType = $wasteCategory AND containerSize = $containerSizeStr");
	
	for eachRecord in pricePerYardRecordSet{
		averagePricePerYardStr = get(eachRecord, "pricePerYard");
		break;	//Only one result is desired
	}	
	
		//averagePricePerYard - Found in pricePerYard, based on the division number, wasteType, and container size. Used in calculations of ROI
		averagePricePerYard = 0.0;
		if(isnumber(averagePricePerYardStr)){	//Convert the table result to a float for use in calculations
			averagePricePerYard = atof(averagePricePerYardStr);
		}
		put(returnDict, "averagePricePerYard", string(averagePricePerYard));
		
	//=============================== END - Lookups on the pricePerYard table ===============================//
	
	//=============================== START - Lookups on the industries table ===============================//
	industriesRecordSet = bmql("SELECT container, weightsSW, weightsRE FROM industries WHERE industryName = $industry");
	
	for eachRecord in industriesRecordSet{
		containerStr = get(eachRecord, "container");
		
		//Get the appropriate columns for Solid Waste
		if(wasteCategory == "Solid Waste"){
			weightsStr = get(eachRecord, "weightsSW");
		}
		
		//Get the appropriate columns for Recycling
		if(wasteCategory == "Recycling"){
			weightsStr = get(eachRecord, "weightsRE");
		}
		
		break;	//Only one result is desired
	}
	
		//containerFactor - Found in industries, based on the industry. Used in calculations of assetCost
		containerFactor = 0.0;
		if(isnumber(containerStr)){	//Convert the table result to a float for use in calculations
			containerFactor = atof(containerStr);
		}
		put(returnDict, "containerFactor", string(containerFactor));
		
		//weightIndustryFactor - Found in industries, based on the industry. Used in calculations of disposalProcessingCost and disposalTripCost
		weightIndustryFactor = 0.0;
		if(isnumber(weightsStr)){	//Convert the table result to a float for use in calculations
			weightIndustryFactor = atof(weightsStr);
		}
		put(returnDict, "weightIndustryFactor", string(weightIndustryFactor));
	
	//=============================== END - Lookups on the industries table ===============================//
	
	//=============================== START - Lookups on the disposalSiteCosts table ===============================//
	disposalSiteCostsRecordSet = bmql("SELECT cost FROM disposalSiteCosts WHERE siteName = $siteName AND wasteType = $wasteType AND LOB = $LOB");
	
	for eachRecord in disposalSiteCostsRecordSet{
		disposalCostPerTonStr = get(eachRecord, "cost");
	}
	
		//disposalCostPerTon - Found in disposalSiteCosts, based on the site name, waste type, and LOB category from Config. Used in calculations of disposalProcessingCost
		disposalCostPerTon = 0.0;
		if(isnumber(disposalCostPerTonStr)){	//Convert the table result to a float for use in calculations
			disposalCostPerTon = atof(disposalCostPerTonStr);
		}
		put(returnDict, "disposalCostPerTon", string(disposalCostPerTon));

	//=============================== END - Lookups on the disposalSiteCosts table ===============================//
	
	//=============================== START - Lookups on the trucks table ===============================//
		
		//truckAssets - Found in trucks, based on the route type from Config. Used in calculations of ROI
		truckAssetsRecordSet = bmql("SELECT allocation FROM trucks WHERE type = $routeType");
		
		for eachRecord in truckAssetsRecordSet{
			truckAssetsStr = get(eachRecord, "allocation");
		}
		truckAssets = 0.0;
		if(isnumber(truckAssetsStr)){	//Convert the table result to a float for use in calculations
			truckAssets = atof(truckAssetsStr);
		}
		put(returnDict, "truckAssets", string(truckAssets));
		
		//truckDepreciationPerMonth - Found in trucks, based on the route type from Config and region from Commerce. Used in calculations of assetCost
		truckDepreciationPerMonthRecordSet = bmql("SELECT depreciationPerMonth FROM trucks WHERE type = $routeType AND region = $region");
		
		for eachRecord in truckDepreciationPerMonthRecordSet{
			truckDepreciationPerMonthStr = get(eachRecord, "depreciationPerMonth");
		}
		truckDepreciationPerMonth = 0.0;
		if(isnumber(truckDepreciationPerMonthStr)){	//Convert the table result to a float for use in calculations
			truckDepreciationPerMonth = atof(truckDepreciationPerMonthStr);
		}
		put(returnDict, "truckDepreciationPerMonth", string(truckDepreciationPerMonth));
	
	//=============================== END - Lookups on the trucks table ===============================//
	
	//=============================== START - Lookups on the Parts database ===============================//
	partsRecordSet = bmql("SELECT custom_field2, custom_field4 FROM _parts WHERE part_number = $partNumber");	
	
	for eachRecord in partsRecordSet{
		containerValueStr = get(eachRecord, "custom_field2");
		depreciationPerMonthStr = get(eachRecord, "custom_field4");
	}
	
		//containerValue - Found in parts database, based on the partNumber (SKU). Used in calculations of ROI
		containerValue = 0.0;
		if(isnumber(containerValueStr)){	//Convert the table result to a float for use in calculations
			containerValue = atof(containerValueStr);
		}
		put(returnDict, "containerValue", string(containerValue));
		
		//containerDepreciationPerContainer - Found in parts database, based on the partNumber (SKU). Used in calculations of assetCost
		containerDepreciationPerContainer = 0.0;
		if(isnumber(depreciationPerMonthStr)){	//Convert the table result to a float for use in calculations
			containerDepreciationPerContainer = atof(depreciationPerMonthStr);
		}
		put(returnDict, "containerDepreciationPerContainer", string(containerDepreciationPerContainer));
	
	//=============================== START - Lookups on the Parts database ===============================//
	

//=============================== END - Table Lookups ===============================//

//=============================== START - Cost Calculation ===============================//
//Calculate the Cost for this configuration (Floor = Cost + ROI)

	//=============================== START - Disposal Processing Cost Calculation ===============================//
	//Calculate the first major component of the Cost (Cost = disposalProcessingCost + disposalTripCost + operatingCost + assetCost)
		
	//Customer Tons Per Month calculation - also used in calculation of operatingCost
	//divisionPoundsPerYard and weightIndustryFactor come from data tables
	customerTonsPerMonth = yardsPerMonth * divisionPoundsPerYard * weightIndustryFactor * (1.0 / 2000.0); //1 ton / 2000 yards
	put(returnDict, "customerTonsPerMonth", string(customerTonsPerMonth));
	
	//Final Disposal Processing Cost calculation
	//disposalCostPerTon comes from data table
	disposalProcessingCost = disposalCostPerTon * customerTonsPerMonth;
	print "--disposalProcessingCost--"; print disposalProcessingCost;
	put(returnDict, "disposalProcessingCost", string(disposalProcessingCost));
	//=============================== END - Disposal Processing Cost Calculation ===============================//
	
	
	//=============================== START - Disposal Trip Cost Calculation ===============================//
	//Calculate the second major component of the Cost (Cost = disposalProcessingCost + disposalTripCost + operatingCost + assetCost)
	
	//Total Cost Per Minute calculation - also used in operatingCost calculation
	//truckCostPerMinute and laborCostPerMinute come from data tables
	totalCostPerMinute = truckCostPerMinute + laborCostPerMinute;
	put(returnDict, "totalCostPerMinute", string(totalCostPerMinute));

	//disposalMinutesPerTon comes from data table. customerTonsPerMonth calculated earlier
	customerDisposalMinutes = disposalMinutesPerTon * customerTonsPerMonth;
	put(returnDict, "customerDisposalMinutes", string(customerDisposalMinutes));
	
	//Final Disposal Trip Cost calculation
	disposalTripCost = totalCostPerMinute * customerDisposalMinutes;
	print "--disposalTripCost--"; print disposalTripCost;
	put(returnDict, "disposalTripCost", string(disposalTripCost));
	//=============================== END - Disposal Trip Cost Calculation ===============================//
	
	//=============================== START - Operating Cost Calculation ===============================//
	//Calculate the third major component of the Cost (Cost = disposalProcessingCost + disposalTripCost + operatingCost + assetCost)

	//tripsPerWeek comes from config
	tripsPerMonth = frequency * (52.0 / 12.0); //52 weeks / 12 months
	put(returnDict, "tripsPerMonth", string(tripsPerMonth));

	//containerQuantity comes from config
	liftsPerMonth = containerQuantity * tripsPerMonth;
	put(returnDict, "liftsPerMonth", string(liftsPerMonth));

	//minutesPerLift comes from data table
	tripMinutesPerMonth = tripsPerMonth * minutesPerLift;
	put(returnDict, "tripMinutesPerMonth", string(tripMinutesPerMonth));

	secondLiftMinutesPerMonth = (liftsPerMonth - tripsPerMonth) * 0.5 * minutesPerLift;
	put(returnDict, "secondLiftMinutesPerMonth", string(secondLiftMinutesPerMonth));

	//additionalSiteMinutes comes from config
	additionalSiteMinutesPerMonth = additionalSiteMinutes * tripsPerMonth;

	siteTime = tripMinutesPerMonth + secondLiftMinutesPerMonth + additionalSiteMinutesPerMonth;
	put(returnDict, "siteTime", string(siteTime));
	
	//Final Operating Cost calculation
	//totalCostPerMinute calculated earlier
	operatingCost = siteTime * totalCostPerMinute;
	print "--siteTime--"; print siteTime ; print "--totalCostPerMinute"; print totalCostPerMinute;
	put(returnDict, "operatingCost", string(operatingCost));
	print "--operatingCost--"; print operatingCost;
	//=============================== END - Operating Cost Calculation ===============================//
	
	
	//=============================== START - Asset Cost Calculation ===============================//
	//Calculate the fourth major component of the Cost (Cost = disposalProcessingCost + disposalTripCost + operatingCost + assetCost)

	//containerDepreciationPerContainer and containerFactor comes from data tables, containerQuantity comes from config
	totalContainerDepreciation = containerDepreciationPerContainer * containerFactor * containerQuantity;
	put(returnDict, "totalContainerDepreciation", string(totalContainerDepreciation));
	
	//siteTime and customerDisposalMinutes calculated earlier, truckHoursPerMonth comes from a data table
	customerSharePct = 0.0;
	if(truckHoursPerMonth <> 0.0){		//Necessary to avoid divide by zero error if truckHoursPerMonth is zero
		customerSharePct = (siteTime + customerDisposalMinutes) / 60.0 / truckHoursPerMonth;
	}
	put(returnDict, "customerSharePct", string(customerSharePct));
	
	//truckDepreciationPerMonth comes from a data table
	totalTruckDepreciation = truckDepreciationPerMonth * customerSharePct;
	put(returnDict, "totalTruckDepreciation", string(totalTruckDepreciation));
	
	//Final Asset Cost calculation
	//containerMaintPerLift comes from a data table, liftsPerMonth calculated earlier
	assetCost = (containerMaintPerLift * liftsPerMonth) + totalContainerDepreciation + totalTruckDepreciation;
	put(returnDict, "assetCost", string(assetCost));
	print "--assetCost--"; print assetCost;
	//=============================== END - Asset Cost Calculation ===============================//
	
	//Final Cost calculation
	cost = disposalProcessingCost + disposalTripCost + operatingCost + assetCost;
	put(returnDict, "cost", string(cost));

//=============================== END - Cost Calculation ===============================//

//=============================== START - ROI Calculation ===============================//
//Calculate the ROI for this configuration (Floor = Cost + ROI)

//averagePricePerYard comes from data table, yardsPerMonth calculated earlier
averageMonthlyRevenue = averagePricePerYard * yardsPerMonth;
put(returnDict, "averageMonthlyRevenue", string(averageMonthlyRevenue));

workingCapital = (45.0 / 30.0) * averageMonthlyRevenue; 
put(returnDict, "workingCapital", string(workingCapital));

//truckAssets comes from a data table, customerSharePct calculated earlier
truckAllocatedValue = truckAssets * customerSharePct;
put(returnDict, "truckAllocatedValue", string(truckAllocatedValue));

commission = (averageMonthlyRevenue * 0.75) / 36.0;
put(returnDict, "commission", string(commission));

//Final ROI calculation
//containerValue comes from data table
ROI = ((containerValue * containerQuantity + workingCapital + truckAllocatedValue) * 0.065 / 12.0) + commission;
put(returnDict, "ROI", string(ROI));

//=============================== END - ROI Calculation ===============================//

//Final Floor calculation
floor = cost + ROI;
print "cost"; print cost; 
print "ROI" ; print ROI;
put(returnDict, "floor", string(floor));

return returnDict;