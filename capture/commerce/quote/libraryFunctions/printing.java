/* 
================================================================================
Name:   Printing
Author:   None
Create date:  Header created 11/6/2014
Description:  Runs on the print action to set attributes specifically needed for the documents.        
Input:      Does not take in any parameters.  Uses quote and line level attributes.                    
Output:     String (documentNumber + "~" + attributeVariableName + "~" + value + "|")

Updates:    20141106 Added logic to set the after year 1-4 dates based on the effective service date.  And, created the header. 
            20141112 Commented out logic from 20141106 due to emergency migration, put logic back in on 20141113
            20141212 John Palubinskas - #240 Updated afterYear#Date_quote to hold MM/YYYY format
            20150117 Julie Felberg - #69 Added logic to set the print versions of the rate restrictions
            20150122 Gaurav Dawar - #352 - correcting the calculations for Delivery for it to flow through infopro
            20150210 John Palubinskas - #68 moved rate restriction logic to postPricingFormulas
			20150226 Gaurav Dawar - #431 - Added Existing Terms to be used as MTM functionality.
			20150326 Gaurav Dawar - #59 - Not auto updating the close date to be a day less than effective date for change of Owner.
			20140327 Mike (Republic) - #145 - Small Container Compactor - Broke small containers out into Base and Compactor Rental.
            20150402 John Palubinskas - #449 removed reason code setting since it is done in setTransactionCode function
            20150403 Mike (Republic) - #145 Small Container Pricing - fixed Base price return
            20150410 Mike (Republic) - #145 Small Container Pricing - Zeroed out Compactor Rental and Installation when compactor is turned off.
=====================================================================================================
*/

res = "";
dBAName = "";
oneTimeLinesExist = false;
closeContainerExists = false;
oneTimeTotalDeliveryAmt = 0.0;
COMM_VALUE_DELIM = "^_^";
FIELD_DELIM = "@_@";
inputDict = dict("string");
disposalPriceDict = dict("float");
disposalTonsDict = dict("float");
minimumTonsDict = dict("float");
billingTypeDict = dict("string");
overagePriceDict = dict("float");
haulPriceDict = dict("float");
flatPriceDict = dict("float");
rentalPriceDict = dict("float");
deliveryPriceDict = dict("float");
haulsPerMonthDict = dict("float"); //This dict will hold the part line items estimatedLifts_line value, but not the model line items value.
compactorRentalDict = dict("float");
installationPriceDict = dict("float");



totalDisposalTons = 0.0;
deliveryChargeSubtotal =0.0;
oneTimeDeliveryCredit = 0.0;
parentDocArr = string[];
put(inputDict,"COMM_VALUE_DELIM",COMM_VALUE_DELIM);
put(inputDict,"FIELD_DELIM",FIELD_DELIM);
displayDeliveryCreditsModelWise = false;
deliveryCreditArr = float[];
totalDeliveryCredit = 0.0;

//Service Close Date to be 1 less than effective date
closeDateStr = "";
if(chooseCSA_quote AND effectiveServiceDate_quote <> ""){ /*This condition added on 06/27/2014 because effectiveService Date can be empty if CSA is not chosen and 
if it is empty and and csa is not chosen, but proposal is chosen, then error should not pop up*/
    closeDate = minusdays(strtojavadate(effectiveServiceDate_quote,"yyyy-MM-dd"), 1); 
    if(NOT(isnull(closeDate))){
        closeDateStr = datetostr(closeDate, "yyyy-MM-dd");
    }
}
if(salesActivity_quote == "Change of Owner"){
    closeDateStr = serviceCloseDate_quote;
}

