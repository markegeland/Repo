/*
================================================================================
Name:   Large Container Pricing
Author:   Zach Schlieder
Create date:  12/2/13
Description:  Calculates the Haul and Disposal for the list price for a given Large Container configuration based on config and commerce attributes as well as data tables. Haul is an array based on the totalTime Config attribute.
        
Input:   	stringDict: String Dictionary - Contains values of Config and Commerce attributes used in calculations and table calls
                    
Output:  	String Dictionary - Contains attribute name and value pairs for use in Config or Commerce

Updates:	12/4/13 - Zach Schlieder - Updated DivsionKPI_IND table call
Updates:	12/5/13 - Srikar - Replaced disposalSiteCosts table with Disposal_Sites
			12/28/13 - Zach Schlieder - Rewrote to match James' code
    
=====================================================================================================
*/

//=============================== START - Variable Initialization ===============================//
//Default Variables
returnDict = dict("string");

//Variables from Config attributes found in the input string dictionary
wasteCategory = get(stringDict, "wasteCategory");
siteName = get(stringDict, "siteName");
wasteType = get(stringDict, "wasteType");
LOB = get(stringDict, "LOB");
arrayDelimiter = get(stringDict, "arrayDelimiter");
routeType = get(stringDict, "routeType");
billingType = get(stringDict, "billingType_l");
accountType = get(stringDict, "accountType");
//Get integer variables from input dictionary in string form
isCompactorCustomerOwnedStr = get(stringDict, "isCompactorCustomerOwned");
isContainerCustomerOwnedStr = get(stringDict, "isContainerCustomerOwned");
//Get float variables from input dictionary in string form
frequencyStr = get(stringDict, "frequency");
haulsPerPeriodStr = get(stringDict, "haulsPerPeriod");
containerQuantityStr = get(stringDict, "containerQuantity");
estHaulsPerMonthStr = get(stringDict, "estHaulsPerMonth");
haulsPerContainerStr = get(stringDict, "haulsPerContainer");
estTonsPerHaulStr = get(stringDict, "estTonsPerHaul");
totalTimePerHaulStr = get(stringDict, "totalTimePerHaul");
compactorValueConfigStr = get(stringDict, "compactorValueConfig");
containerType = get(stringDict, "containerType_l");
newCustomerConfigStr = get(stringDict, "newCustomerConfig");

rental_type = "";
alloc_rentalStr = "0";
if(containskey(stringDict, "rental")){
	rental_type = get(stringDict, "rental");
	alloc_rentalStr = get(stringDict, "alloc_rental");
}

//Convert necessary variables from string to integer for use in calculations
isCompactorCustomerOwned = 0;
isContainerCustomerOwned = 0;

if(isnumber(isCompactorCustomerOwnedStr)){
	isCompactorCustomerOwned = atoi(isCompactorCustomerOwnedStr);
}
if(isnumber(isContainerCustomerOwnedStr)){
	isContainerCustomerOwned = atoi(isContainerCustomerOwnedStr);
}

//Convert necessary variables from string to float for use in calculations
frequency = 0.0;
haulsPerPeriod = 0.0;
containerQuantity = 0.0;
totalTimePerHaul = 0.0;
estHaulsPerMonth = 0.0;
haulsPerContainer = 0.0;
estTonsPerHaul = 0.0;
compactorValueConfig = 0.0;
compactorValue = 0.0;
alloc_rental = 0;
marketRate = 0.0;
hasCompactor = 0;
newCustomerConfig = 1; //Default to new customer

if(isnumber(alloc_rentalStr) AND rental_type <> ""){
	alloc_rental = atoi(alloc_rentalStr);
}

