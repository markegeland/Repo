returnDict = dict("string");

/*Industrial Existing Variables */
LE_accountType = "";
LE_rental = "";
rental_factor_LE = 1.0;
old_rental_factor_LE = 1.0;
LE_wasteType = "";
LE_unitOfMeasure = "";
LE_containerSize = "";
LE_haulsPerContainer = "";
LE_billingType = "";
servicelLevel_LE = "";
LE_unitsPerHaul = "";
estHaulsPerMonthStr_LE = "";
haulsPerContainerPerMonth_LE = "";
competitor_LE = "";
LE_totalEstimatedHaulsMonth = 0.0;
overrideTime_LE = 0.0;
frequency_LE = 0.0;
expirationDate_LE = "";
contractMonths_LE = "";
is_frf_locked_LE = "";
is_erf_locked_LE = "";
is_frf_on_db_LE = "";
is_erf_on_db_LE = "";
is_erf_on_frf_db_LE = "";
is_admn_on_db_LE = "";
customerOwnedContainer_LE = "";
customerOwnedCompactor_LE = "";
adminRate_LE = 0.0;
is_erf_charged_LE = "No";
is_frf_charged_LE = "No";
is_erf_on_frf_charged_LE = "No";
frfPct_LE = 0.0;
erfPct_LE = 0.0;
dsp_change_flag_LE = 0;
dsp_change_loc_flag_LE = 0;
changeType_LE = "";
comp_bid_markup_LE = 0.0;
comp_targ_pct_retain_LE = 0.0;
comp_str_pct_retain_LE = 0.0;
save_base_margin_adj_LE = 0.0;
save_targ_pct_retain_LE = 0.0;
save_str_pct_retain_LE = 0.0;
cat_yards_per_month_LE = "";
pi_retain_base_LE = 0.0;
pi_retain_target_LE = 0.0;
pi_retain_stretch_LE = 0.0;
alloc_rental_LE = 0;
svc_base_marg_prem_LE = 0.0;
svc_targ_marg_prem_LE = 0.0;
svc_str_marg_prem_LE = 0.0;
svc_gap_recovery_pct_LE = 0.0;
permanentFlag_LE = 0;
market_rental_rate_LE = 0.0;
market_dsp_rate_LE = 0.0;
alloc_disposal_LE = 1;
floorAvgSpread_LE = 1.0;
avgTargetSpread_LE = 1.0;
has_compactor_LE = 0;
container_key_LE = "";
competitor_cd_LE = "";
nb_haul_cost_LE = 0.0;
nb_dsp_cost_LE = 0.0;
nb_ovr_cost_LE = 0.0;
nb_ren_cost_LE = 0.0;
haul_rate_LE = 0.0;
dsp_rate_LE = 0.0;
ovr_rate_LE = 0.0;
ren_rate_LE =0.0;
container_roi_LE = 0.0;
container_depr_LE = 0.0;
rental_change_flag_LE = 0;
ren_fac_change_flag_LE = 0;
min_units_per_haul_LE = "";
Flat_Rate_Incl_Tons_LE = 0.0;
haul_cost_components_LE = 0.0;
curr_haul_margin_pct = 0.0;
fee_flag_change_LE = 0;
last_pi_haul_amt_LE = 0.0;
last_pi_dsp_amt_LE = 0.0;
last_pi_ovr_amt_LE = 0.0;
last_pi_ren_amt_LE = 0.0;
curr_haul_margin_dol_LE = 0.0;
curr_dsp_margin_dol_LE = 0.0;
curr_ovr_margin_dol_LE = 0.0;
curr_ren_margin_dol_LE = 0.0;
existing_operating_expense = 0.0;
existing_cts_month_incl_oh = 0.0;
existing_cost_disp_xfer_proc = 0.0;
isERFOnFRFChargedAtDivisionLevel = false;
serviceChangeQuote = false;
competitiveBidQuote = false;
rollbackOfPIQuote = false;
generalSaveQuote = false;

contCat = get(stringDict, "contCat");
containerType_sc = get(stringDict, "containerType_sc");
cat_yards_per_month_LE = get(stringDict, "cat_yards_per_month_LE");
currentContainerGrpNum = get(stringDict, "currentContainerGrpNum");
salesActivity = get(stringDict, "salesActivity");
priceAdjustmentReason = get(stringDict, "priceAdjustmentReason");
serviceChangeReason = get(stringDict, "serviceChangeReason");
changeOfOwnerReason = get(stringDict, "changeOfOwnerReason");
adminRateStr = get(stringDict, "adminRateStr");
salesActivity_LE = get(stringDict, "salesActivity_LE");

serviceChangeQuoteStr = get(stringDict, "serviceChangeQuoteStr");
competitiveBidQuoteStr = get(stringDict, "competitiveBidQuoteStr");
rollbackOfPIQuoteStr = get(stringDict, "rollbackOfPIQuoteStr");
generalSaveQuoteStr = get(stringDict, "generalSaveQuoteStr");
frfRateStr = get(stringDict, "frfRateStr");
erfRateStr = get(stringDict, "erfRateStr");
isERFOnFRFChargedAtDivisionLevelStr = get(stringDict, "isERFOnFRFChargedAtDivisionLevelStr");
eRFOnFRFStr = get(stringDict, "eRFOnFRFStr");

