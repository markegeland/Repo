/* 
================================================================================
Name:   unconfiguredServicesString
Author:       ???
Create date:  ???
Description:  Generates HTML output for the CSA for unconfigured containers (Same containers from existing customers)
        
Input:      
                    
Output:     String ("1~unconfiguredServices_richtext_quote~" + value + "|")

Updates:    20141013 - John Palubinskas - Removed Open/Close Date field, changed compactor and customer owned
                       to be Y/N. Set L/F Code to be disposal_site_code + land_fill_price_cd.  Added code header.
                       Fixed container size field to properly handle sizes to two decimal points; e.g. 0.45 yd.
					  
			20150220 - Gaurav Dawar - #372 - Changed the service frequency string to populate the correct service frequency
        
=====================================================================================================
*/
configuredContainers = string[]; //contains only configured container numbers
allContainerGroups = string[]; //contains all container numbers belonging to the site selected on the quote
unconfiguredContainers = string[]; //contains only unconfigured container numbers
containerDict = dict("dict<string>");
LARGE_CONTAINER = "Large Containers";
SMALL_CONTAINER = "Containers";

htmlStr = "";
if(salesActivity_quote == "Existing Customer" OR salesActivity_quote == "Change of Owner"){
	//Get the containerGroups for the selected account and site
	accountStatusRecordSet = bmql("SELECT Container_Grp_Nbr, Container_Cd, Container_Size, has_Compactor, container_cnt, Acct_Type, is_container_owned, Pickup_Per_Tot_Lifts, Pickup_Period_Length, land_fill_price_cd, disposal_site_code, close_dt, Original_Open_Dt, container_category, monthly_rate, Expiration_Dt, infopro_div_nbr, infopro_acct_nbr, division_nbr, Site_Nbr, acct_nbr  FROM Account_Status WHERE infopro_acct_nbr = $_quote_process_customer_id  AND Site_Nbr = $siteNumber_quote");
	for each in accountStatusRecordSet{
		thisContainerDict = dict("string");
		containerGroup = get(each,"Container_Grp_Nbr");
		append(allContainerGroups,containerGroup);
		put(thisContainerDict, "Container_Grp_Nbr", containerGroup);
		put(thisContainerDict, "Container_Cd", get(each,"Container_Cd"));
		put(thisContainerDict, "Container_Size", get(each,"Container_Size"));
		put(thisContainerDict, "has_Compactor", get(each,"has_Compactor"));
		put(thisContainerDict, "container_cnt", get(each,"container_cnt"));
		put(thisContainerDict, "Acct_Type", get(each,"Acct_Type"));
		put(thisContainerDict, "is_container_owned", get(each,"is_container_owned"));
		put(thisContainerDict, "Pickup_Per_Tot_Lifts", get(each,"Pickup_Per_Tot_Lifts"));
		put(thisContainerDict, "Pickup_Period_Length", get(each,"Pickup_Period_Length"));
		put(thisContainerDict, "land_fill_price_cd", get(each,"land_fill_price_cd"));
		put(thisContainerDict, "disposal_site_code", get(each,"disposal_site_code"));
		put(thisContainerDict, "close_dt", get(each,"close_dt"));
		put(thisContainerDict, "Original_Open_Dt", get(each,"Original_Open_Dt"));
		put(thisContainerDict, "container_category", get(each,"container_category"));
		put(thisContainerDict, "monthly_rate", get(each,"monthly_rate"));
		put(thisContainerDict, "Expiration_Dt", get(each,"Expiration_Dt"));
		put(thisContainerDict, "infopro_div_nbr", get(each,"infopro_div_nbr"));
		put(thisContainerDict, "infopro_acct_nbr", get(each,"infopro_acct_nbr"));
		put(thisContainerDict, "division_nbr", get(each,"division_nbr"));
		put(thisContainerDict, "Site_Nbr", get(each,"Site_Nbr"));
		put(thisContainerDict, "acct_nbr", get(each,"acct_nbr"));
		//Add current container to containers dictionary
		put(containerDict, containerGroup, thisContainerDict);
	}
	//Get the containerGroups that have been configured
	for line in line_process{
		if(line._model_variable_name <> ""){
			if(line.containerNumberOfCurrentService_line <> ""){
				containerNum = line.containerNumberOfCurrentService_line;
				if(findinarray(configuredContainers, containerNum) == -1){
					append(configuredContainers, containerNum);
				}	
			}
		}
	}
	//Find the unconfigured container Groups
	for each in allContainerGroups{
		//if the container is not configured, then capture into unconfigured array
		if(findinarray(configuredContainers, each) == -1){
			append(unconfiguredContainers, each);
		}
	}
	//We need the following details from Account Status table for each of the unconfigured services for CSA line item grid
	/*
	1. New/old line - it is old line
	2. Container Grp Number
	3. Route Type /Container Code
	4. Equipment size 
	5. Has compactor? (Y,N)
	6. Quantity
	7. Account Type
	8. Customer Owned? (Y,N)
	9. Grid - this is empty
	10. PickupTotals lifts and Pickup Period Length
	11. Estimated Lifts - empty
	12. S - empty
	13. P.O Req - empty
	14. Receipt Req - empty
	15. L/F Code - from Account status table
	16. Open/Close Date - from Acct Status table (blank this)
	17. LOB - Container Category from table
	18. PreBill - empty
	19. Lift Charge - empty
	20. Monthly Service - from table
	21. Extra Lift - from table
	22. Disposal Rate - empty
	23. Other - empty
	24. Rate/unit - empty
	25. Period Rate - empty
	*/
	//Get the container string from util 'getContainerGroups'
	//Prepare the HTML string for each unconfigured container
	if(sizeofarray(unconfiguredContainers) > 0){
		htmlStr = htmlStr + "<table>";
		htmlStr = htmlStr + "<tbody>";
		//There should be one row for each unconfigured container in this table
		for each in unconfiguredContainers{
			if(each <> ""){
				if(containskey(containerDict, each)){
					//Get the current container data from containerDict
					thisContainerDict = get(containerDict, each);
					
					infopro_div_nbr = get(thisContainerDict, "infopro_div_nbr");
					infopro_acct_nbr = get(thisContainerDict, "infopro_acct_nbr");
					division_nbr = get(thisContainerDict, "division_nbr");
					Site_Nbr = get(thisContainerDict, "Site_Nbr");
					Container_Cd = get(thisContainerDict, "Container_Cd");
					acct_nbr = get(thisContainerDict, "acct_nbr");
					container_category = get(thisContainerDict, "container_category");
					priceType = "";
					if(lower(container_category) == "commercial"){
						priceType = SMALL_CONTAINER;
					}elif(lower(container_category) == "industrial"){
						priceType = LARGE_CONTAINER;
					}
					
					//Get supplemental charges for current container
					supplementalChargesInputDict = dict("string");
					put(supplementalChargesInputDict, "containerGroup", each);
					put(supplementalChargesInputDict, "infoProDivNumber", infopro_div_nbr);
					put(supplementalChargesInputDict, "siteNumber_quote", Site_Nbr);
					put(supplementalChargesInputDict, "priceType", priceType);
					put(supplementalChargesInputDict, "division", division_nbr); 
					put(supplementalChargesInputDict, "routeTypeDerived", Container_Cd); 
					put(supplementalChargesInputDict, "acct_nbr", acct_nbr);
					
					supplementalChargesOutputDict = util.supplementalCharges(supplementalChargesInputDict);
					extraLiftsCharge = "0.0";
					if(containskey(supplementalChargesOutputDict, "EXT")){
						extraLiftsCharge = get(supplementalChargesOutputDict, "EXT");
					}
					
					border = "0.5pt solid #000000";
					htmlStr = htmlStr + "<tr>";
					//Column 1 - New/old line - it is old line 'S' = Same
					if ((contractStatus_quote <> "Close Account") and (contractStatus_quote <> "Close Site")) {
						htmlStr = htmlStr + "<td style='width:0.225in;border:"+border+";background-color: #e7e7e8;text-align:center;'>S</td>";
					}else {
						htmlStr = htmlStr + "<td style='width:0.225in;border:"+border+";background-color: #e7e7e8;text-align:center;'> </td>";
					}
					//Column 2 - Container Grp Number
					htmlStr = htmlStr + "<td style='width:0.325in;border:"+border+";background-color: #e7e7e8;text-align:center;'>"+get(thisContainerDict, "Container_Grp_Nbr")+"</td>";
					//Column 3 - Route Type /Container Code
					htmlStr = htmlStr + "<td style='width:0.33in;border:"+border+";background-color: #e7e7e8;text-align:center;'>"+Container_Cd+"</td>";
					//Column 4 - Equipment size - need to handle sizes like 2.0, 8.0, 0.45
					contSize = get(thisContainerDict, "Container_Size");
					if(isnumber(contSize)){
						parsedContSize = atof(contSize);
						parsedContSize = round(parsedContSize, 2);
						contSize = string(parsedContSize);
					}
					
					htmlStr = htmlStr + "<td style='width:0.475in;border:"+border+";background-color: #e7e7e8;text-align:center;'>" + contSize + " Yd(s)</td>";
					//Column 5 - Has compactor? (Y,N)
					hasCompactor = get(thisContainerDict, "has_Compactor");
					if(hasCompactor == "1") { 
						hasCompactor = "Y";
					} else { 
						hasCompactor = "N";
					}
					htmlStr = htmlStr + "<td style='width:0.33in;border:"+border+";background-color: #e7e7e8;text-align:center;'>"+hasCompactor+"</td>";
					//Column 6 - Quantity
					htmlStr = htmlStr + "<td style='width:0.325in;border:"+border+";background-color: #e7e7e8;text-align:center;'>"+get(thisContainerDict, "container_cnt")+"</td>";
					//Column 7 - Account Type
					htmlStr = htmlStr + "<td style='width:0.475in;border:"+border+";background-color: #e7e7e8;text-align:center;'>"+get(thisContainerDict, "Acct_Type")+"</td>";
					//Column 8 - Customer Owned?
					customerOwned = get(thisContainerDict, "is_container_owned");
					if(customerOwned == "1") { 
						customerOwned = "Y";
					} else { 
						customerOwned = "N";
					}

					htmlStr = htmlStr + "<td style='width:0.325in;border:"+border+";background-color: #e7e7e8;text-align:center;'>"+customerOwned+"</td>";
					//Column 9 - Grid - this is empty
					htmlStr = htmlStr + "<td style='width:0.325in;border:"+border+";background-color: #e7e7e8;text-align:center;'><p/></td>";
					
					//Column 10 - PickupTotals lifts and Pickup Period Length
					Pickup_Per_Tot_Lifts = get(thisContainerDict, "Pickup_Per_Tot_Lifts");
					Pickup_Period_Length = get(thisContainerDict, "Pickup_Period_Length");
					Pickup_Period_LengthStr = "";
					if(Pickup_Period_Length == "2"){
						Pickup_Period_LengthStr = "2/W"; //#372 - Changed from "1/EOW" - GD - 20150220
					}elif(Pickup_Period_Length == "4"){
						Pickup_Period_LengthStr = "4/W"; //#372 - Changed from "1/Every 4 Weeks" - GD - 20150220
					}elif(Pickup_Period_Length == "3"){
						Pickup_Period_LengthStr = "3/W"; //#372 - Changed from "1/Every 3 Weeks" - GD - 20150220
					}elif(Pickup_Period_Length == "1"){
						Pickup_Period_LengthStr = "1/W";
					}
					htmlStr = htmlStr + "<td style='width:0.58in;border:"+border+";background-color: #e7e7e8;text-align:center;'>"+Pickup_Per_Tot_Lifts+"/"+Pickup_Period_LengthStr+"</td>";
					//Column 11 - Estimated Lifts - empty
					htmlStr = htmlStr + "<td style='width:0.475in;border:"+border+";background-color: #e7e7e8;text-align:center;'><p/></td>";
					//Column 12 - S - empty
					htmlStr = htmlStr + "<td style='width:0.225in;border:"+border+";background-color: #e7e7e8;text-align:center;'><p/></td>";
					//Column 13 - P.O Req - empty
					if ((contractStatus_quote <> "Close Account") and (contractStatus_quote <> "Close Site")) {
						htmlStr = htmlStr + "<td style='width:0.275in;border:"+border+";background-color: #e7e7e8;text-align:center;'><p/></td>";
						//Column 14 - Receipt Req - empty
						htmlStr = htmlStr + "<td style='width:0.38in;border:"+border+";background-color: #e7e7e8;text-align:center;'><p/></td>";
						//Column 15 - L/F Code - from Account status table, set to disposal_site_code + landfill_price_cd
						htmlStr = htmlStr + "<td style='width:0.38in;border:"+border+";background-color: #e7e7e8;text-align:center;'>"+get(thisContainerDict, "disposal_site_code")+get(thisContainerDict, "land_fill_price_cd")+"</td>";
						
						//Column 16 - Open/Close Date - from Acct Status table
						// 20141010 JPalubinskas - decision made to hide date since exp dt is wrong one to put here.
						//   We really need the original rate eff dt from the disposal or haul rate, but since this is a Same container, it's already
						//   governed by an existing CSA...so no date is necessary.
						// Expiration_Dt = get(thisContainerDict, "Expiration_Dt");
						// year = substring(Expiration_Dt, 0, 4);
						// month = substring(Expiration_Dt, 4, 6); 
						// dateStr = substring(Expiration_Dt, 6, 8); 

						// Expiration_Dt_Str = month + "/" + dateStr + "/" + year;
						htmlStr = htmlStr + "<td style='width:0.475in;border:"+border+";background-color: #e7e7e8;text-align:center;'>&nbsp;</td>";
						//Column 17 - LOB - Container Category from table
						htmlStr = htmlStr + "<td style='width:0.375in;border:"+border+";background-color: #e7e7e8;text-align:center;'>"+container_category+"</td>";
						//Column 18 - PreBill - empty
						htmlStr = htmlStr + "<td style='width:0.375in;border:"+border+";background-color: #e7e7e8;text-align:center;'><p/></td>";
						//Column 19 - Lift Charge - empty
						htmlStr = htmlStr + "<td style='width:0.475in;border:"+border+";background-color: #e7e7e8;text-align:center;'><p/></td>";
						//Column 20 - Monthly Service - from table
						htmlStr = htmlStr + "<td style='width:0.48in;border:"+border+";background-color: #e7e7e8;text-align:center;'>"+formatascurrency(atof(get(thisContainerDict, "monthly_rate")),"USD")+"</td>";
						//Column 21 - Extra Lift - from table
						htmlStr = htmlStr + "<td style='width:0.475in;border:"+border+";background-color: #e7e7e8;text-align:center;'>"+formatascurrency(atof(extraLiftsCharge), "USD")+"</td>";
						//Column 22 - Disposal Rate - empty
						htmlStr = htmlStr + "<td style='width:0.475in;border:"+border+";background-color: #e7e7e8;text-align:center;'><p/></td>";
						//Column 23 - Other - empty
						htmlStr = htmlStr + "<td style='width:0.375in;border:"+border+";background-color: #e7e7e8;text-align:center;'><p/></td>";
						//Column 24 - Rate/unit - empty
						htmlStr = htmlStr + "<td style='width:0.48in;border:"+border+";background-color: #e7e7e8;text-align:center;'><p/></td>";
						//Column 25 - Period Rate - empty
						htmlStr = htmlStr + "<td style='width:0.475in;border:"+border+";background-color: #e7e7e8;text-align:center;'><p/></td>";
					}
					else {
						htmlStr = htmlStr + "<td style='width:0.275in;border:"+border+";background-color: #e7e7e8;text-align:center;'><p/></td>";
						//Column 14 - Receipt Req - empty
						htmlStr = htmlStr + "<td style='width:0.38in;border:"+border+";background-color: #e7e7e8;text-align:center;'><p/></td>";
						//Column 15 - L/F Code - from Account status table
						htmlStr = htmlStr + "<td style='width:0.38in;border:"+border+";background-color: #e7e7e8;text-align:center;'></td>";
						
						//Column 16 - Open/Close Date - from Acct Status table
						htmlStr = htmlStr + "<td style='width:0.475in;border:"+border+";background-color: #e7e7e8;text-align:center;'></td>";
						//Column 17 - LOB - Container Category from table
						htmlStr = htmlStr + "<td style='width:0.375in;border:"+border+";background-color: #e7e7e8;text-align:center;'></td>";
						//Column 18 - PreBill - empty
						htmlStr = htmlStr + "<td style='width:0.375in;border:"+border+";background-color: #e7e7e8;text-align:center;'><p/></td>";
						//Column 19 - Lift Charge - empty
						htmlStr = htmlStr + "<td style='width:0.475in;border:"+border+";background-color: #e7e7e8;text-align:center;'><p/></td>";
						//Column 20 - Monthly Service - from table
						htmlStr = htmlStr + "<td style='width:0.48in;border:"+border+";background-color: #e7e7e8;text-align:center;'></td>";
						//Column 21 - Extra Lift - from table
						htmlStr = htmlStr + "<td style='width:0.475in;border:"+border+";background-color: #e7e7e8;text-align:center;'></td>";
						//Column 22 - Disposal Rate - empty
						htmlStr = htmlStr + "<td style='width:0.475in;border:"+border+";background-color: #e7e7e8;text-align:center;'><p/></td>";
						//Column 23 - Other - empty
						htmlStr = htmlStr + "<td style='width:0.375in;border:"+border+";background-color: #e7e7e8;text-align:center;'><p/></td>";
						//Column 24 - Rate/unit - empty
						htmlStr = htmlStr + "<td style='width:0.48in;border:"+border+";background-color: #e7e7e8;text-align:center;'><p/></td>";
						//Column 25 - Period Rate - empty
						htmlStr = htmlStr + "<td style='width:0.475in;border:"+border+";background-color: #e7e7e8;text-align:center;'><p/></td>";
					}
						
					htmlStr = htmlStr + "</tr>";
				}
			}
		}
		htmlStr = htmlStr + "</tbody>";
		htmlStr = htmlStr + "</table>";
	}
}	
//htmlStr = "<table cellspacing='0' cellpadding='0' border='0' bmhasheader='1' bmunits='in' bmcols='0.25,0.35,0.35,0.5,0.35,0.35,0.5,0.35,0.35,0.6,0.5,0.25,0.3,0.4,0.4,0.5,0.4,0.4,0.5,0.5,0.5,0.5,0.4,0.5,0.5,' class='mceItemTable'>" +"<colgroup><col style='width: 0.25in;'><col style='width: 0.35in;'><col style='width: 0.35in;'><col style='width: 0.5in;'><col style='width: 0.35in;'><col style='width: 0.35in;'><col style='width: 0.5in;'><col style='width: 0.35in;'><col style='width: 0.35in;'><col style='width: 0.6in;'><col style='width: 0.5in;'><col style='width: 0.25in;'><col style='width: 0.3in;'><col style='width: 0.4in;'><col style='width: 0.4in;'><col style='width: 0.5in;'><col style='width: 0.4in;'><col style='width: 0.4in;'><col style='width: 0.5in;'><col style='width: 0.5in;'><col style='width: 0.5in;'><col style='width: 0.5in;'><col style='width: 0.4in;'><col style='width: 0.5in;'><col style='width: 0.5in;'></colgroup> " +"<tbody><tr mce_style='background-color: #ffffff;' style='background-color: #ffffff;'>" +"<td style='width: 0.25in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p align='center'>A1</p></td>" +"<td style='width: 0.35in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p align='center'>A2</p></td>" +"<td style='width: 0.3in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p align='center'>A3</p></td>" +"<td style='width: 0.3in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p align='center'>A4</p></td>" +"<td style='width: 0.25in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p align='center'>A5</p></td>" +"<td style='width: 0.25in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p align='center'>A6</p></td>" +"<td style='width: 0.5in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p align='center'>A7</p></td>" +"<td style='width: 0.25in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p align='center'>A8</p></td>" +"<td style='width: 0.3in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p>A9</p></td>" +"<td style='width: 0.6in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p align='center'>A10</p></td>" +"<td style='width: 0.4in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p>A11</p></td>" +"<td style='width: 0.25in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p>A12</p></td>" +"<td style='width: 0.4in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p>A13</p></td>" +"<td style='width: 0.3in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p align='center'>A14</p></td>" +"<td style='width: 0.4in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p align='center'>A15</p></td>" +"<td style='width: 0.5in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p align='center'>A16</p></td>" +"<td style='width: 0.35in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p align='center'>A17</p></td>" +"<td style='width: 0.4in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p>A18</p></td>" +"<td style='width: 0.5in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p>A19</p></td>" +"<td style='width: 0.5in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p align='center'>A20</p></td>" +"<td style='width: 0.5in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p align='center'>A21</p></td>" +"<td style='width: 0.5in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p>A22</p></td>" +"<td style='width: 0.35in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p>A23</p></td>" +"<td style='width: 0.5in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p>A24</p></td>" +"<td style='width: 0.5in; border: 0.5pt solid #000000; background-color: #e7e7e8;'><p>A25</p></td>" +"</tr>" +"</tbody>" +"</table>";

print "------------"; print htmlStr;
retStr = "1~unconfiguredServices_richtext_quote~" + htmlStr + "|";
return retStr;