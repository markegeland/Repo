/*
================================================================================
Name:   		Calculate Guardrails
Author:  		Zach Schlieder
Create date:  	1/14/13
Description:	Calculates the Base, Target, and Stretch for a large or small container price based on the cost to serve and other config attributes
        
Input:   		stringDict: String Dictionary - Contains values of Config and Commerce attributes used in calculations and table calls
                    
Output:  		String Dictionary - Contains attribute name and value pairs for use in Config or Commerce

Updates:	Srikar - 02/05/2014 - Updated basePrice, targetPriceAdj, stretchPriceAdj formulas
Updates:	Srikar - 03/15/2014 - Updated haulBase, haulTarget, haulStretch formulas for small container
		 J Felberg - 11/15/2014 - Changed "Fixed Environmental Recovery Fee (ERF)" to "Fixed Environment Recovery Fee (ERF)"
     J Palubinskas - 11/17/2014 - Updated divisionFeeRate queries to look up based on Lawson and InfoPro division
		20141204 - Aaron Quintanilla - Corrected Industrial Flat Rate Disposal Price Rounding Error
		20141210 - Aaron Quintanilla - Corrected disposal fee removal 
		20151201 - Gaurav Dawar - Lines: 278, 2282, 2304, 2345 - Changes made to fix delivery amount when there is a change in container code in service change.
		02/11/15 - Gaurav Dawar - #406 - Compactor and rental calculation errors (large container)
		02/18/15 - Gaurav Dawar - #427 - Added compactor ROI
    
=====================================================================================================
*/


//=============================== START - Variable Initialization ===============================//
//Default Variables
returnDict = dict("string");
dateFormat="yyyy-MM-dd";

erfOnFrfRateStr = "";
siteName = "";
wasteType = "";
division = "";
accountType = "";
billingType = "";
priceType = "";
wasteCategory = "";
includeERF = "";
includeFRF = "";
costToServeMonthStr = "";
baseMarginStr = "";
targetMarginStr = "";
stretchMarginStr = "";
driverCostStr = "";
truckCostStr = "";
truckROAStr = "";
truckDepreciationStr = "";
commissionStr = "";
tonsPerHaulStr = "";
haulsPerMonthStr = "";
commissionRateStr = "";
flatRateCommissionStr = "";
yardsPerMonthStr = "";
competitorFactorStr = "";
serviceChangeType = "";
priceAdjustmentReason = "";
cr_new_businessStr = "0";
customer_id = "";
containerGroup = "";
customer_zip = "";
industry = "";
segment = "";
infoProDivNum = "";
serviceCode = "";
containerSize = "";
containerStr = "1";
quantityStr = "1";
workingCapitalStr = "";
containerMntPerHaulStr = "";
isContainerCustomerOwnedStr = "";
hasCompactorStr = "";
/*
begin1 = getcurrenttimeinmillis();
begin2 = 0.0;
begin3 = 0.0;
begin4 = 0.0;
*/
perHaulCostsStr = "0.0"; //Added as a part of SR 3-9428639511


if(containskey(stringDict, "erfOnFrfRate")){
	erfOnFrfRateStr = get(stringDict, "erfOnFrfRate");
}
if(containskey(stringDict, "siteName")){
	siteName = get(stringDict, "siteName");
}
if(containskey(stringDict, "wasteType")){
	wasteType = get(stringDict, "wasteType");
}
if(containskey(stringDict, "division")){
	division = get(stringDict, "division");
}
if(containskey(stringDict, "accountType")){
	accountType = get(stringDict, "accountType");
}
if(containskey(stringDict, "billingType")){
	billingType = get(stringDict, "billingType");
}
if(containskey(stringDict, "priceType")){
	priceType = get(stringDict, "priceType");
	}
if(containskey(stringDict, "wasteCategory")){
	wasteCategory = get(stringDict, "wasteCategory");
}

if(containskey(stringDict, "salesActivity_config")){
	serviceChangeType = get(stringDict, "salesActivity_config");
}
if(containskey(stringDict, "priceAdjustmentReason_config")){
	priceAdjustmentReason = get(stringDict, "priceAdjustmentReason_config");
}
if(containskey(stringDict, "customer_id")){
	customer_id = get(stringDict, "customer_id");
}
if(containskey(stringDict, "containerGroup")){
	containerGroup = get(stringDict, "containerGroup");
}
if(containskey(stringDict, "customer_zip")){
	customer_zip = get(stringDict, "customer_zip");
}
if(containskey(stringDict, "industry_quote")){
	industry = get(stringDict, "industry_quote");
}
if(containskey(stringDict, "segment")){
	segment = get(stringDict, "segment");
}

//feesToCharge = get(stringDict, "feesToCharge");
	//Temporary
if(containskey(stringDict, "includeERF")){
	includeERF = get(stringDict, "includeERF");
}
if(containskey(stringDict, "includeFRF")){
	includeFRF = get(stringDict, "includeFRF");
}
if(containskey(stringDict, "costToServeMonth")){
	costToServeMonthStr = get(stringDict, "costToServeMonth");
}
if(containskey(stringDict, "baseMargin")){
	baseMarginStr = get(stringDict, "baseMargin");
}
if(containskey(stringDict, "targetMargin")){
	targetMarginStr = get(stringDict, "targetMargin");
}
if(containskey(stringDict, "stretchMargin")){
	stretchMarginStr = get(stringDict, "stretchMargin");
}
if(containskey(stringDict, "driverCost")){
	driverCostStr = get(stringDict, "driverCost");
}
if(containskey(stringDict, "truckCost")){
	truckCostStr = get(stringDict, "truckCost");
}
if(containskey(stringDict, "truckROA")){
	truckROAStr = get(stringDict, "truckROA");
}
if(containskey(stringDict, "truckDepreciation")){
	truckDepreciationStr = get(stringDict, "truckDepreciation");
}
if(containskey(stringDict, "commission")){
	commissionStr = get(stringDict, "commission");
}
if(containskey(stringDict, "tonsPerHaul")){
	tonsPerHaulStr = get(stringDict, "tonsPerHaul");
}
if(containskey(stringDict, "haulsPerMonth")){
	haulsPerMonthStr = get(stringDict, "haulsPerMonth");
}
if(containskey(stringDict, "commissionRate")){
	commissionRateStr = get(stringDict, "commissionRate");
}
if(containskey(stringDict, "flatRateCommission")){
	flatRateCommissionStr = get(stringDict, "flatRateCommission");
}
if(containskey(stringDict, "yardsPerMonth")){
	yardsPerMonthStr = get(stringDict, "yardsPerMonth");
}
if(containskey(stringDict, "competitorFactor")){
	competitorFactorStr = get(stringDict, "competitorFactor");
}
competitiveBidStr = "0";
if(containskey(stringDict, "competitiveBidAmount")){
	competitiveBidStr = get(stringDict, "competitiveBidAmount");
}

if(containskey(stringDict, "cr_new_business")){
	cr_new_businessStr = get(stringDict, "cr_new_business");
}

if(containskey(stringDict, "infoProDivNumber")){
	infoProDivNum = get(stringDict, "infoProDivNumber");
}

if(containskey(stringDict, "serviceCode")){
	serviceCode = get(stringDict, "serviceCode");
}
if(containskey(stringDict, "containerSize")){
	containerSize = get(stringDict, "containerSize");
}
containerSizeFloat = 0.0;
if(isnumber(containerSize)) {
	containerSizeFloat = atof(containerSize);
}

if(containskey(stringDict, "quantity")){
	quantityStr = get(stringDict, "quantity");
}

if(containskey(stringDict, "workingCapital")){
	workingCapitalStr = get(stringDict, "workingCapital");
}

if(containskey(stringDict, "containerMntPerHaul")){
	containerMntPerHaulStr = get(stringDict, "containerMntPerHaul");
}

if(containskey(stringDict, "isContainerCustomerOwned")){
	isContainerCustomerOwnedStr = get(stringDict, "isContainerCustomerOwned");
}

isFRFFixed = false;
isERFFixed = false;
if(containskey(stringDict, "isFRFFixed")){
	isFRFFixedStr = get(stringDict, "isFRFFixed");
	if(isFRFFixedStr == "true"){
		isFRFFixed = true;
	}
}

if(containskey(stringDict, "isERFFixed")){
	isERFFixedStr = get(stringDict, "isERFFixed");
	if(isERFFixedStr == "true"){
		isERFFixed = true;
	}
}


customerType = "New/New";
//Rental - Large container
market_rateStr = "";
if(containskey(stringDict, "marketRate")){
	market_rateStr= get(stringDict, "marketRate");
}
rental_type = "";
if(containskey(stringDict, "rental")){
	rental_type = get(stringDict, "rental");
}
containerType_lStr = "";
if(containskey(stringDict, "containerType_l")){
	containerType_lStr = get(stringDict, "containerType_l");
}
containerDepreciationStr = "0";
if(containskey(stringDict, "containerDepreciation")){
	containerDepreciationStr = get(stringDict, "containerDepreciation");
}
compactorDepreciationStr = "0";
if(containskey(stringDict, "compactorDepreciation")){
	compactorDepreciationStr = get(stringDict, "compactorDepreciation");
}
containerROAStr = "0";
if(containskey(stringDict, "containerROA")){
	containerROAStr = get(stringDict, "containerROA");
}
compactorROAStr= "0";
if(containskey(stringDict, "compactorROA")){
	compactorROAStr = get(stringDict, "compactorROA");
}

if(containskey(stringDict, "salesActivity_quote")){
	customerType = get(stringDict, "salesActivity_quote");
}
compactorCustomerOwned = 0;
if(containskey(stringDict, "isCompactorCustomerOwned")){
	compactorCustomerOwnedStr = get(stringDict, "isCompactorCustomerOwned");
	if(isnumber(compactorCustomerOwnedStr)){
		compactorCustomerOwned = atoi(compactorCustomerOwnedStr);
	}
}
routeType = "";
routeType_updated = "";//Initializing new variable
if(containskey(stringDict, "routeType")){
	routeType = get(stringDict, "routeType");
}
initialTermStr = "";
if(containskey(stringDict, "initialTerm_quote")){
	initialTermStr = get(stringDict, "initialTerm_quote");
}

compactorValueStr = "0";
compactorLifeStr = "0";
if(containskey(stringDict, "compactorValue")){
	compactorValueStr = get(stringDict, "compactorValue");
}
if(containskey(stringDict, "compactorLife")){
	compactorLifeStr = get(stringDict, "compactorLife");
}

if(containskey(stringDict, "hasCompactor")){
	hasCompactorStr = get(stringDict, "hasCompactor");
}
//Added as a part of SR 3-9428639511, Pulls value for cost_operating + cost_roa from util.largeContainerPricing
if(containskey(stringDict, "perHaulCosts")){
	perHaulCostsStr = get(stringDict, "perHaulCosts");
}

//Convert necessary variables from string to integer for use in calculations
frfFlag = 0;
erfFlag = 0;
erfOnFrfFlag = 0;
allocatedDisposalFlag = 0;
permanentFlag = 0;
currentERF = 0.0;
currentFRF = 0.0;
isContainerCustomerOwned = 0;
hasCompactor = 0;

cr_new_business = 1; //Initialize for new business, overwrite for price changes for existing customers
cr_general_save = 0;
cr_rollback_of_pi = 0;
cr_competitive_bid = 0;
cr_service_change = 0;

//START - temporary workaround while resolving fee issues. Requires running Save & Price action twice
/*
if(feesToCharge <> ""){
	if(find(feesToCharge, "FRF") <> -1){
		frfFlag = 1;
	}
	if(find(feesToCharge, "ERF") <> -1){
		erfFlag = 1;
	}
}*/
if(includeERF == "Yes"){
	erfFlag = 1;
}
if(includeFRF == "Yes"){
	frfFlag = 1;
}
/* converted this to be table driven - 18th March 2014
if(erfFlag == 1 AND frfFlag == 1){
	erfOnFrfFlag = 1;
}*/
//END - temporary workaround while resolving fee issues.


if(isnumber(cr_new_businessStr)){
	cr_new_business = atoi(cr_new_businessStr);
}

if(isnumber(hasCompactorStr)){
	hasCompactor = atoi(hasCompactorStr);
}

isExistingCustomer = false;
if(customerType <> "New/New" AND customerType <> "New from Competitor"){
	isExistingCustomer = true;
}

//if(find(billingType, "Disposal") <> -1){
if(find(billingType, "Disposal") <> -1 OR billingType == "Haul + Minimum Tonnage"){
	allocatedDisposalFlag = 1;
}
if(accountType == "Permanent" OR accountType == "Seasonal"){
	permanentFlag = 1;
}

//Convert necessary variables from string to float for use in calculations
erfOnFrfRate = 0.0;
costToServeMonth = 0.0;
driverCost = 0.0;
truckCost = 0.0;
truckROA = 0.0;
commission = 0.0;
tonsPerHaul = 0.0;
haulsPerMonth = 0.0;
commissionRate = 0.0;
flatRateCommission = 0.0;
competitorFactor = 0.0;
rental_floor = 0.0;
rental_factor = 0.0;
rental_base = 0.0;
rental_target = 0.0;
rental_stretch = 0.0;
alloc_rental = 0;
competitive_bid_amt = 0.0;
initialTerm = 0.0;
compactor_cost = 0.0;
compactor_life = 0.0;
quantity = 1;
workingCapital = 0.0;
containerMntPerHaul = 0.0;
perHaulCosts = 0.0; //Added as a part of SR 3-9428639511

if(isnumber(erfOnFrfRateStr)){
	erfOnFrfRate = atof(erfOnFrfRateStr);
}
if(isnumber(costToServeMonthStr)){
	costToServeMonth = atof(costToServeMonthStr);
	put(returnDict, "J6", string(costToServeMonth));
	put(returnDict, "J7", costToServeMonthStr);
}
if(isnumber(driverCostStr)){
	driverCost = atof(driverCostStr);
}
if(isnumber(truckCostStr)){
	truckCost = atof(truckCostStr);
}
if(isnumber(truckROAStr)){
	truckROA = atof(truckROAStr);
}
if(isnumber(truckDepreciationStr)){
	truckDepreciation = atof(truckDepreciationStr);
}
if(isnumber(commissionStr)){
	commission = atof(commissionStr);
}
if(isnumber(tonsPerHaulStr)){
	tonsPerHaul = atof(tonsPerHaulStr);
}
if(isnumber(haulsPerMonthStr)){
	haulsPerMonth = atof(haulsPerMonthStr);
}
if(isnumber(commissionRateStr)){
	commissionRate = atof(commissionRateStr);
}
if(isnumber(flatRateCommissionStr)){
	flatRateCommission = atof(flatRateCommissionStr);
}
if(isnumber(yardsPerMonthStr)){
	yardsPerMonth = atof(yardsPerMonthStr);
}
if(isnumber(competitorFactorStr)){
	competitorFactor = atof(competitorFactorStr);
}
if(isnumber(competitiveBidStr)){
	competitive_bid_amt = atof(competitiveBidStr);
}

if(isnumber(initialTermStr)){
	initialTerm = atof(initialTermStr);
}
if(isnumber(compactorValueStr)){
	compactor_cost = atof(compactorValueStr);
}
if(isnumber(compactorLifeStr)){
	compactor_life = atof(compactorLifeStr);
}
 