if(isnumber(frfRateStr)){
	frfRate = atof(frfRateStr);
}
if(isnumber(erfRateStr)){
	erfRate = atof(erfRateStr);
}
if(isnumber(eRFOnFRFStr)){
	eRFOnFRF = atof(eRFOnFRFStr);
}
if(isERFOnFRFChargedAtDivisionLevelStr == "true"){
	isERFOnFRFChargedAtDivisionLevel = true;
}
if(serviceChangeQuoteStr == "true"){
	serviceChangeQuote = true;
}
if(competitiveBidQuoteStr == "true"){
	competitiveBidQuote = true;
}
if(rollbackOfPIQuoteStr == "true"){
	rollbackOfPIQuote = true;
}
if(generalSaveQuoteStr == "true"){
	generalSaveQuote = true;
}

competitiveBidAmountHaul_lc = get(stringDict, "competitiveBidAmountHaul_lc");
competitiveBidAmountDisposal_lc = get(stringDict, "competitiveBidAmountDisposal_lc");
competitiveBidAmountRental_lc = get(stringDict, "competitiveBidAmountRental_lc");
accountType_current_readonly_lc = get(stringDict, "accountType_current_readonly");
accountType_lc = get(stringDict, "accountType_lc");
containerType_lc = get(stringDict, "containerType_lc");
rental_lc_readonly = get(stringDict, "rental_lc_readOnly");
rental_lc = get(stringDict, "rental_lc");
wasteType_lc_readonly = get(stringDict, "wasteType_lc_readonly");
wasteType_lc = get(stringDict, "wasteType_lc");
unitOfMeasure_lc_readOnly = get(stringDict, "unitOfMeasure_lc_readOnly");
unitOfMeasure_lc = get(stringDict, "unitOfMeasure_lc");
quantity_lc_readonly = get(stringDict, "quantity_lc_readonly");
quantity_lc = get(stringDict, "quantity_lc");
containerSize_lc_readonly = get(stringDict, "containerSize_lc_readonly");
containerSize_lc = get(stringDict, "containerSize_lc");
customerOwnedContainer_lc_readonly = get(stringDict, "customerOwnedContainer_lc_readonly");
customerOwnedCompactor_lc_readonly = get(stringDict, "customerOwnedCompactor_lc_readonly");
haulsPerContainer_current_lc_readonly = get(stringDict, "haulsPerContainer_current_lc_readonly");
haulsPerContainer_lc = get(stringDict, "haulsPerContainer_lc");
compactorValue_lc = get(stringDict, "compactorValue");
totalEstimatedHaulsMonth_lc = get(stringDict, "totalEstimatedHaulsMonth_lc");
totalEstimatedHaulsMonth_lc_readonly = get(stringDict, "totalEstimatedHaulsMonth_lc_readonly");
customerSiteTime_lc_readOnly = get(stringDict, "customerSiteTime_lc_readOnly");
disposalSite_lc = get(stringDict, "disposalSite_config");
//routeTypeDervied_lc = get(stringDict, "routeTypeDervied");
billingType_lc = get(stringDict, "billingType_lc");
billingType_lc_readOnly = get(stringDict, "billingType_lc_readOnly");
estTonsHaul_lc = get(stringDict, "estTonsHaul_lc");
estTonsHaul_lc_readOnly = get(stringDict, "estTonsHaul_lc_readOnly");
minimumTonsPerHaul_lc = get(stringDict, "minimumTonsPerHaul_lc");
minimumTonsPerHaul_lc_readOnly = get(stringDict, "minimumTonsPerHaul_lc_readOnly");
tonsIncludedInHaulRate_lc = get(stringDict, "tonsIncludedInHaulRate_lc");
tonsIncludedInHaulRate_lc_readOnly = get(stringDict, "tonsIncludedInHaulRate_lc_readOnly");
specialHandlingCode_lc = get(stringDict, "specialHandlingCode");
additionalPaperwork_lc = get(stringDict, "additionalPaperwork_l");
disposalTicketSignature_lc = get(stringDict, "disposalTicketSignature_l");
washout_lc = get(stringDict, "washout_l");
onsiteTimeInMins_lc = get(stringDict, "onsiteTimeInMins");
landfillCode_readOnly_lc = get(stringDict, "landfillCode_readOnly");
dspSiteNumber_lc = get(stringDict, "alternateSite_l");
customerSiteTimeOverride_lc = get(stringDict, "customerSiteTimeOverride_l");
roundtripDriveTimeOverride_lc = get(stringDict, "roundtripDriveTimeOverride_l");
disposalTimeOverride_lc = get(stringDict, "disposalTimeOverride_l");
adjustedTotalTime_lc = get(stringDict, "adjustedTotalTime_l");
longitude_LE = get(stringDict, "longitude_l");
latitude_LE = get(stringDict, "latitude_l");
longitudeCustomerSite_LE = get(stringDict, "longitudeCustomerSite");
latitudeCustomerSite_LE = get(stringDict, "latitudeCustomerSite");
overrideTimeStr_LE = get(stringDict, "OverrideTotalTime_l");
routeTypeDervied_LE = get(stringDict, "routeTypeDervied");
frequencyStr_LE = get(stringDict, "frequency");
wasteCategory_LE = get(stringDict, "wasteCategory");
estTonsHaul_lc = get(stringDict, "estTonsHaul_lc");
division_quote = get(stringDict, "division_quote");
siteNumber_quote = get(stringDict, "siteNumber_quote");
quote_process_customer_id = get(stringDict, "quote_process_customer_id");
feesToCharge_quote = get(stringDict, "feesToCharge_quote");
includeFRF_quote = get(stringDict, "includeFRF_quote");
lOBCategoryDerived_LE = get(stringDict, "lOBCategoryDerived_LE");

