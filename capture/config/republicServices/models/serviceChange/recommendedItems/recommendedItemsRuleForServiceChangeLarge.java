//Large Service change rec rule
//Instantiate Variables
retStr= "";
tempSize = "";
tempWaste = "";
tempCode = "";
tempLifts = "";
tempBilling = "";
tempUOM = "";
tempRental = "";
lob = "";
lobCategoryDerived = "";
VALUE_DELIM = "^_^";
ATTR_DELIM = "@_@";


compactorStr = "0";
if(containerType_lc == "Open Top"){ //Open top implies no compactor
	compactorStr = "0";
}elif(containerType_lc == "Self-Contained Container"){ //Self-contained and stationary imply compactor exists, so should be set to 1.
	compactorStr = "1";
}elif(containerType_lc == "Stationary Compactor"){
	compactorStr = "1";
}

deliveryReq = true;
if(containerType_lc == "Open Top" AND customerOwnedContainer_lc_readonly == "Yes"){ 
	deliveryReq = false;
}elif(containerType_lc == "Self-Contained Container" AND customerOwnedCompactor_lc_readOnly == "Yes"){ 
	deliveryReq = false;
}elif(containerType_lc == "Stationary Compactor" AND customerOwnedContainer_lc_readonly == "Yes" AND customerOwnedCompactor_lc_readOnly == "Yes"){
	deliveryReq = false;
}

//Handle Waste Type
if(wasteType_lc == "No Change"){
	tempWaste = wasteType_lc_readonly;
}else{
	tempWaste = wasteType_lc;
}

codeString = "";
//Handle Size
if(containerSize_lc == "No Change"){
	//Find Valid Sizes	
	contCode = "";
	contCodeRecs = BMQL("SELECT recycling_flag FROM waste_type_map WHERE waste_type = $tempWaste");
	for code in contCodeRecs{
		contCode = get(code, "recycling_flag");
	}
	if(containerType_lc == "Self-Contained Container"){
		codeString = "SC";
	}else{
		if(contCode == "1"){
			codeString = "IR";
		}else{
			codeString = "RO";
		}
	}
	resultset = bmql("SELECT containerSize, division, container_cd FROM Div_Container_Size WHERE division = $division_config and container_cd = $codeString" );
	divisionDict = dict("string[]");

	print resultset;
	for result in resultset{
		size = get(result, "containerSize");
		division = get(result, "division");
		code = get(result, "container_cd");
		if(NOT(isnull(size)) AND NOT(isnull(division))){
			sizesArray = string[];
			if(containskey(divisionDict, division)){
				sizesArray = get(divisionDict, division);
			}
			append(sizesArray, size);
			put(divisionDict, division, sizesArray);
		}
	}
	sizeArray = string[];
	if(containskey(divisionDict, division_config)){
		sizeArray = get(divisionDict, division_config);
	}
	
	sort(sizeArray,"asc","numeric");
	print sizeArray;
	for each in sizeArray{

		if(atof(containerSize_lc_readonly) == atof(each)){
			tempSize = containerSize_lc_readonly;
			break;
		}elif(atof(containerSize_lc_readonly) > atof(each)){
			continue;
		}elif(atof(containerSize_lc_readonly) < atof(each)){
			tempSize = each;
			break;
		}
	}
}else{
	tempSize = containerSize_lc;
}

//Handle Container Code
TempCodeSC = codeString;
/*
//Handle Lifts Per Container
if(haulsPerContainer_lc == "No Change"){
	tempLifts = liftsPerContainer_readonly;
}else{
	tempLifts = haulsPerContainer_lc;
}
*/
//Set LOB Category Derived
lobRecordSet = BMQL("SELECT recycling_flag FROM waste_type_map WHERE waste_type = $tempWaste");
for record in lobRecordSet{
	lob = get(record, "recycling_flag");
}
if(lob == "1"){
	lobCategoryDerived = "Industrial Recycling";
}
if(lob == "0"){
	lobCategoryDerived = "Industrial";
}

//Set billing Type
if(billingType_lc == "No Change"){
	tempBilling = billingType_lc_readOnly;
	if(billingType_lc_readOnly == "Flat Rate"){
		tempBilling = "Flat Rate + Overage";
	}
}else{
	tempBilling = billingType_lc;
}

//Set UOM
if(unitOfMeasure_lc == "No Change"){
	tempUOM = unitOfMeasure_lc_readOnly;
}else{
	tempUOM = unitOfMeasure_lc;
}