if(isnumber(quantityStr)){
	quantity = atoi(quantityStr);
}

if(isnumber(workingCapitalStr)){
	workingCapital = atof(workingCapitalStr);
}

if(isnumber(containerMntPerHaulStr)){
	containerMntPerHaul = atof(containerMntPerHaulStr);
}
if(isnumber(isContainerCustomerOwnedStr)){
	isContainerCustomerOwned = atoi(isContainerCustomerOwnedStr);
}


//Get the ERF on FRF flag based on division & Info pro number from divisionFeeRate table
divisionFeeRateRS = bmql("SELECT erf_on_frf FROM divisionFeeRate WHERE divisionNumber = $division AND infopro_div_nbr = $infoProDivNum");

for eachRecord in divisionFeeRateRS{
	erfOnFrfFlag = getInt(eachRecord, "erf_on_frf");
	break;
}
put(returnDict, "erfOnFrfFlag", string(erfOnFrfFlag));

containerType = "";
if(containskey(stringDict, "containerType")){
	containerType = get(stringDict, "containerType");
}

if(containerType == "Containers" OR priceType == "Containers"){
	containerType = "Small Container";
}
elif(containerType == "Large Containers" OR priceType == "Large Containers"){
	containerType = "Large Container";
}

//Added as a part of SR 3-9428639511, SR 3-9437035701
if(isnumber(perHaulCostsStr)){
	perHaulCosts = atof(perHaulCostsStr);
}


today = getdate();

//Initialize string variables set by data table results
//Get these from Division Fee Rate table
frfRateStr = "";
erfRateStr = "";
adminAmountStr = "";

//Get this from Disposal_Sites table
disposalCostPerTonStr = "";

//Get these from IND_Margins table
disposalRatePerTonStr = "";
baseMarginStr = "";
targetMarginStr = "";
stretchMarginStr = "";

//Get these from miscConfigData table
rounding_ind_haul = 1.0;
rounding_com_rate = 1.0;
rounding_ind_dsp = 1.0;
rounding_ind_rental = 1.0;
rounding_ind_rent_daily = 1.0;


rental_floor_price = 0.0;
rsg_compactor_base_roa = 0.0;
rsg_compactor_stretch_roa = 0.0;
rsg_compactor_target_roa = 0.0;
corp_monthly_min_per_container = 1.0;

//Get this from tbl_div_mark_ups table - mainly used in existing customer pricing
cat_yards_per_month = ""; 

//=============================== END - Variable Initialization ===============================//

//For Existing customer get fee, revenue and other information from Accounts table
//Initialize Account specific data for existing customers
is_frf_locked = 0;
is_erf_locked = 0;
monthly_rate = 0.0;
pi_amount = 0.0;
revenue = 0.0;
frf_rate_pct = 0.0;
erf_rate_pct = 0.0;
Admin_Rate = 0.0;
is_ERF_Charged = 0;
is_FRF_Charged = 0;
curr_margin_percentile = 0.0;
currentExpirationDateStr = "";
routeTypeDerived_current = "";
onsiteTimeInMins_current = "";
containerType_current = "";
accountNumber = "";
siteNumber = "";
//=============================== START - Table Lookups ===============================//

//=============================== START - Account Status table lookup  ===============================//
//Get Customer Account Specific Information and populate in respective variables for later use
if(isExistingCustomer){ //If its a new business for existing customer
	if(containskey(stringDict, "siteNumber_quote")){
		siteNumber = get(stringDict, "siteNumber_quote");
	}
	
	//Begin Tables lookup for existing customer
	//Get pi_amount from Account_Status data table
	
	
	accountStatusRecordset = bmql("SELECT monthly_rate, last_pi_amt, frf_rate_pct, erf_rate_pct, is_frf_locked, is_erf_locked, is_frf_charged, is_erf_charged, waste_type, Expiration_Dt, Container_Cd, margin_percentile, container_category, Admin_Rate, Site_Nbr, acct_nbr, is_erf_on_frf FROM Account_Status WHERE infopro_acct_nbr = $customer_id AND Container_Grp_Nbr = $containerGroup AND Site_Nbr = $siteNumber");
	//print accountStatusRecordset;
	//print "siteNumber=="+siteNumber;
	for eachRecord in accountStatusRecordset{
		 is_frf_locked = getInt(eachRecord, "is_frf_locked");
		 is_erf_locked = getInt(eachRecord, "is_erf_locked");
		 frf_rate_pct = getFloat(eachRecord, "frf_rate_pct");
		 erf_rate_pct = getFloat(eachRecord, "erf_rate_pct");
		 Admin_Rate = getFloat(eachRecord, "Admin_Rate");
		 		 
		 if(serviceChangeType <> ""){
			currentExpirationDateStr = get(eachRecord, "Expiration_Dt");
			routeTypeDerived_current = get(eachRecord, "Container_Cd"); 
			monthly_rate = getFloat(eachRecord, "monthly_rate");
			pi_amount = getFloat(eachRecord, "last_pi_amt"); 			
			is_FRF_Charged = getInt(eachRecord, "is_frf_charged");
			is_ERF_Charged = getInt(eachRecord, "is_erf_charged");
			is_erf_on_frf = getInt(eachRecord, "is_erf_on_frf");
			onsiteTimeInMins_current = ""; //To be added to table
			containerType_current = get(eachRecord, "container_category"); 
			curr_margin_percentile = getFloat(eachRecord, "margin_percentile");
			accountNumber = get(eachRecord, "acct_nbr");

		}
	}
	

	if(serviceChangeType <> ""){
		put(returnDict, "monthly_rate", string(monthly_rate));
		put(returnDict, "pi_amount", string(pi_amount));
		put(returnDict, "curr_margin_percentile", string(curr_margin_percentile));

		
		//revenue =  monthly_rate; //add frf and erf fee as well to this
		
		revenue = monthly_rate * (1 + frf_rate_pct/100 * is_FRF_Charged * (1 + is_erf_on_frf * erf_rate_pct/100 * is_ERF_Charged) + erf_rate_pct/100 * is_ERF_Charged);
		put(returnDict, "revenue", string(revenue));
		

	}
}

//=============================== END - Account Status table lookup  ===============================//

//Get values of all necessary values for calculations from data tables or parts database
//=============================== START - Lookups on the Division Fee Rate table ===============================//
frfRate = 0.0;
erfRate = 0.0;
adminAmount = 0.0;
divisionFeeRateRecordSet = bmql("SELECT fRFRate, eRFRate, infopro_div_nbr, adminAmount FROM divisionFeeRate WHERE divisionNumber = $division AND infopro_div_nbr = $infoProDivNum");
	
for eachRecord in divisionFeeRateRecordSet{
	
	frfRateStr = get(eachRecord, "fRFRate");
	erfRateStr = get(eachRecord, "eRFRate");
	//only use the admin amount if admin fee is selected
	
	if(get(stringDict, "feesToCharge") <> "" AND NOT(isnull(get(stringDict, "feesToCharge"))) AND find(get(stringDict, "feesToCharge"), "Admin Fee") > -1 ){
		adminAmountStr = get(eachRecord, "adminAmount");
	}
	
	break;
}

if(find(get(stringDict, "feesToCharge"), "Fixed Fuel Recovery Fee (FRF)") > -1){
	frfRateStr = string(frf_rate_pct);
}	

//Edit from 11/15/2014
if(find(get(stringDict, "feesToCharge"), "Fixed Environment Recovery Fee (ERF)") > -1){
	erfRateStr = string(erf_rate_pct);	
}


if(isExistingCustomer AND get(stringDict, "feesToCharge") <> "" AND NOT(isnull(get(stringDict, "feesToCharge"))) AND find(get(stringDict, "feesToCharge"), "Admin Fee") > -1 ){
	adminAmountStr = string(Admin_Rate);
}
		

	if(isnumber(frfRateStr)){	//Convert the table result to a float for use in calculations
		frfRate = atof(frfRateStr) / 100.0;
	}
	
	if(isnumber(erfRateStr)){	//Convert the table result to a float for use in calculations
		erfRate = atof(erfRateStr) / 100.0;
	}
	

	if(isnumber(adminAmountStr)){	//Convert the table result to a float for use in calculations
		adminAmount = atof(adminAmountStr);
	}
	
	put(returnDict, "frfRate", string(frfRate));
	put(returnDict, "erfRate", string(erfRate));
	put(returnDict, "adminAmount", string(adminAmount));
	//=============================== END - Lookups on the Division Fee Rate table ===============================//
	
	//=============================== Table Lookup for Rounding Values ===============================//
	//Get the rounding factors
	roundingFactorsRS = bmql("SELECT attribute, value FROM miscConfigData");
	for each in roundingFactorsRS{
		attr = get(each, "attribute");
		attr_value = getFloat(each, "value");
		if(attr == "rounding_ind_haul"){
			rounding_ind_haul = attr_value;
		}elif(attr == "rounding_com_rate"){
			rounding_com_rate = attr_value;
		}elif(attr == "rounding_ind_dsp"){
			rounding_ind_dsp = attr_value;
		}elif(attr == "rsg_compactor_base_roa"){
			rsg_compactor_base_roa = attr_value;
		}elif(attr == "rsg_compactor_stretch_roa"){
			rsg_compactor_stretch_roa = attr_value;
		}elif(attr == "rsg_compactor_target_roa"){
			rsg_compactor_target_roa = attr_value;
		}elif(attr == "rounding_ind_rental"){
			rounding_ind_rental = attr_value;
		}elif(attr == "rounding_ind_rent_daily"){
			rounding_ind_rent_daily = attr_value;
		}
		put(returnDict, attr, string(attr_value));
	}
	//=============================== END - Table Lookup for Rounding Values ===============================//			

	//=============================== START - Lookups on the Disposal_Sites table ===============================//
	disposalCostPerTon = 0.0;
	disposalRatePerTon = 0.0;
	if(priceType == "Large Containers"){
		if(wasteType == "Solid Waste"){
			disposalSiteCostsRecordSet = bmql("SELECT cost, market_rate FROM Disposal_Sites WHERE Site_Name = $siteName AND WasteType = $wasteType AND DisposalSite_DivNbr = $division");
			for eachRecord in disposalSiteCostsRecordSet{
				disposalCostPerTonStr = get(eachRecord, "cost");
				disposalRatePerTonStr = get(eachRecord, "market_rate");
				break;	//Only one record is expected
			}
			
			if(isnumber(disposalCostPerTonStr)){	//Convert the table result to a float for use in calculations
				disposalCostPerTon = atof(disposalCostPerTonStr);
			}
			if(isnumber(disposalRatePerTonStr)){	//Convert the table result to a float for use in calculations
				disposalRatePerTon = atof(disposalRatePerTonStr);
			}
		}
		put(returnDict, "disposalCostPerTon", string(disposalCostPerTon));
		put(returnDict, "disposalRatePerTon", string(disposalRatePerTon));
	}
	//=============================== END - Lookups on the Disposal_Sites table ===============================//
	
	//===================================  Begin Get various Adjustment factors =========================================
	divisionNbr = 0;
	if(isnumber(division)){
		divisionNbr = atoi(division); 
	}
	//=====Lookups on Contract_Dur_Premium - Price Adjustments - Contract Duration ============================================
	// To get the Adjustment factor from the data table apply it on base
	contractDurationResultSet = bmql("SELECT contract_dur_factor FROM Contract_Dur_Premium WHERE (division = $divisionNbr OR division = 0) AND contractTerm = $initialTerm ORDER BY division DESC");
	contract_dur_factor=1.0;			
	for data in contractDurationResultSet{
		//Updated on 05/29/2014 - Price adjustment to be applied only for current container of existing customer & containers of new customers
		if(isExistingCustomer AND cr_new_business == 1 AND serviceChangeType <> ""){
			contract_dur_factor= contract_dur_factor;	
		}else{
			contract_dur_factor= contract_dur_factor + getFloat(data,"contract_dur_factor");	
		}		
		break;	
	}	
	put(returnDict, "contract_dur_adj_factor", string(contract_dur_factor));
	//=====Ends Lookups on Contract_Dur_Premium - Price Adjustments - Contract Duration ============================================

	//=====Lookups on Div_Segment - Price Adjustments - Segment ============================================
	// Get the Adjustment factor from the data table apply it on base
	segmentPriceResultSet = bmql("SELECT segment_factor FROM div_segment_adj WHERE (division = $divisionNbr OR division = 0) AND container_type = $containerType AND waste_type = $wasteCategory AND segment = $segment ORDER BY division DESC");
	segment_factor=1.0;			
	for data in segmentPriceResultSet{
		//Updated on 05/29/2014 - Price adjustment to be applied only for current container of existing customer & containers of new customers
		if(isExistingCustomer AND cr_new_business == 1 AND serviceChangeType <> ""){
			segment_factor= segment_factor;		
		}else{
			segment_factor= segment_factor + getFloat(data,"segment_factor");
		}
		break;	
	}
	put(returnDict, "segment_adj_factor", string(segment_factor));
	
	//=====Ends Lookups on Div_Segment - Price Adjustments - Segment ============================================

	//=====Lookups on industryAdjustments - Price Adjustments - Industry ============================================
	// Get the Adjustment factor from the data table apply it on base
	industryAdjustmentResultSet = bmql("SELECT industry_factor FROM div_industry_adj WHERE (division = $divisionNbr OR division = 0) AND container_type = $containerType AND waste_type = $wasteCategory AND industry = $industry ORDER BY division DESC");
	industry_factor=1.0;			
	for data in industryAdjustmentResultSet{
		//Updated on 05/29/2014 - Price adjustment to be applied only for current container of existing customer & containers of new customers
		if(isExistingCustomer AND cr_new_business == 1 AND serviceChangeType <> ""){
			industry_factor= industry_factor;
		}else{
			industry_factor= industry_factor + getFloat(data,"industry_factor");
		}
		break;	
	}	
	put(returnDict, "industry_adj_factor", string(industry_factor));
	
	//=====Ends Lookups on industryAdjustments - Price Adjustments - Industry ============================================
	
	peakRate=1.0;
	//Updated on 05/29/2014 - Price adjustment to be applied only for current container of existing customer & containers of new customers

	if(isExistingCustomer AND cr_new_business == 1 AND serviceChangeType <> ""){
		peakRate = peakRate;
	}
	else{
		peakRateFound = false;
		//peakSeasonRecordSet = bmql("SELECT peak_adj_factor, startDate,endDate, wasteType, division, zip FROM Peak_Season_Rates WHERE (division = $divisionNbr OR division = 0) AND (zip = $customer_zip OR zip = '0') AND accountType = $accountType AND container_type = $containerType AND container_size = $containerSizeFloat ORDER BY division DESC, zip DESC");
		peakSeasonRecordSet = bmql("SELECT peak_adj_factor, startDate,endDate, wasteType, division, zip FROM Peak_Season_Rates WHERE (division = $divisionNbr OR division = 0) AND (zip = $customer_zip OR zip = '0') AND accountType = $accountType AND container_type = $containerType AND container_size = $containerSizeFloat AND wasteType = $wasteCategory ORDER BY division DESC, zip DESC");
		// BB 10-Jun-2014: Update query to pull Division 0 records as well as specific division, and to pull only matching or 0 zip codes
		// BB 10-Jun-2014: Added Container Size to query
		/*
		Order of precedence: 
		1.    Matching Division & Zip
		2.    Matching Division with Zip = 0
		3.    Division = 0 and Zip = 0
		No provision for Division = 0 with Zip populated.
		Note: customer address may have Zip + 4, which needs to be parsed and matched to 5-digit Zip.
		*/print "peakSeasonRecordSet:"; print peakSeasonRecordSet;
		Key = division + "@_@" + customer_zip; //Initialize key to be more specific match - zip code here can contain zip - xxxxx
		print "--key--"; print Key;
		n = 0;
		precedenceArray = string[]{division, customer_zip};
		for each in precedenceArray{ //These loop is only to iterate over result set until we find exact match
			print "each in prec array--"; print each;
			if(n <= sizeofarray(precedenceArray)){
				for insideEach in precedenceArray{ //These loop is to create recursion based on precedence rules as listed above
					print "insideEach in prec array--"; print insideEach;
					if(n <= sizeofarray(precedenceArray)){ //Do not run inside loop if iterations exceed number of elements in array
						//Keep replacing 0 from the end until we get exact match as per order of precedence
						if(n <> 0){
							keyArray = split(Key, "@_@");
							size = sizeofarray(keyArray);
							keyArray[size-n] = "0"; //Replaces "0" from the end
							Key = join(keyArray,"@_@"); //Reconstruct Key with replaced value(s)
							print "--new key"; print Key;
							print "n"; print n;
							
						}
						n = n + 1;
						for data in peakSeasonRecordSet{
						//print "data";
						//print data;
							if (peakRateFound == false) {
								wasteTypeDB=get(data,"wasteType");		
								record_division = get(data, "division"); // BB 10-Jun-2014: Use a temporary variable to store the division from the data table -- this was overwriting the customer's actual division
								zip = get(data, "zip");
								thisKey = record_division + "@_@" + zip;
								
								//if(key <> thisKey){ //Skip records until we find a specific match
								if(find(key, thisKey) == -1){ //Skip records until we find a specific match
									print "--thisKey"; print thisKey;
									continue;
								}
								startDate=strtojavadate(get(data,"startDate"),dateFormat); 
								endDate=strtojavadate(get(data,"endDate"), dateFormat);		
								print "UTIL";print wasteType;print wasteTypeDB;print today;print "UTIL";
								// today >= StartDate && today <= EndDate	
								//if( (comparedates(today,startDate) == 1 OR comparedates(today,startDate) == 0) AND (comparedates(today,endDate) == -1  OR comparedates(today,endDate) == 0) AND wasteType == wasteTypeDB){ 	
								if( (comparedates(today,startDate) == 1 OR comparedates(today,startDate) == 0) AND (comparedates(today,endDate) == -1  OR comparedates(today,endDate) == 0)){
									peakRate = peakRate + getFloat(data, "peak_adj_factor");
									print "found"; 
									print "insideEach "; print insideEach; 
									print "each "; print each;
									peakRateFound = true;
								}		
								if(peakRateFound) {
									break;
								}
							}
						}
					}
					if(peakRateFound) {
						break;
					}
				}
			}
		}
	}
	print "--peakRate--"; print peakRate;
	put(returnDict, "peak_adj_factor", string(peakRate));
	
	//=======================================  Ends Get various Adjustment factors ===================================================================

	//=============================== START - Lookups on the Margins table ===============================//
	baseMargin = 0.0;
	targetMargin = 0.0;
	stretchMargin = 0.0;

	if(priceType == "Large Containers"){
	
		indMarginsRecordSet = bmql("SELECT base_margin_new, target_margin_new, stretch_margin_new, base_margin_existing, targetMarginExisting, stretchMrgnExisting FROM tbl_IND_margins WHERE cur_div_nbr = $division AND waste_type = $wasteCategory");
		//Commented existing margin values - these are information only columns and only new margins to be used irrespective of sales activity type
		for eachRecord in indMarginsRecordSet{
			/*if(isExistingCustomer){
				baseMarginStr = get(eachRecord, "base_margin_existing");
				targetMarginStr = get(eachRecord, "targetMarginExisting");
				stretchMarginStr = get(eachRecord, "stretchMrgnExisting");
			}else{*/
				baseMarginStr = get(eachRecord, "base_margin_new");
				targetMarginStr = get(eachRecord, "target_margin_new");
				stretchMarginStr = get(eachRecord, "stretch_margin_new");
			//}
			break;	//Only one record is expected
		}
	}
	
	if(priceType == "Containers"){
		divMarkupsRecordSet = bmql("SELECT cat_yards_per_month, base_margin_new, target_margin_new, stretch_margin_new, base_margin_existing, targetMarginExisting, stretchMrgnExisting FROM tbl_div_mark_ups WHERE division = $division AND waste_type = $wasteCategory AND (min_yds < $yardsPerMonth AND max_yds >= $yardsPerMonth)");
		
		for eachRecord in divMarkupsRecordSet{
			//Updated 04/07/2014, per James existing columns are reference only as of today for the business team. Irrespective of customer type we have to use new margin values all the time.
			/*if(isExistingCustomer){
				cat_yards_per_month = get(eachRecord, "cat_yards_per_month");
				baseMarginStr = get(eachRecord, "base_margin_existing");
				targetMarginStr = get(eachRecord, "targetMarginExisting");
				stretchMarginStr = get(eachRecord, "stretchMrgnExisting");	
			}else{*/
				cat_yards_per_month = get(eachRecord, "cat_yards_per_month");
				baseMarginStr = get(eachRecord, "base_margin_new");
				targetMarginStr = get(eachRecord, "target_margin_new");
				stretchMarginStr = get(eachRecord, "stretch_margin_new");	
			//}
			
			break;	//Only one record is expected
		}
	}
	put(returnDict, "cat_yards_per_month", cat_yards_per_month);
	if(isnumber(baseMarginStr)){	//Convert the table result to a float for use in calculations
		baseMargin = atof(baseMarginStr);
	}
	put(returnDict, "baseMargin", string(baseMargin));
	
	if(isnumber(targetMarginStr)){	//Convert the table result to a float for use in calculations
		targetMargin = atof(targetMarginStr);
	}
	put(returnDict, "targetMargin", string(targetMargin));
	
	if(isnumber(stretchMarginStr)){	//Convert the table result to a float for use in calculations
		stretchMargin = atof(stretchMarginStr);
	}
	put(returnDict, "stretchMargin", string(stretchMargin));
	//=============================== END - Lookups on the Margins table ===============================//
	
	//=============================== START - Lookups on the Site_Char_Factors table ===============================//
	if(priceType == "Containers"){
		siteCharFactorsRS = bmql("SELECT SiteCharacteristic, Factor FROM Site_Char_Factors WHERE SiteCharacteristic = 'Minimum Monthly Rate'");
		for eachRecord in siteCharFactorsRS{
			corp_monthly_min_per_container = getFloat(eachRecord, "Factor");	
			break;
		}
		put(returnDict, "corp_monthly_min_per_container", string(corp_monthly_min_per_container));
	}
	
	//=============================== END - Lookups on the Site_Char_Factors table ===============================//
	
