/*Large Existing - Refactoring of Prepricing*/
returnDict = dict("string");
hasDelivery = false;
hasExchange = false;
hasRemDel = false;
floorPrice = 0.0;
basePrice = 0.0;
targetPrice = 0.0;
stretchPrice = 0.0;
currentPrice = 0.0;
floorPriceStr = "";
basePriceStr = "";
targetPriceStr = "";
stretchPriceStr = "";
currentPriceStr = "";
rateTypeLower = get(stringDict, "rateTypeLower");
part_custom_field9 = get(stringDict, "part_custom_field9");

if(rateTypeLower == "haul" OR rateTypeLower == "flat" OR rateTypeLower == "base"){
	if(containskey(stringDict, "haulFloor")){
		floorPriceStr = get(stringDict, "haulFloor");
	}
	if(containskey(stringDict, "haulBase")){
		basePriceStr = get(stringDict, "haulBase");
	}
	if(containskey(stringDict, "haulTarget")){
		targetPriceStr = get(stringDict, "haulTarget");
	}
	if(containskey(stringDict, "haulStretch")){
		stretchPriceStr = get(stringDict, "haulStretch");
	}
	if(containskey(stringDict, "monthlyRate")){
		currentPriceStr = get(stringDict, "monthlyRate");  
	}
}
elif(rateTypeLower == "compactor rental"){
	if(containskey(stringDict, "compactorRentalFloor")){
		floorPriceStr = get(stringDict, "compactorRentalFloor");
	}
	if(containskey(stringDict, "compactorRentalBase")){
		basePriceStr = get(stringDict, "compactorRentalBase");
	}
	if(containskey(stringDict, "compactorRentalTarget")){
		targetPriceStr = get(stringDict, "compactorRentalTarget");
	}
	if(containskey(stringDict, "compactorRentalStretch")){
		stretchPriceStr = get(stringDict, "compactorRentalStretch");
	}
	if(containskey(stringDict, "monthlyRate")){
		currentPriceStr = get(stringDict, "monthlyRate");  
	}
}
elif(rateTypeLower == "disposal"){
	if(containskey(stringDict, "disposalFloor")){
		floorPriceStr = get(stringDict, "disposalFloor");
	}
	if(containskey(stringDict, "disposalBase")){
		basePriceStr = get(stringDict, "disposalBase");
	}
	if(containskey(stringDict, "disposalTarget")){
		targetPriceStr = get(stringDict, "disposalTarget");
	}
	if(containskey(stringDict, "disposalStretch")){
		stretchPriceStr = get(stringDict, "disposalStretch");
	}
}
elif(rateTypeLower == "overage"){
	if(containskey(stringDict, "overageFloor")){
		floorPriceStr = get(stringDict, "overageFloor");
	}
	if(containskey(stringDict, "overageBase")){
		basePriceStr = get(stringDict, "overageBase");
	}
	if(containskey(stringDict, "overageTarget")){
		targetPriceStr = get(stringDict, "overageTarget");
	}
	if(containskey(stringDict, "overageStretch")){
		stretchPriceStr = get(stringDict, "overageStretch");
	}
}
elif(rateTypeLower == "rental"){
	if(containskey(stringDict, "rental_floor")){
		floorPriceStr = get(stringDict, "rental_floor");
	}
	if(containskey(stringDict, "rental_base")){
		basePriceStr = get(stringDict, "rental_base");
	}
	if(containskey(stringDict, "rental_target")){
		targetPriceStr = get(stringDict, "rental_target");
	}
	if(containskey(stringDict, "rental_stretch")){
		stretchPriceStr = get(stringDict, "rental_stretch");
	}
}elif(rateTypeLower == "delivery"){
	if(containskey(stringDict, "DEL")){
		floorPriceStr = get(stringDict, "deliveryFloor");
		floorPriceStr = string(atof(floorPriceStr)); 
		basePriceStr = get(stringDict, "DEL");
		basePriceStr = string(atof(basePriceStr));
		targetPriceStr = get(stringDict, "DEL");
		targetPriceStr = string(atof(targetPriceStr));
		stretchPriceStr = get(stringDict, "DEL");
		stretchPriceStr = string(atof(stretchPriceStr));
		hasDelivery = true;
	}
	if(part_custom_field9 == "HP"){
		floorPriceStr = "0.0";
		basePriceStr = "0.0";
		targetPriceStr = "0.0";
		stretchPriceStr = "0.0";
	}
}
elif(rateTypeLower == "exchange"){
	//put(exchangeExistsDict, line._parent_doc_number, true);
	hasExchange = true;
	if(containskey(stringDict, "EXC")){
		floorPriceStr = get(stringDict, "EXC");    
		basePriceStr = get(stringDict, "EXC");
		targetPriceStr = get(stringDict, "EXC");
		stretchPriceStr = get(stringDict, "EXC");
	}
}elif(rateTypeLower == "removal"){
	if(containskey(stringDict, "REM")){
		floorPriceStr = "0.0";    
		floorPriceStr = string(atof(floorPriceStr));
		basePriceStr = get(stringDict, "REM");
		basePriceStr = string(atof(basePriceStr));
		targetPriceStr = get(stringDict, "REM");
		targetPriceStr = string(atof(targetPriceStr));
		stretchPriceStr = get(stringDict, "REM");
		stretchPriceStr = string(atof(stretchPriceStr));
		if(hasDelivery){
			//returnStr = returnStr + line._document_number + "~hasDelivery_line~true|";
			hasRemDel = true;
		}
	}
}elif(rateTypeLower == "washout"){
	if(containskey(stringDict, "WAS")){
		floorPriceStr = get(stringDict, "WAS");    
		basePriceStr = get(stringDict, "WAS");
		targetPriceStr = get(stringDict, "WAS");
		stretchPriceStr = get(stringDict, "WAS");
	}
}

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
	currentPrice = atof(currentPriceStr);
	currentPrice = round(currentPrice, 2);
}

put(returnDict, "floorPrice", string(floorPrice));
put(returnDict, "basePrice", string(basePrice));
put(returnDict, "targetPrice", string(targetPrice));
put(returnDict, "stretchPrice", string(stretchPrice));
put(returnDict, "currentPrice", string(currentPrice));
put(returnDict, "hasDelivery", string(hasDelivery));
put(returnDict, "hasExchange", string(hasExchange));

return returnDict;