/* Test with this: 23161172
    Purpose: Returns delimited string of disposal sites location, coordinates (latitude & longitude)& pricing
    
    Pending: floorPrice logic
    Sequence: 
    Step 1: Get customer location, division from commerce
    Step 2: Identify the rec items that match the config selections
    Step 3: Get Disposal sites for selected division
    Step 4: Get distance between customer location & each disposal site using Bing Map API (route) services - Util function
    Step 5: Calculate Pricing for each rec item, for each disposal site
    Step 6: Form a delimited string to prepare input for the Disposal sites Array set
    
    Change Log:
    20141202  J Felberg     - Replaced estHaulsPerMonth_l with totalEstimatedHaulsMonth_l
    20150327  J Palubinskas - #449 remove check for new from competitor
    
*/

//Begin of Rec Items logic
returnLocations = 0; 

res = "";
totalRateDict = dict("string[]");
RStotalRateDict = dict("string[]");
unitOfTravelDuration = "";
travelDuration = 0.0;
travelDistance = 0.0;
distanceUnit= "";
VALUE_DELIM = "^_^";
RECDELIM = "@_@";
COLDELIM = "#_#";
decimalPlaces = 2;
containerCustomerOwned = 0;
compactorCustomerOwned = 0;
DEBUG = false;
if(isCustomerOwned){
    containerCustomerOwned = 1;
}
if(customerOwnedCompactor){
    compactorCustomerOwned = 1;
}

partsArray = string[];
compactorStr = "0";

if(containerType_l == "Open Top"){ //Open top implies no compactor
    compactorStr = "0";
}elif(containerType_l == "Self-Contained Container"){ //Self-contained and stationary imply compactor exists, so should be set to 1.
    compactorStr = "1";
}elif(containerType_l == "Stationary Compactor"){
    compactorStr = "1";
}

//Set alloc_rental flag - Rental
//Include Rental flags in large container pricing calculations - Added 02/06/2014
alloc_rental = 0;
if(containerType_l == "Open Top" AND billingType_l == "Haul + Disposal" AND (rental == "Monthly" OR rental == "Daily")){
    alloc_rental = 1;
}

partsDict = dict("dict<string>");

partsRecordSet = bmql("SELECT part_number, custom_field5, custom_field9, custom_field10, custom_field11, custom_field12, custom_field15, custom_field18, price FROM _parts WHERE custom_field9 = $routeTypeDervied AND custom_field10 = $equipmentSize_l AND custom_field11 = $compactorStr AND  custom_field12 = $lOBCategoryDerived AND custom_field13 = 'Y'"); //Added custom field 13 - Case Number 00179857
qty = quantity;
for eachRec in partsRecordSet{
    partNum = get(eachRec, "part_number");
    partInfoDict = dict("string");
    put(partInfoDict, "custom_field9", get(eachRec, "custom_field9"));
    put(partInfoDict, "custom_field10", get(eachRec, "custom_field10"));
    put(partInfoDict, "custom_field11", get(eachRec, "custom_field11"));
    put(partInfoDict, "custom_field12", get(eachRec, "custom_field12"));
    put(partInfoDict, "custom_field5", get(eachRec, "custom_field5"));
    put(partInfoDict, "custom_field15", get(eachRec, "custom_field15"));
    put(partInfoDict, "custom_field18", get(eachRec, "custom_field18"));
    put(partInfoDict, "price", get(eachRec, "price"));
    put(partsDict, partNum, partInfoDict);
    append(partsArray, partNum);
}
if(DEBUG){
    print partsArray;
}
//End of Rec Items logic

//Begin Disposal locations logic
//Get the supplier company name from commerce
supplierCompany = "";
industry_quote = "";
salesActivity_quote = "";
new_quote = 0;
resultset = bmql("SELECT supplierCompanyName_quote, industry_quote, salesActivity_quote FROM commerce.quote_process");
for result in resultset{
    supplierCompany = get(result, "supplierCompanyName_quote");
    industry_quote = get(result, "industry_quote");
    salesActivity_quote = get(result, "salesActivity_quote");
}
if(lower(salesActivity_quote) == "new/new"){
    new_quote = 1;
} 

//Get division-dependent factors
//If the current division is not found in the table or does not have a value listed, use the corporate standard (division 0) values
//Orders by division to get the division values first. If these values are unavailable, overwrite with the corporate values
resultset = bmql("SELECT Division, Cust_Site_Time, Speed_Adjust_Factor FROM Div_Lg_Cont_Factors WHERE Division = $division_config OR Division = '0' ORDER BY Division DESC");