if(customerOwnedContainer_lc_readonly == "No"){
	customerOwnedContainer_LE = "0";
}else{
	customerOwnedContainer_LE = "1";
}
if(customerOwnedCompactor_lc_readonly == "No"){
	customerOwnedCompactor_LE = "0";
}else{
	customerOwnedCompactor_LE = "1";
}	
if(containerType_lc <> "Open Top"){
	has_compactor_LE = 1;
}
if(unitOfMeasure_lc == "No Change"){
	LE_unitOfMeasure = unitOfMeasure_lc_readOnly;
}else{
	LE_unitOfMeasure = unitOfMeasure_lc;
}
if(LE_unitOfMeasure == "Ton"){
	LE_unitsPerHaul = estTonsHaul_lc;
}elif(LE_unitOfMeasure == "Yard"){
	LE_unitsPerHaul = containerSize_lc;
}else{
	LE_unitsPerHaul = "1";
}
if(accountType_current_readonly_lc == "Temporary" AND accountType_lc <> "No Change"){
	LE_accountType = accountType_lc;
}else{
	LE_accountType = accountType_current_readonly_lc;
}
if(LE_accountType == "Permanent" OR LE_accountType == "Seasonal"){
	permanentFlag_LE = 1;
}
if(rental_lc == "No Change"){
	LE_rental = rental_lc_readonly;
}else{
	LE_rental = rental_lc;
}
if(LE_rental == "Daily"){
	rental_factor_LE = 365.0/12.0;
}
if(rental_lc_readonly == "Daily"){
	old_rental_factor_LE = 365.0/12.0;
}
if(rental_lc <> "No Change"){
	if(rental_lc_readonly == "None" OR rental_lc == "None"){
		rental_change_flag_LE = 1;
	}
	if((rental_lc_readonly == "Daily" AND rental_lc == "Monthly") OR (rental_lc_readonly == "Monthly" AND rental_lc == "Daily")){
		ren_fac_change_flag_LE = 1;
	}
}		
if(wasteType_lc == "No Change"){
	LE_wasteType = wasteType_lc_readonly;
}else{
	LE_wasteType = wasteType_lc;
}
if(wasteType_lc == "No Change" AND unitOfMeasure_lc == "No Change"){
	dsp_change_flag_LE = 0;
}else{
	dsp_change_flag_LE = 1;
}
if(containerSize_lc == "No Change"){
	LE_containerSize = containerSize_lc_readonly;
}else{
	LE_containerSize = containerSize_lc;
}
if(haulsPerContainer_lc == "No Change"){
	LE_haulsPerContainer = haulsPerContainer_current_lc_readonly;
}else{
	LE_haulsPerContainer = haulsPerContainer_lc;
}
if(billingType_lc == "No Change"){
	LE_billingType = billingType_lc_readOnly;
	if(billingType_lc_readOnly == "Flat Rate"){
		LE_billingType = "Flat Rate + Overage";
	}
}else{
	LE_billingType = billingType_lc;
}
if(LE_rental == "Monthly" OR LE_rental == "Daily"){
	alloc_rental_LE = 1;
}
if(LE_haulsPerContainer == "On-Call"){
	servicelLevel_LE = LE_haulsPerContainer;
}else{
	servicelLevel_LE = "Scheduled";
}
if(haulsPerContainer_current_lc_readonly <> "On-Call" AND haulsPerContainer_lc == "On-Call"){
	if(isnumber(totalEstimatedHaulsMonth_lc)){
		LE_totalEstimatedHaulsMonth = atof(totalEstimatedHaulsMonth_lc);
	}else{
		LE_totalEstimatedHaulsMonth = 0.0;
	}
}else{
	if(isnumber(totalEstimatedHaulsMonth_lc_readonly)){
		LE_totalEstimatedHaulsMonth = atof(totalEstimatedHaulsMonth_lc_readonly);
	}else{
		LE_totalEstimatedHaulsMonth = 0.0;
	}
}
if(isnumber(overrideTimeStr_LE)){
	overrideTime_LE = atof(overrideTimeStr_LE);
}
if(isnull(LE_unitOfMeasure) OR LE_unitOfMeasure == ""){
	LE_unitOfMeasure = "Ton";
}
if(LE_unitOfMeasure == "Ton"){
	if(LE_billingType == "Flat Rate + Overage"){
		LE_unitsPerHaul = tonsIncludedInHaulRate_lc;
	}
	elif(LE_billingType == "Haul + Minimum Tonnage"){
		tempArray = float[];
		if(NOT isnull(estTonsHaul_lc)){
			if(isnumber(estTonsHaul_lc)){
				append(tempArray,atof(estTonsHaul_lc)); 
			}
		}
		if(NOT isnull(minimumTonsPerHaul_lc)){
			if(isnumber(minimumTonsPerHaul_lc)){
				append(tempArray,atof(minimumTonsPerHaul_lc)); 
			}
		}
		if(sizeofarray(tempArray) > 0){
			LE_unitsPerHaul = string(max(tempArray));
		}
	}
}
if(LE_billingType == "Flat Rate + Overage"){
	alloc_disposal_LE = 0;
}
if(isnumber(frequencyStr_LE)){
	frequency_LE = atof(frequencyStr_LE);
}
if(servicelLevel_LE <> "On-Call"){ //if service is scheduled, this must be calculate from frequency
	estHaulsPerMonthStr_LE = string(frequency_LE * (52.0 / 12.0) * atoi(quantity_lc));
	haulsPerContainerPerMonth_LE = string(frequency_LE * (52.0 / 12.0));
}
else{
	estHaulsPerMonthStr_LE = string(LE_totalEstimatedHaulsMonth * atof(quantity_lc));
	haulsPerContainerPerMonth_LE = string(LE_totalEstimatedHaulsMonth);
}
//ChangeType determination - Start
curr_haulsPerMonth = 0.0;
new_haulsPerMonth = 0.0;
if(haulsPerContainer_current_lc_readonly <> "On-Call"){
	frequencyConversionResultSet = bmql("SELECT Frequency, conversionFactor FROM Frequency_Conversion WHERE Frequency = $haulsPerContainer_current_lc_readonly");
	hauls_per_period = 0.0;
	for each in frequencyConversionResultSet{
		if(isnumber(get(each, "conversionFactor"))){
			hauls_per_period = getFloat(each, "conversionFactor");
		}
	}
	curr_haulsPerMonth = hauls_per_period * (52.0 / 12.0) * atoi(quantity_lc_readonly);
}else{
	curr_haulsPerMonth = atof(totalEstimatedHaulsMonth_lc_readonly) * atoi(quantity_lc_readonly);
}
if(haulsPerContainer_lc == "No Change"){
	if(haulsPerContainer_current_lc_readonly <> "On-Call"){
		frequencyConversionResultSet = bmql("SELECT Frequency, conversionFactor FROM Frequency_Conversion WHERE Frequency = $haulsPerContainer_current_lc_readonly");
		hauls_per_period = 0.0;
		for each in frequencyConversionResultSet{
			if(isnumber(get(each, "conversionFactor"))){
				hauls_per_period = getFloat(each, "conversionFactor");
			}
		}
		new_haulsPerMonth = hauls_per_period * (52.0 / 12.0) * atoi(quantity_lc);
	}else{
		new_haulsPerMonth = atof(totalEstimatedHaulsMonth_lc_readonly) * atoi(quantity_lc);
	}
}else{
	if(haulsPerContainer_lc == "On-Call"){
		new_haulsPerMonth = atof(totalEstimatedHaulsMonth_lc) * atoi(quantity_lc);
	}else{
		frequencyConversionResultSet = bmql("SELECT Frequency, conversionFactor FROM Frequency_Conversion WHERE Frequency = $haulsPerContainer_lc");
		hauls_per_period = 0.0;
		for each in frequencyConversionResultSet{
			if(isnumber(get(each, "conversionFactor"))){
				hauls_per_period = getFloat(each, "conversionFactor");
			}
		}
		new_haulsPerMonth = hauls_per_period * (52.0 / 12.0) * atoi(quantity_lc);
	}
}
if(curr_haulsPerMonth * atof(containerSize_lc_readonly) <= new_haulsPerMonth * atof(LE_containerSize)){
	changeType_LE = "Increase";
}else{
	changeType_LE = "Decrease";
}
//ChangeType determination - End
container_key_LE = routeTypeDervied_LE + LE_containerSize;
if(has_compactor_LE == 1){
	if(routeTypeDervied_LE == "IR"){
		container_key_LE = routeTypeDervied_LE + LE_containerSize + "C.IR";
	}else{
		container_key_LE = routeTypeDervied_LE + LE_containerSize + "C.I";
	}
}else{
	if(routeTypeDervied_LE == "IR"){
		container_key_LE = routeTypeDervied_LE + LE_containerSize + ".IR";
	}else{
		container_key_LE = routeTypeDervied_LE + LE_containerSize + ".I";
	}
}
if(dsp_change_flag_LE == 1){
	Latitude_tbl_LE = 0.0;
	Longitude_tbl_LE = 0.0;
	resultset = bmql("SELECT Latitude, Longitude FROM Disposal_Sites WHERE (DisposalSite_DivNbr = $division_quote) AND (WasteType = $LE_wasteType) AND unit_of_measure = $LE_unitOfMeasure AND Disposal_Site_Cd = $landfillCode_readOnly_lc");
	for result in resultset{
		Latitude_tbl_LE = getFloat(result, "Latitude");
		Longitude_tbl_LE = getFloat(result, "Longitude");	
	}
	longArray = split(longitude_LE,"$,$");
	latArray = split(latitude_LE,"$,$");
	longSelected = atof(longArray[atoi(dspSiteNumber_lc) - 1]);
	latSelected = atof(latArray[atoi(dspSiteNumber_lc) - 1]);
	longDiff = fabs(Longitude_tbl_LE - longSelected);
	latDiff = fabs(Latitude_tbl_LE - latSelected);
	if(longDiff > 0.02 OR latDiff > 0.02){
		dsp_change_loc_flag_LE = 1;
	}else{
		dsp_change_loc_flag_LE = 0;
	}	
}