if(isnumber(frequencyStr)){
	frequency = atof(frequencyStr);
}
if(isnumber(haulsPerPeriodStr)){
	haulsPerPeriod = atof(haulsPerPeriodStr);
}
if(isnumber(containerQuantityStr)){
	containerQuantity = atof(containerQuantityStr);
}
if(isnumber(estHaulsPerMonthStr)){
	estHaulsPerMonth = atof(estHaulsPerMonthStr);
}
if(isnumber(haulsPerContainerStr)){
	haulsPerContainer = atof(haulsPerContainerStr);
}
if(isnumber(estTonsPerHaulStr)){
	estTonsPerHaul = atof(estTonsPerHaulStr);
}
if(isnumber(totalTimePerHaulStr)){
	totalTimePerHaul = atof(totalTimePerHaulStr);
}
if(isnumber(compactorValueConfigStr)){
	compactorValueConfig = atof(compactorValueConfigStr);
}
if(isnumber(newCustomerConfigStr)){
	newCustomerConfig = atoi(newCustomerConfigStr);
}


//Get variables from Commerce attributes found in the input string dictionary
region = get(stringDict, "region_quote");
division = get(stringDict, "division_quote");
partNumber = get(stringDict, "partNumber");
contractTermStr = get(stringDict, "initialTerm_quote");
salesActivity = get(stringDict, "salesActivity_quote");
industry = get(stringDict, "industry");

//adjust contract term to choose the max of the term or 12 months - Updated 05/12/2014
contractTerm = 12.0;	//Default term was 36 months

if(isnumber(contractTermStr)){
	contractTerm = atof(contractTermStr);
}

//Initialize string variables set by data table results
//divisionKPI_IND table
laborCostPerMinuteStr = "";
truckCostPerMinuteStr = "";
containerMaintPerHaulStr = "";
truckHoursPerMonthStr = "";
averagePricePerTonStr = "";
//parts database
containerValueStr = "";
depreciationPerMonthStr = "";
compactorLifeStr = "";
compactorValueStr = "";
//trucks table
truckAssetsStr = "";
truckDepreciationPerMonthStr = "";
//Disposal_Sites table
disposalCostPerTonStr = "";
//industries table
containerStr = "";
//miscConfigData table
averageDaysAcctsReceivableStr = "";
averageCustomerSiteTimeStr = "";
floorROIStr = "";
//tbl_IND_margins
marketRateStr = "";

//financial Summary variable
dsp_xfer_price_per_ton_str = "";
royalties_per_ton_str = "";
supervisor_labor_cost_per_haul_str = "";
insurance_cost_per_haul_str = "";
facility_cost_per_haul_str = "";
depr_amort_excl_trk_cont_per_haul_str = "";
other_oper_cost_per_haul_str = "";
selling_cust_svc_per_haul_str = "";
general_admin_per_haul_str = "";
ind_bad_debt_str = "";
bad_debt_factor_str = "";

//=============================== END - Variable Initialization ===============================//

