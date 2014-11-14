nonEditableNRD = "";
editableNRD = "";
if(_system_current_step_var == "submitted_process"){
	numOfDaysIn8Months = (365 * 8) / 12;
	numOfDaysIn10Months = (365 * 10) / 12;
	formattedEffectiveServiceDate = strtojavadate(substring(effectiveServiceDate_quote,0,10), "MM/dd/yyyy");
	dateAfterMonths = adddays(formattedEffectiveServiceDate, numOfDaysIn8Months);
	if(lower(salesActivity_quote) == "new/new" OR lower(salesActivity_quote) == "new from competitor"){
		return true;
	}else{
		nextReviewDateFromTable = getdate();
		expectedNRD = adddays(formattedEffectiveServiceDate, numOfDaysIn8Months);
		if(nextReviewDate_quote <> ""){
			nextReviewDateFromTable = strtojavadate(substring(nextReviewDate_quote,0,10), "MM/dd/yyyy");
		}
		if(NOT(customerRateRestriction_quote)){
			expectedNRD = adddays(formattedEffectiveServiceDate, numOfDaysIn8Months);
		}else{
			
			expectedNRD = adddays(formattedEffectiveServiceDate, numOfDaysIn10Months);
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