for line in line_process{
    //Model Line Items
    docNum = line._document_number;
    if(line._model_variable_name <> ""){
		//serviceType = ""; //Moved to quote attributes 
        equipmentType = "";
        equipmentSize = "";
        frequency = "";
        wasteType = "";
        customerOwned = "";
        hasCompactor = "";
        containerGroup = "";
        oldContainerGroup = "";
        compactor = "";
        quantity = "";
        estimatedLifts = "";
        landfillCode = "";
        //Attributes that are specifically created for small container configurator
        if(line._model_variable_name == "containers_m"){
            //Get Equipment Type
            equipmentType = getconfigattrvalue(docNum, "routeType");
            
            //Get Container Size
            equipmentSize = getconfigattrvalue(docNum, "containerSize");
            
            //Get Frequency
            frequency = getconfigattrvalue(docNum, "liftsPerContainer_s");
            
            //Get Material Type
            wasteType = getconfigattrvalue(docNum, "wasteType");
            
            //Customer Owned
            customerOwned = getconfigattrvalue(docNum, "isCustomerOwned");
            
            //Compactor
            compactor = getconfigattrvalue(docNum, "compactor");
            
            //Estimated Lifts
            estimatedLifts = getconfigattrvalue(docNum, "estimatedLifts_s");
            
            //Landfill code
            landfillCode = disposalPolygon; //disposalPolygon is a quote attribute for small containers set in Pre-formula pricing library
            
            //Container Group
            containerGroup = ""; //for existing if it’s price change, make the container group what the user selects for both lines.  And for service changes make the old what the user selects and the new blank.
            
        }
        //Attributes that are specifically created for large container configurator
        elif(line._model_variable_name == "largeContainers"){
            //Get Equipment Type
            equipmentType = getconfigattrvalue(docNum, "containerType_l");
            //removing 'Container' word from Self-Contained  
            if(find(equipmentType, " Container") <> -1){
                equipmentType = replace(equipmentType, " Container", "");
            }
            
            //Get Container Size
            equipmentSize = getconfigattrvalue(docNum, "equipmentSize_l");
            
            //Get Frequency
            frequency = getconfigattrvalue(docNum, "haulsPerPeriod");
            
            //Get Material Type
            wasteType = getconfigattrvalue(docNum, "wasteType");
            
            //Customer Owned
            customerOwned = getconfigattrvalue(docNum, "isCustomerOwned");
            
            //Estimated Lifts
            estimatedLifts = getconfigattrvalue(docNum, "haulsPerPeriod");
            
            estTonsHaul = getconfigattrvalue(docNum, "estTonsHaul_l");
            
            if(isnumber(estTonsHaul)){
                put(disposalTonsDict, docNum, atof(estTonsHaul));
            }
            
            minimumTonsHaul = getconfigattrvalue(docNum, "minimumTonshaul_l");
            if(isnumber(minimumTonsHaul)){
                put(minimumTonsDict, docNum, atof(minimumTonsHaul));
            }
            
            put(billingTypeDict, docNum, line.largeContainerBillingType_line);
            
            //Landfill code
            landfillCode = getconfigattrvalue(docNum, "landfillCode_l");
            
            //Container Group
            containerGroup = "";//for existing if it’s price change, make the container group what the user selects for both lines.  And for service changes make the old what the user selects and the new blank.
            
        }
        //Attributes that are specifically created for the Service Change configurator
        elif(line._model_variable_name == "serviceChange"){
            //Get Container Size
            equipmentSize = getconfigattrvalue(docNum, "containerSize_sc");
            
            if(equipmentSize == "No Change"){
                equipmentSize = getconfigattrvalue(docNum, "containerSize_readOnly");
            }
            elif(isnumber(equipmentSize) AND find(equipmentSize, ".") == -1) {
                //Add the .0 portion present on other attribute values
                equipmentSize = equipmentSize + ".0";
            }
            //Add space after the numeral for Service Change attributes
            equipmentSize = equipmentSize + " ";
            salesActivity = getconfigattrvalue(docNum, "salesActivity");
            //for existing if it’s price change, make the container group what the user selects for both lines.  And for service changes make the old what the user selects and the new blank.
            if(salesActivity == "Close container group"){
                closeContainerExists = true;
                oldContainerGroup = getconfigattrvalue(docNum, "containerGroup_readOnly");
                containerGroup = "";
            }elif(salesActivity == "Price adjustment"){
                oldContainerGroup = getconfigattrvalue(docNum, "containerGroup_readOnly");
                containerGroup = oldContainerGroup;
            }elif(salesActivity == "Service level change"){
                oldContainerGroup = getconfigattrvalue(docNum, "containerGroup_readOnly");
                containerGroup = "";
            }
            
        }
        //Attributes that are used commonly among all configurators
        
            
        if(isnull(equipmentType)){
            equipmentType = "";
        }
        if(isnull(equipmentSize)){
            equipmentSize = "";
        }else{
            equipmentSize = equipmentSize + "Yd(s)";
        }
        if(isnull(customerOwned)){
            customerOwned = "";
        }
        if(isnull(compactor)){
            hasCompactor = "false";
        }
        if(isnull(containerGroup)){
            containerGroup = "";
        }
        if(isnull(frequency)){
            frequency = "";
        }
        if(isnull(estimatedLifts)){
            estimatedLifts = "";
        }
                
        //Quantity
        quantity = getconfigattrvalue(docNum, "quantity");  
        //Special case for service change line items. Check new, then current quantity value. If no quantity found, enter zero
        if(NOT isnumber(quantity)){
            quantity = getconfigattrvalue(docNum, "quantity_sc");   
        }
        if(NOT isnumber(quantity)){
            quantity = getconfigattrvalue(docNum, "quantity_readOnly"); 
        }
        if(NOT isnumber(quantity)){
            quantity = "0"; 
        }
        
        res = res + docNum + "~equipmentType_line~" + equipmentType + "|"
                  + docNum + "~equipmentSize_line~" + equipmentSize + "|"
                  + docNum + "~pickupFrequency_line~" + frequency + "|"
                  + docNum + "~customerOwned_line~" + customerOwned + "|"
                  + docNum + "~hasCompactor_line~" + compactor + "|"
                  + docNum + "~containerGroup_line~" + containerGroup + "|"
                  + docNum + "~existingContainerGroup_line~" + oldContainerGroup + "|"
                  + docNum + "~quantity_line~" + quantity + "|"
                  + docNum + "~estimatedLifts_line~" + estimatedLifts + "|"
                  + docNum + "~landFillCode_line~" + landfillCode + "|";
    }else{
        if(findinarray(parentDocArr, line._parent_doc_number) == -1){
            append(parentDocArr, line._parent_doc_number);
        }
        
        if(line.rateType_line == "Base"){
            // If the line is of type Base then assign the value to the Model sell price
			res = res + line._parent_doc_number + "~sellPrice_line~" + string(line.sellPrice_line) + "|"; 
	}
	elif(line.rateType_line == "Compactor Rental"){
                put(compactorRentalDict, line._parent_doc_number,line.sellPrice_line );
        }
        elif( line.rateType_line == "Haul"){
        // If the line is of type Haul then populate haul rate
            if(containskey(haulPriceDict,line._parent_doc_number)){
                put(haulPriceDict, line._parent_doc_number,get(haulPriceDict,line._parent_doc_number)+ line.sellPrice_line );
            }
            else{
                put(haulPriceDict, line._parent_doc_number,line.sellPrice_line );
            }
            estHaulsPerMonth = 0.0;
            if(isnumber(line.estimatedLifts_line)){
                estHaulsPerMonth = round(atof(line.estimatedLifts_line), 2);
                put(haulsPerMonthDict, line._parent_doc_number, estHaulsPerMonth);
            }
        }
        elif( line.rateType_line == "Disposal"){
            // If the line is of type Disposal then populate Disposal rate
            /*
            if(containskey(disposalPriceDict,line._parent_doc_number)){
                put(disposalPriceDict, line._parent_doc_number,get(disposalPriceDict,line._parent_doc_number)+ line.sellPrice_line );
            }
            else{
                put(disposalPriceDict, line._parent_doc_number,line.sellPrice_line );
            }*/
            put(disposalPriceDict, line._parent_doc_number,line.sellPrice_line);
        }
        elif( line.rateType_line == "Overage"){
            put(overagePriceDict, line._parent_doc_number,line.sellPrice_line);
        }
        elif( line.rateType_line == "Flat"){
            // If the line is of type Flat then populate Flat rate
            if(containskey(flatPriceDict,line._parent_doc_number)){
                put(flatPriceDict, line._parent_doc_number,get(flatPriceDict,line._parent_doc_number)+ line.sellPrice_line );
            }
            else{
                put(flatPriceDict, line._parent_doc_number,line.sellPrice_line );
            }
            estHaulsPerMonth = 0.0;
            if(isnumber(line.estimatedLifts_line)){
                estHaulsPerMonth = round(atof(line.estimatedLifts_line), 2);
                put(haulsPerMonthDict, line._parent_doc_number, estHaulsPerMonth);
            }
        }
        elif( line.rateType_line == "Rental"){      
            // If the line is of type Rental then populate Rental rate
            if(containskey(rentalPriceDict,line._parent_doc_number)){
                put(rentalPriceDict, line._parent_doc_number,get(rentalPriceDict,line._parent_doc_number)+ line.sellPrice_line );
            }
            else{
                put(rentalPriceDict, line._parent_doc_number,line.sellPrice_line );
            }
        }
        elif( line.rateType_line == "Delivery"){
            oneTimeLinesExist = true;
            put(deliveryPriceDict, line._parent_doc_number,line.sellPrice_line );
            deliveryCredit = line.totalTargetPrice_line -  line.sellPrice_line ;
            
            if(sizeofarray(deliveryCreditArr) >= 1 AND findinarray(deliveryCreditArr, deliveryCredit) == -1 ){
                displayDeliveryCreditsModelWise = true;
            }
            else{
                append(deliveryCreditArr, deliveryCredit);
            }
            totalDeliveryCredit = totalDeliveryCredit + deliveryCredit;
            if(deliveryCredit < 0.0){//#352 - correcting the calculations for Delivery for it to flow through infopro
                deliveryCredit = 0.0;
            }
            res = res + line._parent_doc_number + "~deliveryCredit_line~" + string(deliveryCredit) + "|";
        }elif( line.rateType_line == "Exchange"){
            oneTimeLinesExist = true;
        }   
        elif(line.rateType_line == "Installation"){
                put(installationPriceDict, line._parent_doc_number,line.installationCharge_line);
	}
    }
}
print haulPriceDict;
print disposalPriceDict;