//=============================== START - Table Lookups ===============================//
//Get values of all necessary values for calculations from data tables or parts database
	//=============================== START - Lookups on Corporate_Hierarchy table ===============================//
	//Get the region for selected division
	regionSet = bmql("SELECT Cur_Region_Nbr FROM Corporate_Hierarchy WHERE Cur_Div_Nbr = $division");
	for eachRecord in regionSet{
		region = get(eachRecord, "Cur_Region_Nbr");
		break;
	}
	//End get the region of selected division
	//=============================== START - Lookups on the divisionKPI_IND table ===============================//
	//Get all necessary values from the divisionKPI_IND table based 
	divisionKPIRecordSet = bmql("SELECT royalties_per_ton, sprvsr_lbr_cst_pr_hl, insurance_cost_pr_hl, facility_cost_per_hl, dpr_amt_x_trk_cnt_hl, other_oper_cst_pr_hl, sllng_cst_svc_pr_hl, gen_admin_per_haul, bad_dbt_indstry_fctr, bad_debt_pct_of_rvnu , labor_cost_pr_minute, truck_cost_pr_minute, container_mnt_pr_hl, truck_hours_pr_month, revenue_per_ton FROM tbl_division_kpi_ind WHERE div_nbr = $division");
	
	for eachRecord in divisionKPIRecordSet{
		laborCostPerMinuteStr = get(eachRecord, "labor_cost_pr_minute");
		truckCostPerMinuteStr = get(eachRecord, "truck_cost_pr_minute");
		containerMaintPerHaulStr = get(eachRecord, "container_mnt_pr_hl");
		truckHoursPerMonthStr = get(eachRecord, "truck_hours_pr_month");
		averagePricePerTonStr = get(eachRecord, "revenue_per_ton");
		royalties_per_ton_str = get(eachRecord, "royalties_per_ton");
		supervisor_labor_cost_per_haul_str = get(eachRecord, "sprvsr_lbr_cst_pr_hl");
		insurance_cost_per_haul_str = get(eachRecord, "insurance_cost_pr_hl");
		facility_cost_per_haul_str = get(eachRecord, "facility_cost_per_hl");
		depr_amort_excl_trk_cont_per_haul_str = get(eachRecord, "dpr_amt_x_trk_cnt_hl");
		other_oper_cost_per_haul_str = get(eachRecord, "other_oper_cst_pr_hl");
		selling_cust_svc_per_haul_str = get(eachRecord, "sllng_cst_svc_pr_hl");
		general_admin_per_haul_str = get(eachRecord, "gen_admin_per_haul");
		//ind_bad_debt_str = get(eachRecord, "bad_dbt_indstry_fctr");
		ind_bad_debt_str = get(eachRecord, "bad_debt_pct_of_rvnu"); //As od email from James on 04/08/2014, use bad_debt_pct_of_rvnu column instead of bad_dbt_indstry_fctr
		break;	//Only one record is expected
	}
	
		//laborCostPerMinute - Found in divisionKPI_IND, based on the division number. Used in calculations of operatingCost
		laborCostPerMinute = 0.0;
		if(isnumber(laborCostPerMinuteStr)){	//Convert the table result to a float for use in calculations
			laborCostPerMinute = atof(laborCostPerMinuteStr);
		}
		put(returnDict, "laborCostPerMinute", string(laborCostPerMinute));
		
		//truckCostPerMinute - Found in divisionKPI_IND, based on the division number and waste type. Used in calculations of operatingCost
		truckCostPerMinute = 0.0;
		if(isnumber(truckCostPerMinuteStr)){	//Convert the table result to a float for use in calculations
			truckCostPerMinute = atof(truckCostPerMinuteStr);
		}
		put(returnDict, "truckCostPerMinute", string(truckCostPerMinute));
		
		//containerMntPerHaul - Found in divisionKPI_IND, based on the division number. Used in calculations of assetCost
		containerMntPerHaul = 0.0;
		if(isnumber(containerMaintPerHaulStr)){	//Convert the table result to a float for use in calculations
			containerMntPerHaul = atof(containerMaintPerHaulStr);
		}
		put(returnDict, "containerMntPerHaul", string(containerMntPerHaul));
		
		//truckHoursPerMonth  - Found in divisionKPI_IND, based on the division number and waste type. Used in calculations of assetCost and ROA
		truckHoursPerMonth = 0.0;
		if(isnumber(truckHoursPerMonthStr)){	//Convert the table result to a float for use in calculations
			truckHoursPerMonth = atof(truckHoursPerMonthStr);
		}
		put(returnDict, "truckHoursPerMonth", string(truckHoursPerMonth));
		
		//averagePricePerTon - Found in divisionKPI_IND, based on division number and waste type Used in calculations of ROA
		averagePricePerTon = 0.0;
		if(isnumber(averagePricePerTonStr)){	//Convert the table result to a float for use in calculations
			averagePricePerTon = atof(averagePricePerTonStr);
		}
		put(returnDict, "averagePricePerTon", string(averagePricePerTon));
	
	//=============================== END - Lookups on the divisionKPI_IND table ===============================//
	
	//=============================== START - Lookups on the Parts database ===============================//
	partsRecordSet = bmql("SELECT custom_field2, custom_field4, custom_field15, custom_field18, custom_field11 FROM _parts WHERE part_number = $partNumber");	
	
	for eachRecord in partsRecordSet{
		containerValueStr = get(eachRecord, "custom_field2");
		depreciationPerMonthStr = get(eachRecord, "custom_field4");
		compactorLifeStr = get(eachRecord, "custom_field15");
		compactorValueStr = get(eachRecord, "custom_field18");
		hasCompactor = getInt(eachRecord, "custom_field11");
		break;	//Only one record is expected
	}
	
	put(returnDict, "hasCompactor", string(hasCompactor));
	
		//containerValue - Found in parts database, based on the partNumber (SKU). Used in calculations of ROA
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
		
		//compactorLife - Found in parts database, based on the partNumber (SKU). Used in calculations of assetCost
		compactorLife = 0.0;
		if(isnumber(compactorLifeStr)){	//Convert the table result to a float for use in calculations
			compactorLife = atof(compactorLifeStr);
		}
		// for customers renting a compactor from us, incorporate this into quote
		// assume full depreciation over contract term; if contract term is NA, assume 36 months
		if(isCompactorCustomerOwned == 0){
			tempArray = float[];
			//Updated 05/12/2014
			//append(tempArray, contractTerm);
			//append(tempArray, 36.0);
			//compactorLife = max(tempArray);
			if(compactorLife == 0.0){
				compactorLife = 120.0;
			}
		}
		put(returnDict, "compactorLife", string(compactorLife));
		
		//compactorValue - Found in parts database, based on the partNumber (SKU). Used in calculations of assetCost. Comes from Config if this is a new customer
		//if(salesActivity <> "New/New"){
		
		if(isnumber(compactorValueStr)){	//Convert the table result to a float for use in calculations
			compactorValue = atof(compactorValueStr);
		}
		compactor_depr = 0.0;
		if(compactorValueConfig > 0 AND isCompactorCustomerOwned == 0 AND compactorLife > 0){
			compactor_depr = compactorValueConfig/compactorLife; //atof(compactorLifeStr);
		}else{
			if(isnumber(compactorLifeStr) AND atof(compactorLifeStr) > 0){
				compactor_depr = compactorValue/atof(compactorLifeStr);
			}
		}
		put(returnDict, "compactor_depr", string(compactor_depr));
		//Added below IF condition on 8th March 2014, source: pricing script 7th March 2014
		//For customers renting a compactor from us, incorporate this into quote
		
		//Updated 04/07/2014
		/*if(isCompactorCustomerOwned == 0){
			if(((containerType <> "Self-Contained Container") AND (containerType <> "Stationary Compactor"))){
				compactorValue = compactorValueConfig;	
			}
		}*/
		if(hasCompactor == 1 AND compactorValueConfig > 0 AND isCompactorCustomerOwned == 0){
			compactorValue = compactorValueConfig;	
		}
		put(returnDict, "compactorValue", string(compactorValue));
	
	//=============================== END - Lookups on the Parts database ===============================//
	
	//=============================== START - Lookups on the trucks table ===============================//
		
		//truckDepreciationPerMonth - Found in trucks, based on the route type from Config and region from Commerce. Used in calculations of assetCost
		trucksRecordSet = bmql("SELECT depreciationPerMonth, cost FROM trucks WHERE type = $routeType AND region = $region");
		
		for eachRecord in trucksRecordSet{
			truckDepreciationPerMonthStr = get(eachRecord, "depreciationPerMonth");
			truckAssetsStr = get(eachRecord, "cost");
			break;	//Only one record is expected
		}
		truckDepreciationPerMonth = 0.0;
		if(isnumber(truckDepreciationPerMonthStr)){	//Convert the table result to a float for use in calculations
			truckDepreciationPerMonth = atof(truckDepreciationPerMonthStr);
		}
		put(returnDict, "truckDepreciationPerMonth", string(truckDepreciationPerMonth));
		
		truckAssets = 0.0;
		if(isnumber(truckAssetsStr)){	//Convert the table result to a float for use in calculations
			truckAssets = atof(truckAssetsStr);
		}
		put(returnDict, "truckAssets", string(truckAssets));
	
	//=============================== END - Lookups on the trucks table ===============================//

	//=============================== START - Lookups on the Disposal_Sites table ===============================//
	//Replaced disposalSiteCosts table with Disposal_Sites
	disposalSiteCostsRecordSet = bmql("SELECT WasteType, cost, dsp_xfer_priceperton FROM Disposal_Sites WHERE Site_Name = $siteName AND WasteType = $wasteType AND DisposalSite_DivNbr = $division");
	for eachRecord in disposalSiteCostsRecordSet{
		disposalCostPerTonStr = get(eachRecord, "cost");
		wasteType_db = get(eachRecord, "WasteType");
		dsp_xfer_price_per_ton_str = get(eachRecord, "dsp_xfer_priceperton");
		//if wastetype is solid waste, but disp transfer price is 0.0, get the price for 3rd part disposal which is in KPI table
		//If waste type is not solid waste (then all other are recycling wastes), 3rd party does not apply, so in that case, 0.0 value for disp transfer price should be used as is in calculations
		if(lower(wasteType_db) == "solid waste" AND dsp_xfer_price_per_ton_str == "0.0"){
			thirdPartyDispTrsferPriceRecSet = bmql("SELECT default_disposal_3p FROM tbl_division_kpi_ind WHERE div_nbr = $division");
			for each in thirdPartyDispTrsferPriceRecSet{
				dsp_xfer_price_per_ton_str = get(each, "default_disposal_3p");
				break;
			}
		}
		break;	//Only one record is expected
	}
	disposalCostPerTon = 0.0;
	if(isnumber(disposalCostPerTonStr)){	//Convert the table result to a float for use in calculations
			disposalCostPerTon = atof(disposalCostPerTonStr);
	}
	put(returnDict, "disposalCostPerTon", string(disposalCostPerTon));
	//=============================== END - Lookups on the Disposal_Sites table ===============================//
	
	//=============================== START - Lookups on the industries table ===============================//
	industriesRecordSet = bmql("SELECT container, bad_debt_factor FROM industries WHERE industryName = $industry");
	
	for eachRecord in industriesRecordSet{
		containerStr = get(eachRecord, "container");
		bad_debt_factor_str = get(eachRecord, "bad_debt_factor");
		break;	//Only one result is desired
	}
	
		//containerFactor - Found in industries, based on the industry. Used in calculations of assetCost
		containerFactor = 0.0;
		if(isnumber(containerStr)){	//Convert the table result to a float for use in calculations
			containerFactor = atof(containerStr);
		}
		put(returnDict, "containerFactor", string(containerFactor));
	
	//=============================== END - Lookups on the industries table ===============================//
	
	//=============================== START - Lookups on miscConfigData table ===============================//
	//Temporarily used to get values that may come from other sources later
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
	
	//=============================== START - Lookups on Disposal_Sites	table ===============================//
	
	marketRate = 0.0;  //marketRate is disposal price associated with the disposal location;
	// currently marketRate is in the LargeContainers::tbl_ind_margins data table.  For wave 4 this functionality needs to move to the LargeContainers::Disposal_Sites table
	
	if(isnumber(alloc_rentalStr) AND rental_type <> ""){
		alloc_rental = atoi(alloc_rentalStr);
		//The or condition is needed for wasteType because for solid wastes, only category of Solid waste entry is there, not "solid waste TYPES' entry
		disposalSitesRecordSet = bmql("SELECT market_rate FROM Disposal_Sites WHERE Site_Name = $siteName AND (WasteType = $wasteType OR WasteType = 'Solid Waste') AND DisposalSite_DivNbr = $division");
		for eachRec in disposalSitesRecordSet{
			marketRateStr = get(eachRec, "market_rate");
		}
		if(isnumber(marketRateStr)){
			marketRate = atof(marketRateStr);
		}
	}
	//=============================== END - Lookups on tbl_IND_margins table ===============================//
	
