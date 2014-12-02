/* 
Test Transaction: 5665313
================================================================================
Name:   Save Action - Pre Formula Modify
Author:   
Create date:  
Description:  Runs code prior to Commerce formulas. Performs complex functionality not supported by formulas. Currently used in the Save And Price action
        
Input:      division_quote: String - Division of quote
            region_quote:   String - Region of quote
            _part_number:   String - Part number for this line
            _config_attr_info:  String - attributes from Config for use by getconfigattrvalue
                    
Output:     String (documentNumber + "~" + attributeVariableName + "~" + value + "|")

Updates:    20130913 - ??? - Added functionality to run large container pricing
            20131111 - Zach Schlieder - Added functionality to run small container pricing 
            20131128 - Populating values to Customer Information Address Set, Setting Home Area Number Value
            20131202 - amajji - Moving homeArea code to homeAreaNumber_quote default value as this needs to be set as soon as user creates the quote            
            20131213 - Srikar - Added new line item loop to store line item data into dictionaries
            20140923 - John Palubinskas - Added AdHoc PerHaul, Monthly, OneTime flags to break out on proposal
            20141008 - John Palubinskas - Fixed DRY rates to pull from division table, then 1/2 haul rate
            20141021 - Julie Felberg - Added containerGroupDict dictionary and containerGroupLine.  The containerGroupDict houses the container groups 
                    pulled from config for each line and containerGroupLine is populated from the dictionary during line level loop.  Pass containerGroupLine instead of containerGroup
                    to GuardrailCurrentInputDict 
            20141022 - James Shrenk - Emergency fix to override large container route type
            20141023 - John Palubinskas - Make divisionFeeRate lookup use the key (divisionNumber, infopro_div_nbr) in order to pull the correct rates
                         for a division that has multiple InfoPro divs per Lawson div.
            20141027 - Julie Felberg - Passed ContainerGroupLine instead of containerGroup to GuardrailInputDict
            20141107 - Aaron Quintanilla - Removed price for Hand Pickup delivery
            20141202 - Julie Felberg - Replaced estHaulsPerMonth_l with totalEstimatedHaulsMonth_l
        
=====================================================================================================
*/


//=============================== START - Variable Initialization ===============================//
//Default variables
returnStr = "";
DEBUG = false;
if(lower(_system_supplier_company_name) == "devrepublicservices"){
    DEBUG = true;
}
COMM_VALUE_DELIM = "^_^";
FIELD_DELIM = "@_@";
homeDivNbr = "";
homeAreaNbr = "";
model = "";
industrialExists = false;
commercialExists = false;
disposalSiteCostCommercial = 0.0;
dsp_xfer_priceperton_small = 0.0;
priceTypeDict = dict("string"); //Key: document number, value: Model Name
modelNameDict = dict("string"); //Key: model document number; value: Model Name
allocationDaysDict = dict("string[]"); //Key: waste type, value: allocation days
totalTripsDict = dict("integer"); //Key: waster type, value: total trips
wasteTypeArray = string[];
allocationFactorDict = dict("float");
installChargeDict = dict("float"); //Key: Parent Doc Num, Value is config attr installation charge
containerTypesArray = string[]; //Stores container types ex: Large, Small
decimalPlaces = 2;
allocationFactorStringHTML = "";
LARGE_CONTAINER = "Large Containers";
SMALL_CONTAINER = "Containers";
SERVICE_CHANGE = "Service Change";
accountStatusRecordSet = recordset();
accountSalesHistRecordSet = recordset();
existingCustDataDict = dict("string"); //Key: Document Number:<property> - example:- 2:wasteType
serviceChangeDataDict = dict("string"); //Key: Document Number:<property> - example:- 2:wasteType
exchangeExistsDict = dict("boolean"); //Key: Parent Document number, Value is true/false based on exchange Line item exists or not
dateFormat="yyyy-MM-dd";
customerHasAFixedFee = customerHasAFixedFee_quote;
priceIncreaseQuote = false;
serviceChangeQuote = false;
closeContainerGroupQuote = false;
competitiveBidQuote = false;
rollbackOfPIQuote = false;
generalSaveQuote = false;
smallContainerPickupDays = "";
smallContainerPickupDaysArray = string[];
errors = "";
existingAdminAmount = 0.0;
frfRate = 0.0;
erfRate = 0.0;
adminRate = 0.0;
eRFOnFRF = 0.0;
frfRateStr = "";
erfRateStr = "";
adminRateStr = "";
newCustomerConfig = 1; //0 - Existing customer; 1 - New Customer            
isERFAndFRFChargedOnAdmin = "Yes"; //Default - All new customers will be charged
modelTypeDict = dict("string"); //Key: document number, value: service type of current model
//initialize variables for existing term
expirationDate = "";
todayDate = getdate();
diffDay = 0.0;
diffMth = 0;
diffMthRnd = 0;
dateString1 = "";
dateString2 = getdate();
lessThan90days = false;
containerGroupDict = dict("string"); //container group logic
containerGroup = "";
containerGroupLine = "";



/*These dictionaries are declared only for debugging purposes*/
pricingDebugInputDict = dict("string");
pricingDebugOutputDict = dict("string");
guardrailDebugInputDict = dict("string");
guardrailDebugOutputDict = dict("string");
/*End of debugging dictionaries*/
            
//=============================== END - Variable Initialization ===============================//

//=============================== START - Get FRF & ERF Rates ===============================//

//Moved division fee rates from formulas to get record based on division & info pro or division combinations
//Step1: Search for division & info pro 
//Step2: If no record at step 1 Search for division specific
divisionFeeRateRecordSet = bmql("SELECT fRFRate, eRFRate, adminAmount, erf_on_frf, infopro_div_nbr FROM divisionFeeRate WHERE divisionNumber = $division_quote AND infopro_div_nbr = $infoproDivision_RO_quote");

for eachRecord in divisionFeeRateRecordSet{
    frfRateStr = get(eachRecord, "fRFRate");
    erfRateStr = get(eachRecord, "eRFRate");
    adminRateStr = get(eachRecord, "adminAmount");
    break;
}
if(isnumber(frfRateStr)){   //Convert the table result to a float for use in calculations
    frfRate = atof(frfRateStr) / 100.0;
}

if(isnumber(erfRateStr)){   //Convert the table result to a float for use in calculations
    erfRate = atof(erfRateStr) / 100.0;
}
print "frfRate: " + string(frfRate);
print "erfRate: " + string(erfRate);

//=============================== END - Get FRF & ERF Rates ===============================//  

//=============================== Start - Calculation for Existing Terms=====================//
if(salesActivity_quote == "Existing Customer"){
    accountDifferentInDays = bmql("SELECT Expiration_Dt FROM Account_Status WHERE infopro_acct_nbr = $_quote_process_customer_id AND Site_Nbr = $siteNumber_quote"); //Add any other filters as appropriate to get more specific record
            for eachRecord in accountDifferentInDays{
            expirationDate = get(eachRecord, "Expiration_Dt");
            break;
            }
    
    print "......................";
    print expirationDate;
    dateString1 = substring(expirationDate, 4, 6) + "/" +
           substring(expirationDate, 6, 9) + "/" +
           substring(expirationDate, 0, 4);
    if (expirationDate <> ""){
        dateString2 = strtojavadate(dateString1, "MM/dd/yyyy");
    }
    
    print dateString1;
    print dateString2;
    print todayDate;
    diffDay = getdiffindays(todayDate, dateString2);
    diffMth = (diffDay * 12) / 365;
    print diffDay;
    print diffMth;
    if((diffMth > 6) AND (diffMth <= 9)){ diffMthRnd = 6; }
    if((diffMth >= 10) AND (diffMth <= 21)){ diffMthRnd = 12; }
    if((diffMth >= 22) AND (diffMth <= 33)){ diffMthRnd = 24; }
    if((diffMth > 33)){ diffMthRnd = 36; }
    
    //Set existingTermFlag_quote to true is diffDays is less than 90
    if ((diffDay < 90) OR (todayDate > dateString2) ){
        lessThan90Days = true;
        print "Less Than 90 Days";
    }
}

//=============================== End - Calculation for Existing Terms=====================//

//=============================== START - Customer Type Factor Calculation ===============================//
competitorFactorStr = "";
competitorCode = "";
competitorFactor = 1.0;
competitor = "";
//If no competitor was selected on the quote, use the default value New/New to get the default table row
if(salesActivity_quote <> "New/New"){
    competitor = competitor_quote;
    //If current customer has more than 1 site isERFAndFRFChargedOnAdmin should be OFF
    if(customerHasMultipleSites_quote){
        isERFAndFRFChargedOnAdmin = "No";
    }
}
if(competitor == ""){
    competitor = "New/New";
}

//Changed 6/30/14 by Andrew, added in logic for division = 0
customerTypeFactorRecordSet = bmql("SELECT competitor, competitor_factor, Competitor_Cd, division FROM div_competitor_adj WHERE division = $division_quote OR division = '0' ORDER BY division DESC");
for eachRecord in customerTypeFactorRecordSet{
            competitor_db = get(eachRecord, "competitor");
                    if(lower(competitor_db) == lower(competitor)){
                        competitorFactorStr = get(eachRecord, "competitor_factor");
                        //Added by Aaron Q on 09/01/2014 for SR 3-9423125271
                        if (competitorCode_quote == ""){
                            competitorCode = get(eachRecord, "Competitor_Cd");
                        }
                        else{
                            competitorCode = competitorCode_quote;
                        }
                        break;
                        }

}
print "-----";
print competitorFactorStr;
print "-----";


if(isnumber(competitorFactorStr)){
    competitorFactor = 1.0 + atof(competitorFactorStr);
}

siteContainerstextStr = siteContainersData_quote;
if(salesActivity_quote == "Existing Customer" AND siteContainersData_quote == ""){
    siteContainershtmlStr = "";
    containersInputDict = dict("string");
    put(containersInputDict, "_quote_process_customer_id", _quote_process_customer_id);
    put(containersInputDict, "SiteNumber_quote", SiteNumber_quote);
    put(containersInputDict, "containerGroupForTransaction_quote", containerGroupForTransaction_quote);
    containersOutputDict = util.getContainerGroups(containersInputDict);
    
    if(containskey(containersOutputDict, "html")){
        siteContainershtmlStr = get(containersOutputDict, "html");
    }
    if(containskey(containersOutputDict, "text")){
        siteContainerstextStr = get(containersOutputDict, "text");
    }
    returnStr = returnStr + "1" + "~" + "siteContainerGroupsHTMLText_quote" + "~" + siteContainershtmlStr + "|"
                          + "1" + "~" + "siteContainersData_quote" + "~" + siteContainerstextStr + "|";
}
if(siteContainerstextStr <> ""){
    /*siteContainersData_quote -sample string as below:
    containerGroup:1$_$containerCnt:1.0$_$size:4.0$_$pickupPeriodLength:1$_$pickupPerTotLifts:1.0$_$period:1.0$_$hasCompactor:0$_$Lift_Days:0010000$_$waste_type:Solid Waste$_$frequency:1/Week@_@containerGroup:2$_$containerCnt:1.0$_$size:4.0$_$pickupPeriodLength:1$_$pickupPerTotLifts:1.0$_$period:1.0$_$hasCompactor:0$_$Lift_Days:0010000$_$waste_type:Recycling$_$frequency:1/Week
    */
    ROWDELIM = "@_@";
    ITEMDELIM = "$_$";
    VALDELIM = ":";
    containersArray = split(siteContainerstextStr, ROWDELIM);
    if(sizeofarray(containersArray) > 0){
        for eachRecord in containersArray{
            if(eachRecord <> ""){
                thisContainerDict = util.convertStringToDictionary(eachRecord,VALDELIM, ITEMDELIM);
                if(containskey(thisContainerDict,"Lift_Days")){
                    liftDays = get(thisContainerDict, "Lift_Days");
                    liftDayNames = util.getLiftDayNames(liftDays);
                    //Find allocation factor for existing customer
                    /*  Srikar: As on 04/22/14 Existing Containers allocation factors are based on Waste Category 
                        whereas New Container allocation factors are based on Waste Type
                        Repurcussions of moving to Waste Category for New Containers to be evaluated in future - TO DO 
                    */
                    wasteType = "";
                    if(containskey(thisContainerDict, "waste_type")){
                        wasteType = get(thisContainerDict, "waste_type");
                    }
                    frequencyStr = liftDayNames;
                    append(smallContainerPickupDaysArray, wasteType+":"+frequencyStr);
                    tempArray = string[];
                    //print frequencyStr;
                    append(tempArray, frequencyStr);
                    //if(not(isnull(wasteType)) AND containskey(allocationDaysDict, wasteType)){                
                    if(not(isnull(wasteType))){
                        if(containskey(allocationDaysDict, wasteType)){
                            pickupDaysArr = get(allocationDaysDict, wasteType);
                            for each in pickupDaysArr{
                                append(tempArray,each);
                            }
                            
                        }
                        if(findinarray(wasteTypeArray, wasteType) == -1){
                            append(wasteTypeArray, wasteType); 
                        }
                    }
                    //frequencyStr will either have pickup days if selected or frequency if no pickup days selected
                    
                    put(allocationDaysDict, wasteType, tempArray);
                }
            }
        }
    }
}   



returnStr = returnStr + "1" + "~" + "competitorFactor_quote" + "~" + string(competitorFactor) + "|"
                        + "1" + "~" + "competitorCode_quote" + "~" + competitorCode + "|";
                        
//=============================== END - Customer Type Factor Calculation ===============================//

