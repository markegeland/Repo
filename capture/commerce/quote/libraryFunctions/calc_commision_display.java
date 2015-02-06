/*
================================================================================
Name:   		Calc_Commission_Display
Author:   		Aaron Quintanilla
Create date:  	1/29/2015
Description:  	Displays estimated commission on Commerce Quote Layout
        
Input:   		totalCommission: String - Total Commission attributes for floor, base, target, stretch, and sell
                    
Output:  		String - Output parsed as HTML by the attribute to display table of attributes

Updates:	  
    
=====================================================================================================
*/
//=============================== START - Building HTML String ===============================//
returnStr = "";
isSalesrep = true;
isExistingCustomer = false;
// Cost is seen only by manager profile, exec manager profile, and admin profile
// Manager profile is assigned to users in manager groups; exec manager profile is assigned to users in exec manager groups
// Admin profile is assigned to users with Full Access user type
if(find(lower(_system_user_groups), "manager"  ) <> -1 OR _system_user_type == "FullAccess"){
	isSalesrep = false;
}
if(lower(salesActivity_quote) == "existing customer"){
	isExistingCustomer = true;
}

//return string parser for Calc_commission
parserResults = calcCommReturnString; //The Return String comes in the format of (Document Number ~ Attribute Name ~ Attribute's Value That's why all indexes selected start index of [2] and increase by multiples of 3 
parserResults = replace(parserResults,"|","~");
attributes = split(parserResults,"~");
parseSmall = attributes[2];		
parseSmallPercent = attributes[5];
parseLarge = attributes[8];	
parseLargePercent = attributes[11];	
parseTotalOTC = attributes[14];	
parseTotal = attributes[17];
parseShowSmall = false;
if(attributes[26]=="true"){parseShowSmall = true;}	
parseShowLarge = false;
if(attributes[29]=="true"){parseShowLarge = true;}

//attributeVar

detailSmall = split(parseSmall,"^_^");
detailSmallPct = split(parseSmallPercent,"^_^");
detailLarge = split(parseLarge,"^_^");
detailLargePct = split(parseLargePercent,"^_^");
detailTotalOTC = split(parseTotalOTC,"^_^");
detailTotal = split(parseTotal,"^_^");
showCommission = false;
if(parseShowSmall == true OR parseShowLarge == true){
	showCommission = true;
}

//Combine the HTML code into a string for storage in an attribute
returnStr = returnStr   + "<table  class='rs_table_style'  width=\"100%\" cellpadding=\"3\" cellspacing=\"0\" style=\"font-family:Open Sans;\">"
						+ "<tr class='rs_table_style'>"//Start of labels row
						+ "<td align=\"center\"  width=\"20%\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Base Rate and Fee(s)</td>";
if(NOT(isSalesrep)){
	returnStr = returnStr + "<td align=\"center\"  width=\"10%\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Cost Price</td>";
}
returnStr = returnStr 	+ "<td align=\"center\"  width=\"10%\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Floor Price</td>"
						+ "<td align=\"center\"  width=\"10%\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Average Price</td>"
						+ "<td align=\"center\"  width=\"10%\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Target Price</td>";
if(isExistingCustomer){
	returnStr = returnStr + "<td align=\"center\"  width=\"10%\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Current Price</td>";
}
returnStr = returnStr 	+ "<td align=\"center\"  width=\"10%\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Proposed Price</td>";
if(isExistingCustomer){
	returnStr = returnStr + "<td align=\"center\"  width=\"20%\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Change in Price</td>";
}
returnStr = returnStr   + "</tr>";
 //End of labels row
 //Start of Commission Section