//=============================== END - Table Lookups ===============================//

//=============================== START - Haul Calculation ===============================//
	//=============================== START - Operating Cost Calculation ===============================//

	haulsPerMonthPerContainer = haulsPerPeriod;
	put(returnDict, "haulsPerMonthPerContainer", string(haulsPerMonthPerContainer));

	haulMinutesPerMonth = totalTimePerHaul; 
	put(returnDict, "haulMinutesPerMonth", string(haulMinutesPerMonth));
	
	driverCost = haulMinutesPerMonth * laborCostPerMinute;
	put(returnDict, "driverCost", string(driverCost));
	
	truckCost = haulMinutesPerMonth * truckCostPerMinute;
	put(returnDict, "truckCost", string(truckCost));
	
	containerDepreciation = 0.0;
	if(haulsPerContainer <> 0.0){
		containerDepreciation = (1 - isContainerCustomerOwned) * containerDepreciationPerContainer * containerFactor / haulsPerContainer;
	}
	put(returnDict, "containerDepreciation", string(containerDepreciation));
	
	compactorDepreciation = 0.0;
	compactorDepreciationPerContainer = 0.0;
	if(compactorLife <> 0.0 AND haulsPerContainer <> 0){
		//Updated 04/07/2014
		compactorDepreciation = (1 - isCompactorCustomerOwned) * (compactorValue / compactorLife) * containerFactor / haulsPerContainer;
		compactorDepreciationPerContainer = compactorValue / compactorLife;
	}
	put(returnDict, "compactorDepreciation", string(compactorDepreciation));
	put(returnDict, "compactorDepreciationPerContainer", string(compactorDepreciationPerContainer));
	
	truckDepreciation = 0.0;
	if(truckHoursPerMonth <> 0.0){
		truckDepreciation = (truckDepreciationPerMonth / truckHoursPerMonth) * (haulMinutesPerMonth / 60);
	}
	put(returnDict, "truckDepreciation", string(truckDepreciation));
	
	//# 20140228: note that oper_comp_depr has been removed
	// and will be moved to rental calcs for compactor
	//operatingCost = driverCost + truckCost + containerDepreciation + compactorDepreciation + truckDepreciation + (containerMntPerHaul * (1 - isContainerCustomerOwned));
	operatingCost = driverCost + truckCost + containerDepreciation + truckDepreciation + (containerMntPerHaul * (1 - isContainerCustomerOwned));

	put(returnDict, "operatingCost", string(operatingCost));
	//=============================== END - Operating Cost Calculation ===============================//
	
	//=============================== START - ROA Calculation ===============================//
	
		containerROA = 0.0;
		compactorROA = 0.0;
		
		if(estHaulsPerMonth <> 0.0){
			containerROA = containerValue * containerQuantity * floorROI / 12.0 * (1 - isContainerCustomerOwned) / estHaulsPerMonth;
			//Updated 05/11/2014
			compactorROA = (compactorValue * containerQuantity * floorROI / 12.0 * (1 - isCompactorCustomerOwned) /12.0) / estHaulsPerMonth;
		}
		
		put(returnDict, "containerROA", string(containerROA));
		put(returnDict, "compactorROA", string(compactorROA));
	
		//estTonsPerHaul and estHaulsPerMonth come from Config. targetRatePerHaul and averagePricePerTon come from a data table
		averageMonthlyRevenue = (averagePricePerTon * estTonsPerHaul) * estHaulsPerMonth;//+ (estHaulsPerMonth * targetRatePerHaul);
		put(returnDict, "averageMonthlyRevenue", string(averageMonthlyRevenue));
		
		workingCapital = (averageDaysAcctsReceivable / 30.0) * (averagePricePerTon * estTonsPerHaul) * floorROI / 12.0;
		put(returnDict, "workingCapital", string(workingCapital));
		
		//truckAssets comes from a data table. customerSharePct is calculated earlier.
		//Updated on 30 April 2014
		//truckAllocatedValue = truckAssets / 14.0 / 2.0; //* customerSharePct;
		truckAllocatedValue = truckAssets / 2.0; //* customerSharePct;
		put(returnDict, "truckAllocatedValue", string(truckAllocatedValue));
		
		truckROA = 0.0;
		if(truckHoursPerMonth <> 0.0){
			//truckROA = truckAllocatedValue / truckHoursPerMonth * floorROI * (totalTimePerHaul / 60.0);
			//Updated on 30 April 2014
			truckROA = truckAllocatedValue / truckHoursPerMonth * (floorROI/12.0) * (totalTimePerHaul / 60.0);
		}
		put(returnDict, "truckROA", string(truckROA));
		
		flatRateCommission = 0.0;
		commissionRate = 0.75;
		if(accountType == "Temporary"){
			commissionRate = 0.0;
			flatRateCommission	= 15.0;		
		}
		put(returnDict, "commissionRate", string(commissionRate));
		
		//averageMonthlyRevenue calculated earlier. 
		if(contractTerm <> 0.0){
			//Updated 05/12/2014
			/* 20140509: adjust contract term to choose the max of the term or 12 months 
			   made this adjustment to prevent all commission from being loaded into a single month or unsigned contract
			 */
			if(contractTerm < 12.0){
				contractTerm = 12.0;
			}
			commission = (averagePricePerTon * estTonsPerHaul * commissionRate / contractTerm) + flatRateCommission;
		}
		put(returnDict, "commission", string(commission));
	
	//Final ROA calculation
	//containerValue comes from data table. 
	//ROA = containerROA + compactorROA + truckROA + workingCapital + commission;
	ROA = containerROA + truckROA + workingCapital + commission;
	put(returnDict, "ROA", string(ROA));
	//=============================== END - ROA Calculation ===============================//