//=============================== START- Division Service Calculation ===============================//
//Begin Loop over line items - this loop is only to store attribute values in dictionaries/ arrays/ variable - not for any calculations
for line in line_process{
    partNum = line._part_number;
    docNum = line._document_number;
    //Part line item loop
    if(partNum <> ""){
        //Recommended Items
        if(line._parent_doc_number <> ""){
            //Get the model name of the current recommended item
            if(containskey(modelNameDict, line._parent_doc_number)){
                modelName = get(modelNameDict, line._parent_doc_number);
                put(priceTypeDict, docNum, modelName);  
            }
        }
    }
    //Model loop
    else{
        if(not(containskey(modelNameDict, docNum))){
            put(modelNameDict, docNum, line._model_name);
        }
        //20141021 container group logic
    containerGroup = "";
    containerGroup = getconfigattrvalue(docNum, "containerGroup_config");
    if(isnull(containerGroup)){
        containerGroup = "";
    }
        
    put(containerGroupDict, docNum, containerGroup);
    //end container group logic
        //Small Container
        if(line._model_name == SMALL_CONTAINER){
            liftsPerContainer = getconfigattrvalue(docNum, "liftsPerContainer_s");
            requestPickupDays = getconfigattrvalue(docNum, "requestPickupDays");
            tentativePickupDays = getconfigattrvalue(docNum, "tentativePickupDays");
            wasteType = getconfigattrvalue(docNum, "wasteType");
            
            frequencyStr = "";
            //Pickup days selected by user in config for this model
            if(not(isnull(requestPickupDays)) AND requestPickupDays == "true"){
                frequencyStr = tentativePickupDays;
            }elif(not(isnull(requestPickupDays)) AND requestPickupDays == "false" AND not(isnull(liftsPerContainer))){
                frequencyConversionResultSet = bmql("SELECT Frequency, conversionFactor FROM Frequency_Conversion WHERE Frequency = $liftsPerContainer");
                frequencyStr = "";
                for result in frequencyConversionResultSet{
                    frequencyStr = get(result, "conversionFactor");
                }
            }
            append(smallContainerPickupDaysArray, wasteType+":"+frequencyStr);
            tempArray = string[];
            //print frequencyStr;
            append(tempArray, frequencyStr);
            //if(not(isnull(wasteType)) AND containskey(allocationDaysDict, wasteType)){                
            if(not(isnull(wasteType))){
                if(containskey(allocationDaysDict, wasteType)){
                    pickupDaysArr = get(allocationDaysDict, wasteType);
                    for each in pickupDaysArr{
                        append(tempArray,each);
                    }
                    
                }
                if(findinarray(wasteTypeArray, wasteType) == -1){
                    append(wasteTypeArray, wasteType); 
                }
            }
            //frequencyStr will either have pickup days if selected or frequency if no pickup days selected
            
            put(allocationDaysDict, wasteType, tempArray);
            put(modelTypeDict, docNum, SMALL_CONTAINER);
        }elif(line._model_name == SERVICE_CHANGE){
            commercialExists = true; //For now, since we are doing service change for only small containers, we don't have to check if the service change is for small or large, going forward, we need to check this and set commercialExists, industrialExists flags accordingly
            containerType_sc = "Small"; //getconfigattrvalue(docNum, "containerType_sc");
            salesActivity = getconfigattrvalue(docNum, "salesActivity");
            /*Anitha making changes to fix yardsPerMonth Existing customer issue - 09/11/2014 
            issue is with containerGroupNumber not being maintained at config levelFor this, i fixed containerGroup_config attribute rule in config and now 
            assigning containerGroup_config rather than quote level attribute*/
            currentContainerGrpNum = getconfigattrvalue(docNum, "containerGroup_config");
            priceAdjustmentReason = getconfigattrvalue(docNum, "priceAdjustmentReason");
            
            if(not(isnull(salesActivity)) AND salesActivity == "Service level change"){
                serviceChangeQuote = true;
            }
            if(not(isnull(salesActivity)) AND salesActivity == "Close container group"){
                closeContainerGroupQuote = true;
                put(modelTypeDict, docNum, "Close container group");
            }
            if(not(isnull(priceAdjustmentReason)) AND priceAdjustmentReason == "Rollback: Competitive Bid"){
                competitiveBidQuote = true;
            }elif(not(isnull(priceAdjustmentReason)) AND priceAdjustmentReason == "Rollback of PI"){
                rollbackOfPIQuote = true;
            }elif(not(isnull(priceAdjustmentReason)) AND priceAdjustmentReason == "Rollback of Current Price"){
                generalSaveQuote = true;
            }
            elif(not(isnull(priceAdjustmentReason)) AND (priceAdjustmentReason == "Price Increase: Personally Secured" OR priceAdjustmentReason == "Price Increase: Contractually Obligated") AND (salesActivity == "Price adjustment")){
                priceIncreaseQuote = true;
                put(modelTypeDict, docNum, "Price Increase");
            }
            liftsPerContainer_sc = getconfigattrvalue(docNum, "liftsPerContainer_sc");  
            liftsPerContainer_db = getconfigattrvalue(docNum, "liftsPerContainer_readOnly");    
            wasteType_sc = getconfigattrvalue(docNum, "wasteType_sc");
            wasteCategory_sc = getconfigattrvalue(docNum, "wasteCategory");
            quantity_sc = getconfigattrvalue(docNum, "quantity_sc");
            containerSize_sc = getconfigattrvalue(docNum, "containerSize_sc");
            competitiveBidAmount_sc = getconfigattrvalue(docNum, "competitiveBidAmount_sc");
            useCurrentPickupsPerDay_sc = getconfigattrvalue(docNum, "useCurrentPickupsPerDay_sc");
            
            routeTypeDerived_db = "";
            onsiteTimeInMins = "0";
            tentativePickupDays_db = "";
            wasteType_db = "";
            containerType_db = "";
            
            
            //accountSalesHistRecordSet = bmql("SELECT Container_Cnt, Monthly_Sales_Amt FROM Account_Sales_Hist WHERE infopro_acct_nbr = $_quote_process_customer_id AND Container_Grp_Nbr = $containerGroupForTransaction_quote");- this menu is not applicable anymore 
            //accountSalesHistRecordSet = bmql("SELECT Container_Cnt, Monthly_Sales_Amt FROM Account_Sales_Hist WHERE infopro_acct_nbr = $_quote_process_customer_id AND Container_Grp_Nbr = $containerGroupForTransaction_quote");
            //Anitha making changes to above query on 09/11/2014 to fix yardsPerMonth issue. Instead of using containerGroupForTransaction_quote, we must use currentContainerGrpNum
            accountSalesHistRecordSet = bmql("SELECT Container_Cnt, Monthly_Sales_Amt FROM Account_Sales_Hist WHERE infopro_acct_nbr = $_quote_process_customer_id AND Container_Grp_Nbr = $currentContainerGrpNum");
            for eachRecord in accountSalesHistRecordSet{
                //Populate dictionary with existing customer data for from data table
                put(existingCustDataDict, docNum+":Monthly_Sales_Amt", get(eachRecord, "Monthly_Sales_Amt"));
                put(existingCustDataDict, docNum+":Container_Cnt", get(eachRecord, "Container_Cnt"));
            }
            
            //Need containerType, wasteType, wasteCategory, routeTypeDerived, onsiteTimeInMins columns
            /*We don't need Pickup_Period_Unit column anymore in account_statu table because it's value is always "week", so just hard-coding the local variable here*/
            //Anitha making changes to below query on 09/11/2014 to fix yardsPerMonth issue. Instead of using containerGroupForTransaction_quote, we must use currentContainerGrpNum
            //accountStatusRecordSet = bmql("SELECT container_cnt, Shared_Cont_Grp_Nbr, Acct_Type, Pickup_Per_Tot_Lifts, Pickup_Period_Length, has_Compactor, Pickup_Schedule, Lift_Days, Container_Size, Container_Cd, waste_type, frf_rate_pct, erf_rate_pct, is_frf_charged, is_erf_charged, is_frf_locked, is_erf_locked, is_erf_on_frf, contract_term, is_Admin_Charged, Admin_Rate, monthly_rate, is_container_owned, container_category, new_cust_cg_flag, period, acct_nbr FROM Account_Status WHERE infopro_acct_nbr = $_quote_process_customer_id AND Site_Nbr = $siteNumber_quote AND Container_Grp_Nbr = $containerGroupForTransaction_quote");
            accountStatusRecordSet = bmql("SELECT container_cnt, Shared_Cont_Grp_Nbr, Acct_Type, Pickup_Per_Tot_Lifts, Pickup_Period_Length, has_Compactor, Pickup_Schedule, Lift_Days, Container_Size, Container_Cd, waste_type, frf_rate_pct, erf_rate_pct, is_frf_charged, is_erf_charged, is_frf_locked, is_erf_locked, is_erf_on_frf, contract_term, is_Admin_Charged, Admin_Rate, monthly_rate, is_container_owned, container_category, new_cust_cg_flag, period, acct_nbr FROM Account_Status WHERE infopro_acct_nbr = $_quote_process_customer_id AND Site_Nbr = $siteNumber_quote AND Container_Grp_Nbr = $currentContainerGrpNum"); //Add any other filters as appropriate to get more specific record
            
            for eachRecord in accountStatusRecordSet{
                containerType_db = "Small";//get(eachRecord, "containerType"); //Should have been in table - removed?
                Pickup_Per_Tot_Lifts_db = get(eachRecord, "Pickup_Per_Tot_Lifts");
                Pickup_Period_Length_db = get(eachRecord, "Pickup_Period_Length");
                container_category_db = get(eachRecord, "container_category");
                Pickup_Period_Unit_db = "week";
                tentativePickupDays_db = get(eachRecord, "Lift_Days");
                containerSize_db = get(eachRecord, "Container_Size");
                quantity_db = string(getint(eachRecord, "container_cnt"));
                compactor_db = get(eachRecord, "has_Compactor");
                routeTypeDerived_db = get(eachRecord, "Container_Cd");//This is route type derived
                onsiteTimeInMins_db = "0";//get(eachRecord, "onsiteTimeInMins");//To be added in table
                wasteCategory_db = get(eachRecord, "waste_type"); //Account status considers waste category as waste type
                Is_FRF_On_db = string(getint(eachRecord, "is_frf_charged"));
                Is_ERF_On_db = string(getint(eachRecord, "is_erf_charged"));
                Is_ERF_On_FRF_db = get(eachRecord, "is_erf_on_frf");
                Is_Admin_On_db = get(eachRecord, "is_Admin_Charged");
                adminRate = getFloat(eachRecord, "Admin_Rate");
                current_monthlyRate = getFloat(eachRecord, "monthly_rate");
                is_container_owned = get(eachRecord, "is_container_owned");
                newCustomerConfig = getInt(eachRecord, "new_cust_cg_flag");
                period = get(eachRecord, "period");
                contract_term = get(eachRecord, "contract_term");
                accountNumber = get(eachRecord, "acct_nbr");
                                
                containerOwned = false;
                if(is_container_owned == "1"){
                    containerOwned = true;
                }
                //Fixed Fees
                is_frf_locked = get(eachRecord, "is_frf_locked");
                is_erf_locked = get(eachRecord, "is_erf_locked");
                                
                if(is_frf_locked == "1" OR is_erf_locked == "1"){
                    customerHasAFixedFee = true;
                }
                
                is_ERF_Charged = "No";
                is_FRF_Charged = "No";
                is_ERF_On_FRF_charged = "No";
                
                if(Is_FRF_On_db == "1"){
                    is_FRF_Charged = "Yes";
                }
                if(Is_ERF_On_db == "1"){
                    is_ERF_Charged = "Yes";
                }
                if(Is_ERF_On_FRF_db == "1"){
                    is_ERF_On_FRF_charged = "Yes";
                }
                
                //If the user has selected the fix value, use it, otherwise don't set fee here but use the divisionRate fee
                if(find(feesToCharge_quote, "Fixed Fuel Recovery Fee (FRF)") > -1 ){
                    frfPct = getFloat(eachRecord, "frf_rate_pct");
                    frfRate = frfPct/100;
                }
                if(find(feesToCharge_quote, "Fixed Environment Recover Fee (ERF)" ) > -1){
                    erfPct = getFloat(eachRecord, "erf_rate_pct");
                    erfRate = erfPct/100;
                }
                 
                print "This is third frf " + string(frfRate);
                
                put(existingCustDataDict, docNum+":Is_FRF_On", (is_FRF_Charged));
                put(existingCustDataDict, docNum+":FRF_Pct", string(frfRate));
                put(existingCustDataDict, docNum+":Is_ERF_On", (is_ERF_Charged));
                put(existingCustDataDict, docNum+":Is_ERF_On_FRF", (is_ERF_On_FRF_charged));
                put(existingCustDataDict, docNum+":ERF_Pct", string(erfRate));
                put(existingCustDataDict, docNum+":is_frf_locked", is_frf_locked);
                put(existingCustDataDict, docNum+":is_erf_locked", is_erf_locked);
                put(existingCustDataDict, docNum+":contract_term", contract_term);
                put(existingCustDataDict, docNum+":Is_Admin_On", Is_Admin_On_db);
                put(existingCustDataDict, docNum+":Admin_Rate", string(adminRate));
                put(existingCustDataDict, docNum+":monthlyRate", string(current_monthlyRate));
                put(existingCustDataDict, docNum+":container_owned", string(containerOwned));
                put(existingCustDataDict, docNum+":container_category", container_category_db);
                put(existingCustDataDict, docNum+":Pickup_Per_Tot_Lifts", (Pickup_Per_Tot_Lifts_db));
                put(existingCustDataDict, docNum+":period", (period));
                put(existingCustDataDict, docNum+":Pickup_Period_Length", (Pickup_Period_Length_db));
                put(existingCustDataDict, docNum+":contract_term", (contract_term));
                
                if(lower(wasteCategory_db) == "solid waste"){
                    wasteType_db = "Solid Waste";
                }elif(lower(wasteCategory_db) == "recycling"){
                    wasteType_db = "All in One - Single stream";
                }
                
                //No changes were made to Waste Type selections
                if(wasteType_sc == "No Change"){
                    wasteType_sc = wasteType_db;
                }
                
                //No Changes made to lifts per container selections 
                if(liftsPerContainer_sc == "No Change"){
                    liftsPerContainer_sc = liftsPerContainer_db;
                }
                
                if(containerType_sc == ""){
                    containerType_sc = containerType_db;
                }
                if(containerSize_sc == "" OR containerSize_sc == "No Change"){
                    containerSize_sc = containerSize_db;
                }
                if(quantity_sc == ""){
                    quantity_sc = quantity_db;
                }
                
                
                //Populate dictionary with existing customer data for from data table
                put(existingCustDataDict, docNum+":wasteType", wasteType_db);
                put(existingCustDataDict, docNum+":containerType", containerType_db);
                put(existingCustDataDict, docNum+":salesActivity", salesActivity);
                put(existingCustDataDict, docNum+":liftsPerContainer", liftsPerContainer_db);
                put(existingCustDataDict, docNum+":quantity", quantity_db);
                put(existingCustDataDict, docNum+":containerSize", containerSize_db);
                put(existingCustDataDict, docNum+":compactor", compactor_db);
                put(existingCustDataDict, docNum+":routeTypeDerived", routeTypeDerived_db);
                put(existingCustDataDict, docNum+":onsiteTimeInMins", onsiteTimeInMins_db);
                put(existingCustDataDict, docNum+":priceAdjustmentReason", priceAdjustmentReason);
                put(existingCustDataDict, docNum+":wasteCategory", wasteCategory_db);
                put(existingCustDataDict, docNum+":useCurrentPickupsPerDay_sc", useCurrentPickupsPerDay_sc);
                put(existingCustDataDict, docNum+":acct_nbr", accountNumber);
            }//End of result set iteration
            
            if(containerType_sc == ""){
                containerType_sc = "Small"; //Default container type to Small for time being - service change is only for small container currently
            }
            //Add container type in array
            if(findinarray(containerTypesArray, containerType_sc) == -1){
                append(containerTypesArray, containerType_sc);
            }
            
            frequencyStr_db = "";
            frequencyStr_sc = "";
            
            if(frequencyStr_db == ""){
                frequencyConversionResultSet = bmql("SELECT Frequency, conversionFactor FROM Frequency_Conversion");
                
                for result in frequencyConversionResultSet{
                    frequency = get(result, "Frequency");
                    frequencyFactor = get(result, "conversionFactor");
                    if(lower(frequency) == lower(liftsPerContainer_db)){
                        frequencyStr_db = frequencyFactor;
                    }
                    if(lower(frequency) == lower(liftsPerContainer_sc)){
                        frequencyStr_sc = frequencyFactor;
                    }
                }
            }
            
            put(serviceChangeDataDict, docNum+":wasteType", wasteType_sc);
            put(serviceChangeDataDict, docNum+":wasteCategory", wasteCategory_sc);
            put(serviceChangeDataDict, docNum+":containerType", containerType_sc);
            put(serviceChangeDataDict, docNum+":liftsPerContainer", liftsPerContainer_sc);
            put(serviceChangeDataDict, docNum+":quantity", quantity_sc);
            put(serviceChangeDataDict, docNum+":containerSize", containerSize_sc);
            put(serviceChangeDataDict, docNum+":frequency", frequencyStr_sc);
            put(serviceChangeDataDict, docNum+":routeTypeDerived", routeTypeDerived_db); //Route type can't be changed
            put(serviceChangeDataDict, docNum+":competitiveBidAmount", competitiveBidAmount_sc); //Route type can't be changed
            //End Store Service change user selections in a dictionary
            
            if(isnumber(frequencyStr_db)){
                put(existingCustDataDict, docNum+":frequency", frequencyStr_db);
            }
        }//End of Service change
        if(isnumber(line._parent_doc_number)){
            returnStr = returnStr + line._parent_doc_number + "~" + "existingQuantity_line" + "~" + getconfigattrvalue(line._parent_doc_number, "quantity_readOnly") + "|"
                        + line._parent_doc_number + "~" + "existingWasteType_line" + "~" + getconfigattrvalue(line._parent_doc_number, "wasteType_readOnly") + "|"
                        + line._parent_doc_number + "~" + "existingContainerSize_line" + "~" + getconfigattrvalue(line._parent_doc_number, "containerSize_readOnly") + "|"
                        + line._parent_doc_number + "~" + "existingLiftsPerContainer_line" + "~" + getconfigattrvalue(line._parent_doc_number, "liftsPerContainer_readOnly") + "|";     
        }
    }

    returnStr = returnStr + "1~priceIncreaseQuote_quote" + "~" + string(priceIncreaseQuote) + "|"
                          + "1~serviceChangeQuote_quote" + "~" + string(serviceChangeQuote) + "|"
                          + "1~competitiveBidQuote_quote" + "~" + string(competitiveBidQuote) + "|"
                          + "1~rollbackOfPIQuote_quote" + "~" + string(rollbackOfPIQuote) + "|"
                          + "1~generalSaveQuote_quote" + "~" + string(generalSaveQuote) + "|"
                          + "1~customerHasAFixedFee_quote" + "~" + string(customerHasAFixedFee) + "|";
}
//End Loop over line items - this loop is only to store attribute values in dictionaries/ arrays/ variable - not for any calculations
/*if(DEBUG){
print "containerTypesArray #####";
print containerTypesArray;
print "allocationDaysDict";
print allocationDaysDict;print wasteTypeArray;
print "existingCustDataDict"; print existingCustDataDict;
print "serviceChangeDataDict"; print serviceChangeDataDict;
}*/

