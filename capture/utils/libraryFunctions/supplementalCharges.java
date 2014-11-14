/*
	Get Supplemental charges for existing customer for Price Increase containers
*/
containerGroup = "";
infoProDivNum = "";
division = "";
accountNumber = "";
siteNumber = "";
routeTypeDerived_current = "";
priceType = "";
quotes = "\"";

if(containskey(stringDict, "containerGroup")){
	containerGroup = get(stringDict, "containerGroup");
}
if(containskey(stringDict, "infoProDivNumber")){
	infoProDivNum = get(stringDict, "infoProDivNumber");
}
if(containskey(stringDict, "siteNumber_quote")){
	siteNumber = get(stringDict, "siteNumber_quote");
}
if(containskey(stringDict, "priceType")){
	priceType = get(stringDict, "priceType");
}
if(containskey(stringDict, "division")){
	division = get(stringDict, "division");
}


if(containskey(stringDict, "routeTypeDerived")){
	routeTypeDerived_current = get(stringDict, "routeTypeDerived");
	//routeTypeDerived_db = get(eachRecord, "Container_Cd");//This is route type derived
}
if(containskey(stringDict, "acct_nbr")){
	accountNumber = get(stringDict, "acct_nbr");
	//accountNumber = get(eachRecord, "acct_nbr");
}


serviceCodesArray = string[];
if(priceType == "Containers"){
	serviceCodesArray = string[]{"DEL", "REM", "REL", "EXC", "EXT", "EXY"};	
}elif(priceType == "Large Containers"){
	serviceCodesArray = string[]{"DEL", "REM", "REL", "EXC", "EXT", "WAS", "DRY"};
}
	
servicesCodesLen = sizeofarray(serviceCodesArray);

ratesDict = dict("float");
returnDict = dict("string");

//For existing customer, get rates from Account_Rates, if not found then get from Div_Service_Price table
accountRatesResultSet = recordset();
//if(isExistingCustomer AND serviceChangeType <> ""){
	accountRatesResultSet = bmql("SELECT rate_Amt, charge_cd, division_nbr FROM Account_Rates WHERE container_Grp_Nbr = $containerGroup AND infopro_div_nbr = $infoProDivNum AND (division_nbr = $division OR division_nbr = '0') AND charge_cd IN $serviceCodesArray AND acct_nbr = $accountNumber AND site_Nbr = $siteNumber AND container_Grp_Nbr = $containerGroup ORDER BY division_nbr DESC");	
//}

//Get rates for new customer from Div_Service_Price table
divServicePriceRS = bmql("SELECT infopro_div_nbr, serviceCode, containerType, servicePrice, divisionNumber FROM Div_Service_Price WHERE (divisionNumber = $division OR divisionNumber = '0') AND (serviceCode IN $serviceCodesArray) AND (containerType = $routeTypeDerived_current OR containerType IS NULL OR containerType = $quotes) ORDER BY infopro_div_nbr ASC"); //ordering by ASC makes sure all records with info pro div number blank are at bottom