//Final Haul calculation
haul = operatingCost + ROA;
put(returnDict, "perHaul", string(haul));

//Haul price per month
monthlyHaul = haul * estHaulsPerMonth;
put(returnDict, "haul", string(monthlyHaul));
//=============================== END - Haul Calculation ===============================//

//=============================== START - Disposal Calculation ===============================//
	//=============================== START - Customer Tons Per Month Calculation ===============================//
	//Final customerTonsPerMonth calculation
	//estTonsPerHaul comes from Config. haulsPerMonthPerContainer calculated earlier
	customerTonsPerMonth = haulsPerMonthPerContainer * estTonsPerHaul;
	put(returnDict, "customerTonsPerMonth", string(customerTonsPerMonth));
	
	disposalPerHaul = disposalCostPerTon * estTonsPerHaul;
	put(returnDict, "disposalPerHaul", string(disposalPerHaul));
	//=============================== END - Customer Tons Per Month Calculation ===============================//
	
//Final Disposal calculation
//disposalCostPerTon comes from data table. Commission calculated earlier.
disposal = (disposalCostPerTon * customerTonsPerMonth); 
put(returnDict, "disposal", string(disposal));
//=============================== END - Disposal Calculation ===============================//

costToServeHaul = haul + disposalPerHaul;
put(returnDict, "costToServeHaul", string(costToServeHaul));