thirdPartyDispTrsferPriceRecSet = bmql("SELECT default_disposal_3p FROM tbl_division_kpi_ind WHERE div_nbr = $division_quote");
for each in thirdPartyDispTrsferPriceRecSet{
	market_dsp_rate_LE = getFloat(each, "default_disposal_3p");
	break;
}
market_dsp_rate_LE = market_dsp_rate_LE * (1 + frfRate + erfRate + (erfRate * frfRate * eRFOnFRF));

marketRentalRS = bmql("SELECT market_rental_rate FROM tbl_division_rental WHERE (division = $division_quote OR division = '0') AND container_cd = $routeTypeDervied_LE AND perm_flag = $permanentFlag_LE ORDER BY division DESC");
for each in marketRentalRS{
	market_rental_rate_LE = getFloat(each, "market_rental_rate");
	break;
}
market_rental_rate_LE = market_rental_rate_LE * (1 + frfRate + erfRate + (erfRate * frfRate * eRFOnFRF));

siteNumberInt = 0;
siteNumberShort = "0";
if(isnumber(siteNumber_quote)){
	siteNumberInt = atoi(siteNumber_quote);
	siteNumberShort = string(siteNumberInt);
}
key = 0;
monthsRemaining = 0;
accountStatusKeyRS = bmql("SELECT Acct_Status_Ind_PK, frf_rate_pct, erf_rate_pct, is_frf_charged, is_erf_charged, is_frf_locked, is_erf_locked, is_erf_on_frf, contract_term, is_adm_charged, adm_rate, contract_exp_dt, competitor_cd FROM Account_Status_Ind WHERE acct_key = $quote_process_customer_id AND site_nbr = $siteNumberShort");
for each in accountStatusKeyRS{
	expirationDate_LE = get(each, "contract_exp_dt");
	contractMonths_LE = get(each, "contract_term");	
	is_frf_locked_LE = get(each, "is_frf_locked");
	is_erf_locked_LE = get(each, "is_erf_locked");
	is_frf_on_db_LE = string(getint(each, "is_frf_charged"));
	is_erf_on_db_LE = string(getint(each, "is_erf_charged"));
	is_erf_on_frf_db_LE = get(each, "is_erf_on_frf");
	is_admn_on_db_LE = get(each, "is_adm_charged");
	adminRate_LE = getFloat(each, "adm_rate");
	key = getint(each, "Acct_Status_Ind_PK");
	competitor_cd_LE = get(each, "competitor_cd");
	if(competitor_cd_LE == ""){
		competitor_cd_LE = "OTH";
	}
	if(is_frf_locked_LE == "1" OR is_erf_locked_LE == "1"){
		customerHasAFixedFee = true;
	}		
	if(is_frf_on_db_LE == "1"){
		is_frf_charged_LE = "Yes";
	}
	if(is_erf_on_db_LE == "1"){
		is_erf_charged_LE = "Yes";
	}
	if((is_erf_on_frf_db_LE == "1") AND (isERFOnFRFChargedAtDivisionLevel == true)){
		is_erf_on_frf_charged_LE = "Yes";
	}
	if(find(feesToCharge_quote, "Fixed Fuel Recovery Fee (FRF)") > -1 ){
		frfPct_LE = getFloat(each, "frf_rate_pct");
		frfRate = frfPct_LE/100;
	}
	if(find(feesToCharge_quote, "Fixed Environment Recover Fee (ERF)" ) > -1){
		erfPct_LE = getFloat(each, "erf_rate_pct");
		erfRate = erfPct_LE/100;
	}
	if((is_frf_charged_LE <> includeFRF_quote) OR (is_frf_locked_LE == "1")){
		fee_flag_change_LE = 1;
	}
		
	if(expirationDate_LE <> "" AND expirationDate_LE <> "99991231"){
		if(len(expirationDate_LE) == 8){
			expirationDateYearStr = substring(expirationDate_LE, 0, 4);
			expirationDateMonthStr = substring(expirationDate_LE, 4, 6);
			expirationDateDayStr = substring(expirationDate_LE, 6, 8);
			expirationDateTemp = expirationDateYearStr + "-" + expirationDateMonthStr + "-" + expirationDateDayStr;
			currentExpirationDate = strtojavadate(expirationDateTemp, "yyyy-MM-dd");
			today = getdate();
			if(currentExpirationDate < today){
				monthsRemaining = 0;
			}
			else{
				monthFactor = 365.0/12.0;
				diffDays = getdiffindays(currentExpirationDate, today);
				monthsRemaining = integer(diffDays/monthFactor);
			}
		}
	}
}
accountStatusRecsLargeM1 = bmql("SELECT min_units_per_haul,nb_haul_cost,nb_dsp_cost,nb_ovr_cost,nb_ren_cost,last_pi_haul_amt,last_pi_dsp_amt,last_pi_ovr_amt,last_pi_ren_amt FROM Acct_Status_Ind_M1 WHERE Acct_Status_Ind_SK = $key");
for rec in accountStatusRecsLargeM1{
	min_units_per_haul_LE = get(rec,"min_units_per_haul");
	nb_haul_cost_LE = getFloat(rec,"nb_haul_cost");
	nb_dsp_cost_LE = getFloat(rec,"nb_dsp_cost");
	nb_ovr_cost_LE = getFloat(rec,"nb_ovr_cost");
	nb_ren_cost_LE = getFloat(rec,"nb_ren_cost");	
	last_pi_haul_amt_LE = getFloat(rec,"last_pi_haul_amt");
	last_pi_dsp_amt_LE = getFloat(rec,"last_pi_dsp_amt");
	last_pi_ovr_amt_LE = getFloat(rec,"last_pi_ovr_amt");
	last_pi_ren_amt_LE = getFloat(rec,"last_pi_ren_amt");
}
curr_margin_percentile = 0.0;
rate_pct_base = 0.0;
accountStatusRecsLarge = bmql("SELECT ttl_mnth_all_in_cost,fin_mnth_dsp_expense,fin_mnth_opex,haul_cost_components,curr_haul_margin_dol,curr_dsp_margin_dol,curr_ovr_margin_dol,curr_ren_margin_dol,container_roi,container_depr,margin_percentile,curr_haul_margin_pct,curr_rate_pct_base,haul_rate,dsp_rate,ovr_rate,ren_rate FROM Acct_Status_Ind_M2 WHERE Acct_Status_Ind_SK = $key");
for rec in accountStatusRecsLarge{
	existing_operating_expense = getFloat(rec,"fin_mnth_opex");
	existing_cts_month_incl_oh = getFloat(rec,"ttl_mnth_all_in_cost");
	existing_cost_disp_xfer_proc = getFloat(rec,"fin_mnth_dsp_expense");
	haul_cost_components_LE = getFloat(rec,"haul_cost_components");
	container_roi_LE = getFloat(rec,"container_roi");
	container_depr_LE = getFloat(rec,"container_depr");
	curr_margin_percentile = getFloat(rec,"margin_percentile");
	curr_haul_margin_pct = getFloat(rec,"curr_haul_margin_pct");
	rate_pct_base = getFloat(rec,"curr_rate_pct_base");
	haul_rate_LE = getFloat(rec,"haul_rate");
	dsp_rate_LE = getFloat(rec,"dsp_rate");
	ovr_rate_LE = getFloat(rec,"ovr_rate");
	ren_rate_LE = getFloat(rec,"ren_rate");	
	curr_haul_margin_dol_LE = getFloat(rec,"curr_haul_margin_dol");
	curr_dsp_margin_dol_LE = getFloat(rec,"curr_dsp_margin_dol");
	curr_ovr_margin_dol_LE = getFloat(rec,"curr_ovr_margin_dol");
	curr_ren_margin_dol_LE = getFloat(rec,"curr_ren_margin_dol");
}