//=============================== END - Table Lookups ===============================//

change_type = "Increase";
svc_base_marg_prem = 0.0; //get from table service change factors table
svc_targ_marg_prem = 0.0; //get from table service change factors table
svc_str_marg_prem = 0.0; //get from table service change factors table
svc_gap_recovery_pct = 0.0;//get from table service change factors table

save_base_margin_adj = 0.0; //get from general save table
save_targ_pct_retain = 0.0; //get from general save table
save_str_pct_retain = 0.0; //get from general save table

comp_bid_markup = 0.0; //get from competitive bid table
comp_targ_pct_retain = 0.0; //get from competitive bid table 
comp_str_pct_retain = 0.0; //get from competitive bid table

pi_retain_base = 0.0; //get from percent pi to retain table
pi_retain_target = 0.0; //get from percent pi to retain table
pi_retain_stretch = 0.0; //get from percent pi to retain table

currentCost = 0.0;
currentFloor = 0.0;
curr_margin_percent = 0.0; //Calculate current margin relative to current floor price

curr_yards_per_month = 0.0;
rate_pct_base = 0.0;
curr_price_base_adj = 0.0;

pi_retain_target = 0.0; //get from table
pi_retain_base = 0.0; //get from table
save_targ_pct_retain = 0.0; //get from table
comp_targ_pct_retain = 0.0; //get from table

priceBeforePI = 0.0; //This is difference between revenue and price increase
monthsRemaining = 0;
monthFactor = 365.0/12.0;

minimum_haul_rate = 0.0;
comp_maint_factor = 1.0;
curr_margin_dollars = 0.0;

//=============================== START Get Current Service pricing guardrails for existing customers ===============================//
if(cr_new_business == 1 AND isExistingCustomer AND serviceChangeType <> ""){ //If its a new business for existing customer
	wasteType_current = wasteType;
	liftsPerContainer_current = "";
	wasteCategory_current = "";
	
	//START - Get price adjustment flag & service change flag - for service change
	if( lower(serviceChangeType) == "service level change"){
		cr_service_change = 1;
	}elif(lower(serviceChangeType) == "price adjustment"){
		if(lower(priceAdjustmentReason) == "rollback of pi"){
			cr_rollback_of_pi = 1;
		}elif(lower(priceAdjustmentReason) == "rollback of current price"){
			cr_general_save = 1;
		}elif(lower(priceAdjustmentReason) == "rollback: competitive bid"){
			cr_competitive_bid = 1;
		}
	}
	put(returnDict, "cr_general_save", string(cr_general_save));
	put(returnDict, "cr_rollback_of_pi", string(cr_rollback_of_pi));
	put(returnDict, "cr_competitive_bid", string(cr_competitive_bid));
	put(returnDict, "cr_service_change", string(cr_service_change));
	//END - Get price adjustment flag & service change flag - for service change
	
	if(containskey(stringDict, "current_wasteCategory")){
		wasteCategory_current = get(stringDict, "current_wasteCategory");
	}
	
	if(containskey(stringDict, "current_basePriceAdj1")){
		current_basePriceAdj1Str = get(stringDict, "current_basePriceAdj1");
		if(isnumber(current_basePriceAdj1Str)){
			curr_price_base_adj = atof(current_basePriceAdj1Str);
		}
	}
	
	if(containskey(stringDict, "current_yardsPerMonth")){
		current_yardsPerMonthStr = get(stringDict, "current_yardsPerMonth");
		if(isnumber(current_yardsPerMonthStr)){
			curr_yards_per_month = atof(current_yardsPerMonthStr);
		}
	}
	
	if(containskey(stringDict, "current_cost")){
		current_costStr = get(stringDict, "current_cost");
		if(isnumber(current_costStr)){
			currentCost = atof(current_costStr);
		}
	}
	
	if(containskey(stringDict, "current_floor")){
		current_floorStr = get(stringDict, "current_floor");
		if(isnumber(current_floorStr)){
			currentFloor = atof(current_floorStr);
		}
	}
	//print "======================";
	//print revenue; print currentCost;
	curr_margin_dollars = revenue - currentCost;
	put(returnDict, "curr_margin_dollars", string(curr_margin_dollars));
	
	//Calculate customer's current margin relative to current floor price
	//cost & floor prices are unit prices
	/*
	if(currentCost > 0){	
		curr_margin_percent = ((revenue - currentCost)/currentCost) * 100;
	}*/
	
	if(revenue > 0){
		curr_margin_percent = curr_margin_dollars/revenue;
	}
	put(returnDict, "curr_margin_percent", string(curr_margin_percent));
	
	infoProDivNbr =  0;
	if(isnumber(infoProDivNum)){
		infoProDivNbr = atoi(infoProDivNum);
	}
	
	/* calculate change type {increase,decrease}; set default as increase for non-service change customers*/
	if(curr_yards_per_month >= yardsPerMonth){
		change_type = "Decrease";
	}
	put(returnDict, "change_type", change_type);
	
	/* and the customer's current rate as a percentage of their current base */
	rate_pct_base = 0.0;
	if(curr_price_base_adj > 0){
		rate_pct_base = 100 * revenue / curr_price_base_adj;
	}
	put(returnDict, "rate_pct_base", string(rate_pct_base));
	
	//Get months remaining : expiration date - today's date, round down to nearest whole month
	if(currentExpirationDateStr <> "" AND currentExpirationDateStr <> "99991231"){
		if(len(currentExpirationDateStr) == 8){
			expirationDateYearStr = substring(currentExpirationDateStr, 0, 4);
			expirationDateMonthStr = substring(currentExpirationDateStr, 4, 6);
			expirationDateDayStr = substring(currentExpirationDateStr, 6, 8);
			expirationDateTemp = expirationDateYearStr + "-" + expirationDateMonthStr + "-" + expirationDateDayStr;
			currentExpirationDate = strtojavadate(expirationDateTemp, "yyyy-MM-dd");
			//print currentExpirationDate;
			//print (currentExpirationDate < today);
			if(currentExpirationDate < today){ //added check to see if the expiration date has passed 
				monthsRemaining = 0;
				//print "EXPIRED";
			}
			else{
				//print "CURRENT"; 
				diffDays = getdiffindays(currentExpirationDate, today);
				monthsRemaining = integer(diffDays/monthFactor); //ignore decimal value to round down to nearest whole month
			}
			put(returnDict, "monthsRemaining", string(monthsRemaining));
		}
	}
	
	//==================== START - If Service level change selected ============================================
	//==================== START - TABLE LOOKUP FOR SERVICE CHANGE FACTORS ============================================
	if(cr_service_change == 1){
		//Service_Chng_Factors data table
		serviceChangeFactorsRecordSet = bmql("SELECT Division, Rate_Pct_Base_Min, Rate_Pct_Base_Max, change_type, Container_Type, cat_yds_per_month, WasteType, svc_base_marg_prem, svc_targ_marg_prem, svc_str_marg_prem, svc_gap_recovery_pct FROM Service_Chng_Factors WHERE (Division = $division OR Division = '0') AND Container_Type = $containerType AND WasteType = $wasteCategory AND Rate_Pct_Base_Min <= $rate_pct_base AND Rate_Pct_Base_Max > $rate_pct_base AND change_type = $change_type AND cat_yds_per_month = $cat_yards_per_month ORDER BY Division DESC");
		for eachRecord in serviceChangeFactorsRecordSet{
			svc_base_marg_prem = getFloat(eachRecord, "svc_base_marg_prem");
			svc_targ_marg_prem = getFloat(eachRecord, "svc_targ_marg_prem");
			svc_str_marg_prem = getFloat(eachRecord, "svc_str_marg_prem");
			svc_gap_recovery_pct = getFloat(eachRecord, "svc_gap_recovery_pct");
			break;
		}
	}
	put(returnDict, "svc_base_marg_prem", string(svc_base_marg_prem));
	put(returnDict, "svc_targ_marg_prem", string(svc_targ_marg_prem));
	put(returnDict, "svc_str_marg_prem", string(svc_str_marg_prem));
	put(returnDict, "svc_gap_recovery_pct", string(svc_gap_recovery_pct));
	//==================== END - TABLE LOOKUP FOR SERVICE CHANGE FACTORS ============================================
	
	//==================== START - TABLE LOOKUP FOR Rollback of PI FACTORS ============================================
	if(cr_rollback_of_pi == 1){
		//Percent_PI_To_Retain data table
		piToRetainRecordset = bmql("SELECT MarginPctileMin, MarginPctileMax, ContainerType, pi_retain_base, pi_retain_target, pi_retain_stretch FROM Percent_PI_To_Retain WHERE (Division = $division OR Division = '0') AND ContainerType = $containerType AND WasteType = $wasteCategory AND  MarginPctileMin <= $curr_margin_percentile AND MarginPctileMax > $curr_margin_percentile AND cat_yds_per_month = $cat_yards_per_month ORDER BY Division DESC");
		//print "piToRetainRecordset";
		//print piToRetainRecordset;
		for eachRecord in piToRetainRecordset{
			pi_retain_base = getFloat(eachRecord, "pi_retain_base");
			pi_retain_target = getFloat(eachRecord, "pi_retain_target");
			pi_retain_stretch = getFloat(eachRecord, "pi_retain_stretch");
			break;
		}
	}
	put(returnDict, "pi_retain_base", string(pi_retain_base));
	put(returnDict, "pi_retain_target", string(pi_retain_target));
	put(returnDict, "pi_retain_stretch", string(pi_retain_stretch));
	
	//==================== END - TABLE LOOKUP FOR Rollback of PI FACTORS ============================================
	
	//==================== START - TABLE LOOKUP FOR Rollback of Current Price FACTORS ============================================
	//Rollback of Current Price
	if(cr_general_save == 1){
		//General_Save_Rates data table
		generalSaveRecordSet = bmql("SELECT MarginPctileMin, MarginPctileMax, Months_Remain_Min, Months_Remain_Max, ContainerType, cat_yds_per_month, WasteType, save_base_margin_adj, save_targ_pct_retain, save_str_pct_retain FROM General_Save_Rates WHERE (Division = $division OR Division = '0') AND (MarginPctileMin <= $curr_margin_percentile AND MarginPctileMax > $curr_margin_percentile) AND (Months_Remain_Min <= $monthsRemaining AND  Months_Remain_Max > $monthsRemaining) AND WasteType = $wasteCategory AND ContainerType = $containerType AND cat_yds_per_month = $cat_yards_per_month ORDER BY Division DESC");
		for eachRecord in generalSaveRecordSet{
			save_base_margin_adj = getFloat(eachRecord, "save_base_margin_adj");
			save_targ_pct_retain = getFloat(eachRecord, "save_targ_pct_retain");
			save_str_pct_retain = getFloat(eachRecord, "save_str_pct_retain");
			break;
		}
	}
	put(returnDict, "save_base_margin_adj", string(save_base_margin_adj));
	put(returnDict, "save_targ_pct_retain", string(save_targ_pct_retain));
	put(returnDict, "save_str_pct_retain", string(save_str_pct_retain));
	//==================== END - TABLE LOOKUP FOR Rollback of Current Price FACTORS ============================================
	
	//==================== START - TABLE LOOKUP FOR Competitive Rollback FACTORS ============================================
	if(cr_competitive_bid == 1){
		//CompetitiveBid_Mrkup data table
		competitiveBidRecordSet = bmql("SELECT MarginPctileMin, MarginPctileMax, monthsRemainMin, monthsRemainMax, ContainerType, cat_yds_per_month, WasteType, comp_bid_markup, comp_targ_pct_retain, comp_str_pct_retain FROM CompetitiveBid_Mrkup WHERE (Division = $division OR Division = '0') AND MarginPctileMin <= $curr_margin_percentile AND MarginPctileMax > $curr_margin_percentile AND (monthsRemainMin <= $monthsRemaining AND  monthsRemainMax > $monthsRemaining) AND WasteType = $wasteCategory AND ContainerType = $containerType AND cat_yds_per_month = $cat_yards_per_month ORDER BY Division DESC");
		//print "competitiveBidRecordSet";
		//print competitiveBidRecordSet;
		for eachRecord in competitiveBidRecordSet{
			comp_bid_markup = getFloat(eachRecord, "comp_bid_markup");
			comp_targ_pct_retain = getFloat(eachRecord, "comp_targ_pct_retain"); 
			comp_str_pct_retain = getFloat(eachRecord, "comp_str_pct_retain");
			break;
		}
	}
	put(returnDict, "comp_bid_markup", string(comp_bid_markup));
	put(returnDict, "comp_targ_pct_retain", string(comp_targ_pct_retain));
	put(returnDict, "comp_str_pct_retain", string(comp_str_pct_retain));
	//==================== END - TABLE LOOKUP FOR Competitive Rollback FACTORS ============================================
	//==================== END - If Service level change selected ============================================
	//End Tables lookup for existing customer
}
//Added 04/06/2014
if(cr_new_business == 0 AND isExistingCustomer AND serviceChangeType <> ""){
	curr_margin_dollars = revenue - costToServeMonth;

	put(returnDict, "curr_margin_dollars", string(curr_margin_dollars));
	if(revenue > 0){
			curr_margin_percent = curr_margin_dollars/revenue;
	}
	put(returnDict, "curr_margin_percent", string(curr_margin_percent));
}

