retStr  = "";
VALUE_DELIM = "^_^";
ATTR_DELIM = "@_@";

compactorStr = "0";
if(containerType_l == "Open Top"){ //Open top implies no compactor
	compactorStr = "0";
}elif(containerType_l == "Self-Contained Container"){ //Self-contained and stationary imply compactor exists, so should be set to 1.
	compactorStr = "1";
}elif(containerType_l == "Stationary Compactor"){
	compactorStr = "1";
}

haulRate = "";
rentalRate = "";
flatRate = "";
disposalRate = "";
siteName = "";
rateType = "RATE_TYPE";
totalTime = "";
overageRate = "";
PRICE_STR = "PRICE_STR";
OCCURRENCE_STR = "OCCURRENCE_STR";
if(alternateSite_l <> "" AND isnumber(alternateSite_l)){
	alternateSite = atoi(alternateSite_l) -1;
	haulRate = string(haulRate_disposalSite[alternateSite]);
	rentalRate = string(rentalRate_disposalSite[alternateSite]);
	flatRate = string(flatRate_disposalSite[alternateSite]);
	disposalRate = string(disposalRate_disposalSite[alternateSite]);
	siteName = site_disposalSite[alternateSite];
	totalTime = string(totalTime_disposalSite[alternateSite]);
	overageRate = string(overageRate_disposalSite[alternateSite]);
}

partsRecordSet = bmql("SELECT part_number FROM _parts WHERE custom_field9 = $routeTypeDervied AND custom_field10 = $equipmentSize_l AND custom_field11 = $compactorStr AND  custom_field12 = $lOBCategoryDerived AND custom_field13 = 'Y'"); //Added Custom Field 13 filter based on Case Number 00179857
qty = quantity;
unknownBillingType = false;

priceDict = dict("string");
put(priceDict, "Haul", haulRate);
put(priceDict, "Disposal", disposalRate);
put(priceDict, "Flat", flatRate);
put(priceDict, "Rental", rentalRate);
put(priceDict, "Overage", overageRate);