competitorRecSet = bmql("SELECT competitor, infopro_reg FROM div_competitor_adj WHERE division = $division_quote AND Competitor_Cd = $competitor_cd_LE");

for each in competitorRecSet{
    competitor_LE = get(each, "Competitor_Cd");
}

if(competitiveBidQuote == true){
	competitiveBidRecordSet = bmql("SELECT comp_bid_markup, comp_targ_pct_retain, comp_str_pct_retain FROM CompetitiveBid_Mrkup WHERE (Division = $division_quote OR Division = '0') AND MarginPctileMin <= $curr_margin_percentile AND MarginPctileMax > $curr_margin_percentile AND (monthsRemainMin <= $monthsRemaining AND  monthsRemainMax > $monthsRemaining) AND WasteType = $wasteCategory_LE AND ContainerType = $containerType_sc AND cat_yds_per_month = $cat_yards_per_month_LE ORDER BY Division DESC");
	for eachRecord in competitiveBidRecordSet{
		comp_bid_markup_LE = getFloat(eachRecord, "comp_bid_markup");
		comp_targ_pct_retain_LE = getFloat(eachRecord, "comp_targ_pct_retain"); 
		comp_str_pct_retain_LE = getFloat(eachRecord, "comp_str_pct_retain");
		break;
	}
}
if(generalSaveQuote == true){
	generalSaveRecordSet = bmql("SELECT save_base_margin_adj, save_targ_pct_retain, save_str_pct_retain FROM General_Save_Rates WHERE (Division = $division_quote OR Division = '0') AND (MarginPctileMin <= $curr_margin_percentile AND MarginPctileMax > $curr_margin_percentile) AND (Months_Remain_Min <= $monthsRemaining AND  Months_Remain_Max > $monthsRemaining) AND WasteType = $wasteCategory_LE AND ContainerType = $containerType_sc AND cat_yds_per_month = $cat_yards_per_month_LE ORDER BY Division DESC");
	for eachRecord in generalSaveRecordSet{
		save_base_margin_adj_LE = getFloat(eachRecord, "save_base_margin_adj");
		save_targ_pct_retain_LE = getFloat(eachRecord, "save_targ_pct_retain");
		save_str_pct_retain_LE = getFloat(eachRecord, "save_str_pct_retain");
		break;
	}
}
if(rollbackOfPIQuote == true){
	piToRetainRecordset = bmql("SELECT pi_retain_base, pi_retain_target, pi_retain_stretch FROM Percent_PI_To_Retain WHERE (Division = $division_quote OR Division = '0') AND ContainerType = $containerType_sc AND WasteType = $wasteCategory_LE AND  MarginPctileMin <= $curr_margin_percentile AND MarginPctileMax > $curr_margin_percentile AND cat_yds_per_month = $cat_yards_per_month_LE ORDER BY Division DESC");
	for eachRecord in piToRetainRecordset{
		pi_retain_base_LE = getFloat(eachRecord, "pi_retain_base");
		pi_retain_target_LE = getFloat(eachRecord, "pi_retain_target");
		pi_retain_stretch_LE = getFloat(eachRecord, "pi_retain_stretch");
		break;
	}
}
if(serviceChangeQuote == true){
	serviceChangeFactorsRecordSet = bmql("SELECT svc_base_marg_prem, svc_targ_marg_prem, svc_str_marg_prem, svc_gap_recovery_pct FROM Service_Chng_Factors WHERE (Division = $division_quote OR Division = '0') AND Container_Type = $containerType_sc AND WasteType = $wasteCategory_LE AND Rate_Pct_Base_Min <= $rate_pct_base AND Rate_Pct_Base_Max > $rate_pct_base AND change_type = $changeType_LE AND cat_yds_per_month = $cat_yards_per_month_LE ORDER BY Division DESC");
	for eachRecord in serviceChangeFactorsRecordSet{
		svc_base_marg_prem_LE = getFloat(eachRecord, "svc_base_marg_prem");
		svc_targ_marg_prem_LE = getFloat(eachRecord, "svc_targ_marg_prem");
		svc_str_marg_prem_LE = getFloat(eachRecord, "svc_str_marg_prem");
		svc_gap_recovery_pct_LE = getFloat(eachRecord, "svc_gap_recovery_pct");
		break;
	}
}
marketRentalRS = bmql("SELECT market_rental_rate FROM tbl_division_rental WHERE (division = $division_quote OR division = '0') AND container_cd = $routeTypeDervied_LE AND perm_flag = $permanentFlag_LE ORDER BY division DESC");
for each in marketRentalRS{
	market_rental_rate_LE = getFloat(each, "market_rental_rate");
	break;
}
spreadRecs = bmql("SELECT Flat_Rate_Incl_Tons, Dsp_Floor_Avg, Dsp_Avg_Target FROM Div_Lg_Cont_Factors WHERE Division = $division_quote OR Division = '0' ORDER BY Division DESC");
for rec in spreadRecs{
	Flat_Rate_Incl_Tons_LE = getfloat(rec, "Flat_Rate_Incl_Tons");
	floorAvgSpread_LE = 1.0 + getfloat(rec, "Dsp_Floor_Avg");
	avgTargetSpread_LE = 1.0 + getfloat(rec, "Dsp_Avg_Target") + getfloat(rec, "Dsp_Floor_Avg");
	break;
}

