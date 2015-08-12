returnDict = dict("string");
divisionExchangeStr = get(stringDict, "divisionExchangeStr");
divisionExcStr_ui = get(stringDict, "divisionExcStr_ui");
divisionExtYdStr_ui = get(stringDict, "divisionExtYdStr_ui");
divisionExtLiftStr = get(stringDict, "divisionExtLiftStr");
divisionExtLiftStr_ui = get(stringDict, "divisionExtLiftStr_ui");
divisionExtYardStr = get(stringDict, "divisionExtYardStr");
divisionRemoveStr = get(stringDict, "divisionRemoveStr");
divisionRemStr_ui = get(stringDict, "divisionRemStr_ui");
divisionReloStr = get(stringDict, "divisionReloStr");
divisionReloStr_ui = get(stringDict, "divisionReloStr_ui");
divisionWASStr = get(stringDict, "divisionWASStr");
divisionWashoutStr_ui = get(stringDict, "divisionWashoutStr_ui");
contCat = get(stringDict, "contCat");
priceType = get(stringDict, "priceType");
rateType = get(stringDict, "rateType");
SMALL_CONTAINER = get(stringDict, "SMALL_CONTAINER");
container = get(stringDict, "container");
SERVICE_CHANGE = get(stringDict, "SERVICE_CHANGE");
LARGE_CONTAINER = get(stringDict, "LARGE_CONTAINER");
sellPrice_line = get(stringDict, "sellPrice_line");
divisionDeliveryStr = get(stringDict, "divisionDeliveryStr");
divisionDelStr_ui = get(stringDict, "divisionDelStr_ui");
divisionDRYStr = "";
divisionDryStr_ui = "";
frfRateStr = get(stringDict, "frfRate");
erfRateStr = get(stringDict, "erfRate");
adminRateStr = get(stringDict, "adminAmount");
erfOnFrfStr = get(stringDict, "erfOnFrfFlag");
 
if(containskey(stringDict, "DEL")){
	divisionDeliveryStr = get(stringDict, "DEL");
	divisionDelStr_ui = get(stringDict, "DEL");
}           
if(divisionExchangeStr == divisionExcStr_ui AND containskey(stringDict, "EXC")){
	divisionExchangeStr = get(stringDict, "EXC");
	divisionExcStr_ui = get(stringDict, "EXC");
}
if(divisionExtLiftStr == divisionExtLiftStr_ui AND containskey(stringDict, "EXT")){
	divisionExtLiftStr = get(stringDict, "EXT");
	divisionExtLiftStr_ui = get(stringDict, "EXT");
}
if(priceType == SMALL_CONTAINER OR container == SMALL_CONTAINER OR (priceType == SERVICE_CHANGE AND contCat <> "Industrial")){
	//Specific to small container
	if(divisionExtYardStr == divisionExtYdStr_ui AND containskey(stringDict, "EXY")){
		divisionExtYardStr = get(stringDict, "EXY");
		divisionExtYdStr_ui = get(stringDict, "EXY");
	}
}
if(divisionRemoveStr == divisionRemStr_ui AND containskey(stringDict, "REM")){
	divisionRemoveStr = get(stringDict, "REM");
	divisionRemStr_ui = get(stringDict, "REM");
}
if(divisionReloStr == divisionReloStr_ui AND containskey(stringDict, "REL")){
	divisionReloStr = get(stringDict, "REL");
	divisionReloStr_ui = get(stringDict, "REL");
}
     
if(priceType == LARGE_CONTAINER OR container == LARGE_CONTAINER OR (priceType == SERVICE_CHANGE AND contCat == "Industrial")){

	//Specific to Large container
	if(divisionWASStr == divisionWashoutStr_ui AND containskey(stringDict, "WAS")){
		divisionWASStr = get(stringDict, "WAS");
		divisionWashoutStr_ui = get(stringDict, "WAS");
	}

	//divisionDRY is pulled from the division first.  If the division did not set the
	//DRY rate, then set it to 1/2 the haul rate.
	if(rateType == "Haul"){

		if(containskey(stringDict, "DRY")){
			divisionDRYStr = get(stringDict, "DRY");
			divisionDryStr_ui = divisionDRYStr;
		}
		else{
			divisionDRYStr = string(round(atof(sellPrice_line)/2.0, 2));
			divisionDryStr_ui = string(round(atof(sellPrice_line)/2.0, 2));                            
		}
		put(returnDict, "divisionDRYStr", divisionDRYStr);
		put(returnDict, "divisionDryStr_ui", divisionDryStr_ui);
	}
}

put(returnDict, "divisionExchangeStr", divisionExchangeStr);
put(returnDict, "divisionExcStr_ui", divisionExcStr_ui);
put(returnDict, "divisionExtYdStr_ui", divisionExtYdStr_ui);
put(returnDict, "divisionExtLiftStr", divisionExtLiftStr);
put(returnDict, "divisionExtLiftStr_ui", divisionExtLiftStr_ui);
put(returnDict, "divisionExtYardStr", divisionExtYardStr);
put(returnDict, "divisionRemoveStr", divisionRemoveStr);
put(returnDict, "divisionRemStr_ui", divisionRemStr_ui);
put(returnDict, "divisionReloStr", divisionReloStr);
put(returnDict, "divisionReloStr_ui", divisionReloStr_ui);
put(returnDict, "divisionWASStr", divisionWASStr);
put(returnDict, "divisionWashoutStr_ui", divisionWashoutStr_ui);
put(returnDict, "divisionDeliveryStr", divisionDeliveryStr);
put(returnDict, "divisionDelStr_ui", divisionDelStr_ui);
put(returnDict, "frfRateStr", frfRateStr);
put(returnDict, "erfRateStr", erfRateStr);
put(returnDict, "adminRateStr", adminRateStr);
put(returnDict, "erfOnFrfStr", erfOnFrfStr);

return returnDict;