//=============================== END Get Current Service pricing guardrails for existing customers ===============================//

//=============================== START - Calculate Guardrails Excluding Fees ===============================//
//totalFeePct = 1 + ((1 * frfRate) + (1 * erfRate) + erfOnFrfRate * erfRate * frfRate);
//totalFeePct = (frfFlag * frfRate) + (erfFlag * erfRate) + (erfOnFrfFlag * erfRate * frfRate);
//put(returnDict, "totalFeePct", string(totalFeePct));


//=============================== START - Calculate Fee Percent ===============================//
feePct = (frfFlag * frfRate) + (erfFlag * erfRate) + (erfOnFrfFlag * erfRate * frfRate * erfFlag * frfFlag);
fullFeePct = frfRate + erfRate + (erfOnFrfFlag * erfRate * frfRate); //Added for SR 3-9437035701
disposalRatePerTon = disposalRatePerTon * (1 + fullFeePct);
put(returnDict, "feePct", string(feePct));
put(returnDict, "fullFeePct", string(fullFeePct));
//=============================== END - Calculate Fee Percent ===============================//

/*
if(priceType == "Large Containers"){		//NOTE: The formula for Large Containers should apply to both types. There is an error in the Small Containers formula, which will be resolved after the Validation testing
	basePrice = (perHaulCosts * haulsPerMonth / (1 - baseMargin); //basePrice = costToServeMonth / (1 + baseMargin);
	//disposalPerTonFeeAdj = disposalRatePerTon * (1 + feePct) * allocatedDisposalFlag; //Moved as part of SR 3-9428639511 Removed as part of 3-9437035701
	
}
if(priceType == "Containers"){
	basePrice = costToServeMonth + (1 + baseMargin); //Incorrect as per James 09/24/2014, corrected below in calculation of new_business_base
}*/
//Base Price formula common for both small and large containers - changed as per the new formula provided by James on 01/28/14
//basePrice = costToServeMonth / (1 - baseMargin); 

//New Base price calculation
// new business pricing
new_business_base = 0.0;
if(baseMargin <> 1.0){
	//Below line updated on 04/06/2014
	//new_business_base = cr_new_business * costToServeMonth / (1-baseMargin);  
	//new_business_base = costToServeMonth / (1-baseMargin);  Changed as part of SR 3-9437035701
	if(priceType == "Large Containers"){
	new_business_base = (perHaulCosts * haulsPerMonth) / (1 - baseMargin);
	}
	else{ //(priceType == "Containers"){
	new_business_base = costToServeMonth / (1 - baseMargin);
	}
}
//Added 04/06/2014
if(isExistingCustomer AND cr_new_business == 1 AND serviceChangeType <> ""){
	new_business_base = 0.0; 
}
put(returnDict, "new_business_base", string(new_business_base));

//Get Price Before PI with fee
//pi_amount is a before fee amount; recalc to pull in fees
priceBeforePI = revenue - pi_amount;

//pi_rollback = cr_rollback_of_pi * ((revenue - (pi_amount * (1+feePct))) + (pi_amount * pi_retain_base)); 
pi_rollback = (cr_rollback_of_pi * ((revenue - (pi_amount * (1 + feePct))) + ((pi_amount * (1 + feePct)) * pi_retain_base)));
put(returnDict, "pi_rollback_base", string(pi_rollback));

//general save
tempArray = float[];
append(tempArray, revenue);
append(tempArray, costToServeMonth + (save_base_margin_adj * curr_margin_dollars));
general_save_amt = min(tempArray);
general_save = cr_general_save * general_save_amt; 
put(returnDict, "general_save_base", string(general_save));

//competitive bid
//new competitive bid floor guardrail SR 3-9423758861
tempArray = float[];
newTempArray = float[];
append(newTempArray, costToServeMonth/(1-baseMargin));
append(newTempArray, competitive_bid_amt * comp_bid_markup);
append(tempArray, max(newTempArray));
//append(tempArray, competitive_bid_amt * comp_bid_markup);
append(tempArray, revenue);
//print "Competitive Bid Arrays: ";
//print newTempArray;
//print tempArray;
//end SR 3-9423758861

competitive_bid = cr_competitive_bid * min(tempArray); 
put(returnDict, "competitive_bid_base", string(competitive_bid));

//service change
temp = curr_margin_percent + svc_base_marg_prem;
costToServeBaseAdj = 0.0;
if(temp <> 1.0 AND revenue > 0){
	costToServeBaseAdj = (costToServeMonth / (1 - curr_margin_percent + svc_base_marg_prem));
}

tempArray = float[];
append(tempArray, 0.0);
if(baseMargin <> 1.0){
	append(tempArray, (((costToServeMonth / (1 - baseMargin)) - costToServeBaseAdj) * svc_gap_recovery_pct));
}
service_change_amt = cr_service_change * (costToServeBaseAdj + max(tempArray));
put(returnDict, "service_change_base", string(service_change_amt));


basePrice = new_business_base + general_save + competitive_bid + service_change_amt + pi_rollback;
print "basePrice: "; print basePrice;                
put(returnDict, "basePrice", string(basePrice));
//End of common calculations for both Small & Large containers

//Apply adjustments to small container to basePrice and porpogate ratios to target & stretch
basePremiumStr = "";
baseFRFPremium = 0.0;

basePriceAdj = basePrice;
if(priceType == "Containers"){
	//Adjust base pricing - Acquisition type
	//Updated on 05/29/2014 - Price adjustment to be applied only for current container of existing customer & containers of new customers
	if(isExistingCustomer AND cr_new_business == 1 AND serviceChangeType <> ""){
		competitorFactor = 1.0;
	}
	
	basePriceAdj = basePriceAdj * competitorFactor;
	put(returnDict, "basePriceAdjCompetitor", string(basePriceAdj));
	
	//Industry Price Adjustment (Beside the disposal punds variance) - PBS
	
	basePriceAdj = basePriceAdj * industry_factor;
	put(returnDict, "basePriceAdjIndustry", string(basePriceAdj));
	
	//Segment Price Adjustment (Price, Environmental Solutions, Performance) - PBS
	
	basePriceAdj = basePriceAdj * segment_factor;
	put(returnDict, "basePriceAdjSegment", string(basePriceAdj));

	//Contract Duration Adjustment on Price (36, 24, 12 & MTM)
	
	basePriceAdj = basePriceAdj * contract_dur_factor;
	put(returnDict, "basePriceAdjContract", string(basePriceAdj));
	
	//Calculate frf premiums and fee for new quotes (turn off premium for existing)
	basePremiumRecordSet = bmql("SELECT premium FROM Fee_Removal_Premiums WHERE minMonthlyRevenue <= $basePriceAdj AND maxMonthlyRevenue > $basePriceAdj AND (Lawson_DivNbr = $divisionNbr OR Lawson_DivNbr = 0) ORDER BY Lawson_DivNbr DESC");
	for eachRecord in basePremiumRecordSet{
		basePremiumStr = get(eachRecord, "premium");
		break;
	}

	if(isnumber(basePremiumStr)){
		baseFRFPremium = atof(basePremiumStr);
	}
	put(returnDict, "baseFRFPremium", string(baseFRFPremium));
	
	basePriceAdj = basePriceAdj * (1 + ((1 - frfFlag) * baseFRFPremium));
	
	put(returnDict, "basePriceAdjFRFPremium", string(basePriceAdj));
	
	//Adjust Peak rate for small container
	peakRateArray = float[];
	append(peakRateArray, 0.0);
	if(basePriceAdj > 0){
		append(peakRateArray, (costToServeMonth/basePriceAdj) -1.0);
	}
	peakRateAdj = min(peakRateArray);
	
	peakRateArray = float[];
	append(peakRateArray, peakRate);
	append(peakRateArray, peakRateAdj);
	peakRate = max(peakRateArray);
	put(returnDict, "peak_rate_factor1", string(peakRate));

	//Account Type Adjustment on Price between Perm and Temp accounts – particularly on Large Container
	basePriceAdj = basePriceAdj * peakRate;
	put(returnDict, "basePriceAdjPeakRate", string(basePriceAdj));
	
	/*Prevent Base Price from falling below Cost To Serve
	final prices cannot fall below floor (or each other)
	except in certain scenarios where the customer's existing price is an anchor*/
	basePriceAdjArr = Float[];
	append(basePriceAdjArr, (costToServeMonth - basePriceAdj));
	append(basePriceAdjArr, 0.0);
	

	if(sizeofarray(basePriceAdjArr) > 0){
		basePriceAdj = basePriceAdj + ((1 - cr_general_save) * max(basePriceAdjArr));
	}
	put(returnDict, "basePriceAdj1", string(basePriceAdj));
}//end of small container