for eachDocNum in parentDocArr{
    print eachDocNum;
    price = 0.0;
    
    if(containskey(haulPriceDict, eachDocNum)){
        print get(haulPriceDict, eachDocNum) ;
        res = res + eachDocNum + "~haulRate_line~" + string(get(haulPriceDict, eachDocNum) )+ "|"; 
    }
    if(containskey(disposalPriceDict, eachDocNum)){
        disposalRate = get(disposalPriceDict, eachDocNum);
        if(get(billingTypeDict, eachDocNum) == "Haul + Minimum Tonnage"){
            disposalTons = get(minimumTonsDict, eachDocNum);
        }
        else{
            disposalTons = get(disposalTonsDict, eachDocNum);
        }
        if(disposalRate > 0.0){
            totalDisposalTons = totalDisposalTons + disposalTons;   
        }
        res = res + eachDocNum + "~disposalRate_line~" + string(disposalRate)+ "|"; 
    }
    if(containskey(overagePriceDict, eachDocNum)){
        res = res + eachDocNum + "~overageRate_line~" + string(get(overagePriceDict, eachDocNum)) + "|";
    }
    if(containskey(flatPriceDict, eachDocNum)){
        res = res + eachDocNum + "~flatRate_line~" + string(get(flatPriceDict, eachDocNum) )+ "|"; 
        res = res + eachDocNum + "~haulRate_line~" + string(get(flatPriceDict, eachDocNum) )+ "|"; //Case 180936 fix
    }
    if(containskey(rentalPriceDict, eachDocNum)){
        rentalRate = get(rentalPriceDict, eachDocNum);
        res = res + eachDocNum + "~rentalRate_line~" + string(rentalRate)+ "|"; 
        
        //Get the monthly rental rate. Convert the rental rate if it is set to Daily.
        rentalRateFactor = 365.0/12.0;
        if(getconfigattrvalue(eachDocNum, "rental") == "Daily"){
            rentalRate = rentalRate * rentalRateFactor;
        }
        res = res + eachDocNum + "~monthlyRentalRate_line~" + string(rentalRate)+ "|"; 
    }
    if(containskey(compactorRentalDict, eachDocNum)){
        compactorRentalRate = get(compactorRentalDict, eachDocNum);
	res = res + eachDocNum + "~smallRentalPrice_line~" + string(compactorRentalRate) + "|"; 
    }
    else{
	res = res + eachDocNum + "~smallRentalPrice_line~" + "0.0" + "|"; 
    }
    if(containskey(installationPriceDict, eachDocNum)){
        installationRate = get(installationPriceDict, eachDocNum);
	res = res + eachDocNum + "~installationCharge_line~" + string(InstallationRate) + "|"; 
    }
    else{
	res = res + eachDocNum + "~installationCharge_line~" + "0.0" + "|"; 
    }
    if(containskey(deliveryPriceDict, eachDocNum)){
        deliveryRate = get(deliveryPriceDict, eachDocNum);
        res = res + eachDocNum + "~deliveryRate_line~" + string(deliveryRate)+ "|"; 
    }
    if(containskey(haulsPerMonthDict, eachDocNum)){
        res = res + eachDocNum + "~estHaulsPerMonth_line~" + string(get(haulsPerMonthDict, eachDocNum)) + "|";
    }
    
}