for eachWasteType in wasteTypeArray{
    allocationFactor = 0.0;
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
                    append(pickUpDaysArray, eachItem);  //get all tentative pick up days selected
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
            allocationFactor =  uniqueTrips/ totalTrips;
        }
        put(allocationFactorDict, eachWasteType, allocationFactor);
        allocationFactorStringHTML = allocationFactorStringHTML + eachWasteType + "#_#" + string(allocationFactor) + "@_@";
    }   
}
if(DEBUG){
    print "allocationFactorDict";
    print allocationFactorDict;
}

/*
//Variables used to find Transaction Code
totalMonthlyYardsNew = 0.0;
totalMonthlyTonsNew = 0.0;
totalHaulNew = 0.0;
totalRentalNew = 0.0;
totalDisposalNew = 0.0;
totalRentalNew = 0.0;
totalSmallContainerPriceNew = 0.0;*/

//divistionSelected = atoi(division_quote);
//divistionSelected = atoi(division_quote);


if(initialTerm_quote == "Existing Terms"){
    initialTerm = diffMthRnd;
}
else{
initialTerm=atoi(initialTerm_quote);
}

print "//////////////////";
print initialTerm;
print "//////////////////";

//Begin looping over line items
modelCount=0;
exchangeDict = dict("string");
exchangeUIDict = dict("string");

extraLiftDict = dict("string");
extraLiftUIDict = dict("string");

extraYdDict = dict("string");
extraYdUIDict = dict("string");

relocateDict = dict("string");
relocateUIDict = dict("string");

removeDict = dict("string");
removeUIDict = dict("string");

dryDict = dict("string");
dryUIDict = dict("string");

washoutDict = dict("string");
washoutUIDict = dict("string");