costToServeMonth = costToServeHaul * estHaulsPerMonth;
put(returnDict, "costToServeMonth", string(costToServeMonth));

//=============================== START - Rental Calculation ===============================//
price_rental_per_month = marketRate * alloc_rental;
rental_factor = price_rental_per_month;
rental_floor = 0.0;
if(rental_type == "Daily"){
	rental_factor = 365.0/12.0;
}
if(rental_factor > 0){
	rental_floor = ((containerDepreciation  + compactorDepreciation + containerROA + compactorROA) * estHaulsPerMonth * alloc_rental/ rental_factor); 
}

put(returnDict, "marketRate", string(marketRate));
put(returnDict, "price_rental_per_month", string(price_rental_per_month));
put(returnDict, "rental_factor", string(rental_factor));
put(returnDict, "rental_floor", string(rental_floor));
//=============================== END - Rental Calculation ===============================//
//=============================== START - Financial Summary Cost Calculation ===============================//
/*
cts_month_incl_oh:= cts_haul_incl_oh * hauls_per_month
cts_haul_incl_oh:=cost_operating + cost_dsp_xfer_per_haul + cost_roa +
                  cost_overhead - roa_commission
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
if(isnumber(dsp_xfer_price_per_ton_str)){
	dsp_xfer_price_per_ton = atof(dsp_xfer_price_per_ton_str);
}
cost_dsp_xfer_per_haul = dsp_xfer_price_per_ton * estTonsPerHaul;
put(returnDict, "cost_dsp_xfer_per_haul", string(cost_dsp_xfer_per_haul));

cost_dsp_xfer_per_month = cost_dsp_xfer_per_haul * estHaulsPerMonth;
put(returnDict, "cost_dsp_xfer_per_month", string(cost_dsp_xfer_per_month));

//Overhead Cost Calculations
/*overhead costs include 
1. royalties
2. supervisor labor cost
3. insurance
4. facility
5. other_depreciation
6. bad_debt
7. other_operation
8. sales
9. gen_admin
All these are look ups from division_kpi datatable
*/