if(parseShowSmall == true){
	returnStr = returnStr	+ "<tr class='rs_tr_td_style'>" 
							+ "<td >Small Container Commission on Recurring Monthly Revenue</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailSmall[0]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailSmall[1]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailSmall[2]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailSmall[3]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailSmall[4]),"USD") + "</td>"
							+ "</tr>"		
							+ "<tr class='rs_tr_td_style'>" 
							+ "<td >Small Container Commission Rate on Recurring Monthly Revenue</td>"
							+ "<td align=\"center\" >" + string(round(atof(detailSmallPct[0])*100,2)) + "%" + "</td>"
							+ "<td align=\"center\" >" + string(round(atof(detailSmallPct[1])*100,2)) + "%" + "</td>"
							+ "<td align=\"center\" >" + string(round(atof(detailSmallPct[2])*100,2)) + "%" + "</td>"
							+ "<td align=\"center\" >" + string(round(atof(detailSmallPct[3])*100,2)) + "%" + "</td>"
							+ "<td align=\"center\" >" + string(round(atof(detailSmallPct[4])*100,2)) + "%" + "</td>"
							+ "</tr>";		
}		
if(parseShowLarge == true){
	returnStr = returnStr	+ "<tr class='rs_tr_td_style'>" 
							+ "<td >Large Container Commission on Recurring Monthly Revenue</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailLarge[0]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailLarge[1]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailLarge[2]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailLarge[3]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailLarge[4]),"USD") + "</td>"
							+ "</tr>"		
							+ "<tr class='rs_tr_td_style'>" 
							+ "<td >Large Container Commission Rate on Recurring Monthly Revenue</td>"
							+ "<td align=\"center\" >" + string(round(atof(detailLargePct[0])*100,2)) + "%" + "</td>"
							+ "<td align=\"center\" >" + string(round(atof(detailLargePct[1])*100,2)) + "%" + "</td>"
							+ "<td align=\"center\" >" + string(round(atof(detailLargePct[2])*100,2)) + "%" + "</td>"
							+ "<td align=\"center\" >" + string(round(atof(detailLargePct[3])*100,2)) + "%" + "</td>"
							+ "<td align=\"center\" >" + string(round(atof(detailLargePct[4])*100,2)) + "%" + "</td>"
							+ "</tr>";		
}	
if(showCommission == true){
	returnStr = returnStr	+ "<tr class='rs_tr_td_style'>" 
							+ "<td >Commission on One Time Charges</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailTotalOTC[0]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailTotalOTC[1]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailTotalOTC[2]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailTotalOTC[3]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailTotalOTC[4]),"USD") + "</td>"
							+ "</tr>"		
							+ "<tr class='totalRow'>" 
							+ "<td >Total Estimated Commission</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailTotal[0]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailTotal[1]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailTotal[2]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailTotal[3]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailTotal[4]),"USD") + "</td>"
							+ "</tr>";		
}				
//End of Commission Section				
	 returnStr = returnStr	+ "<tr style='display:none;'>" //Start of Commissions - Hide this for Pilot
							+ "<td  >Est. Commission</td>";
if(NOT(isSalesrep)){
	returnStr = returnStr + "<td  align=\"center\" >" +  formatascurrency(totalCommissionFloor_quote,"USD") + "</td>";
}
returnStr = returnStr 	+ "<td  align=\"center\" >" +  formatascurrency(totalCommissionBase_quote,"USD") + "</td>"
						+ "<td  align=\"center\" >" +  formatascurrency(totalCommissionTarget_quote,"USD") + "</td>"
						+ "<td  align=\"center\" >" +  formatascurrency(totalCommissionStretch_quote,"USD") + "</td>";
if(isExistingCustomer){	
	returnStr = returnStr + "<td  align=\"center\" >" +  "Commission Value" + "</td>";
}
returnStr = returnStr   + "<td  align=\"center\" >" +  formatascurrency(totalCommissionSell_quote,"USD") + "</td>";
if(isExistingCustomer){	
	returnStr = returnStr + "<td  align=\"center\" >" +  "Commission Value" + "</td>";
}
returnStr = returnStr + "</tr>"  //End of Commissions
						+ "</table>";

//=============================== End - Building HTML String ===============================//

return returnStr;