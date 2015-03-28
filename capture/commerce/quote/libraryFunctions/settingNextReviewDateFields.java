/* 
================================================================================
       Name: settingNextReviewDateFields
     Author: ???
Create date: ??? 
Description: Sets the editable and non-editable NRD fields
        
  Input:  salesActivity_quote
          effectiveServiceDate_quote
          customerRateRestriction_quote
          nextReviewDate_quote
                    
 Output:  String (documentNumber + "~" + nextReviewDate_non_editable_quote + "~" + value + "|"
                  documentNumber + "~" + nextReviewDate_editable_quote + "~" + value + "|")

Updates:
          20141022 John Palubinskas - (Issue 870) updated to always set the NRD to fall on the 1st of the month
          20140930 John Palubinskas - (Issue 809) updated to have NRD of 10 months even if not rate restricted
          20150325 John Palubinskas - #449 remove references to new from competitor
=====================================================================================================
*/

retStr = "";
dateSeparator="/";
dateFormat="MM/dd/yyyy";
nonEditableNRD = "";
editableNRD = "";

if(_system_current_step_var == "generateDocuments"){

    numOfDaysIn8Months = (365 * 8) / 12;
    numOfDaysIn10Months = (365 * 10) / 12;
    formattedEffectiveServiceDate = strtojavadate(substring(effectiveServiceDate_quote,0,10), "yyyy-MM-dd");
    dateAfterMonths = adddays(formattedEffectiveServiceDate, numOfDaysIn10Months);

    if(lower(salesActivity_quote) == "new/new"){

        nonEditableNRD = datetostr(adddays(formattedEffectiveServiceDate, numOfDaysIn10Months)); 

    }else{

        nextReviewDateFromTable = getdate();
        expectedNRD = getdate();

        if(nextReviewDate_quote <> ""){
            nextReviewDateFromTable = strtojavadate(substring(nextReviewDate_quote,0,10), "yyyy-MM-dd");
        }

        // exiting customers with a rate restriction defined get NRD set 10 months out, without a restriction gets 8 months
        if(NOT(customerRateRestriction_quote)){
            expectedNRD = adddays(formattedEffectiveServiceDate, numOfDaysIn8Months);
        }else{
            expectedNRD = adddays(formattedEffectiveServiceDate, numOfDaysIn10Months);
        }

        if(comparedates(nextReviewDateFromTable, expectedNRD) == 1 OR comparedates(nextReviewDateFromTable, expectedNRD) == 0){
            // actual NRD is after the expected NRD
            nonEditableNRD = datetostr(nextReviewDateFromTable);
        }else{ 
            // actual NRD is before the expected NRD
            editableNRD = datetostr(expectedNRD);
        }

    }

    if (nonEditableNRD <> "") {
        nonEditableNRDDateOnly = substring(nonEditableNRD,0,10);
        dateArr = split(nonEditableNRDDateOnly, "/");
        dateArr[1] = "01";
        nonEditableNRD = dateArr[0] + dateSeparator + dateArr[1] + dateSeparator + dateArr[2];
    }

    if (editableNRD <> "") {
        editableNRDDateOnly = substring(editableNRD,0,10);
        dateArr = split(editableNRDDateOnly, "/");
        dateArr[1] = "01";
        editableNRD = dateArr[0] + dateSeparator + dateArr[1] + dateSeparator + dateArr[2];
    }

    retStr = retStr + "1~nextReviewDate_non_editable_quote~" + nonEditableNRD + "|";
    retStr = retStr + "1~nextReviewDate_editable_quote~" + editableNRD + "|";

}

return retStr;