for line in line_process{

    //Set the value of partLineItem. Returns true for each line that is not a model
    partLineItem = "false";
    occurrence = "";
    priceType = "";
    frequencyAttribute = "";
    
    if(containskey(priceTypeDict, line._document_number)){
        priceType = get(priceTypeDict, line._document_number);
    }
    if(line._parent_doc_number == ""){ //Model loop
        put(exchangeExistsDict, line._document_number, false);//Initialize this Dict to false for each model and if exchange exists, set it to true
        put(exchangeDict, line._document_number, string(line.divisionExchange_line));
        put(exchangeUIDict, line._document_number, string(line.divisionExchange_ui_line));
        
        put(extraLiftDict, line._document_number, string(line.divisionExtLift_line));
        put(extraLiftUIDict, line._document_number, string(line.divisionExtLift_ui_line));
        
        put(extraYdDict, line._document_number, string(line.divisionExtYard_line));
        put(extraYdUIDict, line._document_number, string(line.divisionExtYard_ui_line));
        
        put(dryDict, line._document_number, string(line.divisionDRY_line));
        put(dryUIDict, line._document_number, string(line.divisionDRY_ui_line));
        
        put(washoutDict, line._document_number, string(line.divisionWAS_line));
        put(washoutUIDict, line._document_number, string(line.divisionWAS_ui_line));
        
        put(relocateDict, line._document_number, string(line.divisionRelo_line));
        put(relocateUIDict, line._document_number, string(line.divisionRelo_ui_line));
        
        put(removeDict, line._document_number, string(line.divisionRemove_line));
        put(removeUIDict, line._document_number, string(line.divisionRemove_ui_line));
    }
    if(line._parent_doc_number <> ""){  //Only apply pricing to non-model line items

        //Initialize variables
        arrayDelimiter = "@@";
        outputDict = dict("string");
        frequency = 0.0;
        overrideTime = 0.0;
        containerCustomerOwned = 0;
        compactorCustomerOwned = 0;
        routeTypeDervied = "";
        frequencyAttribute = "";
        lineItemComment = line._line_item_comment;
        containerSize = "";
        container = "";
        installationChg = 0.0;
        
        haulRateStr = "";
        haulRate = 0.0;
        disposalRateStr = "";
        disposalRate = 0.0;
        costToServe = 0.0;
        cts_month_incl_oh = 0.0;
        cost_disp_xfer_proc = 0.0;
        operating_expense = 0.0;
        existing_cts_month_incl_oh = 0.0;
        existing_cost_disp_xfer_proc = 0.0;
        existing_operating_expense = 0.0;
        outputDict = dict("string");
        guardrailInputDict = dict("string");
        guardrailOutputDict = dict("string");
        guardrailCurrentOutputDict = dict("string");
        billingType = "";
        installationCharge_l = 0.0;
        estHaulsPerMonthStr = "";
        yardsPerMonth = 0.0;
        rental = "";
        containerType_l = "";
        alloc_rental = 0;
        monthlySalesAmt_db = "0.0";
        serviceCode = "";
        parentDoc = line._parent_doc_number;
        print "parentDoc=="+parentDoc;
        priceIncreaseLine = false;
        closeContainerLine = false;
        
        //Determine if service change is price increase or close container
        if(containskey(modelTypeDict, line._parent_doc_number)){
            lineType = get(modelTypeDict, line._parent_doc_number);
            if(lineType == "Price Increase"){
                priceIncreaseLine = true;
            }elif(lineType == "Close container group"){
                closeContainerLine = true;
            }
        }
        
        if(containskey(installChargeDict, line._parent_doc_number)){
            installationChg = get(installChargeDict, line._parent_doc_number);
        }
        //Get all common config attributes among Large & Small Containers
        isCustomerOwned = getconfigattrvalue(line._parent_doc_number, "isCustomerOwned");
        wasteType = getconfigattrvalue(line._parent_doc_number, "wasteType");
        wasteCategory = getconfigattrvalue(line._parent_doc_number, "wasteCategory");
        routeTypeDervied = getconfigattrvalue(line._parent_doc_number, "containerCodeDerived");
        lOBCategoryDerived = getconfigattrvalue(line._parent_doc_number, "lOBCategoryDerived");
        quantityStr = getconfigattrvalue(line._parent_doc_number, "quantity");

        if(priceType == SERVICE_CHANGE){ //For Service Change items, get the special Config attributes
            wasteType = getconfigattrvalue(line._parent_doc_number, "wasteType_sc");
            if(wasteType == "No Change"){
                wasteType = getconfigattrvalue(line._parent_doc_number, "wasteType_readOnly");
            }
            frequencyAttribute = getconfigattrvalue(line._parent_doc_number, "liftsPerContainer_sc");
            if(frequencyAttribute == "No Change"){
                frequencyAttribute = getconfigattrvalue(line._parent_doc_number, "liftsPerContainer_readOnly");
            }
        }
        //End all common config attributes
        
        if(isCustomerOwned == "true"){
            containerCustomerOwned = 1;
        }
        
        inputDict = dict("string");     //Build the input dict to parse Config line item comments. inputStr, COMM_VALUE_DELIM, and FIELD_DELIM are constant for all inputs
        put(inputDict, "inputStr", lineItemComment);
        put(inputDict, "COMM_VALUE_DELIM", COMM_VALUE_DELIM);
        put(inputDict, "FIELD_DELIM", FIELD_DELIM);
        
        put(inputDict, "key", "siteName");  //Get the site name the user chose in Config
        siteName = util.parseThroughLineItemComment(inputDict);
        
        put(inputDict, "key", "totalTime"); //Get the total disposal time for the site the user chose in Config. Add 15 minutes for time on-site
        totalTime = util.parseThroughLineItemComment(inputDict);
        
        put(inputDict, "key", "rateType");  //Get the total disposal time for the site the user chose in Config
        rateType = util.parseThroughLineItemComment(inputDict);
        
        //Get the service code for service change line items
        put(inputDict, "key", "ServiceCode");   
        serviceCode = util.parseThroughLineItemComment(inputDict);
        
        //=============================== START - Large Container Price Calculation ===============================//
        //Get the haul and disposal rates from the output
        if(priceType == LARGE_CONTAINER){
            stringDict = dict("string");
            
            if(findinarray(containerTypesArray, "Large") == -1){
                append(containerTypesArray, "Large");
            }
            
            billingType = getconfigattrvalue(line._parent_doc_number, "billingType_l");
            overrideTimeStr = getconfigattrvalue(line._parent_doc_number, "OverrideTotalTime_l");
            customerOwnedCompactor = getconfigattrvalue(line._parent_doc_number, "customerOwnedCompactor");
            frequencyStr = getconfigattrvalue(line._parent_doc_number, "frequency");
            // 20141022(James): overwrite routeTypeDervied variable from above for large container
            routeTypeDervied = getconfigattrvalue(line._parent_doc_number, "routeTypeDervied");
            oncallHaulsStr = getconfigattrvalue(line._parent_doc_number, "totalEstimatedHaulsMonth_l"); //Used as quantity for on-call parts
            frequencyAttribute = getconfigattrvalue(line._parent_doc_number, "haulsPerPeriod");
            containerSize = getconfigattrvalue(line._parent_doc_number, "equipmentSize_l");
            rental = getconfigattrvalue(line._parent_doc_number, "rental");
            containerType_l = getconfigattrvalue(line._parent_doc_number, "containerType_l");
            adjustedTotalTime_l = getconfigattrvalue(line._parent_doc_number, "adjustedTotalTime_l");
            installationChargeStr = getconfigattrvalue(line._parent_doc_number, "onetimeInstallationCharge_l");
            if(isnumber(installationChargeStr)){
                installationCharge_l = atof(installationChargeStr);
            }
            put(installChargeDict, line._parent_doc_number, installationCharge_l);
            
            estHaulsPerMonthStr = frequencyStr;
            haulsPerContainer = frequencyStr;
            servicelLevel = getconfigattrvalue(line._parent_doc_number, "serviceRequirement");
            
            if(isnumber(oncallHaulsStr)){
                oncallHauls = atof(oncallHaulsStr);
            }
            
            if(customerOwnedCompactor == "true"){
                compactorCustomerOwned = 1;
            }
        
            if(isnumber(overrideTimeStr)){
                overrideTime = atof(overrideTimeStr);
            }
        
            put(stringDict, "wasteCategory", wasteCategory);
            put(stringDict, "siteName", siteName);  //Should be specific to the site the user selected
            put(stringDict, "wasteType", wasteType); 
            put(stringDict, "LOB", lOBCategoryDerived);
            put(stringDict, "arrayDelimiter", arrayDelimiter);
            put(stringDict, "routeType", routeTypeDervied); 
            put(stringDict, "frequency", frequencyStr); 
            //Anitha- 12/13/2013 - To keep it consistent with config, putting frequency value in haulsPerPeriod
            if(servicelLevel <> "On-call"){
                put(stringDict, "haulsPerPeriod", getconfigattrvalue(line._parent_doc_number, "frequency"));
            }
            else{
                put(stringDict, "haulsPerPeriod", "1");
            }
            put(stringDict, "containerQuantity", quantityStr);
            quantity = 0;
            if(isnumber(quantityStr)){
                quantity = atof(quantityStr);
            }
            tonsPerHaul = getconfigattrvalue(line._parent_doc_number, "estTonsHaul_l");
            
            
            if(billingType == "Flat Rate + Overage"){
                tonsPerHaul = getconfigattrvalue(line._parent_doc_number, "tonsIncludedInHaulRate_l");
            }
            elif(billingType == "Haul + Minimum Tonnage"){
                tempArray = float[];
                if(NOT isnull(getconfigattrvalue(line._parent_doc_number, "estTonsHaul_l"))){
                    if(isnumber(getconfigattrvalue(line._parent_doc_number, "estTonsHaul_l"))){
                        append(tempArray,atof(getconfigattrvalue(line._parent_doc_number, "estTonsHaul_l"))); 
                    }
                }
                if(NOT isnull(getconfigattrvalue(line._parent_doc_number, "minimumTonshaul_l"))){
                    if(isnumber(getconfigattrvalue(line._parent_doc_number, "minimumTonshaul_l"))){
                        append(tempArray,atof(getconfigattrvalue(line._parent_doc_number, "minimumTonshaul_l"))); 
                    }
                }
                if(sizeofarray(tempArray) > 0){
                    tonsPerHaul = string(max(tempArray));
                }
            }
            put(stringDict, "estTonsPerHaul", tonsPerHaul);
            
            if(isnumber(frequencyStr)){
                frequency = atof(frequencyStr);
            }
            //Service is on-call, estHaulsPerMonthStr is user input
            if(servicelLevel <> "On-call"){ //if service is scheduled, this must be calculate from frequency
                estHaulsPerMonthStr = string(frequency * (52.0 / 12.0) * quantity);
                haulsPerContainer = string(frequency * (52.0 / 12.0));
            }
            else{
                estHaulsPerMonthStr = string(oncallHauls * quantity);
                haulsPerContainer = string(oncallHauls);
            }
            if(billingType == "Rental"){
                estHaulsPerMonthStr = "0.0";
                haulsPerContainer = "0.0";
            }
            put(stringDict, "estHaulsPerMonth", estHaulsPerMonthStr); 
            put(stringDict, "haulsPerContainer", haulsPerContainer); 
            
            put(stringDict, "totalTimePerHaul", adjustedTotalTime_l); //Should be specific to the site the user selected
            if(isnumber(adjustedTotalTime_l)){
                totalTimeFloat = atof(adjustedTotalTime_l);
            }
            
            put(stringDict, "region_quote", region_quote);
            put(stringDict, "division_quote", division_quote); 
            put(stringDict, "partNumber", line._part_number);
            put(stringDict, "initialTerm_quote", string(initialTerm));
            put(stringDict, "billingType_l", billingType);
            put(stringDict, "accountType", accountType_quote);
            put(stringDict, "industry", industry_quote);
            put(stringDict, "isContainerCustomerOwned", string(containerCustomerOwned));
            put(stringDict, "isCompactorCustomerOwned", string(compactorCustomerOwned));
            put(stringDict, "salesActivity_quote", salesActivity_quote);
            put(stringDict, "compactorValueConfig", getconfigattrvalue(line._parent_doc_number, "compactorValue"));
            put(stringDict, "containerType_l", containerType_l);
            if(DEBUG){print "Large Container Pricing Input Dict"; print stringDict;}
            
            //Include Rental flags in large container pricing calculations - Added 02/06/2014
            //Set alloc_rental flag - Rental
            //Include Rental flags in large container pricing calculations - Added 02/06/2014
            
            if(containerType_l == "Open Top" AND billingType == "Haul + Disposal" AND (rental == "Monthly" OR rental == "Daily")){
                alloc_rental = 1;
            }
            
            if(rental <> "None" AND rental <> ""){
                put(stringDict, "alloc_rental", string(alloc_rental));  
                put(stringDict, "rental", rental);  
            }
                
            outputDict = util.largeContainerPricing(stringDict);
            
            if(DEBUG){
                print "Large Container pricing INPUT";
                print stringDict;
                print "Large Container pricing OUTPUT";
                print outputDict;
            }
            
            pricingDebugInputDict = stringDict;
            pricingDebugOutputDict = outputDict;
            
            //Financial Summary -costs
            if(rateType == "Haul"){
                if(isnumber(get(outputDict, "cts_month_incl_oh"))){
                    cts_month_incl_oh = atof(get(outputDict, "cts_month_incl_oh"));
                }
                if(isnumber(get(outputDict, "cost_dsp_xfer_per_month"))){
                    cost_disp_xfer_proc = atof(get(outputDict, "cost_dsp_xfer_per_month")); //This is Disposal Expense per month value(not per haul value)alone for large 
                }
                //Operating expenses of each container = Overall Expense - Disposal Expense
                operating_expense = cts_month_incl_oh - cost_disp_xfer_proc;
            }   
            if(DEBUG){
                print "Large Container Pricing Output Dict";
                print outputDict;
            }
            
            monthlyTotalHaulSell = 0.0;
            
            estHaulsPerMonth = 0.0;
            if(isnumber(estHaulsPerMonthStr)){
                estHaulsPerMonth = atof(estHaulsPerMonthStr);
            }
            
            if(rateType == "Haul" OR rateType == "Flat"){
                monthlyTotalHaulSell = line.sellPrice_line * estHaulsPerMonth;
            }
            
            estTonsHaul = tonsPerHaul;
            minimumTonsHaul = "0.0";
            if(NOT isnull(getconfigattrvalue(line._parent_doc_number, "minimumTonshaul_l"))){
                minimumTonsHaul = getconfigattrvalue(line._parent_doc_number, "minimumTonshaul_l");
            }
            
            //Call calculateGuardrails util function
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
            put(guardrailInputDict, "tonsPerHaul", tonsPerHaul);
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
            put(guardrailInputDict, "initialTerm_quote", string(initialTerm));
            put(guardrailInputDict, "customer_zip", _quote_process_siteAddress_quote_zip);
            put(guardrailInputDict, "segment", segment_quote);
            put(guardrailInputDict, "industry", industry_quote);
            put(guardrailInputDict, "infoProDivNumber", infoproDivision_RO_quote);
            put(guardrailInputDict, "compactorLife", get(outputDict, "compactorLife"));
            put(guardrailInputDict, "isContainerCustomerOwned", string(containerCustomerOwned));
            put(guardrailInputDict, "workingCapital", get(outputDict, "workingCapital"));
            put(guardrailInputDict, "containerMntPerHaul", get(outputDict, "containerMntPerHaul"));
            put(guardrailInputDict, "newCustomerConfig", string(newCustomerConfig)); //This is customer type flag - use this instead of sales activity
            put(guardrailInputDict, "hasCompactor", get(outputDict, "hasCompactor")); 
            put(guardrailInputDict, "compactor_depr", get(outputDict, "compactor_depr")); 
            put(guardrailInputDict, "perHaulCosts", get(outputDict, "perHaul"));
            //Include Rental for Guard rail calculation 
            
            put(guardrailInputDict, "marketRate", get(outputDict, "marketRate"));
            put(guardrailInputDict, "rental", rental);
            put(guardrailInputDict, "containerType_l", containerType_l);
            put(guardrailInputDict, "containerDepreciation", get(outputDict, "containerDepreciation"));
            put(guardrailInputDict, "compactorDepreciation", get(outputDict, "compactorDepreciation"));
            put(guardrailInputDict, "compactorValue", get(outputDict, "compactorValue"));
            put(guardrailInputDict, "containerROA", get(outputDict, "containerROA"));
            put(guardrailInputDict, "compactorROA", get(outputDict, "compactorROA"));
            put(guardrailInputDict, "isCompactorCustomerOwned", string(compactorCustomerOwned));
            put(guardrailInputDict, "routeType", routeTypeDervied); 
            put(guardrailInputDict, "customer_id", _quote_process_customer_id);  //J.Felberg passing additional fields for FRF and ERF calculations
            print "put containerGroup line 1094";
            put(guardrailInputDict, "containerGroup", containerGroupForTransaction_quote);  //J.Felberg passing additional fields for FRF and ERF calculations
            put(guardrailInputDict, "siteNumber_quote", siteNumber_quote); //J.Felberg passing additional fields for FRF and ERF calculations
            
            if(DEBUG){
            print "guardrailInputDict for large Container";
            print guardrailInputDict;
            }
            
            //JFelberg Fees
            put(guardrailInputDict, "feesToCharge", feesToCharge_quote);
            guardrailOutputDict = util.calculateGuardrails(guardrailInputDict);
            
            guardrailDebugInputDict = guardrailInputDict;
            guardrailDebugOutputDict = guardrailOutputDict;

            if(DEBUG){
            print "guardrailOutputDict for large Container";
            print guardrailOutputDict;
            }
            
            if(DEBUG){
                //Call the debugger function to map BM variables to RS variables
                print "BM -> RS variable mapping";
                str = util.pricingDebugRule(pricingDebugInputDict, pricingDebugOutputDict, guardrailDebugInputDict, guardrailDebugOutputDict);
            }
            
            //End of calculateGuardrail util function call
            
            returnStr = returnStr + line._document_number + "~" + "estimatedLifts_line" + "~" + estHaulsPerMonthStr + "|"
                                  + line._document_number + "~" + "monthlyTotalHaulSell_line" + "~" + string(monthlyTotalHaulSell) + "|"
                                  + line._document_number + "~" + "estTonsHaul_Line" + "~" + estTonsHaul + "|"
                                  + line._document_number + "~" + "minimumTonsHaul_line" + "~" + minimumTonsHaul + "|"
                                  + line._document_number + "~" + "estHaulsPerMonth_line" + "~" + string(estHaulsPerMonth) + "|"
                                  + line._document_number + "~" + "costPerMonthIncludingOverhead_line" + "~" + string(cts_month_incl_oh) + "|"
                                  + line._document_number + "~" + "largeContainerBillingType_line" + "~" + billingType + "|"
                                  + line._parent_doc_number + "~" + "largeContainerBillingType_line" + "~" + billingType + "|";
        }       
        //=============================== END - Large Container Price Calculation ===============================//
    
        //=============================== START - Small Container Price Calculation ===============================//
                
        //Build stringDict for input to calculateFloorPrice for this line item
        elif(priceType == SMALL_CONTAINER){
            
            if(findinarray(containerTypesArray, "Small") == -1){
                append(containerTypesArray, "Small");
            }
            
            allocationFactor = 1.0;
            if(containskey(allocationFactorDict, wasteType)){
                allocationFactor = get(allocationFactorDict, wasteType);
            }
            
            containerSize = getconfigattrvalue(line._parent_doc_number, "containerSize");
            
            stringDict = dict("string");
            put(stringDict, "wasteCategory", wasteCategory);    //Config attribute info is stored on model lines
            put(stringDict, "routeTypeDerived", routeTypeDervied);
            put(stringDict, "containerQuantity", quantityStr);
            put(stringDict, "containerSize", getconfigattrvalue(line._parent_doc_number, "containerSize"));
            put(stringDict, "frequency", getconfigattrvalue(line._parent_doc_number, "frequency"));
            put(stringDict, "compactor", getconfigattrvalue(line._parent_doc_number, "compactor"));
            put(stringDict, "accountType", accountType_quote);
            put(stringDict, "division_quote", division_quote);
            put(stringDict, "region_quote", region_quote); //get this based on division selected rather than home division
            put(stringDict, "partNumber", line._part_number);
            put(stringDict, "initialTerm_quote", string(initialTerm));
            put(stringDict, "disposalCostPerTon", string(disposalSiteCostCommercial));
            put(stringDict, "dsp_xfer_price_per_ton", string(dsp_xfer_priceperton_small));
            put(stringDict, "allocationFactor", string(allocationFactor));
            put(stringDict, "industry", industry_quote);
            put(stringDict, "additionalSmallContainerSiteTime", getconfigattrvalue(line._parent_doc_number, "additionalSmallContainerSiteTime_s"));
            put(stringDict, "lock", getconfigattrvalue(line._parent_doc_number, "lock"));
            put(stringDict, "isEnclosure", getconfigattrvalue(line._parent_doc_number, "isEnclosure"));
            put(stringDict, "rolloutFeet", getconfigattrvalue(line._parent_doc_number, "rolloutFeet"));
            put(stringDict, "casters", getconfigattrvalue(line._parent_doc_number, "casters"));
            put(stringDict, "scoutRoute", getconfigattrvalue(line._parent_doc_number, "scoutRoute"));
            put(stringDict, "onsiteTimeInMins", getconfigattrvalue(line._parent_doc_number, "onsiteTimeInMins"));
            put(stringDict, "isCustomerOwned", getconfigattrvalue(line._parent_doc_number, "isCustomerOwned"));
            put(stringDict, "newCustomerConfig", string(newCustomerConfig)); //Use this instead of Sales Activity for customer type
            
            /*if(DEBUG){
            print "Small Container Pricing Input Dict";
            print stringDict;
            }*/
            
            outputDict = util.smallContainerPricing(stringDict);
            
            pricingDebugInputDict = stringDict; 
            pricingDebugOutputDict = outputDict;
                
            /*if(DEBUG){
                print "Small Container Pricing Output Dict";
                print outputDict;
            }*/
            
            if(isnumber(get(outputDict, "floor"))){
                costToServe = atof(get(outputDict, "floor"));   //This returns the cost to serve, which includes the ERF and FRF
            }
            //Financial Summary -costs applies only once per container/model
            if(rateType == "Base"){
                if(isnumber(get(outputDict, "cts_month_incl_oh"))){
                    cts_month_incl_oh = atof(get(outputDict, "cts_month_incl_oh")); //This is overall cost/expense
                }
                if(isnumber(get(outputDict, "cost_disp_xfer_proc"))){
                    cost_disp_xfer_proc = atof(get(outputDict, "cost_disp_xfer_proc")); //This is Disposal Expense alone
                }
                //Operating expenses of each container = Overall Expense - Disposal Expense
                operating_expense = cts_month_incl_oh - cost_disp_xfer_proc;
            }
                
            //Multiply costToServe with Allocation Factor for all Small Container line items
            allocationFactor = 0.0;
            
            if(containskey(allocationFactorDict, wasteType)){
                allocationFactor = get(allocationFactorDict, wasteType);
                costToServe = costToServe * allocationFactor;
            }
            
            //Capture and return yardsPerMonth from the calculateFloorPrice function
            yardsPerMonth = 0.0;
            yardsPerMonthStr = get(outputDict, "yardsPerMonth");
            if(isnumber(yardsPerMonthStr)){
                yardsPerMonth = atof(yardsPerMonthStr);
                yardsPerMonth = round(yardsPerMonth, 4);
            }
            
            //Call calculateGuardrails util function
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
            put(guardrailInputDict, "initialTerm_quote", string(initialTerm));
            put(guardrailInputDict, "customer_zip", _quote_process_siteAddress_quote_zip);
            put(guardrailInputDict, "segment", segment_quote);
            put(guardrailInputDict, "industry", industry_quote);
            put(guardrailInputDict, "quantity", quantityStr);
            put(guardrailInputDict, "infoProDivNumber", infoproDivision_RO_quote);
            put(guardrailInputDict, "routeType", routeTypeDervied);
            put(guardrailInputDict, "newCustomerConfig", string(newCustomerConfig)); //Use this instead of Sales Activity for customer type
            put(guardrailInputDict, "containerSize", getconfigattrvalue(line._parent_doc_number, "containerSize")); // BB 10-Jun-2014: Container Size is required in guardrail table lookups
            put(guardrailInputDict, "customer_id", _quote_process_customer_id);  //J.Felberg passing additional fields for FRF and ERF calculations
            put(guardrailInputDict, "containerGroup", containerGroupForTransaction_quote);  //J.Felberg passing additional fields for FRF and ERF calculations
            put(guardrailInputDict, "siteNumber_quote", siteNumber_quote); //J.Felberg passing additional fields for FRF and ERF calculations
            
            /*if(DEBUG){
            print "guardrailInputDict for Small Container";
            print guardrailInputDict;
            }*/
            
            print "this is the input for small containers";
            print guardrailInputDict;
            
            //Jfelberg
            put(guardrailInputDict, "feesToCharge", feesToCharge_quote);
            guardrailOutputDict = util.calculateGuardrails(guardrailInputDict);
            
            guardrailDebugInputDict = guardrailInputDict;
            guardrailDebugOutputDict = guardrailOutputDict;
            
            /*if(DEBUG){
                print "guardrailOutputDict for Small Container";
                print guardrailOutputDict;
            }*/
            if(DEBUG){
                //Call the debugger function to map BM variables to RS variables
                print "BM -> RS variable mapping";
                str = util.pricingDebugRule(pricingDebugInputDict, pricingDebugOutputDict, guardrailDebugInputDict, guardrailDebugOutputDict);
            }
            //End of calculateGuardrail util function call
            
            frequencyAttribute = getconfigattrvalue(line._parent_doc_number, "liftsPerContainer_s");
            returnStr = returnStr + line._document_number + "~" + "yardsPerMonth_line" + "~" + string(yardsPerMonth) + "|";
            returnStr = returnStr + line._parent_doc_number + "~" + "yardsPerMonth_line" + "~" + string(yardsPerMonth) + "|";
            
            //Calculate Total Monthly Yards - used to set Transaction Code
            //totalMonthlyYardsNew = totalMonthlyYardsNew + yardsPerMonth;
        
        }elif(priceType == SERVICE_CHANGE AND NOT(priceIncreaseLine) AND NOT(closeContainerLine)){
            //ADded 04/13/2014 - Competitor identification pending
            if(competitor_quote == ""){
                competitorFactor = 1.0;
            }
            /*
            //TO DO - Identify how competitor is selected on existing quote and handle accordingly
            for eachRecord in customerTypeFactorRecordSet{
                //Iterate on result set and get competitor adjustment factor
                competitor_db = get(eachRecord, "competitor");
            } 
            */
            
            //get the container type from data table Account_Status
            allocationFactor = 1.0;
            Is_ERF_On_db = "No";
            Is_FRF_On_db = "No";
            if(containskey(allocationFactorDict, wasteType)){
                allocationFactor = get(allocationFactorDict, wasteType);
            }
            
            //Invoke Small Container Pricing once for Current configuration and once for modified/new configuration
            
            //************************** Invoke Small Container Pricing for Current configuration (Existing) ***************************************//
            //Invoke Small Container util for Current configuration (Existing) 
            wasteType_db = get(existingCustDataDict, parentDoc+":wasteType");
                        
            //Get account specific data
            containerType_db = get(existingCustDataDict, parentDoc+":containerType");
            wasteCategory_db = get(existingCustDataDict, parentDoc+":wasteCategory");
            frequency_db = get(existingCustDataDict, parentDoc+":frequency");
            routeTypeDerived_db = get(existingCustDataDict, parentDoc+":routeTypeDerived");
            onsiteTimeInMins_db = get(existingCustDataDict, parentDoc+":onsiteTimeInMins");
            compactor_db = get(existingCustDataDict, parentDoc+":compactor");
            quantity_db = get(existingCustDataDict, parentDoc+":quantity");
            monthlySalesAmt_db = get(existingCustDataDict, parentDoc+":Monthly_Sales_Amt");
            Container_Cnt_db = get(existingCustDataDict, parentDoc+":Container_Cnt");
            Is_FRF_On_db = get(existingCustDataDict, parentDoc+":Is_FRF_On");
            FRF_Pct_db = get(existingCustDataDict, parentDoc+":FRF_Pct");
            Is_ERF_On_db = get(existingCustDataDict, parentDoc+":Is_ERF_On");
            ERF_Pct_db = get(existingCustDataDict, parentDoc+":ERF_Pct");
            current_monthlyRateStr = get(existingCustDataDict, parentDoc+":monthlyRate");
            container_owned_db = get(existingCustDataDict, parentDoc+":container_owned");
            container_category_db = get(existingCustDataDict, parentDoc+":container_category");
            Pickup_Per_Tot_Lifts = get(existingCustDataDict, parentDoc+":Pickup_Per_Tot_Lifts");
            period = get(existingCustDataDict, parentDoc+":period");
            Pickup_Period_Length = get(existingCustDataDict, parentDoc+":Pickup_Period_Length");
            salesActivityConfig = getconfigattrvalue(parentDoc, "salesActivity");
            contract_term = get(existingCustDataDict, parentDoc+":contract_term");
            useCurrentPickupsPerDay_sc = get(existingCustDataDict, parentDoc+":useCurrentPickupsPerDay_sc");

            compactorStr = "false";
            if(compactor_db == "1"){
                compactorStr = "true";
            }
            currentStringDict = dict("string");
            put(currentStringDict, "wasteCategory", wasteCategory_db);  //WasteType in Account_Status table
            put(currentStringDict, "routeTypeDerived", routeTypeDerived_db); 
            put(currentStringDict, "containerQuantity", quantity_db); // editable in config
            put(currentStringDict, "containerSize", get(existingCustDataDict, parentDoc+":containerSize"));
            put(currentStringDict, "frequency", frequency_db); // editable in config
            put(currentStringDict, "compactor", compactorStr);
            put(currentStringDict, "accountType", accountType_quote);
            put(currentStringDict, "division_quote", division_quote);
            put(currentStringDict, "region_quote", region_quote); //get this based on division selected rather than home division
            put(currentStringDict, "partNumber", line._part_number);
            put(currentStringDict, "initialTerm_quote", contract_term); //Use Contract term from customer account here to support legacy contract terms
            put(currentStringDict, "disposalCostPerTon", string(disposalSiteCostCommercial));
            put(currentStringDict, "dsp_xfer_price_per_ton", string(dsp_xfer_priceperton_small));
            put(currentStringDict, "allocationFactor", string(allocationFactor));
            put(currentStringDict, "industry", industry_quote);
            put(currentStringDict, "additionalSmallContainerSiteTime", getconfigattrvalue(line._parent_doc_number, "additionalSmallContainerSiteTime_s"));
            put(currentStringDict, "lock", getconfigattrvalue(line._parent_doc_number, "lock"));
            put(currentStringDict, "isEnclosure", getconfigattrvalue(line._parent_doc_number, "isEnclosure"));
            put(currentStringDict, "rolloutFeet", getconfigattrvalue(line._parent_doc_number, "rolloutFeet"));
            put(currentStringDict, "casters", getconfigattrvalue(line._parent_doc_number, "casters"));
            put(currentStringDict, "scoutRoute", getconfigattrvalue(line._parent_doc_number, "scoutRoute"));
            put(currentStringDict, "isCustomerOwned", container_owned_db);
            put(currentStringDict, "onsiteTimeInMins", getconfigattrvalue(line._parent_doc_number, "onsiteTimeInMins"));
            put(currentStringDict, "salesActivity_quote", salesActivity_quote);
            put(currentStringDict, "newCustomerConfig", string(newCustomerConfig)); //Use this instead of Sales Activity for customer type
            put(currentStringDict, "Pickup_Per_Tot_Lifts", Pickup_Per_Tot_Lifts); 
            put(currentStringDict, "period", period); 
            put(currentStringDict, "cr_new_business", "0"); //This is current business flag
            put(currentStringDict, "salesActivityConfig", salesActivityConfig);
            put(currentStringDict, "Pickup_Period_Length", Pickup_Period_Length);
            put(currentStringDict, "useCurrentPickupsPerDay_sc", useCurrentPickupsPerDay_sc);
            
            /*if(DEBUG){
            print "Input Dict for Service Change for Current Small Container";
            print currentStringDict;
            }*/
            
            currentSmallContainerDict = util.smallContainerPricing(currentStringDict);
            
            pricingDebugInputDict = currentStringDict;  
            pricingDebugOutputDict = currentSmallContainerDict;
            
            //Financial Summary -costs applies only once per container/model and these costs are from existing service
            if(rateType == "Base"){
                if(isnumber(get(currentSmallContainerDict, "cts_month_incl_oh"))){
                    existing_cts_month_incl_oh = atof(get(currentSmallContainerDict, "cts_month_incl_oh")); //This is overall cost/expense
                }
                if(isnumber(get(currentSmallContainerDict, "cost_disp_xfer_proc"))){
                    existing_cost_disp_xfer_proc = atof(get(currentSmallContainerDict, "cost_disp_xfer_proc")); //This is Disposal Expense alone
                }
                //Operating expenses of each container = Overall Expense - Disposal Expense
                existing_operating_expense = existing_cts_month_incl_oh - existing_cost_disp_xfer_proc;
            }
            
            /*if(DEBUG){
                print "Small Container Pricing Current configuration Dict";
                print currentSmallContainerDict;
            }*/
            //End of current small configuration util invocation
            
            costToServe_db = 0.0;
            //Calculate costToServe to use in guard rails calculations
            if(isnumber(get(currentSmallContainerDict, "floor"))){
                costToServe_db = atof(get(currentSmallContainerDict, "floor")); //This returns the cost to serve, which includes the ERF and FRF
            }   
            
            //Multiply costToServe with Allocation Factor for all Small Container line items
            allocationFactor_db = 1.0;
            
            if(containskey(allocationFactorDict, wasteType_db)){
                allocationFactor_db = get(allocationFactorDict, wasteType_db);
                costToServe_db = costToServe_db * allocationFactor_db;
            }
            
            //Capture and return new configuration yardsPerMonth from the smallContainerPricing function
            yardsPerMonth_db = 0.0;
            yardsPerMonthStr_db = get(currentSmallContainerDict, "yardsPerMonth");
            if(isnumber(yardsPerMonthStr_db)){
                yardsPerMonth_db = atof(yardsPerMonthStr_db);
                yardsPerMonth_db = round(yardsPerMonth_db, 4);
            }
            
            //Currently small container only supports Service level - Scheduled
            //if service is scheduled, this must be calculate from frequency
            if(isnumber(quantity_db)){
                estHaulsPerMonthStr = string(atof(frequency_db) * (52.0 / 12.0) * atof(quantity_db));
                haulsPerContainer = string(atof(frequency_db) * (52.0 / 12.0));
            }
            
            serviceChangePriceType = "";
            if(lower(container_category_db) == "commercial"){
                container = "Small Container";
                serviceChangePriceType = "Containers";
            }elif(lower(containerType_db) == "industrial"){
                container = "Large Container";
                serviceChangePriceType = "Large Container";
            }
            
            
            //Call calculateGuardrails util function for current small container configuration
            //Calculate pricing for current configuration here, these values will be used for new configuration
            guardrailCurrentInputDict = dict("string");
            put(guardrailCurrentInputDict, "erfOnFrfRate", "1");
            put(guardrailCurrentInputDict, "siteName", siteName); 
            put(guardrailCurrentInputDict, "wasteType", wasteType_db); 
            put(guardrailCurrentInputDict, "division", division_quote); 
            put(guardrailCurrentInputDict, "costToServeMonth", get(currentSmallContainerDict, "costToServeMonth")); 
            //Uncomment these when Large container is handled for service change
            //Update this section - TO DO 
            /*
            if(containerType_db == "Large"){
                put(guardrailCurrentInputDict, "billingType", billingType);
                put(guardrailCurrentInputDict, "tonsPerHaul", getconfigattrvalue(line._parent_doc_number, "estTonsHaul_l"));
                put(guardrailCurrentInputDict, "driverCost", get(currentSmallContainerDict, "driverCost")); 
                put(guardrailCurrentInputDict, "truckCost", get(currentSmallContainerDict, "truckCost"));
                put(guardrailCurrentInputDict, "truckROA", get(currentSmallContainerDict, "truckROA"));
                put(guardrailCurrentInputDict, "truckDepreciation", get(currentSmallContainerDict, "truckDepreciation"));
            }*/
            put(guardrailCurrentInputDict, "commission", get(currentSmallContainerDict, "commission"));
            put(guardrailCurrentInputDict, "haulsPerMonth", estHaulsPerMonthStr);
            put(guardrailCurrentInputDict, "accountType", accountType_quote);
            put(guardrailCurrentInputDict, "commissionRate", ".75");
            put(guardrailCurrentInputDict, "flatRateCommission", "15");
            put(guardrailCurrentInputDict, "competitorFactor", string(competitorFactor));
            put(guardrailCurrentInputDict, "priceType", serviceChangePriceType);
            put(guardrailCurrentInputDict, "wasteCategory", wasteCategory_db);
            put(guardrailCurrentInputDict, "yardsPerMonth", string(yardsPerMonth_db));
            put(guardrailCurrentInputDict, "salesActivity_quote", salesActivity_quote);
            put(guardrailCurrentInputDict, "salesActivity_config", get(existingCustDataDict, parentDoc+":salesActivity"));
            put(guardrailCurrentInputDict, "priceAdjustmentReason_config", get(existingCustDataDict, parentDoc+":priceAdjustmentReason"));
            put(guardrailCurrentInputDict, "cr_new_business", "0"); //This is old business flag
            put(guardrailCurrentInputDict, "includeERF", Is_ERF_On_db); //Current ERF Flag
            put(guardrailCurrentInputDict, "includeFRF", Is_FRF_On_db); //Current FRF Flag
            put(guardrailCurrentInputDict, "current_ERF_Pct", ERF_Pct_db);
            put(guardrailCurrentInputDict, "current_FRF_Pct", FRF_Pct_db);
            
            put(guardrailCurrentInputDict, "initialTerm_quote", contract_term); //Get contract term from Accounts table to support legacy contract terms
            put(guardrailCurrentInputDict, "customer_zip", _quote_process_siteAddress_quote_zip);
            put(guardrailCurrentInputDict, "segment", segment_quote);
            put(guardrailCurrentInputDict, "industry", industry_quote);
            put(guardrailCurrentInputDict, "quantity", quantity_db);
            put(guardrailCurrentInputDict, "containerType", container);
            put(guardrailCurrentInputDict, "infoProDivNumber", infoproDivision_RO_quote);
            put(guardrailCurrentInputDict, "routeType", routeTypeDerived_db); 
            put(guardrailCurrentInputDict, "customer_id", _quote_process_customer_id);
            
            //20141021 
            containerGroupLine = "";
            if(containskey(containerGroupDict, line._parent_doc_number)){
                containerGroupLine = get(containerGroupDict, line._parent_doc_number);
            }
            if(containerGroupLine == ""){
                containerGroupLine = containerGroupForTransaction_quote;
            }
            put(guardrailCurrentInputDict, "containerGroup", containerGroupLine);
            print "My Container Group for line " + line._document_number + " is: " + containerGroupLine;
            //end 20141021
            
        put(guardrailCurrentInputDict, "newCustomerConfig", string(newCustomerConfig)); //Use this instead of Sales Activity for customer type
            put(guardrailCurrentInputDict, "siteNumber_quote", siteNumber_quote);
            
            /*if(DEBUG){
            print "guardrailCurrentInputDict for Small Container";
            print guardrailCurrentInputDict;
            }*/
            //Jfelberg fees
            put(guardrailCurrentInputDict, "feesToCharge", feesToCharge_quote);
            guardrailCurrentOutputDict = util.calculateGuardrails(guardrailCurrentInputDict);
            
            guardrailDebugInputDict = guardrailCurrentInputDict;
            guardrailDebugOutputDict = guardrailCurrentOutputDict;
            
            /*if(DEBUG){
            print "guardrailCurrentOutputDict for Small Container";
            print guardrailCurrentOutputDict;
            }*/
            
            if(DEBUG){
                //Call the debugger function to map BM variables to RS variables for Current container
                print "BM -> RS variable mapping for Current container";
                str = util.pricingDebugRule(pricingDebugInputDict, pricingDebugOutputDict, guardrailDebugInputDict, guardrailDebugOutputDict);
            }
            
            if(DEBUG){
                print "######################## END OF CURRENT CONFIGURATION ########################";
                print "######################## BEGIN NEW CONFIGURATION INPUT & OUTPUT ########################";
            }
            //End of calculateGuardrail util function call for current small container configuration
            //********* Invoke Small Container Pricing for Current configuration (Existing) ************************//
            
            //******************** Invoke Small Container Pricing for Modified configuration ***************************//
            
            //Invoke Small container util for modified configuration
            wasteType_sc = get(serviceChangeDataDict, parentDoc+":wasteType");
            containerType_sc = get(serviceChangeDataDict, parentDoc+":containerType");
            liftsPerContainer_sc = get(serviceChangeDataDict, parentDoc+":liftsPerContainer");
            quantity_sc = get(serviceChangeDataDict, parentDoc+":quantity");
            containerSize_sc = get(serviceChangeDataDict, parentDoc+":containerSize");
            //frequency_sc = get(serviceChangeDataDict, parentDoc+":frequency");
            frequency_sc = get(serviceChangeDataDict, parentDoc+":frequency");
            wasteCategory_sc = get(serviceChangeDataDict, parentDoc+":wasteCategory");
            routeTypeDerived_sc = get(serviceChangeDataDict, parentDoc+":routeTypeDerived");
            competitiveBidAmountStr = get(serviceChangeDataDict, parentDoc+":competitiveBidAmount");
            
            if(isnull(competitiveBidAmountStr)){
                competitiveBidAmountStr = "0.0";
            }   
            if(containskey(allocationFactorDict, wasteType_sc)){
                allocationFactor = get(allocationFactorDict, wasteType_sc);
            }
            
            if(isnumber(frequency_sc) AND isnumber(quantity_sc)){
                estHaulsPerMonthStr = string(atof(frequency_sc) * (52.0 / 12.0) * atof(quantity_sc));
                haulsPerContainer = string(atof(frequency_sc) * (52.0 / 12.0));
            }
            stringDict = dict("string");
            put(stringDict, "wasteCategory", wasteCategory_sc); 
            put(stringDict, "routeTypeDerived", routeTypeDerived_sc); 
            put(stringDict, "containerQuantity", quantity_sc); // editable in config
            put(stringDict, "containerSize", containerSize_sc); // editable in config
            put(stringDict, "frequency", frequency_sc); // editable in config 
            put(stringDict, "compactor", compactorStr); 
            put(stringDict, "accountType", accountType_quote);
            put(stringDict, "division_quote", division_quote);
            put(stringDict, "region_quote", region_quote); //get this based on division selected rather than home division
            put(stringDict, "partNumber", line._part_number);
            put(stringDict, "initialTerm_quote", string(initialTerm));
            put(stringDict, "disposalCostPerTon", string(disposalSiteCostCommercial));
            put(stringDict, "dsp_xfer_price_per_ton", string(dsp_xfer_priceperton_small));
            put(stringDict, "allocationFactor", string(allocationFactor));
            put(stringDict, "industry", industry_quote);
            put(stringDict, "additionalSmallContainerSiteTime", getconfigattrvalue(line._parent_doc_number, "additionalSmallContainerSiteTime_s"));
            put(stringDict, "lock", getconfigattrvalue(line._parent_doc_number, "lock"));
            put(stringDict, "isEnclosure", getconfigattrvalue(line._parent_doc_number, "isEnclosure"));
            put(stringDict, "rolloutFeet", getconfigattrvalue(line._parent_doc_number, "rolloutFeet"));
            put(stringDict, "casters", getconfigattrvalue(line._parent_doc_number, "casters"));
            put(stringDict, "scoutRoute", getconfigattrvalue(line._parent_doc_number, "scoutRoute"));
            put(stringDict, "isCustomerOwned", container_owned_db);
            put(stringDict, "onsiteTimeInMins", getconfigattrvalue(line._parent_doc_number, "onsiteTimeInMins"));
            put(stringDict, "salesActivity_quote", salesActivity_quote);
            put(stringDict, "newCustomerConfig", string(newCustomerConfig)); //Use this instead of Sales Activity for customer type
            put(stringDict, "cr_new_business", "1"); //This is new business flag
            put(stringDict, "Pickup_Per_Tot_Lifts", Pickup_Per_Tot_Lifts); 
            put(stringDict, "period", period); 
            put(stringDict, "salesActivityConfig", salesActivityConfig);
            put(stringDict, "Pickup_Period_Length", Pickup_Period_Length);
            put(stringDict, "useCurrentPickupsPerDay_sc", useCurrentPickupsPerDay_sc);
            put(stringDict, "current_Quantity", quantity_db); // editable in config
            
            /*if(DEBUG){
                print "Input Dict for Service Change for New Small Container";
                print stringDict;
            }*/
            
            serviceChangeSmallContainerDict = util.smallContainerPricing(stringDict);
            
            pricingDebugInputDict = stringDict; 
            pricingDebugOutputDict = serviceChangeSmallContainerDict;
            
            //End invoke small container util for modified configuration
            /*if(DEBUG){
                print "New Small Container Config";
                print serviceChangeSmallContainerDict;
            }*/
            
            //Calculate costToServe to use in guard rails calculations
            if(isnumber(get(serviceChangeSmallContainerDict, "floor"))){
                costToServe = atof(get(serviceChangeSmallContainerDict, "floor"));  //This returns the cost to serve, which includes the ERF and FRF
            }   
            
            //Financial Summary -costs applies only once per container/model
            if(rateType == "Base"){
                if(isnumber(get(serviceChangeSmallContainerDict, "cts_month_incl_oh"))){
                    cts_month_incl_oh = atof(get(serviceChangeSmallContainerDict, "cts_month_incl_oh")); //This is overall cost/expense
                }
                if(isnumber(get(serviceChangeSmallContainerDict, "cost_disp_xfer_proc"))){
                    cost_disp_xfer_proc = atof(get(serviceChangeSmallContainerDict, "cost_disp_xfer_proc")); //This is Disposal Expense alone
                }
                //Operating expenses of each container = Overall Expense - Disposal Expense
                operating_expense = cts_month_incl_oh - cost_disp_xfer_proc;
            }
            //Multiply costToServe with Allocation Factor for all Small Container line items
            allocationFactor = 1.0;
            
            if(containskey(allocationFactorDict, wasteType_sc)){
                allocationFactor = get(allocationFactorDict, wasteType_sc);
                costToServe = costToServe * allocationFactor;
            }
            
            //Capture and return new configuration yardsPerMonth from the smallContainerPricing function
            yardsPerMonth_sc = 0.0;
            yardsPerMonthStr = get(serviceChangeSmallContainerDict, "yardsPerMonth");
            if(isnumber(yardsPerMonthStr)){
                yardsPerMonth_sc = atof(yardsPerMonthStr);
                yardsPerMonth_sc = round(yardsPerMonth_sc, 4);
            }
            
            //Call calculateGuardrails util function for modified small container configuration
            put(guardrailInputDict, "includeERF", includeERF_quote);
            put(guardrailInputDict, "includeFRF", includeFRF_quote);
            put(guardrailInputDict, "erfOnFrfRate", "1");
            put(guardrailInputDict, "siteName", siteName); 
            put(guardrailInputDict, "wasteType", wasteType_sc); 
            put(guardrailInputDict, "division", division_quote); 
            put(guardrailInputDict, "costToServeMonth", get(serviceChangeSmallContainerDict, "costToServeMonth")); 
            //Uncomment these when Large container is handled for service change
            /*
            if(containerType_db == "Large"){
                put(guardrailInputDict, "billingType", billingType);
                put(guardrailInputDict, "tonsPerHaul", getconfigattrvalue(line._parent_doc_number, "estTonsHaul_l"));
            }*/
            put(guardrailInputDict, "driverCost", get(serviceChangeSmallContainerDict, "driverCost")); 
            put(guardrailInputDict, "truckCost", get(serviceChangeSmallContainerDict, "truckCost"));
            put(guardrailInputDict, "truckROA", get(serviceChangeSmallContainerDict, "truckROA"));
            put(guardrailInputDict, "commission", get(serviceChangeSmallContainerDict, "commission"));
            put(guardrailInputDict, "tonsPerHaul", getconfigattrvalue(line._parent_doc_number, "estTonsHaul_l"));
            put(guardrailInputDict, "haulsPerMonth", estHaulsPerMonthStr);
            put(guardrailInputDict, "accountType", accountType_quote);
            put(guardrailInputDict, "commissionRate", ".75");
            put(guardrailInputDict, "flatRateCommission", "15");
            put(guardrailInputDict, "competitorFactor", string(competitorFactor));
            put(guardrailInputDict, "priceType", serviceChangePriceType);
            put(guardrailInputDict, "wasteCategory", wasteCategory_sc);
            put(guardrailInputDict, "truckDepreciation", get(serviceChangeSmallContainerDict, "truckDepreciation"));
            put(guardrailInputDict, "yardsPerMonth", string(yardsPerMonth_sc));
            put(guardrailInputDict, "salesActivity_quote", salesActivity_quote);
            put(guardrailInputDict, "salesActivity_config", get(existingCustDataDict, parentDoc+":salesActivity"));
            put(guardrailInputDict, "priceAdjustmentReason_config", get(existingCustDataDict, parentDoc+":priceAdjustmentReason"));
            put(guardrailInputDict, "cr_new_business", "1"); //This is new business flag
            put(guardrailInputDict, "current_cost", get(currentSmallContainerDict, "costToServeMonth")); //This is current business cost
            put(guardrailInputDict, "current_floor", get(currentSmallContainerDict,"floor")); //This is current business floor
            put(guardrailInputDict, "current_yardsPerMonth", get(currentSmallContainerDict,"yardsPerMonth")); //This is current business yards per month
            put(guardrailInputDict, "current_basePriceAdj1", get(guardrailCurrentOutputDict,"basePriceAdj1")); //This is current business base adjustment
            put(guardrailInputDict, "competitiveBidAmount", competitiveBidAmountStr);
            put(guardrailInputDict, "customer_id", _quote_process_customer_id);
            put(guardrailInputDict, "current_wasteCategory", wasteCategory_db);
        //20141027          
            put(guardrailInputDict, "containerGroup", containerGroupLine);
        //20141027 end
            put(guardrailInputDict, "initialTerm_quote", string(initialTerm));
            put(guardrailInputDict, "customer_zip", _quote_process_siteAddress_quote_zip);
            put(guardrailInputDict, "segment", segment_quote);
            put(guardrailInputDict, "industry", industry_quote);
            put(guardrailInputDict, "infoProDivNumber", infoproDivision_RO_quote);
            //put(guardrailInputDict, "serviceCode", serviceCode);
            put(guardrailInputDict, "current_containerSize", containerSize_sc);
            put(guardrailInputDict, "quantity", quantity_sc);
            put(guardrailInputDict, "containerType", container);
            put(guardrailInputDict, "routeType", routeTypeDerived_db); 
            put(guardrailInputDict, "newCustomerConfig", string(newCustomerConfig)); //Use this instead of Sales Activity for customer type
            put(guardrailInputDict, "siteNumber_quote", siteNumber_quote);
            //Check if User still has Fixed ERF & FRF or changed to regular fees
            isFRFFixed = false;
            isERFFixed = false;
            if(find(feesToCharge_quote, "Fixed Fuel Recovery Fee (FRF)") > -1){
                isFRFFixed = true;
            }
            if(find(feesToCharge_quote, "Fixed Environment Recovery Fee (ERF)") > -1){
                isERFFixed = true;
            }
            put(guardrailInputDict, "isFRFFixed", string(isFRFFixed));
            put(guardrailInputDict, "isERFFixed", string(isERFFixed));
            
            /*if(DEBUG){
                print "guardrailInputDict for Small Container";
                print guardrailInputDict;
            }*/
            
            //JFelberg Fees
            put(guardrailInputDict, "feesToCharge", feesToCharge_quote);
            //Return actual results to guardrailOutputDict - this dictionary will be used later in the script, so it should have modified/new configuration calculations
            guardrailOutputDict = util.calculateGuardrails(guardrailInputDict);
            
            guardrailDebugInputDict = guardrailInputDict;
            guardrailDebugOutputDict = guardrailOutputDict;
            
            changeType = "";
            if(containskey(guardrailOutputDict, "change_type")){
                changeType = get(guardrailOutputDict, "change_type");
            }
            /*if(DEBUG){
                print "guardrailOutputDict for Small Container";
                print guardrailOutputDict;
            }*/
            
            if(DEBUG){
                //Call the debugger function to map BM variables to RS variables for New container
                print "BM -> RS variable mapping for New container";
                str = util.pricingDebugRule(pricingDebugInputDict, pricingDebugOutputDict, guardrailDebugInputDict, guardrailDebugOutputDict);
            }
            
            //End of calculateGuardrail util function call for modified small container configuration
            //****************************************** Invoke Small Container Pricing for Modified configuration ***************************************//
            
            //Return attributes for external calculations
            
            returnStr = returnStr + line._document_number + "~" + "currentYardsPerMonth_line" + "~" + yardsPerMonthStr_db + "|"
                                + line._document_number + "~" + "yardsPerMonth_line" + "~" + string(yardsPerMonth_sc) + "|"
                                + line._document_number + "~" + "changeType_line" + "~" + changeType + "|"
                                + line._document_number + "~" + "serviceCode_line" + "~" + serviceCode + "|"
                                + line._document_number + "~" + "activity_line" + "~" + get(existingCustDataDict, parentDoc+":salesActivity") + "|"
                                + line._document_number + "~" + "priceAdjustmentReason_line" + "~" + get(existingCustDataDict, parentDoc+":priceAdjustmentReason") + "|"
                                + line._parent_doc_number + "~" + "yardsPerMonth_line" + "~" + string(yardsPerMonth_sc) + "|"
                                + line._parent_doc_number + "~" + "currentYardsPerMonth_line" + "~" + yardsPerMonthStr_db + "|";
            
            //Calculate Total Monthly Yards - used to set Transaction Code
            //totalMonthlyYardsNew = totalMonthlyYardsNew + yardsPerMonth;
        }
        elif(priceType == SERVICE_CHANGE AND (priceIncreaseLine OR closeContainerLine)){
            //Set the activity flag and Competitor information for non-priced existing customer configurations
            //If no competitor was selected on the quote, use the default value New/New to get the default table row
            //competitor = getconfigattrvalue(line._parent_doc_number, "competitor");
            competitor = "new/new";
            customerTypeFactorRecordSet = bmql("SELECT competitor, competitor_factor, Competitor_Cd FROM div_competitor_adj WHERE division = $division_quote");
            for eachRecord in customerTypeFactorRecordSet{
                competitor_db = get(eachRecord, "competitor");
                if(lower(competitor_db) == lower(competitor)){
                    competitorCode = get(eachRecord, "Competitor_Cd");
                    break;
                }
            }
            if(priceIncreaseLine){
                //Get Customer Account Status data that is already added in first model loop to dictionary
                routeTypeDerived = get(existingCustDataDict, parentDoc+":routeTypeDerived");
                acct_nbr = get(existingCustDataDict, parentDoc+":acct_nbr");
                container_category_db = get(existingCustDataDict, parentDoc+":container_category");
                containerType_db = get(existingCustDataDict, parentDoc+":containerType");
                
                serviceChangePriceType = "";
                if(lower(container_category_db) == "commercial"){
                    container = "Small Container";
                    serviceChangePriceType = "Containers";
                    priceType = SMALL_CONTAINER;
                }elif(lower(containerType_db) == "industrial"){
                    container = "Large Container";
                    serviceChangePriceType = "Large Container";
                }
                
                supplementalChargesInputDict = dict("string");
                put(supplementalChargesInputDict, "containerGroup", containerGroupForTransaction_quote);
                put(supplementalChargesInputDict, "infoProDivNumber", infoproDivision_RO_quote);
                put(supplementalChargesInputDict, "siteNumber_quote", siteNumber_quote);
                put(supplementalChargesInputDict, "priceType", serviceChangePriceType);
                put(supplementalChargesInputDict, "division", division_quote); 
                put(supplementalChargesInputDict, "routeTypeDerived", routeTypeDerived); 
                put(supplementalChargesInputDict, "acct_nbr", acct_nbr); 
                
                guardrailOutputDict = util.supplementalCharges(supplementalChargesInputDict);
                
                if(DEBUG){
                    print "supplementalChargesInput for Small Container";
                    print supplementalChargesInputDict;
                    print "supplementalChargesOutput for Small Container";
                    print guardrailOutputDict;
                }
            }

            returnStr = returnStr + "1" + "~" + "competitorCode_quote" + "~" + competitorCode + "|";
                                    
            returnStr = returnStr + line._document_number + "~" + "activity_line" + "~" + get(existingCustDataDict, parentDoc+":salesActivity") + "|";
            returnStr = returnStr + line._document_number + "~" + "priceAdjustmentReason_line" + "~" + get(existingCustDataDict, parentDoc+":priceAdjustmentReason") + "|";
        }
        //=============================== END - Service Change Price Calculation ===============================//  
        
        //=============================== START - Ad-Hoc Part Description ===============================// 
// 20140926 JP: comment out this description as we are doing the same thing in new function adHocConfigToCommerce

        elif(priceType == "Ad-Hoc Line Items"){
            print "------JP in adhoc part description------";
            put(inputDict, "key", "Description");   //Get the value of Description entered by the user from the Config line item comment
            adHocDescription = util.parseThroughLineItemComment(inputDict);
            print adHocDescription;
            returnStr = returnStr + line._document_number + "~" + "adHocDescription_line" + "~" + adHocDescription + "|";
        }



        //=============================== START - PRICING ===============================// 
        
        if(priceType <> "Ad-Hoc Line Items"){   //Only apply guardrail calculations to Small or Large Container configurations
            floorPriceStr = "0.0";
            basePriceStr = "0.0";
            targetPriceStr = "0.0";
            stretchPriceStr = "0.0";
            currentPriceStr = "0.0";
            
            existingWasteCategory = "";
            existingFrfRate = 0.0;
            existingErfRate = 0.0;
            existingAdminRate = 0.0;
            existingFrfFlag = 0;
            existingErfFlag = 0;
            existingAdminFlag = 0;
            existing_Erf_On_Frf_Flag = 0;
            rateTypeLower = lower(rateType);
            
            if(rateTypeLower == "haul" OR rateTypeLower == "flat" OR rateTypeLower == "base"){
                if(containskey(guardrailOutputDict, "haulFloor")){
                    floorPriceStr = get(guardrailOutputDict, "haulFloor");
                }
                if(containskey(guardrailOutputDict, "haulBase")){
                    basePriceStr = get(guardrailOutputDict, "haulBase");
                }
                if(containskey(guardrailOutputDict, "haulTarget")){
                    targetPriceStr = get(guardrailOutputDict, "haulTarget");
                }
                if(containskey(guardrailOutputDict, "haulStretch")){
                    stretchPriceStr = get(guardrailOutputDict, "haulStretch");
                }
                
                //currentPriceStr = get(guardrailCurrentOutputDict, "monthly_rate");
                if(containskey(existingCustDataDict, parentDoc+":monthlyRate")){
                    currentPriceStr = get(existingCustDataDict, parentDoc+":monthlyRate");  
                }
            }
            elif(rateTypeLower == "disposal"){
                if(containskey(guardrailOutputDict, "disposalFloor")){
                    floorPriceStr = get(guardrailOutputDict, "disposalFloor");
                }
                if(containskey(guardrailOutputDict, "disposalBase")){
                    basePriceStr = get(guardrailOutputDict, "disposalBase");
                }
                if(containskey(guardrailOutputDict, "disposalTarget")){
                    targetPriceStr = get(guardrailOutputDict, "disposalTarget");
                }
                if(containskey(guardrailOutputDict, "disposalStretch")){
                    stretchPriceStr = get(guardrailOutputDict, "disposalStretch");
                }
            }
            elif(rateTypeLower == "overage"){
                if(containskey(guardrailOutputDict, "overageFloor")){
                    floorPriceStr = get(guardrailOutputDict, "overageFloor");
                }
                if(containskey(guardrailOutputDict, "overageBase")){
                    basePriceStr = get(guardrailOutputDict, "overageBase");
                }
                if(containskey(guardrailOutputDict, "overageTarget")){
                    targetPriceStr = get(guardrailOutputDict, "overageTarget");
                }
                if(containskey(guardrailOutputDict, "overageStretch")){
                    stretchPriceStr = get(guardrailOutputDict, "overageStretch");
                }
            }
            elif(rateTypeLower == "rental"){
                if(containskey(guardrailOutputDict, "rental_floor")){
                    floorPriceStr = get(guardrailOutputDict, "rental_floor");
                }
                if(containskey(guardrailOutputDict, "rental_base")){
                    basePriceStr = get(guardrailOutputDict, "rental_base");
                }
                if(containskey(guardrailOutputDict, "rental_target")){
                    targetPriceStr = get(guardrailOutputDict, "rental_target");
                }
                if(containskey(guardrailOutputDict, "rental_stretch")){
                    stretchPriceStr = get(guardrailOutputDict, "rental_stretch");
                }
            }elif(rateTypeLower == "delivery"){
                //J. Felberg added the quantity logic
                if(containskey(guardrailOutputDict, "DEL")){
                    floorPriceStr = get(guardrailOutputDict, "deliveryFloor");
                    floorPriceStr = string(atof(floorPriceStr) * line._price_quantity);
                    basePriceStr = get(guardrailOutputDict, "DEL");
                    basePriceStr = string(atof(basePriceStr) * line._price_quantity);
                    targetPriceStr = get(guardrailOutputDict, "DEL");
                    targetPriceStr = string(atof(targetPriceStr) * line._price_quantity);
                    stretchPriceStr = get(guardrailOutputDict, "DEL");
                    stretchPriceStr = string(atof(stretchPriceStr) * line._price_quantity);
                }
                //AQ 11/07/2104
                if(line._part_custom_field9 == "HP"){
                    floorPriceStr = "0.0";
                    basePriceStr = "0.0";
                    targetPriceStr = "0.0";
                    stretchPriceStr = "0.0";
                }
            }
            elif(rateTypeLower == "exchange"){
                put(exchangeExistsDict, line._parent_doc_number, true);
                print "here--"; print exchangeExistsDict;
                if(containskey(guardrailOutputDict, "EXC")){
                    floorPriceStr = get(guardrailOutputDict, "EXC");    
                    basePriceStr = get(guardrailOutputDict, "EXC");
                    targetPriceStr = get(guardrailOutputDict, "EXC");
                    stretchPriceStr = get(guardrailOutputDict, "EXC");
                }
            }elif(rateTypeLower == "removal"){
                if(containskey(guardrailOutputDict, "REM")){
                    floorPriceStr = get(guardrailOutputDict, "REM");    
                    basePriceStr = get(guardrailOutputDict, "REM");
                    targetPriceStr = get(guardrailOutputDict, "REM");
                    stretchPriceStr = get(guardrailOutputDict, "REM");
                }
            }elif(rateTypeLower == "washout"){
                if(containskey(guardrailOutputDict, "WAS")){
                    floorPriceStr = get(guardrailOutputDict, "WAS");    
                    basePriceStr = get(guardrailOutputDict, "WAS");
                    targetPriceStr = get(guardrailOutputDict, "WAS");
                    stretchPriceStr = get(guardrailOutputDict, "WAS");
                }
            }

            floorPrice = 0.0;
            basePrice = 0.0;
            targetPrice = 0.0;
            stretchPrice = 0.0;
            currentPrice = 0.0;
            currentPriceIncludingFees = 0.0;
            
            if(isnumber(floorPriceStr)){
                floorPrice = atof(floorPriceStr);
                floorPrice = round(floorPrice, 2);
            }

            if(isnumber(basePriceStr)){
                basePrice = atof(basePriceStr);
                basePrice = round(basePrice, 2);
            }
            if(isnumber(targetPriceStr)){
                targetPrice = atof(targetPriceStr);
                targetPrice = round(targetPrice, 2);
            }
            if(isnumber(stretchPriceStr)){
                stretchPrice = atof(stretchPriceStr);
                stretchPrice = round(stretchPrice, 2);
            }
            if(isnumber(currentPriceStr)){
                print "is number=="+currentPriceStr;
                currentPrice = atof(currentPriceStr);
                currentPrice = round(currentPrice, 2);
            }
            print "currentPrice";
            print currentPrice;
            
            divisionDeliveryStr = string(line.divisionDelivery_line);
            
            divisionExchangeStr = get(exchangeDict, line._parent_doc_number);
            divisionExtLiftStr = get(extraLiftDict, line._parent_doc_number);
            divisionExtYardStr = get(extraYdDict, line._parent_doc_number);
            
            divisionReloStr = get(relocateDict, line._parent_doc_number);
            divisionRemoveStr = get(removeDict, line._parent_doc_number);
            
            divisionDRYStr = get(dryDict, line._parent_doc_number);
            divisionWASStr = get(washoutDict, line._parent_doc_number);
            
            //User interface Attributes
            divisionExcStr_ui = get(exchangeUIDict, line._parent_doc_number);
            divisionExtLiftStr_ui = get(extraLiftUIDict, line._parent_doc_number);
            divisionExtYdStr_ui = get(extraYdUIDict, line._parent_doc_number);
            
            divisionReloStr_ui = get(relocateUIDict, line._parent_doc_number);
            divisionRemStr_ui = get(removeUIDict, line._parent_doc_number);
            
            divisionDryStr_ui = get(dryUIDict, line._parent_doc_number);
            divisionWashoutStr_ui = get(washoutUIDict, line._parent_doc_number);
            
            print "--divisionReloStr_ui--"; print divisionReloStr_ui;
            
            if(containskey(guardrailOutputDict, "DEL")){
                divisionDeliveryStr = get(guardrailOutputDict, "DEL");
            }
            
            
            if(divisionExchangeStr == divisionExcStr_ui AND containskey(guardrailOutputDict, "EXC")){
                divisionExchangeStr = get(guardrailOutputDict, "EXC");
                divisionExcStr_ui = get(guardrailOutputDict, "EXC");
            }
            if(divisionExtLiftStr == divisionExtLiftStr_ui AND containskey(guardrailOutputDict, "EXT")){
                divisionExtLiftStr = get(guardrailOutputDict, "EXT");
                divisionExtLiftStr_ui = get(guardrailOutputDict, "EXT");
            }
            if(priceType == SMALL_CONTAINER OR container == SMALL_CONTAINER OR priceType == SERVICE_CHANGE){
                //Specific to small container
                if(divisionExtYardStr == divisionExtYdStr_ui AND containskey(guardrailOutputDict, "EXY")){
                    divisionExtYardStr = get(guardrailOutputDict, "EXY");
                    divisionExtYdStr_ui = get(guardrailOutputDict, "EXY");
                }
            }
            
            if(divisionRemoveStr == divisionRemStr_ui AND containskey(guardrailOutputDict, "REM")){
                divisionRemoveStr = get(guardrailOutputDict, "REM");
                divisionRemStr_ui = get(guardrailOutputDict, "REM");
            }
            if(divisionReloStr == divisionReloStr_ui AND containskey(guardrailOutputDict, "REL")){
                divisionReloStr = get(guardrailOutputDict, "REL");
                divisionReloStr_ui = get(guardrailOutputDict, "REL");
            }
            
            if(priceType == LARGE_CONTAINER OR container == LARGE_CONTAINER){

                //Specific to Large container
                if(divisionWASStr == divisionWashoutStr_ui AND containskey(guardrailOutputDict, "WAS")){
                    divisionWASStr = get(guardrailOutputDict, "WAS");
                    divisionWashoutStr_ui = get(guardrailOutputDict, "WAS");
                }

                //divisionDRY is pulled from the division first.  If the division did not set the
                //DRY rate, then set it to 1/2 the haul rate.
                if(rateType == "Haul"){

                    if(containskey(guardrailOutputDict, "DRY")){
                        divisionDRYStr = get(guardrailOutputDict, "DRY");
                        divisionDryStr_ui = divisionDRYStr;
                    }
                    else{
                        divisionDRYStr = string(round(line.sellPrice_line/2.0, 2));
                        divisionDryStr_ui = string(round(line.sellPrice_line/2.0, 2));                            
                    }

                    returnStr = returnStr +  parentDoc + "~" + "divisionDRY_line" + "~" + divisionDRYStr + "|";
                    returnStr = returnStr +  parentDoc + "~" + "divisionDRY_ui_line" + "~" + divisionDryStr_ui + "|";
                }
            }
            
            //Overwrite ERF & FRF rates with guardrail output dict - this is required as existing customer may have a different erf & frf rates
            frfRateStr = get(guardrailOutputDict, "frfRate");
            erfRateStr = get(guardrailOutputDict, "erfRate");
            adminRateStr = get(guardrailOutputDict, "adminAmount");
            erfOnFrfStr = get(guardrailOutputDict, "erfOnFrfFlag");
            
            if(salesActivity_quote == "Existing Customer"){
                isAdminCharged = false;
               /* if(priceType == SMALL_CONTAINER OR container == SMALL_CONTAINER OR priceType == SERVICE_CHANGE){
                    if(containskey(existingCustDataDict, parentDoc+":Is_Admin_On")){
                        isAdminOn = get(existingCustDataDict, parentDoc+":Is_Admin_On");
                        if(isAdminOn == "1" OR (feesToCharge_quote <> "" AND NOT(isnull(feesToCharge_quote)) AND find(feesToCharge_quote, "Admin Fee") > -1 )){
                            isAdminCharged = true;
                        }
                    }
                }else{*/
                    if(feesToCharge_quote <> "" AND NOT(isnull(feesToCharge_quote)) AND find(feesToCharge_quote, "Admin Fee") > -1 ){
                        isAdminCharged = true;
                    }
               // }
                if(NOT(isAdminCharged)){
                    adminRateStr = "0.0";
                }
            }
            

            if(isnumber(frfRateStr)){
                frfRate = atof(frfRateStr);
            }
            print "This is the last frf " + string(frfRate);

            if(isnumber(erfRateStr)){
                erfRate = atof(erfRateStr);
            }
            if(isnumber(adminRateStr)){ //Convert the table result to a float for use in calculations
                if(NOT(isnull(feesToCharge_quote)) AND find(feesToCharge_quote, "Admin Fee") > -1 ){
                    adminRate = atof(adminRateStr);
                }else{
                    adminRate = 0.0;
                }
            }
            
            //Update eRFOnFRF value
            if(isnumber(erfOnFrfStr)){
                eRFOnFRF = atof(erfOnFrfStr);
            }
            
            //Getting FRFRate, ERFRate and FRF and ERF flags  of current existing customer line - These rates and flags will be applied on currentPrice to find the frf amount on currentPrice and ERF Amount on current price and "total fees" on current price
            
            parentDoc = line._parent_doc_number;
            
            if(containskey(existingCustDataDict, parentDoc+":wasteCategory")){
                existingWasteCategory = get(existingCustDataDict, parentDoc+":wasteCategory");
            }   
            if(containskey(existingCustDataDict, parentDoc+":FRF_Pct") AND isnumber(get(existingCustDataDict, parentDoc+":FRF_Pct"))){
                existingFrfRate = atof(get(existingCustDataDict, parentDoc+":FRF_Pct"));
            }
            if(containskey(existingCustDataDict, parentDoc+":ERF_Pct") AND isnumber(get(existingCustDataDict, parentDoc+":ERF_Pct"))){
                existingErfRate = atof(get(existingCustDataDict, parentDoc+":ERF_Pct"));
            }
            if(containskey(existingCustDataDict, parentDoc+":Is_FRF_On")){
                if(get(existingCustDataDict, parentDoc+":Is_FRF_On") == "Yes"){
                    existingFrfFlag = 1; //If true, value will be 1
                }   
            }
            if(containskey(existingCustDataDict, parentDoc+":Is_ERF_On")){
                if(get(existingCustDataDict, parentDoc+":Is_ERF_On") == "Yes"){
                    existingErfFlag = 1; //If true, value will be 1
                }   
            }
            if(containskey(existingCustDataDict, parentDoc+":Is_ERF_On_FRF")){
                if(get(existingCustDataDict, parentDoc+":Is_ERF_On_FRF") == "Yes"){
                    existing_Erf_On_Frf_Flag = 1; //If true, value will be 1
                }   
            }
            if(containskey(existingCustDataDict, parentDoc+":Is_Admin_On")){
                if(get(existingCustDataDict, parentDoc+":Is_Admin_On") == "1"){
                    existingAdminFlag = 1; //If true, value will be 1
                }   
            }
            if(containskey(existingCustDataDict, parentDoc+":Admin_Rate") AND isnumber(get(existingCustDataDict, parentDoc+":Admin_Rate"))){
                existingAdminRate = atof(get(existingCustDataDict, parentDoc+":Admin_Rate"));
            }
            
            existingFRFAmountOnCurrentPrice = currentPrice * (existingFrfFlag * existingFrfRate);
            existingERFAmountOnCurrentPrice =  currentPrice * ((1 + (existing_Erf_On_Frf_Flag * existingFrfFlag * existingFrfRate)) * (existingErfFlag * existingErfRate)); //erfonfrf should be included if erf and erf flags are set to 1
            existingAdminAmount = existingAdminFlag * existingAdminRate;
            
            /***This is the current price (monthly_rate column from Account_Status table) + frf and erf calculated on that current price based on frf, 
                erf flags and frf_pct and erf_pct. We should NOT include adminRate here because adminRate is at quote level and should be added only once 
                per quote, so should not added for every line. adminRate will be added on currentPriceInclFeesQuotelevel value***/
            currentPriceIncludingFees = currentPrice + existingFRFAmountOnCurrentPrice + existingERFAmountOnCurrentPrice;
            
            monthlyTotalDisposalSell = 0.0;
            customerTonsPerMonth = 0.0;
            if(line.rateType_line=="Disposal"){
                if(containskey(outputDict, "customerTonsPerMonth")){
                    if(isnumber(get(outputDict, "customerTonsPerMonth"))){ 
                        customerTonsPerMonth = atof(get(outputDict, "customerTonsPerMonth"));
                    }
                }
                monthlyTotalDisposalSell  = line.sellPrice_line * customerTonsPerMonth;
            }
            
            returnStr = returnStr + line._document_number + "~" + "floorPrice_line" + "~" + string(floorPrice) + "|"
                              + line._document_number + "~" + "basePrice_line" + "~" + string(basePrice) + "|"
                              + line._document_number + "~" + "targetPrice_line" + "~" + string(targetPrice) + "|"
                              + line._document_number + "~" + "stretchPrice_line" + "~" + string(stretchPrice) + "|"
                              + line._document_number + "~" + "totalFloorPrice_line" + "~" + string(floorPrice) + "|"
                              + line._document_number + "~" + "totalBasePrice_line" + "~" + string(basePrice) + "|"
                              + line._document_number + "~" + "totalTargetPrice_line" + "~" + string(targetPrice) + "|"
                              + line._document_number + "~" + "totalStretchPrice_line" + "~" + string(stretchPrice) + "|"
                              + line._document_number + "~" + "monthlyTotalDisposalSell_line" + "~" + string(monthlyTotalDisposalSell) + "|"
                              + line._document_number + "~" + "currentPrice_line" + "~" + string(currentPrice) + "|"
                              + line._document_number + "~" + "currentPriceFRFAmount_line" + "~" + string(existingFRFAmountOnCurrentPrice) + "|"
                              + line._document_number + "~" + "currentPriceERFAmount_line" + "~" + string(existingERFAmountOnCurrentPrice) + "|"
                              + line._document_number + "~" + "currentPriceIncludingFees_line" + "~" + string(currentPriceIncludingFees) + "|"
                              + line._document_number + "~" + "existingWasteCategory_line" + "~" + existingWasteCategory + "|";
                             // + line._document_number + "~" + "existingAdminAmount_line" + "~" + string(existingAdminAmount) + "|";
            
                //Assign Supplemental charges to parent line item
            returnStr = returnStr +  parentDoc + "~" + "divisionExchange_line" + "~" + divisionExchangeStr + "|"
                                + parentDoc + "~" + "divisionExtLift_line" + "~" + divisionExtLiftStr + "|"
                                + parentDoc + "~" + "divisionExtYard_line" + "~" + divisionExtYardStr + "|"
                                + parentDoc + "~" + "divisionWAS_line" + "~" + divisionWASStr + "|"
                                + parentDoc + "~" + "divisionRelo_line" + "~" + divisionReloStr + "|"
                                + parentDoc + "~" + "divisionRemove_line" + "~" + divisionRemoveStr + "|"
                                + parentDoc + "~" + "divisionDelivery_line" + "~" + divisionDeliveryStr + "|"
                                + parentDoc + "~" + "divisionRelo_ui_line" + "~" + divisionReloStr_ui + "|"
                                + parentDoc + "~" + "divisionExchange_ui_line" + "~" + divisionExcStr_ui + "|"
                                + parentDoc + "~" + "divisionExtLift_ui_line" + "~" + divisionExtLiftStr_ui + "|"
                                + parentDoc + "~" + "divisionExtYard_ui_line" + "~" + divisionExtYdStr_ui + "|"
                                + parentDoc + "~" + "divisionRemove_ui_line" + "~" + divisionRemStr_ui + "|"
                                + parentDoc + "~" + "divisionWAS_ui_line" + "~" + divisionWashoutStr_ui + "|"
                                + parentDoc + "~" + "exchangeLineItemExists_line" + "~" + string(get(exchangeExistsDict, parentDoc)) + "|";
                              
            //=============================== END - Assign Guardrails to Outputs ===============================//  
        }
        else{   //For Ad-Hoc parts, the floor, base, target, and stretch are all the value entered by the user
            floorPrice = line._price_unit_price_each;
            basePrice = line._price_unit_price_each;
            targetPrice = line._price_unit_price_each;
            stretchPrice = line._price_unit_price_each;
            
            returnStr = returnStr + line._document_number + "~" + "floorPrice_line" + "~" + string(floorPrice) + "|"
                                  + line._document_number + "~" + "basePrice_line" + "~" + string(basePrice) + "|"
                                  + line._document_number + "~" + "targetPrice_line" + "~" + string(targetPrice) + "|"
                                  + line._document_number + "~" + "stretchPrice_line" + "~" + string(stretchPrice) + "|"
                                  + line._document_number + "~" + "totalFloorPrice_line" + "~" + string(floorPrice) + "|"
                                  + line._document_number + "~" + "totalBasePrice_line" + "~" + string(basePrice) + "|"
                                  + line._document_number + "~" + "totalTargetPrice_line" + "~" + string(targetPrice) + "|"
                                  + line._document_number + "~" + "totalStretchPrice_line" + "~" + string(stretchPrice) + "|";
        }
        
        //Get the value of Occurrence from the Config line item comment
        inputDict = dict("string");
        put(inputDict,"inputStr",lineItemComment);
        put(inputDict,"key","Occurrence");
        put(inputDict,"COMM_VALUE_DELIM",COMM_VALUE_DELIM);
        put(inputDict,"FIELD_DELIM",FIELD_DELIM);

        occurrence = util.parseThroughLineItemComment(inputDict);
        
        
        
        returnStr = returnStr + line._document_number + "~" + "costToServe_line" + "~" + string(costToServe) + "|"
                              + line._document_number + "~" + "isSellPriceDefaultSetCopy_line" + "~" + line.isSellPriceDefaultSet_line + "|"
                              + line._document_number + "~" + "sellPriceTemp_line" + "~" + string(line.sellPrice_line) + "|"
                              + line._document_number + "~" + "frequency_line" + "~" + occurrence + "|"
                              + line._document_number + "~" + "billingType_line" + "~" + occurrence + "|"
                              + line._document_number + "~" + "priceType_line" + "~" + priceType + "|"
                              + line._document_number + "~" + "routeTypeDerived_line" + "~" + routeTypeDervied + "|"
                              + line._document_number + "~" + "costPerMonthIncludingOverhead_line" + "~" + string(cts_month_incl_oh) + "|"
                              + line._document_number + "~" + "disposalExpensePerMonth_line" + "~" + string(cost_disp_xfer_proc) + "|"
                              + line._document_number + "~" + "operatingExpensePerMonth_line" + "~" + string(operating_expense) + "|"
                              + line._document_number + "~" + "existingCostPerMonthIncludingOverhead_line" + "~" + string(existing_cts_month_incl_oh) + "|"
                              + line._document_number + "~" + "existingDisposalExpensePerMonth_line" + "~" + string(existing_cost_disp_xfer_proc) + "|"
                              + line._document_number + "~" + "existingOperatingExpensePerMonth_line" + "~" + string(existing_operating_expense) + "|";


        //=============================== START - Set additional Commerce attributes ===============================//
        //Add values of attributes from the util and from Config to return string for each line. Also create helper attributes for use in formulas
        returnStr = returnStr   + line._document_number + "~" + "wasteType_line" + "~" + wasteType + "|"
                                + line._document_number + "~" + "liftsPerContainer_line" + "~" + frequencyAttribute + "|"
                                + line._document_number + "~" + "wasteCategory_line" + "~" + wasteCategory + "|"
                                + line._document_number + "~" + "containerSize_line" + "~" + getconfigattrvalue(line._parent_doc_number, "chooseContainer") + "|"
                                + line._document_number + "~" + "isSellPriceDefaultSetCopy_line" + "~" + line.isSellPriceDefaultSet_line + "|"
                                + line._document_number + "~" + "sellPriceTemp_line" + "~" + string(line.sellPrice_line) + "|"
                                + line._document_number + "~" + "rateType_line" + "~" + rateType + "|"
                                + line._document_number + "~" + "installationCharge_line" + "~" + string(installationChg) + "|";
                    
        partLineItem = "true";
        //Setting container group for line items - container group number exists only on service change model, for others it will just be null
        containerGrpNum = getconfigattrvalue(line._parent_doc_number, "containerGroup_readOnly");
        returnStr = returnStr + line._document_number + "~" + "containerNumberOfCurrentService_line" + "~" + containerGrpNum + "|"; 
    }
    //Run pricing on model line items
    elif(line._part_number==""){
        returnStr = returnStr + line._document_number + "~" + "containerNumberOfCurrentService_line" + "~" + getconfigattrvalue(line._document_number, "containerGroup_readOnly") + "|";
        returnStr = returnStr + line._document_number + "~" + "routeTypeDerived_line" + "~" + getconfigattrvalue(line._document_number, "routeTypeDervied") + "|";
        returnStr = returnStr + line._document_number + "~" + "wasteType_line" + "~" + getconfigattrvalue(line._document_number, "wasteType") + "|";
        containerCode = "";
        containerType = "";
        wasteType = getconfigattrvalue(line._document_number, "wasteType");
        routeTypeDerived = getconfigattrvalue(line._document_number, "routeTypeDervied");
        
        if(containskey(existingCustDataDict, line._document_number+":containerType")){
            containerType = get(existingCustDataDict, line._document_number+":containerType");
        }
        smallContainer = false;
        if(containerType == "Small"){
            smallContainer = true;
        }
        if(line._model_name == SMALL_CONTAINER OR smallContainer){
            commercialExists = true;
            containerCode = getconfigattrvalue(line._document_number, "routeTypeDervied");
            if(smallContainer){
                routeTypeDerived = get(existingCustDataDict, line._document_number+":routeTypeDerived");
                containerCode = routeTypeDerived;
            }
            //disposalSitecost for Small containers/commercial is a quote level value so needs to be calculated only once.
            if(disposalSiteCostCommercial == 0.0){
                disposalSiteCostCommercialDict = dict("string");
                disposalSiteCostCommercialStr = "";
                dsp_xfer_priceperton_str = "";
                disposalSiteCommercialStr = "";


                siteLatitude = 0.0;
                siteLongitude = 0.0;
                if(isnumber(_quote_process_siteAddress_quote_company_name)) {
                  siteLatitude = atof(_quote_process_siteAddress_quote_company_name);
                }
                if(isnumber(_quote_process_siteAddress_quote_company_name_2)) {
                  siteLongitude = atof(_quote_process_siteAddress_quote_company_name_2);
                }
                // util.getDispSiteFromPolygon(siteLatitude, siteLongitude, infoproDivision_RO_quote, "");
                disposalSiteCostCommercialDict = util.getDispSiteAndCostFromZip(infoproDivision_RO_quote, division_quote, _quote_process_siteAddress_quote_zip, "Solid Waste", "", false);
                    print "disposalSiteCostCommercialDict ";
                    print disposalSiteCostCommercialDict ;
                //disposalSiteCostCommercialDict = util.getDisposalRegionFromCustomerSiteGeocodes(siteLatitude + COMM_VALUE_DELIM + siteLongitude);
                if(containskey(disposalSiteCostCommercialDict, "disposalCost")){
                    disposalSiteCostCommercialStr = get(disposalSiteCostCommercialDict, "disposalCost");
                }
                if(disposalSiteCostCommercialStr <> "" AND isnumber(disposalSiteCostCommercialStr)){
                    disposalSiteCostCommercial = atof(disposalSiteCostCommercialStr);
                }
                returnStr = returnStr + "1~disposalSiteCostCommercial_quote~"+ string(disposalSiteCostCommercial )+ "|";
                if(containskey(disposalSiteCostCommercialDict, "disposal_cd")){
                    disposalSiteCommercialStr = get(disposalSiteCostCommercialDict, "disposal_cd");
                }
                returnStr = returnStr + "1~disposalPolygon~"+ disposalSiteCommercialStr+ "|";
                if(containskey(disposalSiteCostCommercialDict, "dsp_xfer_priceperton")){
                    dsp_xfer_priceperton_str = get(disposalSiteCostCommercialDict, "dsp_xfer_priceperton");
                }
                if(dsp_xfer_priceperton_str <> "" AND isnumber(dsp_xfer_priceperton_str)){
                    dsp_xfer_priceperton_small = atof(dsp_xfer_priceperton_str);
                }
                if(containskey(disposalSiteCostCommercialDict, "is_serviceable")) {
                    is_serviceable = get(disposalSiteCostCommercialDict, "is_serviceable");
                    if(is_serviceable <> "1") {
                    print "err";
                        thisError = "The customer ZIP Code (" + _quote_process_siteAddress_quote_zip + ") is outside of the serviceable disposal area.\n";
                        if(find(errors, thisError) == -1) {
                            errors = errors + thisError ;
                        }
                    }
                }
            }
        }
        elif(line._model_name == LARGE_CONTAINER){
            industrialExists = true;            
            containerCode = getconfigattrvalue(line._document_number, "routeTypeDervied");
            
        }
        returnStr = returnStr + line._document_number + "~" + "routeTypeDerived_line" + "~" + routeTypeDerived + "|";
        returnStr = returnStr + line._document_number + "~" + "wasteType_line" + "~" + wasteType + "|";
    }
    
    returnStr = returnStr + line._document_number + "~" + "isPartLineItem_line" + "~" + partLineItem + "|";
    
    if(salesActivity_quote == "Existing Customer"){
        returnStr = returnStr + "1~" + "includeSampleInvoice_quote" + "~" + "false" + "|";
    }
}