for eachRec in partsRecordSet{
	
	partNum = get(eachRec, "part_number");
	
	currentPartsString = partNum + "~" + string(qty) + "~";
	commentStr = "";
	
	commentLabelArray = string[]{"rateType", "haulRate", "disposalRate", "flatRate", "rentalRate", "siteName", "totalTime", "Occurrence", "overageRate"};
	commentValueArray = string[]{rateType, haulRate, disposalRate, flatRate, rentalRate, siteName, totalTime, OCCURRENCE_STR, overageRate};
	itemArray = string[];
	i = 0;
	for eachItem in commentLabelArray{
		append(itemArray, commentLabelArray[i] + VALUE_DELIM + commentValueArray[i]); 	
		i = i + 1;
	}
	commentStr = join(itemArray,ATTR_DELIM); 
	
	
	if(billingType_l == "Haul + Disposal" OR billingType_l == "Haul + Minimum Tonnage"){
		rateTypeArray = string[]{"Haul", "Disposal"};
		thisPart =  partNum + "~" + string(qty) + "~" + commentStr + "~" + PRICE_STR + "~" + "identifier|^|";
		
		thisPartStr = "";
		i = 0;
		for eachRate in rateTypeArray{
			thisPartStr = replace(replace(replace(thisPart, rateType, eachRate),  "identifier", "identifier" + string(i+1)),PRICE_STR, get(priceDict, eachRate));
			if(eachRate == "Haul"){
				thisPartStr = replace(thisPartStr, OCCURRENCE_STR, "Per Haul");
			}elif(eachRate == "Disposal"){

				thisPartStr = replace(thisPartStr, OCCURRENCE_STR, unitOfMeasure);
			}
			
			retStr = retStr + thisPartStr; 
			//print "thisPart=="+replace(replace(replace(thisPart, rateType, eachRate),  "identifier", "identifier" + string(i+1)),PRICE_STR, get(priceDict, eachRate));
			i = i + 1;
		}
		//retStr = retStr  + partNum + "~" + string(qty) + "~" + "rateType" + VALUE_DELIM + "Haul" + "~" +haulRate + "~" + "identifier1|^|";
		//retStr = retStr  + partNum + "~" + string(qty) + "~" + "rateType" + VALUE_DELIM + "Disposal" + "~" +disposalRate + "~" + "identifier2|^|";
	}
	elif(billingType_l == "Flat Rate + Overage"){
		rateTypeArray = string[]{"Haul"};
		if ( unitOfMeasure == "Per Ton" ) {
		append(rateTypeArray, "Overage");
		}
		thisPart =  partNum + "~" + string(qty) + "~" + commentStr + "~" + PRICE_STR + "~" + "identifier|^|";
		
		thisPartStr = "";
		i = 0;
		for eachRate in rateTypeArray{
			thisPartStr = replace(replace(replace(thisPart, rateType, eachRate),  "identifier", "identifier" + string(i+1)),PRICE_STR, get(priceDict, eachRate));
			if(eachRate == "Haul"){
				thisPartStr = replace(thisPartStr, OCCURRENCE_STR, "Per Haul");
			}elif(eachRate == "Overage"){

				thisPartStr = replace(thisPartStr, OCCURRENCE_STR, "Per Ton");
			}
			
			retStr = retStr + thisPartStr; 
			//print "thisPart=="+replace(replace(replace(thisPart, rateType, eachRate),  "identifier", "identifier" + string(i+1)),PRICE_STR, get(priceDict, eachRate));
			i = i + 1;
		}
		//retStr = retStr  + partNum + "~" + string(qty) + "~" + "rateType" + VALUE_DELIM + "Haul" + "~" +haulRate + "~" + "identifier1|^|";
		//retStr = retStr  + partNum + "~" + string(qty) + "~" + "rateType" + VALUE_DELIM + "Disposal" + "~" +disposalRate + "~" + "identifier2|^|";
	}
	elif(billingType_l == "Flat Rate"){
		rateTypeArray = string[]{"Flat"};
		thisPart =  partNum + "~" + string(qty) + "~" + commentStr + "~" + PRICE_STR + "~" + "identifier|^|";
		thisPartStr = "";
		i = 0;
		for eachRate in rateTypeArray{
			thisPartStr = replace(replace(replace(thisPart, rateType, eachRate),  "identifier", "identifier" + string(i+1)),PRICE_STR, get(priceDict, eachRate));
			thisPartStr = replace(thisPartStr, OCCURRENCE_STR, "One-Time");
			retStr = retStr + thisPartStr; 
			i = i + 1;
		}
		//retStr = retStr  + partNum + "~" + string(qty) + "~" + "rateType" + VALUE_DELIM + "Flat" + "~" +flatRate + "~" + "identifier1|^|";
	}else{
		if(rental <> "Monthly" AND rental <> "Daily"){		
			retStr = retStr  + partNum + "~" + string(qty) + "~~~|^|" ;
		}	
	}
	if((rental == "Monthly" OR rental == "Daily") AND (billingType_l == "Haul + Disposal" OR billingType_l == "Flat Rate + Overage" OR billingType_l == "Haul + Minimum Tonnage")){
		
		rateTypeArray = string[]{"Rental"};
		thisPart =  partNum + "~" + string(qty) + "~" + commentStr + "~" + "identifier|^|";
		thisPartStr = "";
		i = 0;
		for eachRate in rateTypeArray{
			thisPartStr = replace(replace(replace(thisPart, rateType, eachRate),  "identifier", "identifier" + string(i+1)),PRICE_STR, get(priceDict, eachRate));
			thisPartStr = replace(thisPartStr, OCCURRENCE_STR, rental);
			retStr = retStr + thisPartStr; 
			i = i + 1;
		}
		//retStr = retStr  + partNum + "~" + string(qty) + "~" + "rateType" + VALUE_DELIM + "Rental" + "~" +rentalRate + "~" + "identifier1|^|";
	
	}
	
	deliveryCommentStr = commentStr;
	
	//Add Delivery part
	deliveryCommentStr = replace(replace(deliveryCommentStr, rateType, "Delivery"),OCCURRENCE_STR,"One-Time");
	//08/28/2014 - 3-9453379691 : Only if the container is NOT customer owned, delivery must be added as a line item
	if(NOT(isCustomerOwned)){
		retStr = retStr +  partNum + "~" + string(qty) + "~" + deliveryCommentStr + "~~" + "identifierDeivery|^|";
	}	
	
	//Add Washout part
	if(washout_l){
		washoutCommentStr = commentStr;
		washoutCommentStr = replace(replace(washoutCommentStr, rateType, "Washout"),OCCURRENCE_STR,"Per Haul");
		retStr = retStr +  partNum + "~" + string(qty) + "~" + washoutCommentStr + "~~" + "identifierWashout|^|";
	}
	//Add Installation cost as line item
	if((containerType_l == "Self-Contained Container" OR containerType_l == "Stationary Compactor") AND onetimeInstallationCharge_l > 0){
		installationCommentStr = commentStr;
		installationCommentStr = replace(replace(installationCommentStr, rateType, "Installation"),OCCURRENCE_STR,"One-Time");
		retStr = retStr +  partNum + "~" + string(qty) + "~" + installationCommentStr + "~~" + "identifierInstallation|^|";
	}
}

return  retStr;