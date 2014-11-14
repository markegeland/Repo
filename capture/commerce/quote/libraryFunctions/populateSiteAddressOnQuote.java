/*
Commerce Library function - Populate site address on quote
Purpose - Populate site related quote attributes for CSA Output
inputParams - String Dict - Not used now. Added for future flexibilty if any inputs need to be passed.

Latha
11/13/2013
*/
result = "";
DELIMITER = "|";
partArray = string[];
for line in line_process {
	lineDocNum = line._document_number;
	if(line._part_number == ""){
		//result = result + "1~siteName_quote~"+ getconfigattrvalue(lineDocNum, "siteName") + DELIMITER;
		result = result + "1~siteNumber_quote~"+ getconfigattrvalue(lineDocNum, "siteNumber") + DELIMITER;
		result = result + "1~siteStreet_quote~"+ getconfigattrvalue(lineDocNum, "siteStreet") + DELIMITER;
		result = result + "1~siteDirection1_quote~"+ getconfigattrvalue(lineDocNum, "direction1") + DELIMITER;
		result = result + "1~siteDirection2_quote~"+ getconfigattrvalue(lineDocNum, "direction2") + DELIMITER;
		result = result + "1~siteTypeOfStreet_quote~"+ getconfigattrvalue(lineDocNum, "typeOfStreet") + DELIMITER;
		result = result + "1~siteCity_quote~"+ getconfigattrvalue(lineDocNum, "siteCity") + DELIMITER;
		result = result + "1~siteState_quote~"+ getconfigattrvalue(lineDocNum, "siteState") + DELIMITER;
		result = result + "1~siteZipCode_quote~"+ getconfigattrvalue(lineDocNum, "zipcode") + DELIMITER;
		result = result + "1~siteContactName_quote~"+ getconfigattrvalue(lineDocNum, "contactName") + DELIMITER;
		result = result + "1~siteContactTitle_quote~"+ getconfigattrvalue(lineDocNum, "contactTitle") + DELIMITER;
		break;
	}
}

return result;