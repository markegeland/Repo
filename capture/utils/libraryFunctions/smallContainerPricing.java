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
			11/26/13 - Zach Schlieder - Added Wave 2 functionality - added contactTerm for commission calculations
    
			03/27/2015 - Mike (Republic) - Separated Compactor Rental pricing from Base Container pricing with different margins on new services only
			04/04/2015 - Gaurav Dawar - #145 - compactor cost fix for comapctor customer owned
=====================================================================================================
*/

//=============================== START - Variable Initialization ===============================//
//Default variables
returnDict = dict("string");
floorValue = 0.0; 

//Variables from Config attributes found in the input string dictionary
wasteCategory = get(stringDict, "wasteCategory");
industry = get(stringDict, "industry");
routeTypeDerived = get(stringDict, "routeTypeDerived");
accountType = get(stringDict, "accountType");
//Get float variables from input dictionary in string form
containerQuantityStr = get(stringDict, "containerQuantity");
containerSizeStr = get(stringDict, "containerSize");
frequencyStr = get(stringDict, "frequency");
additionalSiteMinutesStr = get(stringDict, "additionalSmallContainerSiteTime");
onsiteTimeInMinsStr = get(stringDict, "onsiteTimeInMins");
compactor = get(stringDict, "compactor");
allocationFactorStr = get(stringDict, "allocationFactor");
lock = get(stringDict, "lock");
isEnclosure = get(stringDict, "isEnclosure");
rolloutFeet = get(stringDict, "rolloutFeet");
casters = get(stringDict, "casters");
scoutRoute = get(stringDict, "scoutRoute");
isCustomerOwnedStr = get(stringDict, "isCustomerOwned");
newCustomerConfigStr = get(stringDict, "newCustomerConfig");
Pickup_Per_Tot_LiftsStr = get(stringDict, "Pickup_Per_Tot_Lifts");
periodStr = get(stringDict, "period");
Pickup_Period_LengthStr = get(stringDict, "Pickup_Period_Length");

//Small Container Compactor
customerOwnedCompactorStr = get(stringDict, "customerOwnedCompactor");
compactorValueStr = get(stringDict, "compactorValue");
modelName = get(stringDict, "model_name");


//Convert necessary variables from string to float for use in calculations
containerQuantity = 0.0;
containerSize = 0.0;
frequency = 0.0;
additionalSiteMinutes = 0.0;
onsiteTimeInMins = 0.0;
allocationFactor = 1.0;
scout_route_flag = 0;
newCustomerConfig = 1; //Default to new customer
Pickup_Per_Tot_Lifts = 0.0;
period = 0.0;
Pickup_Period_Length = 0.0;
cr_new_business = 1; //default to new business
salesActivityConfig = "";
useCurrentPickupsPerDayStr = "";
useCurrentPickupsPerDay = false;
currentQuantity = 0.0; //This is existing container quantity

//MPB New variable initiation
compactorValue = 0.0;
customerOwnedCompactor = 0;
containerRentalFactor = 0.0;

//Default Quote attribute
customerType = "New/New";

if(isnumber(newCustomerConfigStr)){
	newCustomerConfig = atoi(newCustomerConfigStr);
}

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
if(isnumber(allocationFactorStr)){
	allocationFactor = atof(allocationFactorStr);
}
if(isnumber(onsiteTimeInMinsStr)){
	onsiteTimeInMins = atof(onsiteTimeInMinsStr);
}
isCustomerOwned = 0;
if(isCustomerOwnedStr== "true"){
	isCustomerOwned = 1;
}
if(isnumber(Pickup_Per_Tot_LiftsStr)){
	Pickup_Per_Tot_Lifts = atof(Pickup_Per_Tot_LiftsStr);
}

if(isnumber(periodStr)){
	period = atof(periodStr);
}

if(isnumber(Pickup_Period_LengthStr)){
	Pickup_Period_Length = atof(Pickup_Period_LengthStr);
}

if(containskey(stringDict, "cr_new_business")){
	cr_new_business = atoi(get(stringDict, "cr_new_business"));
}

if(containskey(stringDict, "salesActivityConfig")){
	salesActivityConfig = get(stringDict, "salesActivityConfig");
}
if(containskey(stringDict, "useCurrentPickupsPerDay_sc")){
	useCurrentPickupsPerDayStr = get(stringDict, "useCurrentPickupsPerDay_sc");
}
if(NOT(isnull(useCurrentPickupsPerDayStr)) AND useCurrentPickupsPerDayStr == "true"){
	useCurrentPickupsPerDay = true;
}

if(isnumber(compactorValueStr)){
	compactorValue = atof(compactorValueStr);
}
if(customerOwnedCompactorStr == "true"){
	customerOwnedCompactor = 1;
}
put(returnDict, "customerOwnedCompactor", string(customerOwnedCompactor));

//Get variables from Commerce attributes found in the input string dictionary
division = get(stringDict, "division_quote");
region = "";
if(containskey(stringDict, "region_quote")){
	region = get(stringDict, "region_quote");
}
partNumber = get(stringDict, "partNumber");
contractTermStr = get(stringDict, "initialTerm_quote");

//Updated 05/12/2014 
contractTerm = 12.0;	//Default term was 36 months