// 20140926 JP: all ad-hoc pre-pricing processing moved to function
//returnStr = returnStr + adHocConfigToCommerce();
//=============================== START - AdHoc ===============================//
adHocDescription = "";
adHocFeeType = "";
adHocRateType = "";
adHocDisplayOnProposal = "false";
adHocExists = false;
adHocPerHaulExists = false;
adHocMonthlyExists = false;
adHocOneTimeExists = false;

for line in line_process{
    if(line._part_custom_field1 == "Ad-Hoc"){
        adHocExists = true;
        docNum = line._document_number;

        inputDict = dict("string");
        put(inputDict,"inputStr",line._line_item_comment);
        put(inputDict,"COMM_VALUE_DELIM",COMM_VALUE_DELIM);
        put(inputDict,"FIELD_DELIM",FIELD_DELIM);

        //Get the values entered by the user from the Config line item comment
        put(inputDict, "key", "Description");
        adHocDescription = util.parseThroughLineItemComment(inputDict);

        put(inputDict, "key", "Occurrence");
        adHocFeeType = util.parseThroughLineItemComment(inputDict);
        
        put(inputDict, "key", "rateType");
        adHocRateType = util.parseThroughLineItemComment(inputDict);
        
        put(inputDict, "key", "DisplayedOnProposal");
        adHocDisplayOnProposal = util.parseThroughLineItemComment(inputDict);
        if(adHocDisplayOnProposal <> "false"){
            adHocDisplayOnProposal = "true";
        }

        if(adHocDisplayOnProposal == "true") {
            if(adHocFeeType == "Monthly")  { adHocMonthlyExists = true; }
            if(adHocFeeType == "Per Haul") { adHocPerHaulExists = true; }
            if(adHocFeeType == "One Time") { adHocOneTimeExists = true; }
        }

        returnStr = returnStr + docNum + "~" + "adHocFeeType_line" + "~" + adHocFeeType + "|"
                              + docNum + "~" + "adHocDescription_line" + "~" + adHocDescription + "|"
                              + docNum + "~" + "rateType_line" + "~" + adHocRateType + "|"
                              + docNum + "~" + "adHocDisplayOnProposal_line" + "~" + adHocDisplayOnProposal + "|";
        
    }
}

