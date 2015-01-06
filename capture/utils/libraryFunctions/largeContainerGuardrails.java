/*
================================================================================
Name:   Large Container Guardrail Pricing
Author:   Zach Schlieder
Create date:  12/23/13
Description:  Calculates the Base, Target, and Stretch for a large container price based on the cost to serve and other config attributes
        
Input:   	stringDict: String Dictionary - Contains values of Config and Commerce attributes used in calculations and table calls
                    
Output:  	String Dictionary - Contains attribute name and value pairs for use in Config or Commerce

Updates:	
    
=====================================================================================================
*/

//=============================== START - Variable Initialization ===============================//
//Default Variables
returnDict = dict("string");

erfOnFrfRateStr = get(stringDict, "erfOnFrfRate");
siteName = get(stringDict, "siteName");
wasteType = get(stringDict, "wasteType");
division = get(stringDict, "division");
accountType = get(stringDict, "accountType");
billingType = get(stringDict, "billingType");
//feesToCharge = get(stringDict, "feesToCharge");
	//Temporary
	includeERF = get(stringDict, "includeERF");
	includeFRF = get(stringDict, "includeFRF");
costToServeMonthStr = get(stringDict, "costToServeMonth");
baseMarginStr = get(stringDict, "baseMargin");
targetMarginStr = get(stringDict, "targetMargin");
stretchMarginStr = get(stringDict, "stretchMargin");
driverCostStr = get(stringDict, "driverCost");
truckCostStr = get(stringDict, "truckCost");
truckROAStr = get(stringDict, "truckROA");
commissionStr = get(stringDict, "commission");
tonsPerHaulStr = get(stringDict, "tonsPerHaul");
haulsPerMonthStr = get(stringDict, "haulsPerMonth");
commissionRateStr = get(stringDict, "commissionRate");
flatRateCommissionStr = get(stringDict, "flatRateCommission");
competitorFactorStr = get(stringDict, "competitorFactor");

//Convert necessary variables from string to integer for use in calculations
frfFlag = 0;
erfFlag = 0;
allocatedDisposalFlag = 0;
permanentFlag = 0;
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
//END - temporary workaround while resolving fee issues.