targetPriceAdj = 0.0;
new_business_target_price = 0.0;
if(baseMargin <> -1.0){
	//targetPriceAdj = ((1 + targetMargin) / (1 + baseMargin)) * basePriceAdj;
	//targetPriceAdj = ((1 - baseMargin) / (1 - targetMargin)) * basePriceAdj; //changed as per the new formulas provided by James on 01/28/14

	//New Target Price calculation - Updated 27 Feb 2014
	//new business pricing 
	if(targetMargin <> 1.0){
		//Below line updated on 04/06/2014
		//new_business_target_price = (cr_new_business * ((1-baseMargin)/(1-targetMargin)) * basePriceAdj);
		//Update - 04/07/2014 - base, target & stretch to be porportional
		//new_business_target_price = (((1-baseMargin)/(1-targetMargin)) * basePriceAdj);
		if(priceType == "Containers"){
			new_business_target_price = (((1-baseMargin)/(1-targetMargin)) * basePriceAdj);
		}elif(priceType == "Large Containers"){ //Currently all large containers are new business
			new_business_target_price = ((perHaulCosts * haulsPerMonth)/(1-targetMargin));	//Changed as part of SR 3-9428639511
		}
	}
	//Added 04/06/2014
	if(isExistingCustomer AND cr_new_business == 1 AND serviceChangeType <> ""){
		new_business_target_price = 0.0;
	}
	put(returnDict, "new_business_target_price", string(new_business_target_price));

	// pi rollback
	//pi_rollback_target_price = (cr_rollback_of_pi * (priceBeforePIIncFee + (pi_amount * pi_retain_target)));
	pi_rollback_target_price = (cr_rollback_of_pi * ((revenue - (pi_amount * (1 + feePct))) + ((pi_amount * (1 + feePct)) * pi_retain_target)));
	put(returnDict, "pi_rollback_target_price", string(pi_rollback_target_price));

	//general save
	tempArray = float[];
	append(tempArray, costToServeMonth);
	append(tempArray, basePriceAdj);
	priceAdjMax = max(tempArray);
	
	priceAdjArray = float[];
	append(priceAdjArray, basePriceAdj + fabs((revenue - priceAdjMax) * save_targ_pct_retain));
	append(priceAdjArray, costToServeMonth);

	general_save_target_price = cr_general_save * max(priceAdjArray);
	put(returnDict, "general_save_target_price", string(general_save_target_price));
				 
	//competitive bid
	competitive_bid_target_price = cr_competitive_bid * (basePriceAdj + (revenue - basePriceAdj) * comp_targ_pct_retain);
	put(returnDict, "competitive_bid_target_price", string(competitive_bid_target_price));

	//Service change
	temp = curr_margin_percent + svc_targ_marg_prem;
	costToServeTargetAdj = 0.0;
										 
	if(temp <> 1.0 AND revenue > 0){
		costToServeTargetAdj = (costToServeMonth / (1 - (curr_margin_percent + svc_targ_marg_prem)));
	}
	tempArray = float[];
	append(tempArray, 0.0);
	if(targetMargin <> 1.0){
		costToServeTargetAdj1 = 0.0;
		if(revenue > 0 AND temp <> 1.0){
			costToServeTargetAdj1 = (costToServeMonth / (1 - curr_margin_percent + svc_targ_marg_prem));
		}
		append(tempArray, (((costToServeMonth / (1 - targetMargin)) - costToServeTargetAdj1) * svc_gap_recovery_pct));
	}
	service_change_target_price =  cr_service_change * (costToServeTargetAdj + max(tempArray));
	put(returnDict, "service_change_target_price", string(service_change_target_price));

	targetPriceAdj = new_business_target_price + pi_rollback_target_price + general_save_target_price + competitive_bid_target_price + service_change_target_price;
	print "targetPriceAdj: "; print targetPriceAdj;
}        
put(returnDict, "targetPriceAdj1", string(targetPriceAdj));

//Prevent Target Price Ajustment from falling below base price adjustment
if(priceType == "Containers"){
	targetPriceArr = Float[];
	append(targetPriceArr, (basePriceAdj - targetPriceAdj));
	append(targetPriceArr, 0.0);
	
	if(sizeofarray(targetPriceArr) > 0){
		targetPriceAdj = targetPriceAdj + max(targetPriceArr);
	}
}
put(returnDict, "targetPriceAdj2", string(targetPriceAdj));

targetPremiumStr = "";
targetFRFPremium = 0.0;
//Calculate FRF Premium for target - This approach is only for small container
if(priceType == "Containers"){
	targetPremiumRecordSet = bmql("SELECT premium FROM Fee_Removal_Premiums WHERE minMonthlyRevenue <= $targetPriceAdj AND maxMonthlyRevenue > $targetPriceAdj AND (Lawson_DivNbr = $divisionNbr OR Lawson_DivNbr = 0) ORDER BY Lawson_DivNbr DESC");

	for eachRecord in targetPremiumRecordSet{
		targetPremiumStr = get(eachRecord, "premium");
		break;
	}

	if(isnumber(targetPremiumStr)){
		targetFRFPremium = atof(targetPremiumStr);
	}
	put(returnDict, "targetFRFPremium", string(targetFRFPremium));

	if(frfFlag == 0 AND baseFRFPremium <> -1.0){
		targetPriceAdj = ((1 + targetFRFPremium) / (1 + baseFRFPremium)) * targetPriceAdj;
	}
	put(returnDict, "targetPriceAdj3", string(targetPriceAdj));
}
stretchPriceAdj = 0.0;
new_business_stretch = 0.0;
if(baseMargin <> -1.0){
	//stretchPriceAdj = ((1 + stretchMargin) / (1 + baseMargin)) * basePriceAdj;
	//stretchPriceAdj = ((1 - baseMargin) / (1 - stretchMargin)) * basePriceAdj; //changed as per the new formulas provided by James on 01/28/14
	//New Stretch Price calculation - Updated 27 Feb 2014
	//new business pricing
	if(stretchMargin <> 1.0){
		//Below line updated on 04/06/2014
		//new_business_stretch = cr_new_business * ((1-baseMargin)/(1-stretchMargin)) * basePriceAdj;
		//new_business_stretch = ((1-baseMargin)/(1-stretchMargin)) * basePriceAdj;
		//Updated 04/07/2014
		if(priceType == "Containers"){
			new_business_stretch = ((1-baseMargin)/(1-stretchMargin)) * basePriceAdj;
		}elif(priceType == "Large Containers"){
			new_business_stretch = (perHaulCosts * haulsPerMonth)/(1-stretchMargin); // Added as part of SR 3-9428639511	
		} 
	}
	//Added 04/06/2014
	if(isExistingCustomer AND cr_new_business == 1 AND serviceChangeType <> ""){
		new_business_stretch = 0.0;
	}
	put(returnDict, "new_business_stretch", string(new_business_stretch));
	
	//pi rollback
	//pi_rollback_stretch = cr_rollback_of_pi * (priceBeforePIIncFee + (pi_amount * pi_retain_stretch));
	pi_rollback_stretch = (cr_rollback_of_pi * ((revenue - (pi_amount * (1 + feePct))) + ((pi_amount * (1 + feePct)) * pi_retain_stretch)));
	put(returnDict, "pi_rollback_stretch", string(pi_rollback_stretch));
	
	//general save
	tempArray = float[];
	append(tempArray, basePriceAdj);
	append(tempArray, costToServeMonth);
	priceAdjMax = max(tempArray);
	
	priceAdjArray = float[];
	append(priceAdjArray, costToServeMonth/(1-baseMargin));
	append(priceAdjArray, basePriceAdj + (fabs(revenue - priceAdjMax) * save_str_pct_retain));
	general_save_stretch = cr_general_save * max(priceAdjArray);
	put(returnDict, "general_save_stretch", string(general_save_stretch));
	
	//Competitive Bid
	competitive_bid_stretch = cr_competitive_bid * (basePriceAdj + (revenue - basePriceAdj) * comp_str_pct_retain);
	put(returnDict, "competitive_bid_stretch", string(competitive_bid_stretch));
	
	//Service change
	temp = curr_margin_percent + svc_str_marg_prem;
	costToServeStretchFeeAdj = 0.0;
	if(temp <> 1.0 AND revenue > 0){
		costToServeStretchFeeAdj = (costToServeMonth / (1 - (curr_margin_percent + svc_str_marg_prem)));
	}
	priceAdjArray = float[];
	append(priceAdjArray, 0.0);
	if(stretchMargin <> 1.0){
		costToServeStretchFeeAdj1 = 0.0;
		if(revenue > 0 AND temp <> 1.0){
			costToServeStretchFeeAdj1 = (costToServeMonth / (1 - curr_margin_percent + svc_str_marg_prem));
		}
		append(priceAdjArray, ((costToServeMonth / (1 - stretchMargin)) - costToServeStretchFeeAdj1) * svc_gap_recovery_pct);
	}
	
	service_change_stretch = cr_service_change * (costToServeStretchFeeAdj + max(priceAdjArray));
	put(returnDict, "service_change_stretch", string(service_change_stretch));
	
	stretchPriceAdj =  new_business_stretch +  pi_rollback_stretch + general_save_stretch + competitive_bid_stretch + service_change_stretch;               
	print "stretchPriceAdj: "; print stretchPriceAdj;
}
put(returnDict, "stretchPriceAdj1", string(stretchPriceAdj));
 
//Prevent Stretch Price Adjustment from falling below target price adjustment
if(priceType == "Containers"){
	stretchPriceArr = Float[];
	append(stretchPriceArr, (targetPriceAdj - stretchPriceAdj));
	append(stretchPriceArr, 0.0);
	
	if(sizeofarray(stretchPriceArr) > 0){
		stretchPriceAdj = stretchPriceAdj + max(stretchPriceArr);
	}
}
put(returnDict, "stretchPriceAdj2", string(stretchPriceAdj));


stretchPremiumStr = "";
stretchFRFPremium = 0.0;
//Calculate FRF Premium for Stretch price - this approach is only for Small Containers
if(priceType == "Containers"){
	stretchPremiumRecordSet = bmql("SELECT premium FROM Fee_Removal_Premiums WHERE minMonthlyRevenue <= $stretchPriceAdj AND maxMonthlyRevenue > $stretchPriceAdj AND (Lawson_DivNbr = $divisionNbr OR Lawson_DivNbr = 0) ORDER BY Lawson_DivNbr DESC");

	for eachRecord in stretchPremiumRecordSet{
		stretchPremiumStr = get(eachRecord, "premium");
		break;
	}

	if(isnumber(stretchPremiumStr)){
		stretchFRFPremium = atof(stretchPremiumStr);
	}
	put(returnDict, "stretchFRFPremium", string(stretchFRFPremium));

	if(frfFlag == 0 AND baseFRFPremium <> -1.0){
		stretchPriceAdj = ((1 + stretchFRFPremium) / (1 + baseFRFPremium)) * stretchPriceAdj;
	}
	put(returnDict, "stretchPriceAdj3", string(stretchPriceAdj));
}
//=============================== END - Calculate Guardrails Excluding Fees ===============================//

minimumHaulRateRecordSet = recordset();
//=============================== START - Large container factors table look up ===============================//
if(priceType == "Large Containers"){
	//Get Large container factors 
	minimumHaulRateRecordSet = bmql("SELECT minimum_haul_rate, comp_maint_factor FROM Div_Lg_Cont_Factors WHERE Division = $division OR Division = '0' ORDER BY Division DESC");
	for eachRecord in minimumHaulRateRecordSet{
		minimum_haul_rate = getFloat(eachRecord, "minimum_haul_rate");
		comp_maint_factor = getFloat(eachRecord, "comp_maint_factor");
		put(returnDict, "minimum_haul_rate", string(minimum_haul_rate));
		put(returnDict, "comp_maint_factor", string(comp_maint_factor));
		break;
	}
}
elif(priceType == "Containers"){
	//Get Small container factors 
	minimumHaulRateRecordSet = bmql("SELECT Cont_Min, Cart_Min FROM Div_Sm_Cont_Factors WHERE Division = $division OR Division = '0' ORDER BY Division DESC");
	for eachRecord in minimumHaulRateRecordSet{
		contMin = getFloat(eachRecord, "Cont_Min");
		cartMin = getFloat(eachRecord, "Cart_Min");
		put(returnDict, "contMin", string(contMin));
		put(returnDict, "cartMin", string(cartMin));
		break;
	}
}

//=============================== END - Large container factors table look up ===============================//

//=============================== BEGIN - Calculate Rental Rate ===============================//
//Updated Rental condition on 22 March 2014 as per Test Case TC-0076
//if((rental_type == "Monthly" OR rental_type == "Daily") AND priceType == "Large Containers" AND containerType_lStr == "Open Top" AND (billingType == "Haul + Disposal" OR billingType == "Flat Rate + Overage" OR billingType == "Haul + Minimum Tonnage")){