//Get DBA Name for the selected division
resultset = bmql("SELECT DBA_Name, is_legal_entity_name FROM Div_DBA_Names WHERE Division_Nbr = $division_quote");
dBANameArr = string[];
legalEntity = "";
finalDBAName = ""; //format is <Legal entity> DBA <DBA List>
for result in resultset{
    dbaName_db = get(result, "DBA_Name");
    isLegalEntity = getint(result, "is_legal_entity_name");
    if(isLegalEntity == 0){ //if it is 0, they are just DBAs
        if(findinarray(dBANameArr, dbaName_db) == -1){
            append(dBANameArr, dbaName_db);
        }
    }else{ //if it is 1, then it is legal entity, as per data, ideally, there should be only 1 and at least 1 legal entity, hence capturing it in a string
        legalEntity = dbaName_db;
    }
}
dBAName = join(dBANameArr, ", ");
finalDBAName = legalEntity + " DBA " + dBAName;

//Front TC Content & Rear TC Content
CSAName = "";
Front_TC = "";
Rear_TC = "";
regionArr = string[];
CSAComboResultSet = recordset();
CSARegionRecSet = bmql("SELECT Region FROM CSACombo WHERE Division = $division_quote");
for eachRec in CSARegionRecSet{
    region_db = get(eachRec, "Region");
    if(region_db <> ""){
        if(findinarray(regionArr, region_db) == -1){
            append(regionArr, region_db);
        }
    }   
}
//If a division does not have Regions specified, then query for rows with empty Region, Else, query for the Region that user selected on the Documents screen
if(isempty(regionArr)){
    //print "here--?";
    CSAComboResultSet = bmql("SELECT CSAName FROM CSACombo WHERE Division = $division_quote AND Region IS null AND AccountType = $accountType_quote");
}else{
    CSAComboResultSet = bmql("SELECT CSAName FROM CSACombo WHERE Division = $division_quote AND Region = $csaVersion_quote AND AccountType = $accountType_quote");
}
for record in CSAComboResultSet{
    CSAName = get(record, "CSAName");
    break;
}

