/*
Sets Fee charged for Existing / New Customers
Existing Customers: Looks up Account_Status data table to get Fixed Fee components
	If customer chooses to modify Fee, preserve user selection
New Customers: Always Sets default fee of FRF, ERF, Admin

*/
retStr = "";
customerHasAFixedFee = customerHasAFixedFee_quote;
frfFixed = false;
erfFixed = false;
isAdminCharged = false;
isFRFChaged = false;
isERFChaged = false;
fees=feesToCharge_quote;
fixedFees = fixedFees_quote;
defaultExistingFee = defaultExistingFee_quote;
sitesArray = string[];
hasMultipleSites = false;
contID = contID_quote;

//Overwrite default fee components based on user account status 
if(salesActivity_quote == "Existing Customer" AND NOT(defaultExistingFee_quote)){
	accountStatusRS = bmql("SELECT infopro_div_nbr, is_frf_locked, is_erf_locked, is_Admin_Charged, is_frf_charged, is_erf_charged, site_addr_line_1  FROM Account_Status WHERE infopro_acct_nbr = $_quote_process_customer_id");
	
	for record in accountStatusRS{
		//based on customer id, there can only be 1 unique division possible
		if (get(record, "site_addr_line_1") == _quote_process_siteAddress_quote_address){
			is_frf_locked = get(record, "is_frf_locked");
			is_erf_locked = get(record, "is_erf_locked");
			adminCharged = get(record, "is_Admin_Charged");
			is_frf_charged = get(record, "is_frf_charged");
			is_erf_charged = get(record, "is_erf_charged");
			
			if(is_frf_locked == "1" OR is_erf_locked == "1"){
				customerHasAFixedFee = true;
			}
			if(is_frf_locked == "1"){
				frfFixed = true;
			}
			if(is_erf_locked == "1"){
				erfFixed = true;
			}
			if(adminCharged == "1"){
				isAdminCharged = true;
			}	
			if(is_erf_charged == "1"){
				isERFChaged = true;
			}
			if(is_frf_charged == "1"){
				isFRFChaged = true;
			}
		}
		
		Site_Nbr = get(record, "Site_Nbr");
		//Gather unique site numbers
		if(findinarray(sitesArray, Site_Nbr) == -1){ 
			append(sitesArray, Site_Nbr);
		}
		if(sizeofarray(sitesArray) >= 2){
			//We just need to know if customer has one or more sites - update flag
			//Iterating on Account_Status recordset can be expensive - so break the loop
			hasMultipleSites = true;
			
		}
	}
	//Fixed Fees
	
	fixedFeesArray = string[];

	if(erfFixed AND isERFChaged){
		append(fixedFeesArray, "Fixed Environment Recovery Fee (ERF)");
	}
	if(frfFixed AND isFRFChaged){
		append(fixedFeesArray, "Fixed Fuel Recovery Fee (FRF)");
	}
	if(isAdminCharged){
		append(fixedFeesArray, "Admin Fee");
	}
	fixedFees = join(fixedFeesArray, ",");
	if(len(fixedFees) <> 0 AND customerHasAFixedFee == true){ //customerHasAFixedFee_quote will be true based on data table
		fees=fixedFees; //fixedFees_quote; //fixedFees_quote contains the fixed fee value from data table
		defaultExistingFee = true;
	}else{
		feesArray = string[];
		if(isFRFChaged){
			append(feesArray, "FRF");
		}
		if(isERFChaged){
			append(feesArray, "ERF");
		}
		if(isAdminCharged){
			append(feesArray, "Admin Fee");
		}
		if(sizeofarray(feesArray) > 0){
			fees = join(feesArray, ",");
		}
				
	}
}elif(defaultExistingFee_quote){ //If fee is already modified do not change user selection
	fees = replace(feesToCharge_quote, "~", ",");
}
else{
	fees="FRF,ERF,Admin Fee";
	contID="O"; // new-new default to Open Market
}

retStr = retStr + "1~feesToCharge_quote~" + fees + "|"
               + "1~customerHasAFixedFee_quote~" + string(customerHasAFixedFee) + "|"
               + "1~fixedFees_quote~" + fixedFees + "|"
               + "1~defaultExistingFee_quote~" + string(defaultExistingFee) + "|"
               + "1~customerHasMultipleSites_quote~" + string(hasMultipleSites) + "|"
               + "1~contID_quote~" + contID + "|";

return retStr;