Cust_Site_TimeStr = "";
Speed_Adjust_FactorStr = "";

for result in resultset{
    if(Cust_Site_TimeStr == ""){
        Cust_Site_TimeStr = get(result, "Cust_Site_Time");
    }
    if(Speed_Adjust_FactorStr == ""){
        Speed_Adjust_FactorStr = get(result, "Speed_Adjust_Factor");
    }
}

Cust_Site_Time_db = 0.0;
Speed_Adjust_Factor = 1.0;

if(isnumber(Cust_Site_TimeStr)){
    Cust_Site_Time_db = atof(Cust_Site_TimeStr);
}
if(isnumber(Speed_Adjust_FactorStr)){
    Speed_Adjust_Factor = atof(Speed_Adjust_FactorStr);
}

//Get the Bing Maps URL, key
url = "";
key = "";
resultset = bmql("SELECT site, url, key FROM maps WHERE site = $supplierCompany");
for result in resultset{
    url = get(result, "url");
    key = get(result, "key");
}

//Get list of all available disposal sites - filter records based on division & waste type if available
isWasteTypeNotNull = false;
isDivisionNotNull = false;
if(division_config <> ""){
    isDivisionNotNull = true;
}
if(wasteType <> ""){
    isWasteTypeNotNull = true;
}
resultset = bmql("SELECT Disposal_Site_Cd, Site_Name, DisposalSite_DivNbr, WasteType, Latitude, Longitude, Extra_On_Site_Mins, is_RSG_owned FROM Disposal_Sites WHERE ($isDivisionNotNull AND DisposalSite_DivNbr = $division_config) AND ($isWasteTypeNotNull AND WasteType = $wasteType)");

//print resultset;
//For each record in the resultset, get the latitude, longitude and their distance/ duration