put(returnDict, "nb_haul_cost_LE", string(nb_haul_cost_LE));
put(returnDict, "nb_dsp_cost_LE", string(nb_dsp_cost_LE));
put(returnDict, "nb_ovr_cost_LE", string(nb_ovr_cost_LE));
put(returnDict, "nb_ren_cost_LE", string(nb_ren_cost_LE));
put(returnDict, "haul_rate_LE", string(haul_rate_LE));


put(returnDict, "dsp_rate_LE", string(dsp_rate_LE));
put(returnDict, "ovr_rate_LE", string(ovr_rate_LE));
put(returnDict, "ren_rate_LE", string(ren_rate_LE));
put(returnDict, "salesActivity_LE", salesActivity_LE);
put(returnDict, "market_dsp_rate_LE", string(market_dsp_rate_LE));

put(returnDict, "floorAvgSpread_LE", string(floorAvgSpread_LE));
put(returnDict, "avgTargetSpread_LE", string(avgTargetSpread_LE));
put(returnDict, "dsp_change_flag_LE", string(dsp_change_flag_LE));
put(returnDict, "dsp_change_loc_flag_LE", string(dsp_change_loc_flag_LE));
put(returnDict, "unitOfMeasure_lc", unitOfMeasure_lc);

put(returnDict, "quantity_lc_readonly", quantity_lc_readonly);
put(returnDict, "quantity_lc", quantity_lc);
put(returnDict, "container_roi_LE", string(container_roi_LE));
put(returnDict, "container_depr_LE", string(container_depr_LE));
put(returnDict, "alloc_rental_LE", string(alloc_rental_LE));

