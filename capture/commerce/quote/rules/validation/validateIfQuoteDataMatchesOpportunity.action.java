/* 
=========================================================================================================
       Name: validateIfQuoteDataMatchesOpportunity.action.java
     Author: Rob Brozyna
Create date: May 21 2015
Description: #619 Performs final validation of the quote to ensure that the quote data still matches 
             the opportunity data. 

Updates:

=========================================================================================================
*/

ret = dict("dict<string>");
attributesWithMismatches = "";

if(sourceSystem_quote == "SFDC"){
    valueDict = dict("string");
    
	//values in sfdcOpportunityStringValidation_quote
    put(valueDict, "area_quote", area_quote);
    put(valueDict, "division_quote", division_quote);
    put(valueDict, "salesActivity_quote", salesActivity_quote);
    put(valueDict, "industry_quote", industry_quote);
	
	//values in sfdcSiteStringValidation_quote
	put(valueDict, "_siteAddress_quote_address", _quote_process_siteAddress_quote_address);
	put(valueDict, "_siteAddress_quote_address_2", _quote_process_siteAddress_quote_address_2);
	put(valueDict, "_siteAddress_quote_city", _quote_process_siteAddress_quote_city);
	put(valueDict, "_siteAddress_quote_state", _quote_process_siteAddress_quote_state);
	put(valueDict, "_siteAddress_quote_zip", _quote_process_siteAddress_quote_zip);
	put(valueDict, "siteLongitude", siteLongitude);
	put(valueDict, "siteLatitude", siteLatitude);

    oppValidationArr = split(sfdcOpportunityStringValidation_quote,"!&!");
	siteValidationArr = split( 	sfdcSiteStringValidation_quote, "!&!");

    for oppAttr in oppValidationArr {
        thisPairArr = split(oppAttr,"!nv!");
        if(containskey(valueDict,trim(thisPairArr[1]))){
            thisValue = get(valueDict,trim(thisPairArr[1]));
            if(trim(thisValue) <> trim(thisPairArr[2])){  
				attributesWithMismatches = attributesWithMismatches + thisPairArr[0] + "; ";
			}
               
        }
    }
	
	for siteAttr in siteValidationArr {
		thisPairArr = split(siteAttr,"!nv!");
        if(containskey(valueDict,trim(thisPairArr[1]))){
            thisValue = get(valueDict,trim(thisPairArr[1]));
            if(trim(thisValue) <> trim(thisPairArr[2])){  
				attributesWithMismatches = attributesWithMismatches + thisPairArr[0] + "; ";
			}
               
        }
    }
	
	if (attributesWithMismatches <> "") {
		// inner dictionary
		validationDict = dict("string");
		// assembling the constraint action
		put(validationDict, BM_CM_RULES_MESSAGE, "The following Opportunity fields have been modified since this quote has been created. Please create a new quote. ("+attributesWithMismatches+")");
		// adding the optional message location key
		put(validationDict, BM_CM_RULES_LOCATION, "top");
		// put the inner dictionary into the outer dictionary
		put(ret, "sfdcOpportunityStringValidation_quote", validationDict);
	}
	
}

return ret;