returnArray = string[];
keysArray = Float[];
rsgKeysArray = Float[];
disposalSiteSequence = 1;
for result in resultset{
    Latitude = get(result, "Latitude");
    Longitude = get(result, "Longitude");
    Site_Name = get(result, "Site_Name");
    Extra_On_Site_Mins = 0.0;
    Cust_Site_Time = 0.0;
    if(isnumber(get(result, "Extra_On_Site_Mins"))){
        Extra_On_Site_Mins = atof(get(result, "Extra_On_Site_Mins"));
    }
    is_RSG_owned = get(result, "is_RSG_owned");
    //Query Bing Maps to get the distance of this Disposal site from Customer site
    if(isnumber(Latitude) AND isnumber(Longitude)){
        returnLocations = returnLocations + 1;
        coordinatesDict = dict("string");
        put(coordinatesDict, "latitude1", latitudeCustomerSite);
        put(coordinatesDict, "longitude1", longitudeCustomerSite);
        put(coordinatesDict, "latitude2", Latitude);
        put(coordinatesDict, "longitude2", Longitude);
        //Get distance/duration between current location and customer location
        outputFromService =  util.getDistanceBetween2Locations(coordinatesDict);
        
        durationArr = util.getValueFromXMLNodeByTag(outputFromService, "TravelDuration");
        durationUnitArr = util.getValueFromXMLNodeByTag(outputFromService, "DurationUnit");
        
        distanceArr = util.getValueFromXMLNodeByTag(outputFromService, "TravelDistance");
        distanceUnitArr = util.getValueFromXMLNodeByTag(outputFromService, "DistanceUnit");
        
        //Get Duration 
        if(sizeofarray(durationArr) > 0){
            if(isnumber(durationArr[0])){
                travelDuration = atof(durationArr[0]);
            }   
        }
        if(sizeofarray(durationUnitArr) > 0){
            unitOfTravelDuration = durationUnitArr[0];
        }
        
        //Get Distance
        if(sizeofarray(distanceArr) > 0){
            if(isnumber(distanceArr[0])){
                travelDistance = atof(distanceArr[0]);
            }   
        }
        if(sizeofarray(distanceUnitArr) > 0){
            distanceUnit = distanceUnitArr[0];
        }
        
        //print "--unitOfTravelDuration--"; print unitOfTravelDuration;
        if(unitOfTravelDuration == "Second"){
            travelDuration = travelDuration / 60;
            travelDuration = round(travelDuration, 2);
        }
        
        //Convert Distance from Kilometer to Miles
        if(distanceUnit == "Kilometer"){
            //travelDistance = travelDistance * 0.621371; //use 0.62136995 for mile statute
            travelDistance = travelDistance * 0.621371192;
            distanceUnit = "Miles";
        }
        //print "Latitude="+Latitude;
        //print "Longitude="+Longitude;
        
        compactorAdditionalSiteTime = 0.0;
        
        for eachPart in partsArray{
            if(containskey(partsDict, eachPart)){
                partInfoDict = get(partsDict, eachPart);
                if(containskey(partInfoDict, "custom_field5")){
                    compactorAdditionalSiteTimeStr = get(partInfoDict, "custom_field5");
                    if(isnumber(compactorAdditionalSiteTimeStr)){
                        compactorAdditionalSiteTime = atof(compactorAdditionalSiteTimeStr);
                    }
                }
            }
        }
        
        timeFactors = bmql("select Factor from Site_Char_Factors where SiteCharacteristic = 'Sign Paperwork'");

        // Set the following from the timeFactors query results
        additionalPaperworkTimeStr = "";
        disposalTicketSignatureTimeStr = "";
        additionalPaperworkTime = 0.0;
        disposalTicketSignatureTime = 0.0;
        //print timeFactors;
        for each in timeFactors{
            additionalPaperworkTimeStr = get(each, "Factor");
            disposalTicketSignatureTimeStr = get(each, "Factor");
        }
        if(isnumber(additionalPaperworkTimeStr)){
            additionalPaperworkTime = atof(additionalPaperworkTimeStr);
        }
        if(isnumber(disposalTicketSignatureTimeStr)){
            disposalTicketSignatureTime = atof(disposalTicketSignatureTimeStr);
        }

        // Calculate Total Additional Time
        additionalSiteFactorTime = 0;

        if(additionalPaperwork_l) {
            additionalSiteFactorTime = additionalSiteFactorTime + additionalPaperworkTime;
        }
        if(disposalTicketSignature_l) {
            additionalSiteFactorTime = additionalSiteFactorTime + disposalTicketSignatureTime;
        }
        
        //Add the additional site time - to be displayed to the user
        Cust_Site_Time = Cust_Site_Time_db + compactorAdditionalSiteTime + additionalSiteFactorTime;
    
        //totalAdditionalSiteTime = addSiteTime_l + additionalSiteFactorTime_l + compactorAdditionalSiteTime + Extra_On_Site_Mins;
        //New Wave 4 total additional time
        totalAdditionalSiteTime = Cust_Site_Time + (Extra_On_Site_Mins * new_quote);
        
        if(Speed_Adjust_Factor <> 0.0){
            roundTripDriveTime = (travelDuration * 2) / Speed_Adjust_Factor;
        }
        else{
            roundTripDriveTime = (travelDuration * 2);
        }

        totalTime = roundTripDriveTime + totalAdditionalSiteTime;
        
        landFillCode = get(result, "Disposal_Site_Cd");
        
        owner = "";
        if(is_RSG_owned == "0"){
            owner = "OTH";
        }elif(is_RSG_owned == "1"){
            owner = "RSG";
        }
        
        /*
        Call large container pricing function for each array index
        */
        //Begin Update this section to calculate haulRate, disposalRate, flatRate & rentalRate 

        floorPrice = 0.0; //for each disposal site, calculate floor price-  To call Zach's floor price logic here - We may not need this!
        
        haulRate = 0.0; //haul
        disposalRate = 0.0; //disposal
        flatRate = 0.0; //haul + disposal
        rentalRate = 0.0; //haul + disposal
        totalRate = 0.0;
        overageRate = 0.0;
        disposalRateArray = string[]; //calculated by pricing util
        haulRateArray = string[]; //calculated by pricing util
        
        for eachPart in partsArray{
            if(eachPart <> ""){
                
                arrayDelimiter = "@@";
                stringDict = dict("string");
                put(stringDict, "wasteCategory", wasteCategory);
                put(stringDict, "siteName", Site_Name);
                put(stringDict, "wasteType", wasteType); 
                put(stringDict, "LOB", lOBCategoryDerived);
                put(stringDict, "arrayDelimiter", arrayDelimiter);
                put(stringDict, "routeType", routeTypeDervied); 
                
                /*hauls_per_period = 0.0;
                for each in frequencyConversionResultSet{
                    if(isnumber(get(each, "conversionFactor"))){
                        hauls_per_period = getFloat(each, "conversionFactor");
                    }
                }*/
                
                put(stringDict, "frequency", frequency); //Frequency should be the same as Hauls per Period as frequency is clubbed in Hauls Per Period
                put(stringDict, "haulsPerPeriod", frequency); 
                put(stringDict, "containerQuantity", string(quantity));
                frequencyFloat = 0.0;
                if(isnumber(frequency)){
                    frequencyFloat = atof(frequency);
                }
                
                //Service is on-call, estHaulsPerMonthStr is user input
                estHaulsPerMonthStr = "0.0";
                haulsPerContainer = "0.0";
                if(serviceRequirement <> "On-call"){ //if service is scheduled, this must be calculate from frequency
                    estHaulsPerMonthStr = string(atof(frequency) * (52.0 / 12.0) * quantity);
                    haulsPerContainer = string(atof(frequency) * (52.0 / 12.0));
                }
                else{
                    //12-2-2014 changes J. Felberg
                    estHaulsPerMonthStr = string(totalEstimatedHaulsMonth_l * quantity);
                    haulsPerContainer = string(totalEstimatedHaulsMonth_l);
                }
                    
                if(billingType_l == "Rental"){
                    estHaulsPerMonthStr = "0.0";
                    haulsPerContainer = "0.0";
                }
                put(stringDict, "estHaulsPerMonth", estHaulsPerMonthStr); 
                put(stringDict, "haulsPerContainer", haulsPerContainer); 
            
                tonsPerHaul = estTonsHaul_l;
                if(billingType_l == "Flat Rate + Overage"){
                    tonsPerHaul = tonsIncludedInHaulRate_l;
                }
                elif(billingType_l == "Haul + Minimum Tonnage"){
                    tempArray = float[];
                    append(tempArray,estTonsHaul_l); 
                    append(tempArray,minimumTonshaul_l); 
                    if(sizeofarray(tempArray) > 0){
                        tonsPerHaul = max(tempArray);
                    }
                }
                put(stringDict, "estTonsPerHaul", string(tonsPerHaul));
                
                put(stringDict, "totalTimePerHaul", string(totalTime)); 
                                
                //If Alternate site match with the disposal site array index then consider override total time
                isSiteTimeOverridden = false;
                if(disposalSiteSequence == atoi(alternateSite_l)){
                    if(adjustedTotalTime_l > 0 AND selectedSiteTotalTime_l > 0 AND adjustedTotalTime_l > selectedSiteTotalTime_l){
                        if(alternateSite_l == prevDesiredDisposalSite_l){
                            put(stringDict, "totalTimePerHaul", string(adjustedTotalTime_l));                       
                            isSiteTimeOverridden = true;
                        }
                    }   
                }
                
                put(stringDict, "region_quote", region);
                put(stringDict, "division_quote", division_config); 
                put(stringDict, "partNumber", eachPart);
                put(stringDict, "initialTerm_quote", initialTerm_l);
                put(stringDict, "billingType_l", billingType_l);
                put(stringDict, "isContainerCustomerOwned", string(containerCustomerOwned));
                put(stringDict, "isCompactorCustomerOwned", string(compactorCustomerOwned));
                put(stringDict, "salesActivity_quote", salesActivity_config);
                put(stringDict, "compactorValueConfig", string(compactorValue));
                
                //Single value for stationary or self-container compactor
                /*
                //this code moved to util
                //For Existing customer, compactorValue will come from Parts DB
                if(salesActivity_config == "Service change" OR salesActivity_config == ""){
                    if(containskey(partsDict, eachPart)){
                        partInfoDict = get(partsDict, eachPart);
                        if(containskey(partInfoDict, "custom_field18")){
                            compactor_Value = atof(get(partInfoDict, "custom_field18"));
                        }
                    }   
                }*/
                put(stringDict, "compactorValue", string(compactorValue));
                
                //Get Compactor Life from Parts DB
                compactorLife = 0.0;
                if(containskey(partsDict, eachPart)){
                    partInfoDict = get(partsDict, eachPart);
                    if(containskey(partInfoDict, "custom_field15")){
                        
                        custom_field15 = get(partInfoDict, "custom_field15");
                        if(isnumber(custom_field15)){
                            compactorLife = atof(custom_field15);
                        }
                    }
                }   
                put(stringDict, "compactorLife", string(compactorLife));    
                
                //Include Rental flags in large container pricing calculations - Added 02/06/2014
                if(rental <> "None" AND rental <> ""){
                    put(stringDict, "alloc_rental", string(alloc_rental));  
                    put(stringDict, "rental", rental);  
                }
                put(stringDict, "industry", industry_quote);
                put(stringDict, "containerType_l", containerType_l);
                

                pricingDict = util.largeContainerPricing(stringDict);
                
                /* Make sure overriding total time not to sort the disposal sites actual order which is based on total rate*/
                /* Get the new rate based on overridden time */
                actualTimePricingDict = dict("string");
                totalRateForSorting = 0.0;
                if(isSiteTimeOverridden){
                    temphaulRate = 0.0; tempdisposalRate = 0.0;
                    //put(stringDict, "totalTimePerHaul", string(totalTime)); 
                    put(stringDict, "totalTimePerHaul", string(adjustedTotalTime_l)); 
                    actualTimePricingDict = util.largeContainerPricing(stringDict);
                    if(containskey(actualTimePricingDict, "disposal")){
                        disposalRateStr =  get(pricingDict, "disposal"); //Array for all disposal sites
                        if(isnumber(disposalRateStr)){
                            tempdisposalRate = atof(disposalRateStr);   
                        }
                    }
                    if(containskey(actualTimePricingDict, "haul")){
                        haulRateStr =  get(actualTimePricingDict, "haul"); //Array for all disposal sites
                        if(isnumber(haulRateStr)){
                            temphaulRate = atof(haulRateStr);   
                        }
                    }
                    /*
                    if(billingType_l == "Haul + Disposal"){
                        totalRateForSorting = temphaulRate + tempdisposalRate;
                    }elif(billingType_l == "Flat Rate"){
                        totalRateForSorting = temphaulRate + tempdisposalRate;
                    }elif(billingType_l == "Rental"){
                        totalRateForSorting = temphaulRate + tempdisposalRate;
                    }*/ 
                    //Use the actual total rate for sorting when total time is overridden
                    if(billingType_l <> "None"){
                        totalRateForSorting = totalRate;
                    }
                }
                /* Make sure overriding total time not to sort the disposal sites actual order which is based on total rate*/
                if(DEBUG){
                    print pricingDict;
                }
                haulRateStr = "";
                disposalRateStr = "";
                
                if(containskey(pricingDict, "disposal")){
                    disposalRateStr =  get(pricingDict, "disposal"); 
                    if(isnumber(disposalRateStr)){
                        disposalRate = atof(disposalRateStr);   
                    }
                }
                if(containskey(pricingDict, "haul")){
                    haulRateStr =  get(pricingDict, "haul"); 
                    if(isnumber(haulRateStr)){
                        haulRate = atof(haulRateStr);   
                    }
                }
                if(billingType_l == "Haul + Disposal" OR billingType_l == "Haul + Minimum Tonnage"){
                    totalRate = haulRate + disposalRate;
                }
                elif(billingType_l == "Flat Rate"){
                    flatRate = haulRate + disposalRate;
                    haulRate = 0.0;
                    disposalRate = 0.0;
                    totalRate = flatRate;
                }
                elif(billingType_l == "Flat Rate + Overage"){
                    flatRate = haulRate + disposalRate;
                    totalRate = flatRate;
                    haulRate = 0.0;
                    disposalRate = 0.0;
                    if(containskey(pricingDict, "disposalCostPerTon")){
                    disposalRatePerTonStr = get(pricingDict, "disposalCostPerTon");
                        if(isnumber(disposalRatePerTonStr)){
                            overageRate = atof(disposalRatePerTonStr);
                        }
                    }
                }
                if(rental <> "None" AND rental <> ""){
                    rentalRateStr = "0.0";
                    rentalRate = 0.0;
                    if(containskey(pricingDict, "rental_floor")){
                        rentalRateStr =  get(pricingDict, "rental_floor"); 
                        if(isnumber(rentalRateStr)){
                            rentalRate = atof(rentalRateStr);
                        }
                    }
                    haulRate = haulRate;
                    disposalRate = 0.0;
                    //totalRate = rentalRate + haulRate;
                    totalRate = totalRate - disposalRate + rentalRate;
                }
                
                delimLabelArray = string[]{"longitude", "latitude", "duration", "distance", "owner", "landFillCode", "totalAdditionalSiteTime", "totalTime", "Site_Name", "haulRate", "disposalRate", "rentalRate", "flatRate", "totalRate", "twoWayTravelDuration", "overage", "custSiteTime", "roundTripDriveTime", "dispTime"};
                delimValuesArray = string[]{Longitude, Latitude, string(round(travelDuration,0)), string(round(travelDistance,0)), owner, landFillCode, string(round(totalAdditionalSiteTime,0)), string(round(totalTime,0)), Site_Name, string(round(haulRate,decimalPlaces)), string(round(disposalRate, decimalPlaces)), string(round(rentalRate, decimalPlaces)), string(round(flatRate, decimalPlaces)), string(round(totalRate,decimalPlaces)), string(round(travelDuration *2, 0)), string(round(overageRate,decimalPlaces)), string(round(Cust_Site_Time, 0)), string(round(roundTripDriveTime, 0)), string(round(Extra_On_Site_Mins,0))};
                delimArray = string[];
                counter = 0;
                for each in delimLabelArray{
                    delimStr = delimLabelArray[counter] + VALUE_DELIM + delimValuesArray[counter];  
                    append(delimArray, delimStr);
                    counter = counter + 1;
                }
                //Form a delimited string of location data
                thisRecord = join(delimArray, COLDELIM);
        
                tempArray = string[];
                thisKey = totalRate;
                
                //Get back to original total rate if total time is overridden, to preserve sort order
                if(totalRateForSorting > 0){
                    thisKey = totalRateForSorting;
                }
                
                if(lower(owner) == "rsg"){
                    if(findinarray(rsgKeysArray, thisKey) == -1){
                        append(rsgKeysArray, thisKey);
                    }
                    if(containskey(RStotalRateDict, string(thisKey))){
                        tempArray = get(RStotalRateDict, string(thisKey));
                    }
                    append(tempArray, thisRecord);  
                    put(RStotalRateDict, string(thisKey), tempArray);           
                }else{
                    if(findinarray(keysArray, thisKey) == -1){
                        append(keysArray, thisKey);
                    }
                    if(containskey(totalRateDict, string(thisKey))){
                        tempArray = get(totalRateDict, string(thisKey));
                    }
                    append(tempArray, thisRecord);  
                    put(totalRateDict, string(thisKey), tempArray);     
                }
                append(returnArray, thisRecord);
            
            }//Part non empty
        }//Each Part
        disposalSiteSequence = disposalSiteSequence + 1;
        //End Update this section to calculate haulRate, disposalRate, flatRate & rentalRate 
    }//If disposal site has latitude & longitude
    
}