comp_rental_floor = 0.0;
comp_rental_rate = 0.0;
container_rental_floor = 0.0;
//added to rental_type == None to conditional to handle the rental part of container going to per haul line item if rental is off - Gaurav 20150211	
if((rental_type == "None" OR rental_type == "Monthly" OR rental_type == "Daily") AND priceType == "Large Containers"){
	//Calculate price_rental_per_month 
	alloc_rental = 1;
	if(rental_type == "None"){//added to handle the rental part of container going to per haul line item if rental is off - Gaurav 20150211
		alloc_rental = 0;
	}
	containerDepreciation = 0.0;
	compactorDepreciation = 0.0;
	containerROA = 0.0;
	compactorROA = 0.0;
	price_rental_per_month = 0.0;
	market_rate = 0.0;
	market_rental_rate = 0.0;
	if(isnumber(market_rateStr)){
		market_rate = atof(market_rateStr);
	}
	if(isnumber(containerDepreciationStr)){
		containerDepreciation = atof(containerDepreciationStr);
	}
	if(isnumber(compactorDepreciationStr)){
		compactorDepreciation = atof(compactorDepreciationStr);
	}
	if(isnumber(containerROAStr)){
		containerROA = atof(containerROAStr);
	}
	if(isnumber(compactorROAStr)){
		compactorROA = atof(compactorROAStr);
	}
	//Get Market Rental rate from tbl_div_rental table
	marketRentalRS = bmql("SELECT market_rental_rate FROM tbl_division_rental WHERE (division = $division OR division = '0') AND has_compactor = $hasCompactor AND container_cd = $routeType AND perm_flag = $permanentFlag ORDER BY division DESC");
	for each in marketRentalRS{
		market_rental_rate = getFloat(each, "market_rental_rate");
		break;
	}
	put(returnDict, "market_rental_rate", string(market_rental_rate));
	// rental allocation
	price_rental_per_month = market_rental_rate * (1 + fullFeePct) * alloc_rental; // Changed as per SR 3-9437035701
	
	rental_factor = 1.0;
	//if rental type is Daily convert monthly rental charges into daily charges
	if(rental_type == "Daily"){
		//rental_factor = (price_rental_per_month * 12/365);
		rental_factor = 365.0/12.0;
	}
	put(returnDict, "rental_factor", string(rental_factor));
	
	//rental allocation
	//for non compactor, this is based on maket rates
	//for compactor, this is calculated based on input values
	
	compactor_depr = 0.0;
	
	/*if(hasCompactor == 1){
		compactor_depr = compactor_cost/compactor_life;
	}
	put(returnDict, "compactor_depr", string(compactor_depr));
	*/
	compactorDeprStr = "";
	if(containskey(stringDict, "compactor_depr")){
		compactorDeprStr = get(stringDict, "compactor_depr");
	}
	if(isnumber(compactorDeprStr)){
		compactor_depr = atof(compactorDeprStr);
	}
	
	if(rental_factor > 0 AND feePct <> -1.0 AND alloc_rental == 1){
		//Updated 04/08/2014
		comp_rental_floor = (1 - compactorCustomerOwned) * (compactor_depr + (compactor_cost * comp_maint_factor / 12.0) 
						+ (compactor_cost * 0.065 / 12.0)) / rental_factor; // Removed as part of SR 3-9437035701 / (1 + feePct);
		//comp_rental_rate = compactorCustomerOwned * (compactor_depr + (compactor_cost * comp_maint_factor/12.0) + 
						//(compactor_cost * rsg_compactor_base_roa /12.0))/rental_factor/(1 + feePct);
		comp_rental_rate = (1 - compactorCustomerOwned) * (compactor_depr + (compactor_cost * comp_maint_factor / 12.0) 
						+ (compactor_cost * rsg_compactor_base_roa / 12.0)) / rental_factor; // Removed as part of SR 3-9437035701 / (1 + feePct);				
		//Changed as per SR 3-9428639511 removed  ( containerMntPerHaul * (1 - isContainerCustomerOwned))
		container_rental_floor = (containerDepreciation + containerROA) *  haulsPerMonth / rental_factor; // Changed as part of SR 3-9437035701
		// container_rental_floor formula: container_rental_floor:=(oper_cont_depr + roa_container) * hauls_per_month * alloc_rental / rental_factor
	}
	if(rental_type == "None"){//added to handle the rental part of container going to per haul line item if rental is off - Gaurav 20150211
		container_rental_floor = (containerDepreciation + containerROA) *  haulsPerMonth;
	}
	put(returnDict, "comp_rental_floor", string(comp_rental_floor));
	put(returnDict, "container_rental_floor", string(container_rental_floor));
	put(returnDict, "comp_rental_rate", string(comp_rental_rate));
	
	/*if(rental_factor > 0.0 AND feePct <> -1.0){
	
		//Updated 04/07/2014
		//rental_floor = ((containerDepreciation  + compactorDepreciation + containerROA + compactorROA) * haulsPerMonth * alloc_rental/ rental_factor)/(1 + feePct); 
		rental_floor = ((containerDepreciation + compactorDepreciation + containerROA + compactorROA + (containerMntPerHaul * (1 - isContainerCustomerOwned))) * haulsPerMonth * alloc_rental / rental_factor) / (1 + feePct);
	}
	//set rental base price to floor (will still round later)
	rentalArray = float[];
	rental_floor_price = 0.0;
	if(feePct <> -1.0 AND rental_factor > 0.0){
		append(rentalArray, (price_rental_per_month / rental_factor) / (1 + feePct));
	}
	append(rentalArray, rental_floor);
	if(sizeofarray(rentalArray) > 0){
		rental_floor_price = max(rentalArray);
	}*/
	
	rental_floor = (container_rental_floor * alloc_rental  + comp_rental_floor);
	//put(returnDict, "rental_floor", string(rental_floor));
	
	//Divisions should only be providing market rates on containers but not compactors
	
	if(rental_factor > 0.0){
		//rental_base  = (rental_floor / rental_factor) / (1 + feePct);
		//Set rental base price to floor, add in compactor rental
		//Updated 05/08/2014 - per new formula
		//rental_base  = container_rental_floor + comp_rental_rate;
		rentalBaseArr = float[];
		append(rentalBaseArr, price_rental_per_month * compactorCustomerOwned); // Changed as a part of SR 3-9437035701
		append(rentalBaseArr, container_rental_floor * rental_factor * alloc_rental);
		
		//rental_base  = (max(rentalBaseArr) / rental_factor) + comp_rental_rate;
		rental_base  = (max(rentalBaseArr) / rental_factor) + (comp_rental_floor/(1-rsg_compactor_base_roa));
		//Add compactor differential between base and target
		rentalTargetArray = float[];
		append(rentalTargetArray, price_rental_per_month * compactorCustomerOwned); // Changed as a part of SR 3-9437035701 
		append(rentalTargetArray, container_rental_floor * rental_factor * alloc_rental);
		//rental_target  = (max(rentalTargetArray)/rental_factor) + (comp_rental_rate + (comp_rental_rate * 
						 //(rsg_compactor_target_roa - rsg_compactor_base_roa)));
		rental_target  = (max(rentalTargetArray)/rental_factor) + (comp_rental_floor/(1-rsg_compactor_target_roa));
		
		//Add compactor diffenetial between target and stretch
		rentalStretchArray = float[];
		append(rentalStretchArray, price_rental_per_month * compactorCustomerOwned); // Changed as a part of SR 3-9437035701
		append(rentalStretchArray, container_rental_floor * rental_factor * alloc_rental);
		//rental_stretch = (max(rentalStretchArray) /rental_factor) + (comp_rental_rate + (comp_rental_rate * 
		//				(rsg_compactor_stretch_roa - rsg_compactor_base_roa)));
		rental_stretch = (max(rentalStretchArray) /rental_factor) + (comp_rental_floor/(1-rsg_compactor_stretch_roa));
		
		put(returnDict, "rsg_compactor_base_roa", string(rsg_compactor_base_roa));				 
		put(returnDict, "rsg_compactor_target_roa", string(rsg_compactor_target_roa));				 
		put(returnDict, "rsg_compactor_stretch_roa", string(rsg_compactor_stretch_roa));				 
		
		/*
		# round
		# rounding strategy here changes a bit:
		# if monthly rate use the industrial rounding scheme
		# if daily rate use the commercial rounding scheme
		*/
		roundingFactor = 1.0;
		if(rental_type == "Daily"){
			roundingFactor = rounding_ind_rent_daily;
		}elif(rental_type == "Monthly"){
			roundingFactor = rounding_ind_rental;
		}
		/*
		rental_base = ceil(rental_base/ roundingFactor) * roundingFactor;
		rental_target = ceil(rental_target/ roundingFactor) * roundingFactor;
		rental_stretch = ceil(rental_stretch/ roundingFactor) * roundingFactor;
		
		put(returnDict, "rounding_com_rate", string(rounding_com_rate));
		put(returnDict, "rounding_ind_haul", string(rounding_ind_haul));
		put(returnDict, "price_rental_per_month", string(price_rental_per_month));
		put(returnDict, "rental_base", string(rental_base));
		put(returnDict, "rental_target", string(rental_target));
		put(returnDict, "rental_stretch", string(rental_stretch)); */
	}
}
//=============================== END - Calculate Rental Rate ===============================//

//=============================== START - Calculate Haul Rate ===============================//
//Haul floor rate per haul takes inputs from Config
haulBaseArr = Float[];
haulTargetArr = Float[];
haulStretchArr = Float[];

haulFloor = 0.0;
haulBase = 0.0;
haulTarget = 0.0;
haulStretch = 0.0;

disposalPerTonFeeAdj = 0.0;											

if(priceType == "Large Containers"){
	//Overage should include disposal cost in haul & also have another line item for dollars per ton per disposal  
	/*if(billingType == "Flat Rate + Overage"){
		disposalPerTonFeeAdj = disposalRatePerTon; 
	}else{
		disposalPerTonFeeAdj = disposalRatePerTon * allocatedDisposalFlag;
	}*/
	//disposalPerTonFeeAdj = disposalRatePerTon * (1 + feePct) * allocatedDisposalFlag; //Moved as part of SR 3-9428639511
	put(returnDict, "disposalPerTonFeeAdj", string(disposalPerTonFeeAdj));
	//Updated on 04/07/2014
	//haulFloorRatePerHaul = (driverCost + truckCost + truckROA + commission); 	// / feePct;
	//Changed as per SR 3-9428639511
	haulFloorRatePerHaul = (driverCost + truckCost + truckROA + commission + workingCapital + truckDepreciation) + (containerMntPerHaul * (1 - isContainerCustomerOwned)); 	
	
	put(returnDict, "haulFloorRatePerHaul", string(haulFloorRatePerHaul));

	if(feePct <> -1.0){
		/*haulFloor = (haulFloorRatePerHaul + ((1 - allocatedDisposalFlag) * disposalPerTonFeeAdj * tonsPerHaul)) / (1 + feePct);
		//Reclaculate Haul Price when Rental is selected
		if(alloc_rental == 1){
			haul_base_temp = 0.0;
			if(haulsPerMonth > 0.0){
				//haulFloor = (haulFloorRatePerHaul +((1 - allocatedDisposalFlag) * disposalRatePerTon * tonsPerHaul) + (((1 - alloc_rental) * rental_floor * rental_factor * (1 + feePct)) / haulsPerMonth)) / (1 + feePct);
				haulFloor = (haulFloorRatePerHaul + (( 1 - alloc_rental) * rental_floor * rental_factor / haulsPerMonth ));  //Changed as part of SR 3-9437035701
			}
		}*/
		if(haulsPerMonth > 0){
			//Updated 04/08/2014
			//haulFloor = (haulFloorRatePerHaul +((1 - allocatedDisposalFlag) * disposalRatePerTon * tonsPerHaul) + 
						//(((1 - alloc_rental) * rental_floor * rental_factor * (1 + feePct)) / haulsPerMonth)) / (1 + feePct);
			//Updated as part of 3-9428639511, changed disposalRatePerTon to disposalCostPerTon
			print "haul floor";print haulFloorRatePerHaul;
			print "alloc rantal";print alloc_rental;
			print "cont rental floor";print container_rental_floor;
			print "rental factor";print rental_factor;
			print "hauls per month";print haulsPerMonth;
			haulFloor = haulFloorRatePerHaul +(((1 - alloc_rental) * container_rental_floor * rental_factor) / haulsPerMonth);			
		}
								
		append(haulBaseArr, haulFloorRatePerHaul);
		append(haulTargetArr, haulFloorRatePerHaul);
		append(haulStretchArr, haulFloorRatePerHaul);	
	}
	//put(returnDict, "haulFloor", string(haulFloor));
	
	rentalArray = float[];
	rental_floor_price = 0.0;
	if(alloc_rental == 1){
		append(rentalArray, price_rental_per_month);
		append(rentalArray, container_rental_floor * rental_factor);
		if(sizeofarray(rentalArray) > 0){
			rental_floor_price = max(rentalArray);
		}
	}
	
	if(haulsPerMonth <> -1.0 AND haulsPerMonth <> 0){	
		//append(haulBaseArr, (basePriceAdj - (disposalPerTonFeeAdj * haulsPerMonth * tonsPerHaul)) / haulsPerMonth);

		//Changed as part of SR 3-9428639511
		append(haulBaseArr, (basePriceAdj - (container_rental_floor * rental_factor * alloc_rental))/ haulsPerMonth);
		append(haulTargetArr, (targetPriceAdj - (container_rental_floor * rental_factor * alloc_rental))/ haulsPerMonth);
		append(haulStretchArr, (stretchPriceAdj - (container_rental_floor * rental_factor * alloc_rental))/ haulsPerMonth);
	}
	
	haulBase = max(haulBaseArr);
	haulTarget = max(haulTargetArr);
	haulStretch = max(haulStretchArr);
	print "Haul Base Arr: "; print haulBaseArr; print haulTargetArr; print haulStretchArr;
	//Print "Haul Stretch: "; print haulStretch;
}
elif(priceType == "Containers"){
	
	if(feePct <> -1.0){
		put(returnDict, "J4", string(costToServeMonth));
		put(returnDict, "J5", string(1+feePct));
		haulFloor = costToServeMonth / (1 + feePct);
	}
	put(returnDict, "J3", string(haulFloor));
	put(returnDict, "haulFloor", string(haulFloor));
	//Added as part of Cart Pricing changes.  Adds minimum for carts and containers to the haulBaseArr.  Values pulled from Div_Sm_Cont_Factors on line	1533.
	if(containerSizeFloat < 1.0){
		append(haulBaseArr, cartMin);
	}else{
		append(haulBaseArr, contMin);
	}		
	append(haulBaseArr, costToServeMonth);
	append(haulBaseArr, basePriceAdj);
	append(haulBaseArr, (corp_monthly_min_per_container * (1 + feePct) * quantity));
	
	haulBase = max(haulBaseArr);
	if(feePct <> -1.0){
		haulBase = haulBase/ (1 + feePct);
	}
	//append(haulTargetArr, costToServeMonth); //updated as per pricing script on 14 March 2014
	append(haulTargetArr, (haulBase * (1 + feePct)));
	append(haulTargetArr, targetPriceAdj);
	haulTarget = max(haulTargetArr);
	if(feePct <> -1.0){
		haulTarget = haulTarget / (1 + feePct);
	}	
	
	//append(haulStretchArr, costToServeMonth); //updated as per pricing script on 14 March 2014
	append(haulStretchArr, (haulTarget * (1 + feePct)));
	append(haulStretchArr, stretchPriceAdj);
	haulStretch = max(haulStretchArr);
	if(feePct <> -1.0){
		haulStretch = haulStretch / (1 + feePct);
	}
}
	