put(returnDict, "LE_totalEstimatedHaulsMonth", string(LE_totalEstimatedHaulsMonth));
put(returnDict, "market_rental_rate_LE", string(market_rental_rate_LE));
put(returnDict, "has_compactor_LE", string(has_compactor_LE));
put(returnDict, "rental_factor_LE", string(rental_factor_LE));
put(returnDict, "old_rental_factor_LE", string(old_rental_factor_LE));

put(returnDict, "rental_change_flag_LE", string(rental_change_flag_LE));
put(returnDict, "ren_fac_change_flag_LE", string(ren_fac_change_flag_LE));
put(returnDict, "svc_base_marg_prem_LE", string(svc_base_marg_prem_LE));
put(returnDict, "svc_targ_marg_prem_LE", string(svc_targ_marg_prem_LE));
put(returnDict, "svc_str_marg_prem_LE", string(svc_str_marg_prem_LE));

put(returnDict, "LE_unitsPerHaul", LE_unitsPerHaul);
put(returnDict, "billingType_lc_readOnly", billingType_lc_readOnly);
put(returnDict, "billingType_lc", billingType_lc);
put(returnDict, "LE_billingType", LE_billingType);
put(returnDict, "min_units_per_haul_LE", min_units_per_haul_LE);

put(returnDict, "minimumTonsPerHaul_lc", minimumTonsPerHaul_lc);
put(returnDict, "Flat_Rate_Incl_Tons_LE", string(Flat_Rate_Incl_Tons_LE));
put(returnDict, "tonsIncludedInHaulRate_lc_readOnly", tonsIncludedInHaulRate_lc_readOnly);
put(returnDict, "tonsIncludedInHaulRate_lc", tonsIncludedInHaulRate_lc);
put(returnDict, "LE_wasteType", LE_wasteType);