if(sizeofarray(returnArray) > 0){
    //Sort Republic Services disposal sites
    //Begin sorting based on total Rate
    //Sort total rate (keys) in ascending order to move cheapest locations to the top of the array
    keysArray = sort(keysArray, "asc");
    rsgKeysArray = sort(rsgKeysArray, "asc");
    if(DEBUG){
        print "keys sorted on total rates";
        print "keysArray";
        print keysArray;
        print "rsgKeysArray";
        print rsgKeysArray;
    }
    newResultArray = string[];
    rsSitesArray = string[];
    nonRSSitesArray = string[];
    counter = 1;
    
    //Iterate on the keys to get the # of RS returnLocations data
    for eachKey in rsgKeysArray{
        tempArray = string[];
        if(containskey(RStotalRateDict, string(eachKey))){
            tempArray = get(RStotalRateDict, string(eachKey));
            if(sizeofarray(tempArray) > 0){
                for each in tempArray{
                    if(each <> "" /*AND counter < returnLocations*/){
                        append(rsSitesArray, each);
                        counter = counter + 1;
                    }
                }
            }
        }
    }
    //Non RS Sites 
    for eachKey in keysArray{
        tempArray = string[];
        if(containskey(totalRateDict, string(eachKey))){
            tempArray = get(totalRateDict, string(eachKey));
            if(sizeofarray(tempArray) > 0){
                for each in tempArray{
                    if(each <> "" /*AND counter < returnLocations*/){
                        append(nonRSSitesArray, each);
                        counter = counter + 1;
                    }
                }
            }
        }
    }
    //End sorting
    
    //Append sorted RS & Non RS sites together making sure all RS Sites are on top
    for site in rsSitesArray{
        append(newResultArray, site);
    }
    for site in nonRSSitesArray{
        append(newResultArray, site);
    }
    //Ends combining all sites
    
    if(sizeofarray(newResultArray) > 0){
        res = join(newResultArray, RECDELIM);
    }
    res = res + RECDELIM + "returnLocations" + COLDELIM + string(returnLocations);
}

return res;