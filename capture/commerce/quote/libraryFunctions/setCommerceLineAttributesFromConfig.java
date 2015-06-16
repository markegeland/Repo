/*
=======================================================================================================================
Name:         setCommerceLineAttributesFromConfig
Author:       Ryan Nabor
Create date:  06/12/15

Description:  Splits '_config_attr_info' and returns values in XML format
 
Input:        _config_attr_info: String - attributes from Config

Output:       String containing commerce attributes to display in document XML 

Updates:    

Debugging:     
    
=======================================================================================================================
*/
lock_line = "";
ret = "";
retArr = String[];
value = "0.00";

configArr = String[];
append(configArr, "scoutRoute");
append(configArr, "isEnclosure");
append(configArr, "lock");
append(configArr, "casters");
append(configArr, "requestPickupDays");
append(configArr, "isCustomerOwned");
append(configArr, "additionalPaperwork_l");
append(configArr, "disposalTicketSignature_l");

configFloatArr = String[];
append(configFloatArr, "drivingDistance_disposalSite");
append(configFloatArr, "customerSiteTime_disposalSite");
append(configFloatArr, "roundtripDriveTime_disposalSite");
append(configFloatArr, "disposalTime_disposalSite");
append(configFloatArr, "totalTime_disposalSite");

configIntegerArr = String[];
append(configIntegerArr, "customerSiteTimeOverride_l");
append(configIntegerArr, "roundtripDriveTimeOverride_l");
append(configIntegerArr, "disposalTimeOverride_l");

for line in line_process{
	 if(line._config_attr_info <> ""){
			for each in configArr {
				if (isnull(getconfigattrvalue(line._document_number,each))){
				ret = ret + line._document_number + "~" + each + "_line~" + "N/A" + "|";
				}else {
				ret = ret + line._document_number + "~" + each + "_line~" + getconfigattrvalue(line._document_number,each) + "|";
				}
			}for each in configFloatArr{
				floatArr = split(getconfigattrvalue(line._document_number,each), "$,$");
				alternateSite = getconfigattrvalue(line._document_number, "alternateSite_l");
					if(isnumber(alternateSite)){
						value = floatArr[atoi(alternateSite)-1];
					}	
			if (isnull(getconfigattrvalue(line._document_number,each))){
				ret = ret + line._document_number + "~" + each + "_line~" + "0.00" + "|";
			}else {
				ret = ret + line._document_number + "~" + each + "_line~" + value + "|";
			}		
			}for each in configIntegerArr {
				if (isnull(getconfigattrvalue(line._document_number,each))){
					ret = ret + line._document_number + "~" + each + "_line~" + "0" + "|";
				}else {
					ret = ret + line._document_number + "~" + each + "_line~" + getconfigattrvalue(line._document_number,each) + "|";
				}
			} 
	}
}
return ret;