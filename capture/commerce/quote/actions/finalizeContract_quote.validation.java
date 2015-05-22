/* 
=========================================================================================================
       Name: finalizeContract_quote.validation
     Author: Rob Brozyna
Create date: May 2015
Description: Performs final validation of the quote to ensure that the site and contact info matches
             the SFDC opportunity info.  If validation fails, a message is displayed to Refresh Contacts.

Updates:
20150521 John Palubinskas - #518 initial commit, and add comment header
=========================================================================================================
*/

ret = "";

if(sourceSystem_quote == "SFDC"){
    valueDict = dict("string");
    put(valueDict,"authorizedBy_quote",authorizedBy_quote);
    put(valueDict,"_siteAddress_quote_phone",_quote_process_siteAddress_quote_phone);
    put(valueDict,"_siteAddress_quote_first_name",_quote_process_siteAddress_quote_first_name);
    put(valueDict,"_siteAddress_quote_last_name",_quote_process_siteAddress_quote_last_name);
    put(valueDict,"_siteAddress_quote_email",_quote_process_siteAddress_quote_email);
    put(valueDict,"contactTelNoGenerateDocs_quote",contactTelNoGenerateDocs_quote);
    put(valueDict,"contactNameGenerateDocs_quote",contactNameGenerateDocs_quote);
    put(valueDict,"contactTitleGenerateDocs_quote",contactTitleGenerateDocs_quote);
    put(valueDict,"contactEmailGenerateDocs_quote",contactEmailGenerateDocs_quote);
    put(valueDict,"contactConfirmEmailGenerateDocs_quote",contactConfirmEmailGenerateDocs_quote);
    put(valueDict,"_billTo_first_name",_quote_process_billTo_first_name);
    put(valueDict,"_billTo_email",_quote_process_billTo_email);
    put(valueDict,"billToOfficeNumber_quote",billToOfficeNumber_quote);
    put(valueDict,"billToMobileNumber_quote",billToMobileNumber_quote);

    contactValidationArr = split(sfdcContactStringValidation_quote,"!&!");

    for contact in contactValidationArr {
        print contact;
        validArr = split(contact,"!nv!");
        print validArr[0];
        if(containskey(valueDict,trim(validArr[0]))){
            currValue = get(valueDict,trim(validArr[0]));
            print currValue;
            if(trim(currValue) <> trim(validArr[1])){
                ret = "Contact and/or Site Name does not match the Opportunity. Please go to Previous screen and click ‘Refresh Contacts'";
            }
        }
    }

    if(trim(siteName_quote) <> trim(sfdcSiteNameValidation_quote)){
        ret = "Contact and/or Site Name does not match the Opportunity. Please go to Previous screen and click ‘Refresh Contacts'";
    }
}

return ret;