royalties_per_ton = 0.0;
if(isnumber(royalties_per_ton_str)){
	royalties_per_ton = atof(royalties_per_ton_str);
}
oh_royalties = royalties_per_ton * estTonsPerHaul;

supervisor_labor_cost_per_haul = 0.0;
if(isnumber(supervisor_labor_cost_per_haul_str)){
	supervisor_labor_cost_per_haul = atof(supervisor_labor_cost_per_haul_str);
}
oh_supervisor = supervisor_labor_cost_per_haul;
 
insurance_cost_per_haul = 0.0;
if(isnumber(insurance_cost_per_haul_str)){
	insurance_cost_per_haul = atof(insurance_cost_per_haul_str);
}
oh_insurance = insurance_cost_per_haul;

facility_cost_per_haul = 0.0;
if(isnumber(facility_cost_per_haul_str)){
	facility_cost_per_haul = atof(facility_cost_per_haul_str);
}
oh_facility = facility_cost_per_haul;

depr_amort_excl_trk_cont_per_haul = 0.0;
if(isnumber(depr_amort_excl_trk_cont_per_haul_str)){
	depr_amort_excl_trk_cont_per_haul = atof(depr_amort_excl_trk_cont_per_haul_str);
}
oh_other_depr = depr_amort_excl_trk_cont_per_haul;