if(isnumber(contractTermStr)){
	contractTerm = atof(contractTermStr);
}
if(containskey(stringDict, "salesActivity_quote")){
	customerType = get(stringDict, "salesActivity_quote");	
}

if(containskey(stringDict, "current_Quantity")){
	currentQuantityStr = get(stringDict, "current_Quantity");
	if(isnumber(currentQuantityStr)){
		currentQuantity = atof(currentQuantityStr);
	}
}

//Initialize string variables set by data table results
//miscConfigData table
compactorFactorStr = "";
averageDaysAcctsReceivableStr = "";
averageCustomerSiteTimeStr = "";
floorROIStr = "";
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
compactorAdditionalSiteTimeStr = "";

//Financial Summary - cost Variables
cts_month_incl_oh = 0.0;
cost_disp_xfer_proc =  0.0;
cost_overhead = 0.0;
royalties_per_yard_svcd_str = "";
supervisor_labor_cost_per_lift_str = "";
insurance_cost_per_lift_str = "";
facility_cost_per_lift_str = "";
depr_amort_excl_trk_cont_per_lift_str = "";
com_bad_debt_str = "";
bad_debt_factor_str = "";
other_oper_cost_per_lift_str = "";
marketing_per_lift_str = "";
selling_admin_per_yard_svc_str = "";
general_admin_per_lift_str = "";
default_disposal_3p_float = 0.0;

//=============================== END - Variable Initialization ===============================//