returnStr = returnStr + "1~" + "adHocPerHaulExists_quote" + "~" + string(adHocPerHaulExists)+ "|"
                      + "1~" + "adHocMonthlyExists_quote" + "~" + string(adHocMonthlyExists)+ "|"
                      + "1~" + "adHocOneTimeExists_quote" + "~" + string(adHocOneTimeExists)+ "|"
                      + "1~" + "adHocExists_quote" + "~" + string(adHocExists)+ "|";
//=============================== END - AdHoc ===============================//


//=============================== END - Set additional Commerce attributes ===============================//


returnStr = returnStr + "1~" + "industrialExists_quote" + "~" + string(industrialExists)+ "|"
                      + "1~" + "commercialExists_quote" + "~" + string(commercialExists) + "|"
                      + "1~" + "allocationFactorStringHTML_quote" + "~" + (allocationFactorStringHTML) + "|"
                      + "1~" + "existingAdminAmount_quote" + "~" + string(existingAdminAmount) + "|"
                      + "1~" + "smallContainerTentativePickupDaysSelected_quote" + "~" + join(smallContainerPickupDaysArray, "@_@") + "|"
                      + "1~" + "frfRate_quote" + "~" + string(frfRate) + "|"
                      + "1~" + "erfRate_quote" + "~" + string(erfRate) + "|"
                      + "1~" + "adminRate_quote" + "~" + string(adminRate) + "|"
                      + "1~" + "errors_quote" + "~" + errors + "|"
                      + "1~" + "eRFOnFRF_quote" + "~" + string(eRFOnFRF) + "|"
                      + "1~" + "isERFAndFRFChargedOnAdmin_quote" + "~" + isERFAndFRFChargedOnAdmin + "|"
                      + "1~" + "hiddenExisitingTerm" + "~" + string(diffMthRnd) + "|" 
                      + "1~" + "existingTermFlag_quote" + "~" + string(lessThan90Days) + "|";

return returnStr;