if(find(billingType, "Disposal") <> -1){
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

if(isnumber(erfOnFrfRateStr)){
	erfOnFrfRate = atof(erfOnFrfRateStr);
}
if(isnumber(costToServeMonthStr)){
	costToServeMonth = atof(costToServeMonthStr);
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
if(isnumber(competitorFactorStr)){
	competitorFactor = atof(competitorFactorStr);
}

//Calculate the ERF on FRF flag based on the corresponding flags
erfOnFrfFlag = 0;
if(frfFlag == 1 AND erfFlag == 1){
	erfOnFrfFlag = 1;
}

//Initialize string variables set by data table results
//Division Fee Rate
frfRateStr = "";
erfRateStr = "";
//Disposal_Sites table
disposalCostPerTonStr = "";
//IND_Margins table
disposalRatePerTonStr = "";
baseMarginStr = "";
targetMarginStr = "";
stretchMarginStr = "";

//=============================== END - Variable Initialization ===============================//

//=============================== START - Table Lookups ===============================//
//Get values of all necessary values for calculations from data tables or parts database
	//=============================== START - Lookups on the Division Fee Rate table ===============================//
	frfRate = 0.0;
	erfRate = 0.0;
	divisionFeeRateRecordSet = bmql("SELECT fRFRate, eRFRate FROM divisionFeeRate WHERE divisionNumber = $division");
	
	for eachRecord in divisionFeeRateRecordSet{
		frfRateStr = get(eachRecord, "fRFRate");
		erfRateStr = get(eachRecord, "eRFRate");
	}
	
	if(isnumber(frfRateStr)){	//Convert the table result to a float for use in calculations
		frfRate = atof(frfRateStr) / 100.0;
	}
	put(returnDict, "frfRate", string(frfRate));
	
	if(isnumber(erfRateStr)){	//Convert the table result to a float for use in calculations
		erfRate = atof(erfRateStr) / 100.0;
	}
	put(returnDict, "erfRate", string(erfRate));
	//=============================== END - Lookups on the Division Fee Rate table ===============================//

	//=============================== START - Lookups on the Disposal_Sites table ===============================//
	disposalCostPerTon = 0.0;
	if(wasteType == "Solid Waste"){
		disposalSiteCostsRecordSet = bmql("SELECT cost FROM Disposal_Sites WHERE Site_Name = $siteName AND WasteType = $wasteType AND DisposalSite_DivNbr = $division");

		for eachRecord in disposalSiteCostsRecordSet{
			disposalCostPerTonStr = get(eachRecord, "cost");
			break;	//Only one record is expected
		}
		
		if(isnumber(disposalCostPerTonStr)){	//Convert the table result to a float for use in calculations
				disposalCostPerTon = atof(disposalCostPerTonStr);
		}
	}
	put(returnDict, "disposalCostPerTon", string(disposalCostPerTon));
	//=============================== END - Lookups on the Disposal_Sites table ===============================//

	//=============================== START - Lookups on the Industrial Margins table ===============================//
	disposalRatePerTon = 0.0;
	baseMargin = 0.0;
	targetMargin = 0.0;
	stretchMargin = 0.0;

	indMarginsRecordSet = bmql("SELECT baseMargin, targetMargin, stretchMargin, marketRate FROM IND_margins WHERE cur_div_nbr = $division");

	for eachRecord in indMarginsRecordSet{
		disposalRatePerTonStr = get(eachRecord, "marketRate");
		baseMarginStr = get(eachRecord, "baseMargin");
		targetMarginStr = get(eachRecord, "targetMargin");
		stretchMarginStr = get(eachRecord, "stretchMargin");
		break;	//Only one record is expected
	}
	if(wasteType == "Solid Waste"){
		if(isnumber(disposalRatePerTonStr)){	//Convert the table result to a float for use in calculations
				disposalRatePerTon = atof(disposalRatePerTonStr);
		}
	}
	put(returnDict, "disposalRatePerTon", string(disposalRatePerTon));
	
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
	//=============================== END - Lookups on the Industrial Margins table ===============================//

//=============================== END - Table Lookups ===============================//

//=============================== START - Calculate Guardrails Excluding Fees ===============================//
totalFeePct = 1 + ((1 * frfRate) + (1 * erfRate) + erfOnFrfRate * erfRate * frfRate);
put(returnDict, "totalFeePct", string(totalFeePct));

floorExcludingFees = 0.0;
if(totalFeePct <> 0.0){
	floorExcludingFees = costToServeMonth / totalFeePct;
}
put(returnDict, "floorExcludingFees", string(floorExcludingFees));
baseExcludingFees = floorExcludingFees * (1 + baseMargin);
targetExcludingFees = floorExcludingFees * (1 + targetMargin);
stretchExcludingFees = floorExcludingFees * (1 + stretchMargin);


//Ensure that the guardrails are appropriate relative to each other
if(baseExcludingFees < floorExcludingFees){
	baseExcludingFees = floorExcludingFees;
}
put(returnDict, "baseExcludingFees", string(baseExcludingFees));
if(targetExcludingFees < baseExcludingFees){
	targetExcludingFees = baseExcludingFees;
}
put(returnDict, "targetExcludingFees", string(targetExcludingFees));
if(stretchExcludingFees < targetExcludingFees){
	stretchExcludingFees = targetExcludingFees;
}
put(returnDict, "stretchExcludingFees", string(stretchExcludingFees));
//=============================== END - Calculate Guardrails Excluding Fees ===============================//

//=============================== START - Calculate Fee Removal Premiums ===============================//
//Get the fee removal premiums for each of the guardrails
basePremiumStr = "";
baseFRFPremium = 0.0;
baseExcludingFeesForMarkup = baseExcludingFees * totalFeePct;

basePremiumRecordSet = bmql("SELECT premium FROM Fee_Removal_Premiums WHERE minMonthlyRevenue <= $baseExcludingFeesForMarkup AND maxMonthlyRevenue > $baseExcludingFeesForMarkup");
for eachRecord in basePremiumRecordSet{
	basePremiumStr = get(eachRecord, "premium");
}

if(isnumber(basePremiumStr)){
	baseFRFPremium = atof(basePremiumStr);
}
put(returnDict, "baseFRFPremium", string(baseFRFPremium));

targetPremiumStr = "";
targetFRFPremium = 0.0;
targetExcludingFeesForMarkup = targetExcludingFees * totalFeePct;

targetPremiumRecordSet = bmql("SELECT premium FROM Fee_Removal_Premiums WHERE minMonthlyRevenue <= $targetExcludingFeesForMarkup AND maxMonthlyRevenue > $targetExcludingFeesForMarkup");

for eachRecord in targetPremiumRecordSet{
	targetPremiumStr = get(eachRecord, "premium");
}

if(isnumber(targetPremiumStr)){
	targetFRFPremium = atof(targetPremiumStr);
}
put(returnDict, "targetFRFPremium", string(targetFRFPremium));

stretchPremiumStr = "";
stretchFRFPremium = 0.0;
stretchExcludingFeesForMarkup = stretchExcludingFees * totalFeePct;

stretchPremiumRecordSet = bmql("SELECT premium FROM Fee_Removal_Premiums WHERE minMonthlyRevenue <= $stretchExcludingFeesForMarkup AND maxMonthlyRevenue > $stretchExcludingFeesForMarkup");

for eachRecord in stretchPremiumRecordSet{
	stretchPremiumStr = get(eachRecord, "premium");
}

if(isnumber(stretchPremiumStr)){
	stretchFRFPremium = atof(stretchPremiumStr);
}
put(returnDict, "stretchFRFPremium", string(stretchFRFPremium));
//=============================== END - Calculate Fee Removal Premiums ===============================//

//=============================== START - Add Fees ===============================//
//Add the fees to each guardrail
baseExcludingFees = ((baseExcludingFees + ((1 - frfFlag) * frfRate * baseExcludingFees) + ((1 - erfFlag) * erfRate * baseExcludingFees) + ((1 - erfOnFrfFlag) * erfRate * frfRate * baseExcludingFees)) * (((1 - frfFlag) * baseFRFPremium) + 1)) * competitorFactor;
put(returnDict, "baseExcludingFees", string(baseExcludingFees));

targetExcludingFees = ((targetExcludingFees + ((1 - frfFlag) * frfRate * targetExcludingFees) + ((1 - erfFlag) * erfRate * targetExcludingFees) + ((1 - erfOnFrfFlag) * erfRate * frfRate * targetExcludingFees)) * (((1 - frfFlag) * targetFRFPremium) + 1)) * competitorFactor;
put(returnDict, "targetExcludingFees", string(targetExcludingFees));

stretchExcludingFees = ((stretchExcludingFees + ((1 - frfFlag) * frfRate * stretchExcludingFees) + ((1 - erfFlag) * erfRate * stretchExcludingFees) + ((1 - erfOnFrfFlag) * erfRate * frfRate * stretchExcludingFees)) * (((1 - frfFlag) * stretchFRFPremium) + 1)) * competitorFactor;
put(returnDict, "stretchExcludingFees", string(stretchExcludingFees));

//Calculate Disposals prices
disposalPerTonFeeAdj = ceil((allocatedDisposalFlag + ((1 - frfFlag) * baseFRFPremium * allocatedDisposalFlag)) * (disposalRatePerTon + ((1 - erfFlag) * erfRate * disposalRatePerTon) + ((1 - frfFlag) * frfRate * disposalRatePerTon) + ((1 - erfOnFrfFlag) * erfRate * frfRate * disposalRatePerTon)));
put(returnDict, "disposalPerTonFeeAdj", string(disposalPerTonFeeAdj));

disposalBase = disposalPerTonFeeAdj;
put(returnDict, "disposalBase", string(disposalBase));
disposalTarget = disposalPerTonFeeAdj;
put(returnDict, "disposalTarget", string(disposalTarget));
disposalStretch = disposalPerTonFeeAdj;
put(returnDict, "disposalStretch", string(disposalStretch));

disposalFRF = disposalTarget * frfRate * frfFlag;
put(returnDict, "disposalFRF", string(disposalFRF));
disposalERF = (disposalTarget + disposalFRF) * erfRate * erfFlag;
put(returnDict, "disposalERF", string(disposalERF));
//=============================== END - Add Fees ===============================//

//=============================== START - Calculate Haul Rate ===============================//
//Haul floor rate per haul takes inputs from Config
haulFloorRatePerHaul = 0.0;
if(totalFeePct <> 0.0){
	haulFloorRatePerHaul = (driverCost + truckCost + truckROA + commission) / totalFeePct;
}
put(returnDict, "haulFloorRatePerHaul", string(haulFloorRatePerHaul));

haulFloor = haulFloorRatePerHaul + ((1 - allocatedDisposalFlag) * disposalCostPerTon);
put(returnDict, "haulFloor", string(haulFloor));

haulBase = 0.0;
if(haulsPerMonth <> 0.0){
	haulBase = (baseExcludingFees - (disposalPerTonFeeAdj * haulsPerMonth * tonsPerHaul)) / haulsPerMonth;
}
haulBaseArr = Float[]{haulFloor, haulBase};
haulBase = max(haulBaseArr);
put(returnDict, "haulBase", string(haulBase));

haulTarget = 0.0;
if(haulsPerMonth <> 0.0){
	haulTarget = (targetExcludingFees - (disposalPerTonFeeAdj * haulsPerMonth * tonsPerHaul)) / haulsPerMonth;
}
haulTargetArr = Float[]{haulFloor, haulTarget};
haulTarget = max(haulTargetArr);
put(returnDict, "haulTarget", string(haulTarget));

haulStretch = 0.0;
if(haulsPerMonth <> 0.0){
	haulStretch = (stretchExcludingFees - (disposalPerTonFeeAdj * haulsPerMonth * tonsPerHaul)) / haulsPerMonth;
}
haulStretchArr = Float[]{haulFloor, haulStretch};
haulStretch = max(haulStretchArr);
put(returnDict, "haulStretch", string(haulStretch));

haulFRF = haulTarget * frfRate * frfFlag;
put(returnDict, "haulFRF", string(haulFRF));
haulERF = (haulTarget + haulFRF) * erfRate * erfFlag;
put(returnDict, "haulERF", string(haulERF));
//=============================== END - Calculate Haul Rate ===============================//

//=============================== START - Calculate Delivery and Disposal ===============================//
//Calculate delivery cost
deliveryFloor = 0.0;
if(permanentFlag == 0){
	deliveryFloor = (driverCost + truckCost + truckROA) / 2.0;
}
put(returnDict, "deliveryFloor", string(deliveryFloor));

delivery = 55.0;
if(division == "4800"){
	delivery = 120.0;
}

deliveryBase = delivery;
put(returnDict, "deliveryBase", string(deliveryBase));
deliveryTarget = delivery;
put(returnDict, "deliveryTarget", string(deliveryTarget));
deliveryStretch = delivery;
put(returnDict, "deliveryStretch", string(deliveryStretch));

deliveryFRF = deliveryTarget * frfRate * frfFlag;
put(returnDict, "deliveryFRF", string(deliveryFRF));
deliveryERF = (deliveryTarget + deliveryFRF) * erfRate * erfFlag;
put(returnDict, "deliveryERF", string(deliveryERF));

//Calculate disposal cost
disposalFloor = allocatedDisposalFlag * disposalCostPerTon;
put(returnDict, "disposalFloor", string(disposalFloor));
//=============================== END - Calculate Delivery and Disposal ===============================//

//=============================== START - Calculate Totals ===============================//
totalFloor = (haulFloor * haulsPerMonth) + (disposalFloor * haulsPerMonth * tonsPerHaul);
put(returnDict, "totalFloor", string(totalFloor));
totalFloorFRF = totalFloor * (frfFlag * frfRate);
put(returnDict, "totalFloorFRF", string(totalFloorFRF));
totalFloorERF = (totalFloor + (totalFloorFRF * erfOnFrfFlag)) * (erfFlag * erfRate);
put(returnDict, "totalFloorERF", string(totalFloorERF));

totalBase = (haulBase * haulsPerMonth) + (disposalBase * haulsPerMonth * tonsPerHaul);
put(returnDict, "totalBase", string(totalBase));
totalBaseFRF = totalBase * (frfFlag * frfRate);
put(returnDict, "totalBaseFRF", string(totalBaseFRF));
totalBaseERF = (totalBase + (totalBaseFRF * erfOnFrfFlag)) * (erfFlag * erfRate);
put(returnDict, "totalBaseERF", string(totalBaseERF));

totalTarget = (haulTarget * haulsPerMonth) + (disposalTarget * haulsPerMonth * tonsPerHaul);
put(returnDict, "totalTarget", string(totalTarget));
totalTargetFRF = totalTarget * (frfFlag * frfRate);
put(returnDict, "totalTargetFRF", string(totalTargetFRF));
totalTargetERF = (totalTarget + (totalTargetFRF * erfOnFrfFlag)) * (erfFlag * erfRate);
put(returnDict, "totalTargetERF", string(totalTargetERF));

totalStretch = (haulStretch * haulsPerMonth) + (disposalStretch * haulsPerMonth * tonsPerHaul);
put(returnDict, "totalStretch", string(totalStretch));
totalStretchFRF = totalStretch * (frfFlag * frfRate);
put(returnDict, "totalStretchFRF", string(totalStretchFRF));
totalStretchERF = (totalStretch + (totalStretchFRF * erfOnFrfFlag)) * (erfFlag * erfRate);
put(returnDict, "totalStretchERF", string(totalStretchERF));
//=============================== END - Calculate Totals ===============================//


//=============================== START - Calculate Commissions ===============================//
commissionFloor = ((haulFloor * haulsPerMonth) + (disposalFloor * haulsPerMonth * tonsPerHaul)) * (1 + (frfFlag * frfRate) + (erfFlag * erfRate) + (erfOnFrfFlag * erfRate * frfRate)) * (permanentFlag * commissionRate) + ((1 - permanentFlag) * flatRateCommission);
put(returnDict, "commissionFloor", string(commissionFloor));

commissionBase = ((haulBase * haulsPerMonth) + (disposalBase * haulsPerMonth * tonsPerHaul)) * (1 + (frfFlag * frfRate) + (erfFlag * erfRate) + (erfOnFrfFlag * erfRate * frfRate)) * (permanentFlag * commissionRate) + ((1 - permanentFlag) * flatRateCommission);
put(returnDict, "commissionBase", string(commissionBase));

commissionTarget = ((haulTarget * haulsPerMonth) + (disposalTarget * haulsPerMonth * tonsPerHaul)) * (1 + (frfFlag * frfRate) + (erfFlag * erfRate) + (erfOnFrfFlag * erfRate * frfRate)) * (permanentFlag * commissionRate) + ((1 - permanentFlag) * flatRateCommission);
put(returnDict, "commissionTarget", string(commissionTarget));

commissionStretch = ((haulStretch * haulsPerMonth) + (disposalStretch * haulsPerMonth * tonsPerHaul)) * (1 + (frfFlag * frfRate) + (erfFlag * erfRate) + (erfOnFrfFlag * erfRate * frfRate)) * (permanentFlag * commissionRate) + ((1 - permanentFlag) * flatRateCommission);
put(returnDict, "commissionStretch", string(commissionStretch));
//=============================== END - Calculate Commissions ===============================//

return returnDict;