//Account Type Adjustment on Price between Perm and Temp accounts – particularly on Large Container
if(priceType == "Large Containers"){

	/* if(feePct <> -1.0){ 
		haulBase = haulBase/ (1 + feePct);
		haulTarget = haulTarget / (1 + feePct);
		haulStretch = haulStretch / (1 + feePct);
	} */ //Removed as part of SR 3-9437035701

	put(returnDict, "haulBase_before_rules", string(haulBase));
	put(returnDict, "haulTarget_before_rules", string(haulTarget));
	put(returnDict, "haulStretch_before_rules", string(haulStretch));
	
	//Apply adjustments to haul, target & stretch prices
	//Adjust Pricing - Acquisition type
	haulBase = haulBase * competitorFactor;
	haulTarget = haulTarget * competitorFactor;
	haulStretch = haulStretch * competitorFactor;
	
	put(returnDict, "haulBaseCompetitor", string(haulBase));
	put(returnDict, "haulTargetCompetitor", string(haulTarget));
	put(returnDict, "haulStretchCompetitor", string(haulStretch));
	
	//Divsion Industry adjustment
	haulBase = haulBase * industry_factor;
	haulTarget = haulTarget * industry_factor;
	haulStretch = haulStretch * industry_factor;
	
	put(returnDict, "haulBaseIndustry", string(haulBase));
	put(returnDict, "haulTargetIndustry", string(haulTarget));
	put(returnDict, "haulStretchIndustry", string(haulStretch));
	
	//Divsion Segment adjustment
	haulBase = haulBase * segment_factor;
	haulTarget = haulTarget * segment_factor;
	haulStretch = haulStretch * segment_factor;
	
	put(returnDict, "haulBaseSegment", string(haulBase));
	put(returnDict, "haulTargetSegment", string(haulTarget));
	put(returnDict, "haulStretchSegment", string(haulStretch));
	
	//Contract term default
	//set contract duration factor to 1.0 for temporary accounts for large container - 05/19/2014
	if(permanentFlag == 0){
		contract_dur_factor = 1.0;
		put(returnDict, "contract_dur_adj_factor", string(contract_dur_factor));
	}	
	haulBase = haulBase * contract_dur_factor;
	haulTarget = haulTarget * contract_dur_factor;
	haulStretch = haulStretch * contract_dur_factor;
	
	put(returnDict, "haulBaseContract", string(haulBase));
	put(returnDict, "haulTargetContract", string(haulTarget));
	put(returnDict, "haulStretchContract", string(haulStretch));
	
	/* Peak Rate adjustment */
	peakRateArray = float[];
	if(haulBase > 0){	
		append(peakRateArray, (haulFloor/haulBase) - 1.0);
	}
	append(peakRateArray, 0.0);
	minPeakRate = min(peakRateArray);
	peakRateArray = float[];
	append(peakRateArray, peakRate);
	append(peakRateArray, minPeakRate);
										
	peakRate = max(peakRateArray);
	put(returnDict, "peak_rate_factor1", string(peakRate));
	
	haulBase = haulBase * peakRate;
	put(returnDict, "haulBasePeakRate", string(haulBase));
	
	haulTarget = haulTarget * peakRate;
	put(returnDict, "haulTargetPeakRate", string(haulTarget));
	
	haulStretch = haulStretch * peakRate;
	put(returnDict, "haulStretchPeakRate", string(haulStretch));
	/* Peak Rate adjustment Ends*/
	
	//Moved as part of SR 3-9437035701
	if( feePct <> -1.0){
		disposalFloor = (allocatedDisposalFlag * disposalCostPerTon); 
		//put(returnDict, "disposalFloor", string(disposalFloor));  //AQ 2014-12-10

		//Removed ceiling before rounding
		disposalBase = (disposalRatePerTon * allocatedDisposalFlag);// / (1 + feePct));
		disposalTarget = (disposalRatePerTon * allocatedDisposalFlag);// / (1 + feePct));
		disposalStretch = (disposalRatePerTon * allocatedDisposalFlag);// / (1 + feePct));
		Print "Allocated Disposal Flag:"; print allocatedDisposalFlag; print disposalPerTonFeeAdj;
	}
	print "Disposal: "; print disposalFloor; print disposalBase; print disposalTarget; print disposalStretch;
	
	// 20141016(James): Moved to post disposal calculation so that correct variables are available.
	price_base_adj = 0.0;
	if(feePct <> -1.0){
		//price_base_adj = ((haulBase * haulsPerMonth) + (rental_base * rental_factor) + (disposalPerTonFeeAdj * haulsPerMonth * tonsPerHaul / (1 + feePct))) * (1 + feePct);
		price_base_adj = haulBase * haulsPerMonth + rental_base * rental_factor + disposalRatePerTon * haulsPerMonth * tonsPerHaul;
	}
	put(returnDict, "price_base_adj", string(price_base_adj));
	
	//Calculate frf premiums and fee for new quotes (turn off premium for existing)
	basePremiumRecordSet = bmql("SELECT premium FROM Fee_Removal_Premiums WHERE minMonthlyRevenue <= $price_base_adj AND maxMonthlyRevenue > $price_base_adj AND (Lawson_DivNbr = $divisionNbr OR Lawson_DivNbr = 0) ORDER BY Lawson_DivNbr DESC");
	for eachRecord in basePremiumRecordSet{
		basePremiumStr = get(eachRecord, "premium");
		break;
	}
	haulbaseFRFPremium= 0.0;
	if(isnumber(basePremiumStr)){
		haulbaseFRFPremium = atof(basePremiumStr);
	}
	put(returnDict, "haulbaseFRFPremium", string(haulbaseFRFPremium));
	
	haulBase = haulBase * (1 + ((1 - frfFlag) * haulbaseFRFPremium));
	put(returnDict, "haulBaseFRFPremiumAdj", string(haulBase));
	
	price_target_adj = 0.0;
	if(feePct <> -1){
		//price_target_adj = ((haulTarget * haulsPerMonth) + (rental_target * rental_factor) + (disposalPerTonFeeAdj * haulsPerMonth * tonsPerHaul / (1 + feePct))) * (1 + feePct);
		price_target_adj = (haulTarget * haulsPerMonth) + (rental_target * rental_factor) + (disposalRatePerTon * haulsPerMonth * tonsPerHaul);
	}					
	put(returnDict, "price_target_adj", string(price_target_adj));					
						
	//Calculate frf premiums and fee for new quotes (turn off premium for existing)
	targetPremiumStr = "";
	basePremiumRecordSet = bmql("SELECT premium FROM Fee_Removal_Premiums WHERE minMonthlyRevenue <= $price_target_adj AND maxMonthlyRevenue > $price_target_adj AND (Lawson_DivNbr = $divisionNbr OR Lawson_DivNbr = 0) ORDER BY Lawson_DivNbr DESC");
	for eachRecord in basePremiumRecordSet{
		targetPremiumStr = get(eachRecord, "premium");
		break;
	}
	haulTargetFRFPremium= 0.0;
	if(isnumber(basePremiumStr)){
		haulTargetFRFPremium = atof(targetPremiumStr);
	}
	put(returnDict, "haulTargetFRFPremium", string(haulTargetFRFPremium));

	haulTarget = haulTarget * (1 + ((1 - frfFlag) * haulTargetFRFPremium));
	put(returnDict, "haulTargetFRFPremiumAdj", string(haulTarget));
	
	price_stretch_adj = 0.0;
	if(feePct <> -1.0){
		//price_stretch_adj = ((haulStretch * haulsPerMonth) + (rental_stretch * rental_factor) + (disposalPerTonFeeAdj * haulsPerMonth * tonsPerHaul / (1 + feePct))) * (1 + feePct);
		price_stretch_adj = (haulStretch * haulsPerMonth) + (rental_stretch * rental_factor) + (disposalRatePerTon * haulsPerMonth * tonsPerHaul);
	}	
	put(returnDict, "price_stretch_adj", string(price_stretch_adj));	
						
	//Calculate frf premiums and fee for new quotes (turn off premium for existing)
	stretchPremiumStr = "";
	basePremiumRecordSet = bmql("SELECT premium FROM Fee_Removal_Premiums WHERE minMonthlyRevenue <= $price_stretch_adj AND maxMonthlyRevenue > $price_stretch_adj  AND (Lawson_DivNbr = $divisionNbr OR Lawson_DivNbr = 0) ORDER BY Lawson_DivNbr DESC");
	for eachRecord in basePremiumRecordSet{
		stretchPremiumStr = get(eachRecord, "premium");
		break;
	}
	haulStretchFRFPremium= 0.0;
	if(isnumber(basePremiumStr)){
		haulStretchFRFPremium = atof(stretchPremiumStr);
	}
	put(returnDict, "haulStretchFRFPremium", string(haulStretchFRFPremium));

	haulStretch = haulStretch * (1 + ((1 - frfFlag) * haulStretchFRFPremium));
	put(returnDict, "haulStretchFRFPremiumAdj", string(haulStretch));
		
	//Added as per SR 3-9437035701
	print "Rental and Disposal Information Pre FRF Calc:"; print rental_floor; print rental_base; print rental_target; print rental_stretch; print disposalFloor; print disposalBase; print disposalTarget; print disposalStretch;
	rental_base = rental_base * (1 + ((1-frfFlag) * haulbaseFRFPremium));
	rental_target = rental_target * (1 + ((1-frfFlag) * haultargetFRFPremium));
	rental_stretch = rental_stretch * (1 + ((1 - frfFlag) * haulstretchFRFPremium));
	disposalBase = disposalBase * (1 + ((1-frfFlag) * haulbaseFRFPremium));
	disposalTarget = disposalTarget * (1 + ((1-frfFlag) * haultargetFRFPremium));
	disposalStretch = disposalStretch * (1 + ((1 - frfFlag) * haulstretchFRFPremium));
	print "Rental and Disposal Information Post FRF Calc:"; print rental_floor; print rental_base; print rental_target; print rental_stretch; print disposalFloor; print disposalBase; print disposalTarget; print disposalStretch;
	
	rental_floor = rental_floor /( 1 + feePct);
	rental_base = rental_base /( 1 + feePct);
	rental_target = rental_target /( 1 + feePct);
	rental_stretch = rental_stretch /( 1 + feePct);
	if ((rental_type == "Monthly" OR rental_type == "Daily") AND priceType == "Large Containers"){
		if(roundingFactor <> 0.0 ){
			rental_base = ceil(rental_base/ roundingFactor) * roundingFactor;
			rental_target = ceil(rental_target/ roundingFactor) * roundingFactor;
			rental_stretch = ceil(rental_stretch/ roundingFactor) * roundingFactor;
		}
	}
	print "Disposal 2: "; print disposalFloor; print disposalBase; print disposalTarget; print disposalStretch;
	put(returnDict, "rental_floor", string(rental_floor));
	put(returnDict, "rental_base", string(rental_base));
	put(returnDict, "rental_target", string(rental_target));
	put(returnDict, "rental_stretch", string(rental_stretch));
	put(returnDict, "disposalStretch", string(disposalStretch));
	put(returnDict, "disposalBase", string(disposalBase));
	put(returnDict, "disposalTarget", string(disposalTarget));
	
	//Final prices cannot fall below floor (or each other; or minimum haul rate)
	// note that haul floor and base are already itemized (sans fees)
	
	haulBaseAdjArray = float[];
	append(haulBaseAdjArray, haulFloor);
	append(haulBaseAdjArray, haulBase);
	//append(haulBaseAdjArray, minimum_haul_rate/(1+feePct));
	append(haulBaseAdjArray, minimum_haul_rate);
	haulBase = max(haulBaseAdjArray);

	haulTargetAdjArray = float[];
	//Updated 04/07/2014
	//append(haulTargetAdjArray, haulBase);
	append(haulTargetAdjArray, haulBase * ((1-baseMargin)/(1-targetMargin)));
	append(haulTargetAdjArray, haulTarget);
	haulTarget = max(haulTargetAdjArray);
	
	haulStretchAdjArray = float[];
	//Updated 04/07/2014
	//append(haulStretchAdjArray, haulTarget);
	//append(haulStretchAdjArray, haulTarget * ((1-baseMargin)/(1-stretchMargin)));
	//Updated on 04/28/2014
	append(haulStretchAdjArray, haulBase * ((1-baseMargin)/(1-stretchMargin)));
	append(haulStretchAdjArray, haulStretch);
	haulStretch = max(haulStretchAdjArray);
	
	put(returnDict, "haulBaseMinimumHaul", string(haulBase));
	put(returnDict, "haulTargetMinimumHaul", string(haulTarget));
	put(returnDict, "haulStretchMinimumHaul", string(haulStretch));
	
	// Added as part of SR 3-9428639511 Changed as part of SR 3-9437035701
	haulFloor = haulFloor + ((1 - allocatedDisposalFlag) * disposalCostPerTon * tonsPerHaul);
	haulBase = haulBase + ((1 - allocatedDisposalFlag) * disposalRatePerTon * tonsPerHaul);
	haulTarget = haulTarget + ((1 - allocatedDisposalFlag) * disposalRatePerTon * tonsPerHaul);
	haulStretch = haulStretch + ((1 - allocatedDisposalFlag) * disposalRatePerTon * tonsPerHaul);
	print "DisposalRatePerTon"; print disposalRatePerTon;
	
	put(returnDict, "J2", string(haulFloor));
	haulFloor = haulFloor / (1 + feePct);
	haulBase = haulBase / (1 + feePct);
	haulTarget = haulTarget / (1 + feePct);
	haulStretch = haulStretch / (1 + feePct);
	
}
//Round Haul Base, Target & Stretch but not floor
if(priceType == "Large Containers" AND rounding_ind_haul > 0){  //Should we round even if Rental is not selected?
	haulBase = ceil(haulBase/rounding_ind_haul) * rounding_ind_haul;
	haulTarget = ceil(haulTarget/rounding_ind_haul) * rounding_ind_haul;
	haulStretch = ceil(haulStretch/rounding_ind_haul) * rounding_ind_haul;
}elif(priceType == "Containers" AND rounding_com_rate > 0){ //Remove rounding from pre pricing
	haulBase = ceil(haulBase/rounding_com_rate) * rounding_com_rate;
	haulTarget = ceil(haulTarget/rounding_com_rate) * rounding_com_rate;
	haulStretch = ceil(haulStretch/rounding_com_rate) * rounding_com_rate;
}
	
put(returnDict, "J1", string(haulFloor));
put(returnDict, "haulFloor", string(haulFloor));
put(returnDict, "haulBase", string(haulBase));
put(returnDict, "haulTarget", string(haulTarget));
put(returnDict, "haulStretch", string(haulStretch));

//=============================== END - Calculate Haul Rate ===============================//


if(priceType == "Large Containers"){
	if(feePct <> -1.0){
		//=============================== START - Calculate Disposal Rate ===============================//
		/* Moved as part of SR 3-9437035701
		disposalFloor = (allocatedDisposalFlag * disposalCostPerTon) / (1 + feePct);
		put(returnDict, "disposalFloor", string(disposalFloor));

		//Removed ceiling before rounding
		//disposalBase = (disposalPerTonFeeAdj / (1 + feePct));
		//disposalTarget = (disposalPerTonFeeAdj / (1 + feePct));
		//disposalStretch = (disposalPerTonFeeAdj / (1 + feePct)); */
		//Round Disposal Base, Target, Stretch - Should we round even if Rental is not selected? 
		//Changed 20121204 AQ
		
		targetRoundArr = float[];
			append(targetRoundArr,disposalBase);
			append(targetRoundArr,disposalTarget);
		stretchRoundArr = float[];
			append(stretchRoundArr, disposalTarget);
			append(stretchRoundArr, disposalStretch);
		
		disposalTarget = max(targetRoundArr);
		disposalStretch = max(stretchRoundArr);
		print "Disposal Values: "; print disposalFloor; print disposalBase; print disposalTarget; print disposalStretch;
		
		disposalFloor = disposalFloor /( 1 + feePct);
		disposalBase = disposalBase /( 1 + feePct);
		disposalTarget = disposalTarget /( 1 + feePct);
		disposalStretch = disposalStretch /( 1 + feePct);
		print "Disposal 3: "; print disposalFloor; print disposalBase; print disposalTarget; print disposalStretch; print rounding_ind_dsp;
		
		if(rounding_ind_dsp > 0){
			disposalBase = ceil(disposalBase/rounding_ind_dsp) * rounding_ind_dsp;
			disposalTarget = ceil(disposalTarget/rounding_ind_dsp) * rounding_ind_dsp;
			disposalStretch = ceil(disposalStretch/rounding_ind_dsp) * rounding_ind_dsp;
		}
		print "Disposal 4: "; print disposalFloor; print disposalBase; print disposalTarget; print disposalStretch; print rounding_ind_dsp;
		put(returnDict, "disposalFloor", string(disposalFloor)); //AQ 2014-12-10
		put(returnDict, "disposalStretch", string(disposalStretch));
		put(returnDict, "disposalBase", string(disposalBase));
		put(returnDict, "disposalTarget", string(disposalTarget));
		
		//=============================== END - Calculate Disposal Rate ===============================//

		//=============================== START - Calculate Overage Rate ===============================//

		overageFloor = disposalCostPerTon / (1 + feePct);
		put(returnDict, "overageFloor", string(overageFloor));
		
		overagedisposalPerTonFeeAdj = disposalRatePerTon / (1 + feePct);
		put(returnDict, "overagedisposalPerTonFeeAdj", string(overagedisposalPerTonFeeAdj));
		
		overageBase = overagedisposalPerTonFeeAdj * (1 + ((1-frfFlag) * haulbaseFRFPremium));
		overageTarget = overagedisposalPerTonFeeAdj * (1 + ((1-frfFlag) * haultargetFRFPremium));
		overageStretch = overagedisposalPerTonFeeAdj * (1 + ((1 - frfFlag) * haulstretchFRFPremium));
		
		//Round Disposal Base, Target, Stretch - Should we round even if Rental is not selected?
		if(rounding_ind_dsp > 0){
			overageBase = ceil(overageBase/rounding_ind_dsp) * rounding_ind_dsp;
			overageTarget = ceil(overageTarget/rounding_ind_dsp) * rounding_ind_dsp;
			overageStretch = ceil(overageStretch/rounding_ind_dsp) * rounding_ind_dsp;
		}
		
		put(returnDict, "overageStretch", string(overageStretch));
		put(returnDict, "overageBase", string(overageBase));
		put(returnDict, "overageTarget", string(overageTarget));
		//=============================== END - Calculate Overage Rate ===============================//
	}
}//End of Large Container