//if(cr_new_business == 1 AND isExistingCustomer AND serviceChangeType <> ""){ //If its a new business for existing customer
	/*Select rates from tables listed below in same order
	 1. Account_Rates
	 2. Div_Service_Price
		a. Match Division Number, Service Code & Container Code
		b. Match Division Number & Service Code
		c. Match Division 0, Service Code & Container code
		d. Match Division 0, Service Code & blank Container code 			
	*/
	//Account_Rates table
	for eachRecord in accountRatesResultSet{
		div_nbr = get(eachRecord, "division_nbr");
		rate_Amt = getFloat(eachRecord, "rate_Amt");
		charge_cd = get(eachRecord, "charge_cd");
		//If at all records exist, they must be either specific to division or corporate (0)	
		if(not(containskey(ratesDict, charge_cd))){
			put(ratesDict, charge_cd, rate_Amt); //Get rates for all applicable service codes
		}
	}
	/*
	if(NOT(found)){
		for eachRecord in accountRatesResultSet{
			div_nbr = getFloat(eachRecord, "division_nbr");
			rate_Amt = getFloat(eachRecord, "rate_Amt");
			charge_cd = get(eachRecord, "charge_cd");
			if(div_nbr == "0"){
				put(ratesDict, charge_cd, rate_Amt);
				found = true;
			}
		}	
	}*/
	//Query Div_Service_Price table if rates are not found in Account_Rates for existing customer
	ratesDictValues = values(ratesDict);
	if(sizeofarray(ratesDictValues) < servicesCodesLen){
		//Step 1: 
		//Match Division Number, Service Code & Container Code & InfoProNumber
		for eachRecord in divServicePriceRS{
			service_Code_db = get(eachRecord, "serviceCode");
			container_Type_db = get(eachRecord, "containerType");
			service_Price_db = getFloat(eachRecord, "servicePrice");
			division_Number_db = get(eachRecord, "divisionNumber");
			infopro_div_nbr = get(eachRecord, "infopro_div_nbr");
			if(division_Number_db == division AND (findinarray(serviceCodesArray, service_Code_db) > -1) AND container_Type_db ==  routeTypeDerived_current AND infopro_div_nbr == infoProDivNum){
				if(not(containskey(ratesDict, service_Code_db))){
					put(ratesDict, service_Code_db, service_Price_db);
				}
			}
		}
		print "---ratesDict 1--"; print ratesDict;
		//Step 2: 
		//Match Division Number, Service Code & Container Code
		ratesDictValues = values(ratesDict);
		if(sizeofarray(ratesDictValues) < servicesCodesLen){
			for eachRecord in divServicePriceRS{
				service_Code_db = get(eachRecord, "serviceCode");
				container_Type_db = get(eachRecord, "containerType");
				service_Price_db = getFloat(eachRecord, "servicePrice");
				division_Number_db = get(eachRecord, "divisionNumber");
				if(division_Number_db == division AND (findinarray(serviceCodesArray, service_Code_db) > -1) AND container_Type_db ==  routeTypeDerived_current){
					if(not(containskey(ratesDict, service_Code_db))){
						put(ratesDict, service_Code_db, service_Price_db);
					}
				}
			}
		}
		print "---ratesDict 2--"; print ratesDict;
		//If a record is not found in Step 2 continue to step 3
		//Step 3
		//b. Match Division Number & Service Code
		ratesDictValues = values(ratesDict);
		if(sizeofarray(ratesDictValues) < servicesCodesLen){
			for eachRecord in divServicePriceRS{
				service_Code_db = get(eachRecord, "serviceCode");
				container_Type_db = get(eachRecord, "containerType");
				service_Price_db = getFloat(eachRecord, "servicePrice");
				division_Number_db = get(eachRecord, "divisionNumber");
				if(division_Number_db == division AND (findinarray(serviceCodesArray, service_Code_db) > -1)){
					if(not(containskey(ratesDict, service_Code_db))){
						put(ratesDict, service_Code_db, service_Price_db);
					}
				}
			}
		}
		print "---ratesDict 3--"; print ratesDict;
		//Step 4
		//c. Match Division 0, Service Code & Container code
		ratesDictValues = values(ratesDict);
		if(sizeofarray(ratesDictValues) < servicesCodesLen){
			for eachRecord in divServicePriceRS{
				service_Code_db = get(eachRecord, "serviceCode");
				container_Type_db = get(eachRecord, "containerType");
				service_Price_db = getFloat(eachRecord, "servicePrice");
				division_Number_db = get(eachRecord, "divisionNumber");
				if(division_Number_db == "0" AND (findinarray(serviceCodesArray, service_Code_db) > -1) AND container_Type_db ==  routeTypeDerived_current){
					if(not(containskey(ratesDict, service_Code_db))){
						put(ratesDict, service_Code_db, service_Price_db);
					}
				}
			}
		}
		print "---ratesDict 4--"; print ratesDict;
		//Step 5
		//d. Match Division 0, Service Code & blank or Quotes Container code 			
		ratesDictValues = values(ratesDict);
		if(sizeofarray(ratesDictValues) < servicesCodesLen){
			for eachRecord in divServicePriceRS{
				service_Code_db = get(eachRecord, "serviceCode");
				container_Type_db = get(eachRecord, "containerType");
				service_Price_db = getFloat(eachRecord, "servicePrice");
				division_Number_db = get(eachRecord, "divisionNumber");
				if(division_Number_db == "0" AND (findinarray(serviceCodesArray, service_Code_db) > -1) AND (container_Type_db == "" OR container_Type_db == quotes)){
					if(not(containskey(ratesDict, service_Code_db))){
						put(ratesDict, service_Code_db, service_Price_db);
					}	
				}
			}
		}
	}
	print "---ratesDict 5--"; print ratesDict;
//}//End of new business for existing customer

//Now that ratesDict has some values in it, assign the rate to Service - TO DO
serviceRate = 0.0;
for eachServiceCode in serviceCodesArray{
	if(containskey(ratesDict, eachServiceCode)){
		serviceRate = get(ratesDict, eachServiceCode);
		put(returnDict, eachServiceCode, string(serviceRate));
	}
}
return returnDict;