//p_y = averagePricePerYard as per James pricing function
ind_bad_debt = 0.0;
bad_debt_factor = 0.0;
if(isnumber(ind_bad_debt_str)){
	ind_bad_debt = atof(ind_bad_debt_str);
}
if(isnumber(bad_debt_factor_str)){
	bad_debt_factor = atof(bad_debt_factor_str);
}
put(returnDict, "bad_debt_factor", string(bad_debt_factor));
oh_bad_debt = averagePricePerTon * estTonsPerHaul * ind_bad_debt * bad_debt_factor; //Updated as per Pricing Script 14 March 2014
 
other_oper_cost_per_haul = 0.0;
if(isnumber(other_oper_cost_per_haul_str)){
	other_oper_cost_per_haul = atof(other_oper_cost_per_haul_str);
}
oh_other_opex = other_oper_cost_per_haul;

//Marketing component does not apply for largecontainers
/*marketing_per_lift = 0.0;
if(isnumber(marketing_per_lift_str)){
	marketing_per_lift = atof(marketing_per_lift_str);
}
oh_marketing = marketing_per_lift * liftsPerMonth;*/

selling_cust_svc_per_haul = 0.0;
if(isnumber(selling_cust_svc_per_haul_str)){
	selling_cust_svc_per_haul = atof(selling_cust_svc_per_haul_str);
}
oh_sales = selling_cust_svc_per_haul;

general_admin_per_haul = 0.0;
if(isnumber(general_admin_per_haul_str)){
	general_admin_per_haul = atof(general_admin_per_haul_str);
}
oh_gen_admin = general_admin_per_haul;

//Overhead cost summation
cost_overhead = oh_royalties + oh_supervisor + oh_insurance + oh_facility + oh_other_depr + oh_bad_debt + oh_other_opex + oh_sales + oh_gen_admin;

//Final Financial Summary cost
//cts_month_incl_oh :=(operatingCost + cost_dsp_xfer_per_haul + ROA + cost_overhead - commission) * estHaulsPerMonth
cts_haul_incl_oh = operatingCost + cost_dsp_xfer_per_haul + ROA + cost_overhead - commission;
cts_month_incl_oh = cts_haul_incl_oh * estHaulsPerMonth;

//=============================== END - Financial Summary Cost Calculation ===============================//
put(returnDict, "cts_month_incl_oh", string(cts_month_incl_oh));
return returnDict;