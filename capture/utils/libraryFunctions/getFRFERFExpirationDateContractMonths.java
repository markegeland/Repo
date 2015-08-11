/*Large Existing - Refactoring of Prepricing*/
returnDict = dict("string");

//=============================== START - Get FRF & ERF Rates ===============================//
division_quote = get(stringDict, "division_quote");
infoproDivision_RO_quote = get(stringDict, "infoproDivision_RO_quote");
customerHasMultipleSites = get(stringDict, "customerHasMultipleSites_quote");
customerHasMultipleSites_quote = false;
if(customerHasMultipleSites == "true"){
	customerHasMultipleSites_quote = true;
}
frfRateStr = "";
erfRateStr = "";
adminRateStr = "";
frfRate = 0.0;
erfRate = 0.0;
adminRate = 0.0;
eRFOnFRF = 0.0;
erfOnFrfDivision = 1;
isERFOnFRFChargedAtDivisionLevel = false;
isERFAndFRFChargedOnAdmin = "Yes";

//Moved division fee rates from formulas to get record based on division & info pro or division combinations
//Step1: Search for division & info pro 
//Step2: If no record at step 1 Search for division specific
divisionFeeRateRecordSet = bmql("SELECT fRFRate, eRFRate, adminAmount, erf_on_frf, infopro_div_nbr FROM divisionFeeRate WHERE divisionNumber = $division_quote AND infopro_div_nbr = $infoproDivision_RO_quote");

for eachRecord in divisionFeeRateRecordSet{
    frfRateStr = get(eachRecord, "fRFRate");
    erfRateStr = get(eachRecord, "eRFRate");
    adminRateStr = get(eachRecord, "adminAmount");
    erfOnFrfDivision = getint(eachRecord, "erf_on_frf");
    break;
}
if(isnumber(frfRateStr)){   //Convert the table result to a float for use in calculations
    frfRate = atof(frfRateStr) / 100.0;
}

if(isnumber(erfRateStr)){   //Convert the table result to a float for use in calculations
    erfRate = atof(erfRateStr) / 100.0;
}

if(erfOnFrfDivision == 1){
    isERFOnFRFChargedAtDivisionLevel = true;
    eRFOnFRF = 1.0;
}

if(customerHasMultipleSites_quote){
    isERFAndFRFChargedOnAdmin = "No";
}
put(returnDict, "frfRateStr", frfRateStr);
put(returnDict, "frfRate", string(frfRate));
put(returnDict, "erfRateStr", erfRateStr);
put(returnDict, "erfRate", string(erfRate));
put(returnDict, "adminRateStr", adminRateStr);
put(returnDict, "erfOnFrfDivision", string(erfOnFrfDivision));
put(returnDict, "isERFOnFRFChargedAtDivisionLevel", string(isERFOnFRFChargedAtDivisionLevel));
put(returnDict, "eRFOnFRF", string(eRFOnFRF));
put(returnDict, "isERFAndFRFChargedOnAdmin", isERFAndFRFChargedOnAdmin);
//=============================== END - Get FRF & ERF Rates ===============================// 

//=============================== Start - Calculation for Existing Terms=====================//

salesActivity_quote = get(stringDict, "salesActivity_quote");
quote_process_customer_id = get(stringDict, "quote_process_customer_id");
siteNumber_quote = get(stringDict, "siteNumber_quote");
expirationDate = "";
contractMonths = "0.0";

if(salesActivity_quote == "Existing Customer"){
	isFound = false;
    accountDifferentInDays = bmql("SELECT Expiration_Dt FROM Account_Status WHERE infopro_acct_nbr = $quote_process_customer_id AND Site_Nbr = $siteNumber_quote"); 
	accountDifferentInDays2 = bmql("SELECT contract_term FROM Account_Status WHERE infopro_acct_nbr = $quote_process_customer_id AND Site_Nbr = $siteNumber_quote");
	//Add any other filters as appropriate to get more specific record
        for eachRecord in accountDifferentInDays{
            expirationDate = get(eachRecord, "Expiration_Dt");
			isFound = true;
            break;
        }  
		for eachRecord in accountDifferentInDays2{
            contractMonths = get(eachRecord, "contract_term");
			isFound = true;
            break;
        }
	if(not(isFound)){
		//Because account_status_ind uses shorted site numbers, we have to format it for that table call
		siteNumberInt = 0;
		siteNumberShort = "0";
		if(isnumber(siteNumber_quote)){
			siteNumberInt = atoi(siteNumber_quote);
			siteNumberShort = string(siteNumberInt);
		}
		accountStatusKeyRS = bmql("SELECT contract_exp_dt, contract_term FROM Account_Status_Ind WHERE acct_key = $quote_process_customer_id AND site_nbr = $siteNumberShort");
		for each in accountStatusKeyRS{
            expirationDate = get(each, "contract_exp_dt");
			contractMonths = get(each, "contract_term");
            break;
        }  
	}
	put(returnDict, "expirationDate", expirationDate);
	put(returnDict, "contractMonths", contractMonths);
}

return returnDict;