if(CSAName <> ""){
    TermsBasedOnCSARS = bmql("SELECT Front_T_C, Rear_T_C FROM TermsBasedOnCSA WHERE CSAName = $CSAName");
    for record in TermsBasedOnCSARS{
        Front_TC = get(record, "Front_T_C");
        Rear_TC = get(record, "Rear_T_C");
    }
}


initialTermForDocOutput = "";
renewalTermForDocOutput =  "";

if(salesActivity_quote == "Existing Customer" AND initialTerm_quote == "Existing Terms" AND contractLength_quote <> 1){
    initialTermForDocOutput = string(hiddenExisitingTerm) + " MONTHS";
}elif(salesActivity_quote == "Existing Customer" AND initialTerm_quote == "Existing Terms" AND contractLength_quote == 1){
    initialTermForDocOutput = "MONTH TO MONTH";
}elif(initialTerm_quote == "1"){
    initialTermForDocOutput = "MONTH TO MONTH";
}else{
    initialTermForDocOutput = initialTerm_quote + " MONTHS";
}

if(salesActivity_quote == "Existing Customer" AND renewalTerm_quote == "Existing Terms" AND contractLength_quote <> 1){
    renewalTermForDocOutput = string(hiddenExisitingTerm)  + " MONTHS";
}elif(salesActivity_quote == "Existing Customer" AND renewalTerm_quote == "Existing Terms" AND contractLength_quote == 1){
    initialTermForDocOutput = "MONTH TO MONTH";
}elif(renewalTerm_quote == "1"){
    renewalTermForDocOutput = "MONTH TO MONTH";
}else{
    renewalTermForDocOutput = renewalTerm_quote + " MONTH";
}


lawsonDivisionRS = bmql("SELECT support_phone FROM lawson_division WHERE division = $division_quote");
supportPhone="";
for record in lawsonDivisionRS{
    supportPhone = get(record, "support_phone");
}

if (adHocOneTimeExists_quote == true){
    oneTimeLinesExist = true;
}


res = res + "1~dBAName_TextArea_quote~"                + finalDBAName + "|"
          + "1~frontTCContent_quote~"                  + Front_TC + "|"
          + "1~rearTCContent_quote~"                   + Rear_TC + "|"
          + "1~displayDeliveryCreditsModelWise_quote~" + string(displayDeliveryCreditsModelWise) + "|"
          + "1~totalDisposalTons_quote~"               + string(totalDisposalTons) + "|"
          + "1~totalDeliveryCredit_quote~"             + string(totalDeliveryCredit) + "|"
          + "1~initialTermForDocument_quote~"          + initialTermForDocOutput + "|"
          + "1~renewalTermForDocument_quote~"          + renewalTermForDocOutput + "|"
          + "1~supportPhone_quote~"                    + supportPhone + "|"
          + "1~closedContainerExists_quote~"           + string(closeContainerExists) +"|"
          + "1~serviceCloseDate_quote~"                + closeDateStr + "|"
          + "1~oneTimeLinesExist_quote~"               + string(oneTimeLinesExist) + "|";
     
return res;
