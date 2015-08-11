/*Large Existing - Refactoring of Prepricing*/
returnDict = dict("string");
hasDelivery = false;
hasExchange = false;
hasRemDel = false;
floorPrice = 0.0;
basePrice = 0.0;
targetPrice = 0.0;
stretchPrice = 0.0;
floorPriceStr = "";
basePriceStr = "";
targetPriceStr = "";
stretchPriceStr = "";
rateTypeLower = get(stringDict, "rateTypeLower");
haul_floor = get(stringDict, "haul_floor");
haul_base = get(stringDict, "haul_base");
haul_target = get(stringDict, "haul_target");
haul_stretch = get(stringDict, "haul_stretch");
dsp_floor = get(stringDict, "dsp_floor");
dsp_base = get(stringDict, "dsp_base");
dsp_target = get(stringDict, "dsp_target");
dsp_stretch = get(stringDict, "dsp_stretch");
ovr_floor = get(stringDict, "ovr_floor");
ovr_base = get(stringDict, "ovr_base");
ovr_target = get(stringDict, "ovr_target");
ovr_stretch = get(stringDict, "ovr_stretch");
ren_floor = get(stringDict, "ren_floor");
ren_base = get(stringDict, "ren_base");
ren_target = get(stringDict, "ren_target");
ren_stretch = get(stringDict, "ren_stretch");


if(rateTypeLower == "haul" OR rateTypeLower == "flat" OR rateTypeLower == "base"){
    floorPriceStr = haul_floor;
    basePriceStr = haul_base;
    targetPriceStr = haul_target;
    stretchPriceStr = haul_stretch;
}
elif(rateTypeLower == "disposal"){
	floorPriceStr = dsp_floor;
    basePriceStr = dsp_base;
    targetPriceStr = dsp_target;
    stretchPriceStr = dsp_stretch;
}
elif(rateTypeLower == "overage"){
    floorPriceStr = ovr_floor;
    basePriceStr = ovr_base;
    targetPriceStr = ovr_target;
    stretchPriceStr = ovr_stretch;
}
elif(rateTypeLower == "rental"){
	floorPriceStr = ren_floor;
    basePriceStr = ren_base;
    targetPriceStr = ren_target;
    stretchPriceStr = ren_stretch;
}
elif(rateTypeLower == "delivery"){
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
}elif(rateTypeLower == "exchange"){
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

put(returnDict, "floorPrice", string(floorPrice));
put(returnDict, "basePrice", string(basePrice));
put(returnDict, "targetPrice", string(targetPrice));
put(returnDict, "stretchPrice", string(stretchPrice));
put(returnDict, "hasDelivery", string(hasDelivery));
put(returnDict, "hasExchange", string(hasExchange));

return returnDict;