//=============================== START - Calculate Delivery ===============================//
//Calculate delivery cost
deliveryFloor = 0.0;
if(permanentFlag == 0 AND priceType == "Large Containers"){
	deliveryFloor = ((driverCost + truckCost + truckROA + truckDepreciation) * 0.5) / (1 + feePct); //changed as per SR 3-9437035701
}
put(returnDict, "deliveryFloor", string(deliveryFloor));

delivery = 55.0; //Legacy code, may need to be removed, under investigation
if(division == "4800" AND priceType == "Large Containers"){
	delivery = 120.0;
}
if(division == "4800" AND priceType == "Containers"){
	delivery = 95.0;
} 
put(returnDict, "delivery", string(delivery));

deliveryBase = delivery;
put(returnDict, "deliveryBase", string(deliveryBase));
deliveryTarget = delivery;
put(returnDict, "deliveryTarget", string(deliveryTarget));
deliveryStretch = delivery;
put(returnDict, "deliveryStretch", string(deliveryStretch));
//=============================== END - Calculate Delivery ===============================//
	
//=============================== START - Supplemental Charges ===============================//

//Get Exchange & Removal charges - Other supplemental Service Rates 
serviceCodesArray = string[];
if(priceType == "Containers"){
	serviceCodesArray = string[]{"DEL", "REM", "REL", "EXC", "EXT", "EXY"};	
}elif(priceType == "Large Containers"){
	serviceCodesArray = string[]{"DEL", "REM", "REL", "EXC", "EXT", "WAS", "DRY"};
}
	
servicesCodesLen = sizeofarray(serviceCodesArray);

ratesDict = dict("float");

//begin2 = getcurrenttimeinmillis();
//diff = begin2 - begin1;
//print "from beginning to before account rate query ="+string(diff);

//For existing customer, get rates from Account_Rates, if not found then get from Div_Service_Price table
accountRatesResultSet = recordset();
if(isExistingCustomer AND serviceChangeType <> ""){
	accountRatesResultSet = bmql("SELECT rate_Amt, charge_cd, division_nbr FROM Account_Rates WHERE container_Grp_Nbr = $containerGroup AND infopro_div_nbr = $infoProDivNum AND (division_nbr = $division OR division_nbr = '0') AND charge_cd IN $serviceCodesArray AND acct_nbr = $accountNumber AND site_Nbr = $siteNumber AND container_Grp_Nbr = $containerGroup ORDER BY division_nbr DESC");	
}

//begin3 = getcurrenttimeinmillis();
//diff = begin3 - begin2;
//print "after account rate query =="+string(diff);

quotes = "\"";

//Get rates for new customer from Div_Service_Price table//added "OR containerType = $routeType" in bmql filtering to include results for both new and old container codes
divServicePriceRS = bmql("SELECT infopro_div_nbr, serviceCode, containerType, servicePrice, divisionNumber FROM Div_Service_Price WHERE (divisionNumber = $division OR divisionNumber = '0') AND (serviceCode IN $serviceCodesArray) AND (containerType = $routeTypeDerived_current OR containerType = $routeType OR containerType IS NULL OR containerType = $quotes) ORDER BY infopro_div_nbr ASC"); //ordering by ASC makes sure all records with info pro div number blank are at bottom
	print "---divServicePriceRS--"; print divServicePriceRS;
if(cr_new_business == 1 AND isExistingCustomer AND serviceChangeType <> ""){ //If its a new business for existing customer
	/*Select rates from tables listed below in same order
	 1. Account_Rates
	 2. Div_Service_Price
		a. Match Division Number, Service Code & Container Code
		b. Match Division Number & Service Code
		c. Match Division 0, Service Code & Container code
		d. Match Division 0, Service Code & blank Container code 			
	*/
	//Account_Rates table
	for eachRecord in accountRatesResultSet{
		div_nbr = get(eachRecord, "division_nbr");
		rate_Amt = getFloat(eachRecord, "rate_Amt");
		charge_cd = get(eachRecord, "charge_cd");
		//If at all records exist, they must be either specific to division or corporate (0)	
		if(not(containskey(ratesDict, charge_cd))){
			put(ratesDict, charge_cd, rate_Amt); //Get rates for all applicable service codes
		}
	}
	/*
	if(NOT(found)){
		for eachRecord in accountRatesResultSet{
			div_nbr = getFloat(eachRecord, "division_nbr");
			rate_Amt = getFloat(eachRecord, "rate_Amt");
			charge_cd = get(eachRecord, "charge_cd");
			if(div_nbr == "0"){
				put(ratesDict, charge_cd, rate_Amt);
				found = true;
			}
		}	
	}*/
	//Query Div_Service_Price table if rates are not found in Account_Rates for existing customer
	ratesDictValues = values(ratesDict);
	if(sizeofarray(ratesDictValues) < servicesCodesLen){
		//Step 1: 
		//Match Division Number, Service Code & Container Code & InfoProNumber
		for eachRecord in divServicePriceRS{
			service_Code_db = get(eachRecord, "serviceCode");
			container_Type_db = get(eachRecord, "containerType");
			service_Price_db = getFloat(eachRecord, "servicePrice");
			division_Number_db = get(eachRecord, "divisionNumber");
			infopro_div_nbr = get(eachRecord, "infopro_div_nbr");
			if(service_Code_db == "DEL"){
				routeType_updated = routeType;
			}
			else{
				routeType_updated = routeTypeDerived_current;
			}
			if(division_Number_db == division AND (findinarray(serviceCodesArray, service_Code_db) > -1) AND container_Type_db ==  routeType_updated AND infopro_div_nbr == infoProDivNum){
				if(not(containskey(ratesDict, service_Code_db))){
					put(ratesDict, service_Code_db, service_Price_db);
				}
			}
		}
		print "---ratesDict 1--"; print ratesDict;
		//Step 2: 
		//Match Division Number, Service Code & Container Code
		ratesDictValues = values(ratesDict);
		if(sizeofarray(ratesDictValues) < servicesCodesLen){
			for eachRecord in divServicePriceRS{
				service_Code_db = get(eachRecord, "serviceCode");
				container_Type_db = get(eachRecord, "containerType");
				service_Price_db = getFloat(eachRecord, "servicePrice");
				division_Number_db = get(eachRecord, "divisionNumber");
				if(service_Code_db == "DEL"){
					routeType_updated = routeType;
				}
				else{
					routeType_updated = routeTypeDerived_current;
				}
				if(division_Number_db == division AND (findinarray(serviceCodesArray, service_Code_db) > -1) AND container_Type_db ==  routeType_updated){
					if(not(containskey(ratesDict, service_Code_db))){
						put(ratesDict, service_Code_db, service_Price_db);
					}
				}
			}
		}
		print "---ratesDict 2--"; print ratesDict;
		//If a record is not found in Step 2 continue to step 3
		//Step 3
		//b. Match Division Number & Service Code
		ratesDictValues = values(ratesDict);
		if(sizeofarray(ratesDictValues) < servicesCodesLen){
			for eachRecord in divServicePriceRS{
				service_Code_db = get(eachRecord, "serviceCode");
				container_Type_db = get(eachRecord, "containerType");
				service_Price_db = getFloat(eachRecord, "servicePrice");
				division_Number_db = get(eachRecord, "divisionNumber");
				if(division_Number_db == division AND (findinarray(serviceCodesArray, service_Code_db) > -1)){
					if(not(containskey(ratesDict, service_Code_db))){
						put(ratesDict, service_Code_db, service_Price_db);
					}
				}
			}
		}
		print "---ratesDict 3--"; print ratesDict;
		//Step 4
		//c. Match Division 0, Service Code & Container code
		ratesDictValues = values(ratesDict);
		if(sizeofarray(ratesDictValues) < servicesCodesLen){
			for eachRecord in divServicePriceRS{
				service_Code_db = get(eachRecord, "serviceCode");
				container_Type_db = get(eachRecord, "containerType");
				service_Price_db = getFloat(eachRecord, "servicePrice");
				division_Number_db = get(eachRecord, "divisionNumber");
				if(service_Code_db == "DEL"){
					routeType_updated = routeType;
				}
				else{
					routeType_updated = routeTypeDerived_current;
				}
				if(division_Number_db == "0" AND (findinarray(serviceCodesArray, service_Code_db) > -1) AND container_Type_db ==  routeType_updated){
					if(not(containskey(ratesDict, service_Code_db))){
						put(ratesDict, service_Code_db, service_Price_db);
					}
				}
			}
		}
		print "---ratesDict 4--"; print ratesDict;
		//Step 5
		//d. Match Division 0, Service Code & blank or Quotes Container code 			
		ratesDictValues = values(ratesDict);
		if(sizeofarray(ratesDictValues) < servicesCodesLen){
			for eachRecord in divServicePriceRS{
				service_Code_db = get(eachRecord, "serviceCode");
				container_Type_db = get(eachRecord, "containerType");
				service_Price_db = getFloat(eachRecord, "servicePrice");
				division_Number_db = get(eachRecord, "divisionNumber");
				if(division_Number_db == "0" AND (findinarray(serviceCodesArray, service_Code_db) > -1) AND (container_Type_db == "" OR container_Type_db == quotes)){
					if(not(containskey(ratesDict, service_Code_db))){
						put(ratesDict, service_Code_db, service_Price_db);
					}	
				}
			}
		}
	}
	print "---ratesDict 5--"; print ratesDict;
}//End of new business for existing customer
else{ //If its a new customer
	
	Div_Ext_Serv_PriceRS = bmql("SELECT service_price FROM Div_Ext_Serv_Price WHERE division_nbr = $division AND infopro_div_nbr = $infoProDivNum AND service_cd = 'EXT' AND container_size = $containerSize");
	print "routeType=="+routeType;
	divServicePriceRS = bmql("SELECT infopro_div_nbr, serviceCode, containerType, servicePrice, divisionNumber FROM Div_Service_Price WHERE (divisionNumber = $division OR divisionNumber = '0') AND (serviceCode IN $serviceCodesArray) AND (containerType = $routeType OR containerType IS NULL OR containerType = $quotes) ORDER BY infopro_div_nbr ASC"); //ordering by ASC makes sure all records with info pro div number blank are at bottom
	
	for eachRecord in Div_Ext_Serv_PriceRS{
		service_price = getFloat(eachRecord, "service_price");
		service_cd = get(eachRecord, "service_cd");
		
		if(not(containskey(ratesDict, service_cd))){
			put(ratesDict, service_cd, service_price);
		}
	}
	ratesDictValues = values(ratesDict);
	if(sizeofarray(ratesDictValues) < servicesCodesLen){
		//Step 1: 
		//Match Division Number, Service Code & Container Code & InfoPro
		for eachRecord in divServicePriceRS{
			service_Code_db = get(eachRecord, "serviceCode");
			container_Type_db = get(eachRecord, "containerType");
			service_Price_db = getFloat(eachRecord, "servicePrice");
			division_Number_db = get(eachRecord, "divisionNumber");
			infopro_div_nbr = get(eachRecord, "infopro_div_nbr");
			if(division_Number_db == division AND (findinarray(serviceCodesArray, service_Code_db) > -1) AND container_Type_db ==  routeType AND infopro_div_nbr == infoProDivNum){
				if(not(containskey(ratesDict, service_Code_db))){
					put(ratesDict, service_Code_db, service_Price_db);
				}
			}
		}
		
		//Step 2: 
		//Match Division Number, Service Code & Container Code
		ratesDictValues = values(ratesDict);
		if(sizeofarray(ratesDictValues) < servicesCodesLen){
			for eachRecord in divServicePriceRS{
				service_Code_db = get(eachRecord, "serviceCode");
				container_Type_db = get(eachRecord, "containerType");
				service_Price_db = getFloat(eachRecord, "servicePrice");
				division_Number_db = get(eachRecord, "divisionNumber");
				if(division_Number_db == division AND (findinarray(serviceCodesArray, service_Code_db) > -1) AND container_Type_db ==  routeType){
					if(not(containskey(ratesDict, service_Code_db))){
						put(ratesDict, service_Code_db, service_Price_db);
					}
				}
			}
		}
		//If a record is not found in Step 2 continue to step 3
		//Step 3
		//b. Match Division Number & Service Code
		ratesDictValues = values(ratesDict);
		if(sizeofarray(ratesDictValues) < servicesCodesLen){
			for eachRecord in divServicePriceRS{
				service_Code_db = get(eachRecord, "serviceCode");
				container_Type_db = get(eachRecord, "containerType");
				service_Price_db = getFloat(eachRecord, "servicePrice");
				division_Number_db = get(eachRecord, "divisionNumber");
				if(division_Number_db == division AND (findinarray(serviceCodesArray, service_Code_db) > -1)){
					if(not(containskey(ratesDict, service_Code_db))){
						put(ratesDict, service_Code_db, service_Price_db);
					}
				}
			}
		}
		
		//Step 4
		//c. Match Division 0, Service Code & Container code
		ratesDictValues = values(ratesDict);
		if(sizeofarray(ratesDictValues) < servicesCodesLen){
			for eachRecord in divServicePriceRS{
				service_Code_db = get(eachRecord, "serviceCode");
				container_Type_db = get(eachRecord, "containerType");
				service_Price_db = getFloat(eachRecord, "servicePrice");
				division_Number_db = get(eachRecord, "divisionNumber");
				if(division_Number_db == "0" AND (findinarray(serviceCodesArray, service_Code_db) > -1) AND container_Type_db ==  routeType){
					if(not(containskey(ratesDict, service_Code_db))){
						put(ratesDict, service_Code_db, service_Price_db);
					}
				}
			}
		}
		
		//Step 5
		//d. Match Division 0, Service Code & blank Container code 			
		ratesDictValues = values(ratesDict);
		if(sizeofarray(ratesDictValues) < servicesCodesLen){
			for eachRecord in divServicePriceRS{
				service_Code_db = get(eachRecord, "serviceCode");
				container_Type_db = get(eachRecord, "containerType");
				service_Price_db = getFloat(eachRecord, "servicePrice");
				division_Number_db = get(eachRecord, "divisionNumber");
				if(division_Number_db == "0" AND (findinarray(serviceCodesArray, service_Code_db) > -1) AND (container_Type_db == "" OR container_Type_db == quotes)){
					if(not(containskey(ratesDict, service_Code_db))){
						put(ratesDict, service_Code_db, service_Price_db);
					}	
				}
			}
		}
	}
}
//Now that ratesDict has some values in it, assign the rate to Service - TO DO
serviceRate = 0.0;
for eachServiceCode in serviceCodesArray{
	if(containskey(ratesDict, eachServiceCode)){
		serviceRate = get(ratesDict, eachServiceCode);
		put(returnDict, eachServiceCode, string(serviceRate));
	}
}

	
/*
begin4 = getcurrenttimeinmillis();
diff = begin4 - begin3;
print "from after accounr rates table query to End of script =="+string(diff);
*/
	
//=============================== END - Supplemental Charges ===============================//

return returnDict;