result="";
if(_system_current_step_var == "start_step"){
	divisionSalesGroup = "d" + division_quote + "SalesReps";
	divisionManagerGroup = "d" + division_quote + "Managers";
	divisionExecManagerGroup = "d" + division_quote + "ExecManagers";
	
	result = result + "1~progressStarted_quote~" + getstrdate() + "|"
				+ "1~" + "divisionSalesGroup_quote" + "~" + (divisionSalesGroup) + "|"
				+ "1~" + "divisionManagerGroup_quote" + "~" + (divisionManagerGroup) + "|"
				+ "1~" + "divisionExecManagerGroup_quote" + "~" + (divisionExecManagerGroup) + "|";
}

// added this code for assigning the default fee values
fees="";
containerCnt= 0;
customerHasAFixedFee = customerHasAFixedFee_quote;
if(salesActivity_quote == "Existing Customer"){	
	divisionNbr = atoi(division_quote);
	numberOfContainersRecSet = bmql("SELECT Container_Grp_Nbr FROM Account_Status WHERE infopro_acct_nbr = $_quote_process_customer_id  AND Site_Nbr = $siteNumber_quote");
	for each in numberOfContainersRecSet{
		containerCnt = containerCnt + 1;
	}
	//Overwrite division if user selects a different one than the one related to account
	accountStatusRS = recordset();
	if(containerGroupForTransaction_quote <> ""){
		accountStatusRS = bmql("SELECT division_nbr, infopro_div_nbr, is_frf_locked, is_erf_locked, is_frf_charged, is_erf_charged, is_Admin_Charged FROM Account_Status WHERE infopro_acct_nbr = $_quote_process_customer_id AND Site_Nbr = $siteNumber_quote AND Container_Grp_Nbr = $containerGroupForTransaction_quote");
	}else{
		accountStatusRS = bmql("SELECT division_nbr, infopro_div_nbr, is_frf_locked, is_erf_locked, is_frf_charged, is_erf_charged, is_Admin_Charged FROM Account_Status WHERE infopro_acct_nbr = $_quote_process_customer_id AND Site_Nbr = $siteNumber_quote AND division_nbr = $divisionNbr");	
	} 
	
	division = division_quote;
	feeArray = string[];
	for record in accountStatusRS{
		//based on customer id, there can only be 1 unique division possible
		division = get(record, "division_nbr");
		is_frf_locked = get(record, "is_frf_locked");
		is_erf_locked = get(record, "is_erf_locked");
		is_frf_charged = get(record, "is_frf_charged");
		is_erf_charged = get(record, "is_erf_charged");
		is_Admin_Charged = get(record, "is_Admin_Charged");
		if(is_frf_locked == "1" OR is_erf_locked == "1"){
			customerHasAFixedFee = true;
		}
		if(is_frf_charged == "1"){
			append(feeArray, "FRF");
		}
		if(is_erf_charged == "1"){
			append(feeArray, "ERF");
		}
		if(is_Admin_Charged == "1"){
			append(feeArray, "Admin Fee");
		}
		break;
	}
	//Fixed Fees
	
	if(division <> division_quote){
		result = result + "1~division_quote~"+division + "|";
	}
	
	if(len(fixedFees_quote) <> 0 AND customerHasAFixedFee){ //customerHasAFixedFee will be true based on data table
		fees=fixedFees_quote; //fixedFees_quote contains the fixed fee value from data table
	}else{
		//Make sure to add fee only if customer already has them on current container
		if(sizeofarray(feeArray) > 0){
			fees = join(feeArray, ",");
		}
	}
}
//For all new customers - all 3 fee would apply unless waived by user
else{
	fees="FRF,ERF,Admin Fee";
}
//Populate info pro division for selected division from Division_Mapping table

infoProDivision = infoProDivision_quote;
if(infoProDivision == ""){
	divisionMappingRecordSet = bmql("SELECT infoProDivision FROM Division_Mapping WHERE lawsonDivisionNumber = $division_quote");
	for eachRecord in divisionMappingRecordSet{
		infoProDivision = get(eachRecord, "infoProDivision");
		break;
	}
	//Error handling on next action if multiple info pro divisions are available for a division
	//and user has not selected one from it in 1st screen - handler for legacy quotes
	//result =result + "1~infoProDivision_quote~" + infoProDivision + "|";
}
result =result + "1~infoproDivision_RO_quote~" + infoProDivision + "|";

result =result + "1~feesToCharge_quote~" + fees + "|";


result = result + "1~numOfContainersOnThisAccountAndSite_quote~" + string(containerCnt) + "|";


return result + commerce.setStatus("next");

