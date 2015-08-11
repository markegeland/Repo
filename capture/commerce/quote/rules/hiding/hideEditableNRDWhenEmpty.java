// 20150325 - John Palubinskas - #449 remove check for new from competitor

//Hide Editable NRD when empty
dateFormat="%m/%d/%Y";
formattedEffectiveDate = getdate();
counter = 0;
nonEditableNRD = "";
editableNRD = "";
for line in line_process{
	counter = counter + 1;
	print line.effectiveServiceDate_line;
	print line._parent_doc_number;
	if(counter==1){
		formattedEffectiveDate = strtodate(substring(line.effectiveServiceDate_line,0,10), dateFormat);
	}
	else{
		if(line. _parent_doc_number == ""){
			if(comparedates(formattedEffectiveDate,strtodate(substring(line.effectiveServiceDate_line,0,10), dateFormat)) <> -1){
				formattedEffectiveDate = strtodate(substring(line.effectiveServiceDate_line,0,10), dateFormat);
			}
		}
	}
}

if(_system_current_step_var == "submitted_process"){
	numOfDaysIn8Months = (365 * 8) / 12;
	numOfDaysIn10Months = (365 * 10) / 12;
	
	dateAfterMonths = adddays(formattedEffectiveDate, numOfDaysIn8Months);
	if(lower(salesActivity_quote) == "new/new"){
		return true;
	}else{
		nextReviewDateFromTable = getdate();
		expectedNRD = adddays(formattedEffectiveDate, numOfDaysIn8Months);
		if(nextReviewDate_quote <> ""){
			nextReviewDateFromTable = strtojavadate(substring(nextReviewDate_quote,0,10), "MM/dd/yyyy");
		}
		if(NOT(customerRateRestriction_quote)){
			expectedNRD = adddays(formattedEffectiveDate, numOfDaysIn8Months);
		}else{
			
			expectedNRD = adddays(formattedEffectiveDate, numOfDaysIn10Months);
		}
		if(comparedates(nextReviewDateFromTable, expectedNRD) == 1 OR comparedates(nextReviewDateFromTable, expectedNRD) == 0){ //if actualNRD is after the expected NRD
			nonEditableNRD = datetostr(nextReviewDateFromTable);
		}else{ //if actual Date is before the expected date (ie less than 8 or 10 months from effective date)
			editableNRD = datetostr(expectedNRD);
		}
	}
	if(editableNRD == ""){
		return true;
	}
}	
return false;