//Set Rental
if(rental_lc == "No Change"){
	tempRental = rental_lc_readOnly;
}else{
	tempRental = rental_lc;
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

partsRecordSet = bmql("SELECT part_number FROM _parts WHERE custom_field9 = $TempCodeSC AND custom_field10 = $tempSize AND custom_field11 = $compactorStr AND  custom_field12 = $lobCategoryDerived AND custom_field13 = 'Y'"); //Added Custom Field 13 filter based on Case Number 00179857

qty = quantity_lc;

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
	
	
	if(tempBilling == "Haul + Disposal" OR tempBilling == "Haul + Minimum Tonnage"){
		rateTypeArray = string[]{"Haul", "Disposal"};
		thisPart =  partNum + "~" + string(qty) + "~" + commentStr + "~" + PRICE_STR + "~" + "identifier|^|";
		
		thisPartStr = "";
		i = 0;
		for eachRate in rateTypeArray{
			thisPartStr = replace(replace(replace(thisPart, rateType, eachRate),  "identifier", "identifier" + string(i+1)),PRICE_STR, get(priceDict, eachRate));
			if(eachRate == "Haul"){
				thisPartStr = replace(thisPartStr, OCCURRENCE_STR, "Per Haul");
			}elif(eachRate == "Disposal"){
				tempUOM2 = "Per "+tempUOM;
				thisPartStr = replace(thisPartStr, OCCURRENCE_STR, tempUOM2);
			}
			
			retStr = retStr + thisPartStr; 
			//print "thisPart=="+replace(replace(replace(thisPart, rateType, eachRate),  "identifier", "identifier" + string(i+1)),PRICE_STR, get(priceDict, eachRate));
			i = i + 1;
		}
		//retStr = retStr  + partNum + "~" + string(qty) + "~" + "rateType" + VALUE_DELIM + "Haul" + "~" +haulRate + "~" + "identifier1|^|";
		//retStr = retStr  + partNum + "~" + string(qty) + "~" + "rateType" + VALUE_DELIM + "Disposal" + "~" +disposalRate + "~" + "identifier2|^|";
	}
	elif(tempBilling == "Flat Rate + Overage"){
		rateTypeArray = string[]{"Haul"};
		if ( tempUOM == "Ton" ) {
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
			print thisPartStr;
			retStr = retStr + thisPartStr; 
			//print "thisPart=="+replace(replace(replace(thisPart, rateType, eachRate),  "identifier", "identifier" + string(i+1)),PRICE_STR, get(priceDict, eachRate));
			i = i + 1;
		}
		//retStr = retStr  + partNum + "~" + string(qty) + "~" + "rateType" + VALUE_DELIM + "Haul" + "~" +haulRate + "~" + "identifier1|^|";
		//retStr = retStr  + partNum + "~" + string(qty) + "~" + "rateType" + VALUE_DELIM + "Disposal" + "~" +disposalRate + "~" + "identifier2|^|";
	}
	elif(tempBilling == "Flat Rate + Overage"){
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
		if(tempRental <> "Monthly" AND tempRental <> "Daily"){		
			retStr = retStr  + partNum + "~" + string(qty) + "~~~|^|" ;
		}	
	}
	if((tempRental == "Monthly" OR tempRental == "Daily") AND (tempBilling == "Haul + Disposal" OR tempBilling == "Flat Rate + Overage" OR tempBilling == "Haul + Minimum Tonnage") AND deliveryReq){
		
		rateTypeArray = string[]{"Rental"};
		thisPart =  partNum + "~" + string(qty) + "~" + commentStr + "~" + "identifier|^|";
		thisPartStr = "";
		i = 0;
		for eachRate in rateTypeArray{
			thisPartStr = replace(replace(replace(thisPart, rateType, eachRate),  "identifier", "identifier" + string(i+1)),PRICE_STR, get(priceDict, eachRate));
			thisPartStr = replace(thisPartStr, OCCURRENCE_STR, tempRental);
			retStr = retStr + thisPartStr; 
			i = i + 1;
		}
		//retStr = retStr  + partNum + "~" + string(qty) + "~" + "rateType" + VALUE_DELIM + "Rental" + "~" +rentalRate + "~" + "identifier1|^|";
	
	}
	
	deliveryCommentStr = commentStr;
	
	//Add Delivery part
	deliveryCommentStr = replace(replace(deliveryCommentStr, rateType, "Delivery"),OCCURRENCE_STR,"One-Time");
	//08/28/2014 - 3-9453379691 : Only if the container is NOT customer owned, delivery must be added as a line item
	
	if(deliveryReq){
		retStr = retStr +  partNum + "~" + string(qty) + "~" + deliveryCommentStr + "~~" + "identifierDeivery|^|";
	}
	
	if(washout_l){
		washoutCommentStr = commentStr;
		washoutCommentStr = replace(replace(washoutCommentStr, rateType, "Washout"),OCCURRENCE_STR,"Per Haul");
		retStr = retStr +  partNum + "~" + string(qty) + "~" + washoutCommentStr + "~~" + "identifierWashout|^|";
	}
	//Add Installation cost as line item
	if((containerType_lc == "Self-Contained Container" OR containerType_lc == "Stationary Compactor") AND onetimeInstallationCharge_l > 0){
		installationCommentStr = commentStr;
		installationCommentStr = replace(replace(installationCommentStr, rateType, "Installation"),OCCURRENCE_STR,"One-Time");
		retStr = retStr +  partNum + "~" + string(qty) + "~" + installationCommentStr + "~~" + "identifierInstallation|^|";
	}
}

return  retStr;