put(returnDict, "haul_cost_components_LE", string(haul_cost_components_LE));
put(returnDict, "competitor_cd_LE", competitor_cd_LE);
put(returnDict, "competitor_LE", competitor_LE);
put(returnDict, "wasteCategory_LE", wasteCategory_LE);
put(returnDict, "LE_unitOfMeasure", LE_unitOfMeasure);

put(returnDict, "LE_containerSize", LE_containerSize);
put(returnDict, "lOBCategoryDerived_LE", lOBCategoryDerived_LE);
put(returnDict, "routeTypeDervied_LE", routeTypeDervied_LE);
put(returnDict, "frequencyStr_LE", frequencyStr_LE);
put(returnDict, "estHaulsPerMonthStr_LE", estHaulsPerMonthStr_LE);

put(returnDict, "haulsPerContainerPerMonth_LE", haulsPerContainerPerMonth_LE);
put(returnDict, "adjustedTotalTime_lc", adjustedTotalTime_lc);
put(returnDict, "LE_accountType", LE_accountType);
put(returnDict, "customerOwnedContainer_LE", customerOwnedContainer_LE);
put(returnDict, "customerOwnedCompactor_LE", customerOwnedCompactor_LE);

put(returnDict, "compactorValue_lc", compactorValue_lc);
put(returnDict, "containerType_lc", containerType_lc);
put(returnDict, "LE_rental", LE_rental);
put(returnDict, "LE_haulsPerContainer", LE_haulsPerContainer);
put(returnDict, "svc_gap_recovery_pct_LE", string(svc_gap_recovery_pct_LE));

put(returnDict, "alloc_disposal_LE", string(alloc_disposal_LE));
put(returnDict, "curr_haul_margin_pct", string(curr_haul_margin_pct));
put(returnDict, "comp_targ_pct_retain_LE", string(comp_targ_pct_retain_LE));
put(returnDict, "comp_str_pct_retain_LE", string(comp_str_pct_retain_LE));
put(returnDict, "comp_bid_markup_LE", string(comp_bid_markup_LE));

put(returnDict, "competitiveBidAmountHaul_lc", competitiveBidAmountHaul_lc);
put(returnDict, "competitiveBidAmountDisposal_lc", competitiveBidAmountDisposal_lc);
put(returnDict, "competitiveBidAmountRental_lc", competitiveBidAmountRental_lc);
put(returnDict, "competitiveBidQuoteStr", competitiveBidQuoteStr);
put(returnDict, "rollbackOfPIQuoteStr", rollbackOfPIQuoteStr);

put(returnDict, "generalSaveQuoteStr", generalSaveQuoteStr);
put(returnDict, "serviceChangeQuoteStr", serviceChangeQuoteStr);
put(returnDict, "fee_flag_change_LE", string(fee_flag_change_LE));
put(returnDict, "is_erf_on_frf_charged_LE", is_erf_on_frf_charged_LE);
put(returnDict, "frfRate", string(frfRate));

put(returnDict, "erfRate", string(erfRate));
put(returnDict, "pi_retain_base_LE", string(pi_retain_base_LE));
put(returnDict, "pi_retain_target_LE", string(pi_retain_target_LE));
put(returnDict, "pi_retain_stretch_LE", string(pi_retain_stretch_LE));
put(returnDict, "last_pi_haul_amt_LE", string(last_pi_haul_amt_LE));

put(returnDict, "last_pi_dsp_amt_LE", string(last_pi_dsp_amt_LE));
put(returnDict, "last_pi_ovr_amt_LE", string(last_pi_ovr_amt_LE));
put(returnDict, "last_pi_ren_amt_LE", string(last_pi_ren_amt_LE));
put(returnDict, "save_base_margin_adj_LE", string(save_base_margin_adj_LE));
put(returnDict, "save_targ_pct_retain_LE", string(save_targ_pct_retain_LE));

put(returnDict, "save_str_pct_retain_LE", string(save_str_pct_retain_LE));
put(returnDict, "curr_haul_margin_dol_LE", string(curr_haul_margin_dol_LE));
put(returnDict, "curr_dsp_margin_dol_LE", string(curr_dsp_margin_dol_LE));
put(returnDict, "curr_ovr_margin_dol_LE", string(curr_ovr_margin_dol_LE));
put(returnDict, "curr_ren_margin_dol_LE", string(curr_ren_margin_dol_LE));

put(returnDict, "changeType_LE", changeType_LE);
put(returnDict, "salesActivity", salesActivity);
put(returnDict, "priceAdjustmentReason", priceAdjustmentReason);
put(returnDict, "is_frf_charged_LE", is_frf_charged_LE);
put(returnDict, "is_erf_charged_LE", is_erf_charged_LE);

put(returnDict, "adminRate_LE", string(adminRate_LE));
put(returnDict, "is_admn_on_db_LE", is_admn_on_db_LE);
put(returnDict, "existing_operating_expense", string(existing_operating_expense));
put(returnDict, "existing_cts_month_incl_oh", string(existing_cts_month_incl_oh));
put(returnDict, "existing_cost_disp_xfer_proc", string(existing_cost_disp_xfer_proc));

return returnDict;