//=============================== START - Table Lookups ===============================//
//Get values of all necessary values for calculations from data tables or parts database
	//Temporarily used to get values that may come from other sources later
	//=============================== START - Lookups on Corporate_Hierarchy table ===============================//
	//Get the region for selected division
	regionSet = bmql("SELECT Cur_Region_Nbr FROM Corporate_Hierarchy WHERE Cur_Div_Nbr = $division");
	for eachRecord in regionSet{
		region = get(eachRecord, "Cur_Region_Nbr");
		break;
	}
	//End get the region of selected division
	
	//=============================== START - Lookups on miscConfigData table ===============================//
	compactorFactor = 1.0;
	hasCompactor = 0;
	if(compactor == "true"){
		hasCompactor = 1;
	}
	put(returnDict, "hasCompactor", string(hasCompactor));

	if(hasCompactor == 1){
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
	
	//Get the Average Days Accounts Receivable 
	averageDaysAcctsReceivableRecordSet = bmql("SELECT value FROM miscConfigData WHERE attribute = 'averageDaysAcctsReceivable'");
	
	for eachRecord in averageDaysAcctsReceivableRecordSet{
		averageDaysAcctsReceivableStr = get(eachRecord, "value");
		break;	//Only one result is desired
	}
	averageDaysAcctsReceivable = 0.0;
	if(isnumber(averageDaysAcctsReceivableStr)){
		averageDaysAcctsReceivable = atof(averageDaysAcctsReceivableStr);
	}
	put(returnDict, "averageDaysAcctsReceivable", string(averageDaysAcctsReceivable));
	
	//Get the Average Customer Site Time 
	averageCustomerSiteTimeRecordSet = bmql("SELECT value FROM miscConfigData WHERE attribute = 'averageCustomerSiteTime'");
	
	for eachRecord in averageCustomerSiteTimeRecordSet{
		averageCustomerSiteTimeStr = get(eachRecord, "value");
		break;	//Only one result is desired
	}
	averageCustomerSiteTime = 0.0;
	if(isnumber(averageCustomerSiteTimeStr)){
		averageCustomerSiteTime = atof(averageCustomerSiteTimeStr);
	}
	put(returnDict, "averageCustomerSiteTime", string(averageCustomerSiteTime));
	
	//Get the Floor ROI 
	floorROIRecordSet = bmql("SELECT value FROM miscConfigData WHERE attribute = 'floorROI'");
	
	for eachRecord in floorROIRecordSet{
		floorROIStr = get(eachRecord, "value");
		break;	//Only one result is desired
	}
	floorROI = 0.0;
	if(isnumber(floorROIStr)){
		floorROI = atof(floorROIStr);
	}
	put(returnDict, "floorROI", string(floorROI));
	
	//=============================== END - Lookups on miscConfigData table ===============================//

	//=============================== START - Get container size ===============================//

	//containerQuantity and containerSize come from Config
	yardsPerMonth = containerQuantity * containerSize * frequency * compactorFactor * (52.0 / 12.0); //52 weeks / 12 months
	print "1st yards"; print yardsPerMonth;
	//if(customerType <> "New/New" AND customerType <> "New from Competitor"){
	//New business rollback & close container group scenarios should use values from Accounts_Status to calculate yardsPerMonth
	if(cr_new_business == 0 OR salesActivityConfig == "Price adjustment" OR salesActivityConfig == "Close container group"){
	//Fix for TC-0918, TC-0952, TC-0988 but fails all others - need to discuss with larger audience to handle change of period Vs continuation of period
		//Period can't exceed 1.0 
		period_modified = period;
		if(period > 1.0){
			period_modified = 1.0;
		}
		yardsPerMonth = Pickup_Per_Tot_Lifts * containerSize * period_modified * compactorFactor * (52.0 / 12.0); //52 weeks / 12 months
		print "currentyarsdpermonth"; print yardsPerMonth;
	}
	//If frequency was not modified and period is above 1
	//Fix for TC-0918, TC-0952, TC-0988 but fails all others - need to discuss with larger audience to handle change of period Vs continuation of period
	elif(salesActivityConfig == "Service level change" AND (useCurrentPickupsPerDay)){
		//yardsPerMonth = yardsPerMonth * period;
		period_modified = period;
		if(period > 1.0){
			period_modified = 1.0;
		}
		//Container quantity can change - so get total lifts per new quantity
		if(currentQuantity > 0){
			PickupPerTotLifts = (Pickup_Per_Tot_Lifts/currentQuantity) * containerQuantity;
		}else{
			PickupPerTotLifts = Pickup_Per_Tot_Lifts;
		}
		yardsPerMonth =  PickupPerTotLifts * containerSize * period_modified * compactorFactor * (52.0 / 12.0); //52 weeks / 12 months
		print "2nd yards useCurrentDays"; print yardsPerMonth;
	}
	put(returnDict, "yardsPerMonth", string(yardsPerMonth));
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
	//Get all necessary values from the divisionKPI table based 
	put(returnDict, "J24", division);
	put(returnDict, "J25", wasteCategory);
	divisionKPIRecordSet = bmql("SELECT rylties_pr_yard_svcd, svsr_lbr_cst_pr_lft, insurance_cst_pr_lft, facility_cost_pr_lft, dp_mrt_x_trk_cnt_lft, labor_cost_pr_minute, bad_debt_pct_of_rvnu, other_opr_cst_pr_lft, marketing_per_lift, sllng_admn_pr_yd_svc, general_admin_pr_lft, container_mnt_pr_lft, truck_cost_pr_minute, pounds_per_yard, minutes_per_disp_ton, minutes_per_lift, truck_hours_pr_month, default_disposal_3p FROM tbl_division_kpi WHERE div_nbr = $division AND waste_type = $wasteCategory");

	for eachRecord in divisionKPIRecordSet{
		laborCostPerMinuteStr = get(eachRecord, "labor_cost_pr_minute");
		containerMaintPerLiftStr = get(eachRecord, "container_mnt_pr_lft");
		truckCostPerMinuteStr = get(eachRecord, "truck_cost_pr_minute");
		poundsPerYardStr = get(eachRecord, "pounds_per_yard");
		minutesPerDispTonStr = get(eachRecord, "minutes_per_disp_ton");
		minutesPerLiftStr = get(eachRecord, "minutes_per_lift");
		truckHoursPerMonthStr = get(eachRecord, "truck_hours_pr_month");
		//Overhead fields
		royalties_per_yard_svcd_str = get(eachRecord, "rylties_pr_yard_svcd");
		supervisor_labor_cost_per_lift_str = get(eachRecord, "svsr_lbr_cst_pr_lft");
		insurance_cost_per_lift_str = get(eachRecord, "insurance_cst_pr_lft");
		facility_cost_per_lift_str = get(eachRecord, "facility_cost_pr_lft");
		depr_amort_excl_trk_cont_per_lift_str = get(eachRecord, "dp_mrt_x_trk_cnt_lft");
		com_bad_debt_str = get(eachRecord, "bad_debt_pct_of_rvnu");
		other_oper_cost_per_lift_str = get(eachRecord, "other_opr_cst_pr_lft");
		marketing_per_lift_str = get(eachRecord, "marketing_per_lift");
		selling_admin_per_yard_svc_str = get(eachRecord, "sllng_admn_pr_yd_svc");
		general_admin_per_lift_str  = get(eachRecord, "general_admin_pr_lft");
		default_disposal_3p_float =  getfloat(eachRecord, "default_disposal_3p");
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
	put(returnDict, "new_cust_cg_flag", string(newCustomerConfig));
	
	pricePerYardRecordSet = bmql("SELECT p_y, division, equipment_size, waste_type, new_customer_cg_flag FROM Div_Waste_PPY WHERE division = $division AND waste_type = $wasteCategory AND new_customer_cg_flag = $newCustomerConfig");
	
	//Moved container size check to result set iteration to handle float values; 4.00 <> 4.0, when column data type is string :(
	for eachRecord in pricePerYardRecordSet{
		equipment_size = getFloat(eachRecord, "equipment_size");
		if(equipment_size == containerSize){ 
			averagePricePerYardStr = get(eachRecord, "p_y");
			break;	//Only one result is desired
		}
	}	
	
		//averagePricePerYard - Found in pricePerYard, based on the division number, wasteType, and container size. Used in calculations of ROI
		averagePricePerYard = 0.0;
		if(isnumber(averagePricePerYardStr)){	//Convert the table result to a float for use in calculations
			averagePricePerYard = atof(averagePricePerYardStr);
		}
		put(returnDict, "averagePricePerYard", string(averagePricePerYard));
		
	//=============================== END - Lookups on the pricePerYard table ===============================//
	
	//=============================== START - Lookups on the industries table ===============================//
	industriesRecordSet = bmql("SELECT container, bad_debt_factor, weightsSW, weightsRE FROM industries WHERE industryName = $industry");

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
		
		//Get Bad Debt Factor
		bad_debt_factor_str = get(eachRecord, "bad_debt_factor");
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
	
	//=============================== START - Lookups on the trucks table ===============================//
		
		//truckAssets - Found in trucks, based on the route type from Config. Used in calculations of ROI
		truckAssetsRecordSet = bmql("SELECT allocation, cost FROM trucks WHERE type = $routeTypeDerived AND region = $region");
		
		for eachRecord in truckAssetsRecordSet{
			//truckAssetsStr = get(eachRecord, "allocation");
			//Updated 30 April 2014
			truckAssetsStr = get(eachRecord, "cost");
		}
		truckAssets = 0.0;
		if(isnumber(truckAssetsStr)){	//Convert the table result to a float for use in calculations
			truckAssets = atof(truckAssetsStr);
		}
		put(returnDict, "truckAssets", string(truckAssets));
		
		//truckDepreciationPerMonth - Found in trucks, based on the route type from Config and region from Commerce. Used in calculations of assetCost
		truckDepreciationPerMonthRecordSet = bmql("SELECT depreciationPerMonth FROM trucks WHERE type = $routeTypeDerived AND region = $region");
		
		for eachRecord in truckDepreciationPerMonthRecordSet{
			truckDepreciationPerMonthStr = get(eachRecord, "depreciationPerMonth");
		}
		truckDepreciationPerMonth = 0.0;
		if(isnumber(truckDepreciationPerMonthStr)){	//Convert the table result to a float for use in calculations
			truckDepreciationPerMonth = atof(truckDepreciationPerMonthStr);
		}
		put(returnDict, "truckDepreciationPerMonth", string(truckDepreciationPerMonth));
	
	//=============================== END - Lookups on the trucks table ===============================//
	
	hasCompactorStr = string(hasCompactor);
	lOBCategoryDerived = "Commercial"; //Default value as Small container belongs to Commercial LOB
	//=============================== START - Lookups on the Parts database ===============================//
	partsRecordSet = bmql("SELECT part_number, custom_field2, custom_field4, custom_field5, custom_field18, custom_field10, custom_field15 FROM _parts WHERE custom_field9 = $routeTypeDerived AND custom_field11 = $hasCompactorStr AND  custom_field12 = $lOBCategoryDerived");
	
	compactorValueStr = "0.0";
	compactorLifeStr = "1.0";
	for eachRecord in partsRecordSet{
		containerSize_db = getFloat(eachRecord, "custom_field10");
		//Account_status table has incorrect container sizes 12.0 instead of 12.00
		//Temporary solution - remove container size comparision from query filter & compare it within recordset iteration
		if(containerSize_db == containerSize){
			containerValueStr = get(eachRecord, "custom_field2");
			depreciationPerMonthStr = get(eachRecord, "custom_field4");
			compactorAdditionalSiteTimeStr = get(eachRecord, "custom_field5");
			compactorValueStr = get(eachRecord, "custom_field18");
			compactorLifeStr = get(eachRecord, "custom_field15");
		}
	}
	
		//containerValue, compactorValue - Found in parts database, based on the partNumber (SKU). Used in calculations of ROI
		containerValue = 0.0;
		compactorLife = 1.0;
		if(isnumber(containerValueStr)){	//Convert the table result to a float for use in calculations
			containerValue = atof(containerValueStr);
		}
		//Default to parts database if no compactorValue is provided
		if(compactorValue == 0.0 AND isnumber(compactorValueStr)){
			compactorValue = atof(compactorValueStr);
		}
		if(customerOwnedCompactor==1){
			compactorValue = 0.0;
		}
		if(isnumber(compactorLifeStr)){
			compactorLife = atof(compactorLifeStr);
		}
		put(returnDict, "containerValue", string(containerValue));
		put(returnDict, "compactorValue", string(compactorValue));
		put(returnDict, "compactorLife", string(compactorLife));
		
		//containerDepreciationPerContainer - Found in parts database, based on the partNumber (SKU). Used in calculations of assetCost
		containerDepreciationPerContainer = 0.0;
		if(isnumber(depreciationPerMonthStr)){	//Convert the table result to a float for use in calculations
			containerDepreciationPerContainer = atof(depreciationPerMonthStr);
		}
		put(returnDict, "containerDepreciationPerContainer", string(containerDepreciationPerContainer));
		
		compactor_additional_site_time = 0.0;
		if(isnumber(compactorAdditionalSiteTimeStr)){
			compactor_additional_site_time = atof(compactorAdditionalSiteTimeStr);
		}
		put(returnDict, "compactorAdditionalSiteTime", string(compactor_additional_site_time));
	//=============================== START - Lookups on the Parts database ===============================//
	

//=============================== END - Table Lookups ===============================//

//=============================== START - Cost Calculation ===============================//
//Calculate the Cost for this configuration (Floor = Cost + ROI)

	//=============================== START - Disposal Processing Cost Calculation ===============================//
	//Calculate the first major component of the Cost (Cost = disposalProcessingCost + disposalTripCost + operatingCost + assetCost)
		
	//Customer Tons Per Month calculation - also used in calculation of operatingCost
	//divisionPoundsPerYard and weightIndustryFactor come from data tables
	customerTonsPerMonth = yardsPerMonth * divisionPoundsPerYard * weightIndustryFactor * (1.0 / 2000.0); //1 ton / 2000 yards
	/*print "--yardsPerMonth--"; print yardsPerMonth;
	print "--divisionPoundsPerYard--"; print divisionPoundsPerYard;
	print "--weightIndustryFactor--"; print weightIndustryFactor;*/
	put(returnDict, "customerTonsPerMonth", string(customerTonsPerMonth));
	
	//Final Disposal Processing Cost calculation
	//disposalCostPerTon comes from data table
	disposalCostPerTon = 0.0;
	if(wasteCategory <> "Recycling"){
		if(containskey(stringDict,"disposalCostPerTon") AND isnumber(get(stringDict, "disposalCostPerTon"))){
			disposalCostPerTon = atof(get(stringDict, "disposalCostPerTon"));
		}
	}
	disposalProcessingCost = disposalCostPerTon * customerTonsPerMonth;
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
	put(returnDict, "J21", string(customerDisposalMinutes));
	put(returnDict, "J22", string(disposalMinutesPerTon));
	put(returnDict, "J23", string(customerTonsPerMonth));
	
	//Final Disposal Trip Cost calculation
	disposalTripCost = totalCostPerMinute * customerDisposalMinutes;
	put(returnDict, "J18", string(disposalTripCost));
	put(returnDict, "J19", string(totalCostPerMinute));
	put(returnDict, "J20", string(customerDisposalMinutes));
	put(returnDict, "disposalTripCost", string(disposalTripCost));
	//=============================== END - Disposal Trip Cost Calculation ===============================//
	
	//=============================== START - Operating Cost Calculation ===============================//
	//Calculate the third major component of the Cost (Cost = disposalProcessingCost + disposalTripCost + operatingCost + assetCost)

	//containerQuantity comes from config
	liftsPerMonth = containerQuantity * frequency * (52.0/12.0);
	//Updated 5 May 2014
	if((cr_new_business == 0 OR useCurrentPickupsPerDay) AND salesActivityConfig <> "" AND Pickup_Period_Length == 1){
	//Fix for TC-0918, TC-0952, TC-0988 but disabled because of failing others
             liftsPerMonth = containerQuantity * frequency * period * (52.0/12.0);
	}
	put(returnDict, "liftsPerMonth", string(liftsPerMonth));
	
	//tripsPerWeek comes from config
	tripsPerMonth = frequency * (52.0 / 12.0) * allocationFactor; //52 weeks / 12 months
	if((cr_new_business == 0 OR useCurrentPickupsPerDay) AND salesActivityConfig <> "" AND Pickup_Period_Length == 1){
	//Fix for TC-0918, TC-0952, TC-0988 but disabled because of failing others
		tripsPerMonth = frequency * period * (52.0 / 12.0) * allocationFactor; //52 weeks / 12 months
	}
	put(returnDict, "tripsPerMonth", string(tripsPerMonth));

	//minutesPerLift comes from data table
	tripMinutesPerMonth = tripsPerMonth * minutesPerLift;
	put(returnDict, "tripMinutesPerMonth", string(tripMinutesPerMonth));

	secondLiftMinutesPerMonth = (liftsPerMonth - tripsPerMonth) * 0.5 * minutesPerLift;
	put(returnDict, "secondLiftMinutesPerMonth", string(secondLiftMinutesPerMonth));

	//additionalSiteMinutes comes from config
	additionalSiteMinutesPerMonth = additionalSiteMinutes * tripsPerMonth;
	
	lockStr = get(stringDict, "lock");
	isEnclosureStr = get(stringDict, "isEnclosure");
	rolloutFeetStr = get(stringDict, "rolloutFeet");
	castersStr = get(stringDict, "casters");
	
	customer_site_lock = 0;
	customer_site_enc = 0;
	customer_site_rollOut = 0;
	rolloutFactor = 0.0;
	rollOutSequence = 0;
	rollOutFactorExtraFeet = 0.0;
	lockFactor = 0.0;
	enclosureFactor = 0.0;
	totalSiteFactorTime = 0.0;
	compactor_mainteance_factor = 0.0;
	
	//============================= START - GET SITE FACTORS ==============================================================
	/* Get the site factors to calculate total site time */
	siteFactorsRS = bmql("SELECT SiteCharacteristic, Factor FROM Site_Char_Factors");
	for eachRecord in siteFactorsRS{
		characteristic = get(eachRecord, "SiteCharacteristic");
		factor = getFloat(eachRecord, "Factor");
		
		//ADd Enclosure site factor
		if(isEnclosureStr == "true" AND characteristic == "Enclosure"){
			customer_site_enc = 1;
			totalSiteFactorTime = totalSiteFactorTime + factor;
			enclosureFactor = factor;
			put(returnDict, "enclosureFactor", string(enclosureFactor));
			put(returnDict, "customer_site_enc", string(customer_site_enc));
		}
		
		//ADd Casters site factor
		if(castersStr == "true" AND rolloutFeetStr <> "" AND rolloutFeetStr <> "None"){
			customer_site_rollOut = 1;
			if(characteristic == "Roll Out"){
				rolloutFactor = factor;
			}elif(characteristic == "Roll Out Extra Feet"){
				rollOutFactorExtraFeet = factor;
			}
			put(returnDict, "customer_site_rollOut", string(customer_site_rollOut));
			put(returnDict, "rolloutFactor", string(rolloutFactor));
			put(returnDict, "rollOutFactorExtraFeet", string(rollOutFactorExtraFeet));
		}
		//ADd Lock site factor
		if(lockStr <> "" AND characteristic == "Lock"){
			customer_site_lock = 1;
			totalSiteFactorTime = totalSiteFactorTime + factor;
			lockFactor = factor;
			put(returnDict, "lockFactor", string(lockFactor));
			put(returnDict, "customer_site_lock", string(customer_site_lock));
		}
		//MPB Implementation - Comes from the small container division table now
		if(characteristic == "Compactor Maintenance"){
			compactor_mainteance_factor = factor;
			put(returnDict, "compactor_mainteance_factor", string(compactor_mainteance_factor));
		}
	}
	//============================= END - GET SITE FACTORS ==============================================================
	
	//Rollout
	if(castersStr == "true" AND rolloutFeetStr <> "" AND rolloutFeetStr <> "None"){
		rolloutSequenceRS = bmql("SELECT sequence FROM rollout_factors WHERE value = $rolloutFeetStr");	
		for eachRecord in rolloutSequenceRS{
			rollOutSequence = getInt(eachRecord, "sequence");
		}
		totalSiteFactorTime = totalSiteFactorTime + (rolloutFactor + ((rollOutSequence - 1) * rollOutFactorExtraFeet));
	}

	siteTime = (tripsPerMonth * minutesPerLift) + 
                 ((liftsPerMonth - tripsPerMonth) * minutesPerLift * 0.5) + 
                 (tripsPerMonth * (
                   customer_site_lock * lockFactor +
                     customer_site_enc * enclosureFactor +
                     (containerQuantity * customer_site_rollOut * (rolloutFactor + ((rollOutSequence - 1) * rollOutFactorExtraFeet))) + 
                     onsiteTimeInMins + compactor_additional_site_time));
			 
	put(returnDict, "siteTime", string(siteTime));
	 
	if(scoutRoute == "true"){
		scout_route_flag = 1;
	}

	scoutRouteTime = scout_route_flag * siteTime * 0.5;
	totalSiteFactorTime = totalSiteFactorTime + scoutRouteTime;
	
	put(returnDict, "scoutRouteTime", string(scoutRouteTime));
	put(returnDict, "totalSiteFactorTime", string(totalSiteFactorTime));
	
	//Final Operating Cost calculation
	//totalCostPerMinute calculated earlier
	operatingCost = (siteTime + scoutRouteTime) * totalCostPerMinute;
	  
	put(returnDict, "operatingCost", string(operatingCost));
	//=============================== END - Operating Cost Calculation ===============================//
	
	
	//=============================== START - Asset Cost Calculation ===============================//
	//Calculate the fourth major component of the Cost (Cost = disposalProcessingCost + disposalTripCost + operatingCost + assetCost)

	//containerDepreciationPerContainer and containerFactor comes from data tables, containerQuantity comes from config
  
	//No depreciation if customer owned
	totalContainerDepreciation = containerDepreciationPerContainer * containerFactor * containerQuantity * (1-isCustomerOwned);
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
	
	//Compactor Maintenance
	compactor_depr = 0.0;
	if(compactorLife > 0){
		compactor_depr = compactorValue/compactorLife;
	}

	//Split maintenance into container and compactor if new with compactor
	if(hasCompactor == 1 AND modelName <> "Service Change"){
		compactor_maint_per_container = hasCompactor * (1-customerOwnedCompactor) * compactorValue * compactor_mainteance_factor / 12;
		total_compactor_depr_maint = (compactor_depr + compactor_maint_per_container) * containerQuantity * (1-customerOwnedCompactor);
	}
	//Maintain original pricing fromula if not a new service with a compactor
	else{
		compactor_maint_per_container = hasCompactor * (1-isCustomerOwned) * compactorValue * compactor_mainteance_factor / 12;
		total_compactor_depr_maint = (compactor_depr + compactor_maint_per_container) * containerQuantity * (1-isCustomerOwned);
	}

	put(returnDict, "compactor_depr", string(compactor_depr));	
	put(returnDict, "compactor_maint", string(compactor_maint_per_container));	
	put(returnDict, "compactor_depr_maint", string(total_compactor_depr_maint));	
	
	//Final Asset Cost calculation
	//containerMaintPerLift comes from a data table, liftsPerMonth calculated earlier
	containerAssetCost = 0.0;
	//Calculate total asset cost
	assetCost = totalContainerDepreciation + total_compactor_depr_maint + totalTruckDepreciation + (containerMaintPerLift * liftsPerMonth * (1-isCustomerOwned));
		put(returnDict, "assetCost", string(assetCost));
	//print "--assetCost--"; print assetCost;
	
	//Calculate asset cost for container only for new services with a compactor
	if(hasCompactor == 1 AND modelName <> "Service Change"){
		containerAssetCost = (totalContainerDepreciation + totalTruckDepreciation + (containerMaintPerLift * liftsPerMonth * (1-isCustomerOwned))) * hasCompactor;
	//compactorAssetCost is total_compactor_depr_maint
	}

	//=============================== END - Asset Cost Calculation ===============================//
	
	//Final Cost calculation
	cost = disposalProcessingCost + disposalTripCost + operatingCost + assetCost;
	put(returnDict, "J13", string(cost));
	put(returnDict, "J14", string(disposalProcessingCost));
	put(returnDict, "J15", string(disposalTripCost));
	put(returnDict, "J16", string(operatingCost));
	put(returnDict, "J17", string(assetCost));
	put(returnDict, "cost", string(cost));

//=============================== END - Cost Calculation ===============================//

//=============================== START - ROI Calculation ===============================//
//Calculate the ROI for this configuration (Floor = Cost + ROI)

//averagePricePerYard comes from data table, yardsPerMonth calculated earlier
averageMonthlyRevenue = averagePricePerYard * yardsPerMonth;
put(returnDict, "averageMonthlyRevenue", string(averageMonthlyRevenue));

workingCapital = (averageDaysAcctsReceivable / 30.0) * averageMonthlyRevenue; 
put(returnDict, "workingCapital", string(workingCapital));

//truckAssets comes from a data table, customerSharePct calculated earlier
//Updated 30 April 2014
truckAllocatedValue = truckAssets/2.0 * customerSharePct;

put(returnDict, "truckAllocatedValue", string(truckAllocatedValue));

flatRateCommission = 0.0;
commissionRate = 0.75;
if(accountType == "Temporary"){
	commissionRate = 0.0;
	flatRateCommission = 15.0;		
}
put(returnDict, "commissionRate", string(commissionRate));

/*
	//Updated 05/12/2014
+  # 20140509: choose the contract term or 12 months whichever is greater
+  # prevent commission from overloading MTM or unsigned contracts
+  pre_price_dt[,commission:=p_y * yards_per_month * 0.75 / apply(cbind(contract_term,12),1,max)]
*/
if(contractTerm < 12.0){
	contractTerm = 12.0;
}
commission = (averageMonthlyRevenue * 0.75) / contractTerm;
put(returnDict, "commission", string(commission));

containerROI = 0.0;
compactorROI = 0.0;

//Final ROI calculation
//containerValue comes from data table
//Calculate the total ROI
ROI = ((truckAllocatedValue + ((containerValue * (1 - isCustomerOwned) + compactorValue * (1 - isCustomerOwned)) * containerQuantity) + workingCapital) * floorROI / 12) + commission;

//Calculate ROI separately for compactors and containers 
if(hasCompactor == 1 AND modelName <> "Service Change"){
	containerROI = ((truckAllocatedValue + (containerValue * (1 - isCustomerOwned) * containerQuantity) + workingCapital) * floorROI / 12) + commission;
	compactorROI = compactorValue * containerQuantity * hasCompactor * floorROI / 12;
}

put(returnDict, "ROI", string(ROI));
put(returnDict, "compactorROI", string(compactorROI));
put(returnDict, "containerROI", string(compactorROI));

//=============================== END - ROI Calculation ===============================//
				 
costToServeContainer = 0.0;
costToServeCompactor = 0.0;

if(hasCompactor == 1 AND modelName <> "Service Change"){
	containerCost = disposalProcessingCost + disposalTripCost + operatingCost + containerAssetCost;
	costToServeContainer = containerCost + containerROI;

	costToServeCompactor = total_compactor_depr_maint + compactorROI;

	containerRentalFactor = ((containerValue * containerQuantity * floorROI / 12) + totalContainerDepreciation) / costToServeContainer; 

	floor = costToServeContainer;
}
else{
	//Final Floor calculation
	floor = cost + ROI;
}


put(returnDict, "J10", string(floor));
put(returnDict, "J11", string(cost));
put(returnDict, "J12", string(ROI));

put(returnDict, "floor", string(floor));
put(returnDict, "costToServeMonth", string(floor));

//MPB New variables
put(returnDict, "costToServeCompactor", string(costToServeCompactor));
put(returnDict, "containerRentalFactor", string(containerRentalFactor));
put(returnDict, "costToServeContainer", string(costToServeContainer));

//============= END - Floor Calculation ==========================//

//=============================== START - Financial Summary Cost Calculation ===============================//
/*cts_month_incl_oh:=cost_disp_xfer_proc + cost_disposal_trip + cost_oper_site_time + cost_assets + cost_roi + cost_overhead - commission
Financial summary cost does NOT include disposal processing cost, but it includes the following:
1. cost_disp_xfer_proc (disposal transfer process cost) =  dsp_xfer_price_per_ton * tons_per_month (customerTonsPerMonth)
'dsp_xfer_price_per_ton' is from datatable Disposal_Sites - If it is 0.0 in that table, get it from third party 'default_disposal_3p' in respective(small/large)KPI tables
2. cost_disposal_trip - 'disposalTripCost' calculated in this util 
3. cost_oper_site_time - 'operatingCost' calculated in this util
4. cost_assets - 'assetCost' calculated above in this util
5. cost_roi - 'ROI' calculated above in this util
6. cost_overhead - to be newly calculated as below
7. commission - 'commission' calculated above in this util
*/

//Disposal Transfer Cost (cost_disp_xfer_proc) Calculation
dsp_xfer_price_per_ton = 0.0;
if(containskey(stringDict, "dsp_xfer_price_per_ton")){
	dsp_xfer_price_per_ton_str = get(stringDict, "dsp_xfer_price_per_ton");
	if(isnumber(dsp_xfer_price_per_ton_str)){
		dsp_xfer_price_per_ton = atof(dsp_xfer_price_per_ton_str);
	}	
}
/*print "--dsp_xfer_price_per_ton--"; print dsp_xfer_price_per_ton;
print "--customerTonsPerMonth--"; print customerTonsPerMonth;*/
if(wasteCategory == "Recycling"){
	dsp_xfer_price_per_ton = 0.0;
}
if(wasteCategory == "Solid Waste" AND dsp_xfer_price_per_ton == 0.0){ //if waste is SW and the transfer price from DisposalSites_Comm table is 0.0, then get the 3rd party column in KPI table
	dsp_xfer_price_per_ton = default_disposal_3p_float;
}
cost_disp_xfer_proc = dsp_xfer_price_per_ton * customerTonsPerMonth;
put(returnDict, "cost_disp_xfer_proc", string(cost_disp_xfer_proc));
//print "--cost_disp_xfer_proc--"; print cost_disp_xfer_proc;
//Overhead Cost Calculations
/*overhead costs include 
1. royalties
2. supervisor labor cost
3. insurance
4. facility
5. other_depreciation
6. bad_debt
7. other_operation
8. marketing
9. sales
10. gen_admin
All these are look ups from division_kpi datatable5
*/

royalties_per_yard_svcd = 0.0;
if(isnumber(royalties_per_yard_svcd_str)){
	royalties_per_yard_svcd = atof(royalties_per_yard_svcd_str);
}
oh_royalties = royalties_per_yard_svcd * yardsPerMonth;

supervisor_labor_cost_per_lift = 0.0;
if(isnumber(supervisor_labor_cost_per_lift_str)){
	supervisor_labor_cost_per_lift = atof(supervisor_labor_cost_per_lift_str);
}
oh_supervisor = supervisor_labor_cost_per_lift * liftsPerMonth;
 
insurance_cost_per_lift = 0.0;
if(isnumber(insurance_cost_per_lift_str)){
	insurance_cost_per_lift = atof(insurance_cost_per_lift_str);
}
oh_insurance = insurance_cost_per_lift * liftsPerMonth;

facility_cost_per_lift = 0.0;
if(isnumber(facility_cost_per_lift_str)){
	facility_cost_per_lift = atof(facility_cost_per_lift_str);
}
oh_facility = facility_cost_per_lift * liftsPerMonth;

depr_amort_excl_trk_cont_per_lift = 0.0;
if(isnumber(depr_amort_excl_trk_cont_per_lift_str)){
	depr_amort_excl_trk_cont_per_lift = atof(depr_amort_excl_trk_cont_per_lift_str);
}
oh_other_depr = depr_amort_excl_trk_cont_per_lift * liftsPerMonth;

//p_y = averagePricePerYard as per James pricing function
com_bad_debt = 0.0;
bad_debt_factor = 0.0;
if(isnumber(com_bad_debt_str)){
	com_bad_debt = atof(com_bad_debt_str);
}

if(isnumber(bad_debt_factor_str)){
	bad_debt_factor = atof(bad_debt_factor_str);
}

put(returnDict, "bad_debt_pct_of_rvnu", string(com_bad_debt));
put(returnDict, "bad_debt_factor", string(bad_debt_factor));
oh_bad_debt = averagePricePerYard * yardsPerMonth * com_bad_debt * bad_debt_factor; //Updated as per Pricing Script 14 March 2014
 
other_oper_cost_per_lift = 0.0;
if(isnumber(other_oper_cost_per_lift_str)){
	other_oper_cost_per_lift = atof(other_oper_cost_per_lift_str);
}
oh_other_opex = other_oper_cost_per_lift * liftsPerMonth;

marketing_per_lift = 0.0;
if(isnumber(marketing_per_lift_str)){
	marketing_per_lift = atof(marketing_per_lift_str);
}
oh_marketing = marketing_per_lift * liftsPerMonth;

selling_admin_per_yard_svc = 0.0;
if(isnumber(selling_admin_per_yard_svc_str)){
	selling_admin_per_yard_svc = atof(selling_admin_per_yard_svc_str);
}
oh_sales = selling_admin_per_yard_svc * yardsPerMonth;

general_admin_per_lift = 0.0;
if(isnumber(general_admin_per_lift_str)){
	general_admin_per_lift = atof(general_admin_per_lift_str);
}
oh_gen_admin = general_admin_per_lift * liftsPerMonth;

//Overhead cost summation
cost_overhead = oh_royalties + oh_supervisor + oh_insurance + oh_facility + oh_other_depr + oh_bad_debt + oh_other_opex + oh_marketing + oh_sales + oh_gen_admin;

//Cost per Month including Overhead
//Calulate without compactor costs if this is a new service.
if(hasCompactor == 1 AND modelName <> "Service Change"){
	cts_month_incl_oh = cost_disp_xfer_proc + disposalTripCost + operatingCost + containerAssetCost + containerROI + cost_overhead - commission;
}
//Otherwise calculate with all costs
else{
	cts_month_incl_oh = cost_disp_xfer_proc + disposalTripCost + operatingCost + assetCost + ROI + cost_overhead - commission;
}

//print "--cts_month_incl_oh--"; print cts_month_incl_oh;
put(returnDict, "cts_month_incl_oh", string(cts_month_incl_oh));

return returnDict;
