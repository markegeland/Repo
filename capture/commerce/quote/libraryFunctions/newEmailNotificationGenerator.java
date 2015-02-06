/* 
	==========================================================================
	| COMMERCE BML LIBRARY													 |
	+------------------------------------------------------------------------+
	| NewEmailNotificationGenerator											 |
	==========================================================================
	| Generates HTML email											 		|
	==========================================================================
	Change Log
	20141211 - Julie Felberg Implemented 4 new tables with logic.  This logic involved a new line level loop and several new variables.
	20140115 - Julie Felberg Added Disposal site information
	20140115 - Julie Felberg More Disposal Site Information
	20140116 - Julie Felberg Switched the Disposal Site config attr
	20140117 - Julie Felberg Added NewFees and NewPrice logic - Added Rate Restriction Section
	20140128 - Julie Felberg Added $ to Ad Hoc price
	20140129 - Julie Felberg Redid logic for the container comparison section
	20150202 - Julie Felberg Changed Container Codes
	20150203 - Julie Felberg Changed lifts per container logic
	20150203 - Julie Felberg Corrected Estimated Tons Per Haul
	
*/

//Calculations
monthlyTotalBase = smallMonthlyTotalBase_quote + largeMonthlyTotalBase_quote;
monthlyTotalTarget = smallMonthlyTotalTarget_quote + largeMonthlyTotalTarget_quote;
monthlyTotalStretch = smallMonthlyTotalStretch_quote + largeMonthlyTotalStretch_quote;
monthlyTotalSell = smallMonthlyTotalProposed_quote + largeMonthlyTotalProposed_quote;

//20141211 line level loop to populate table arrays
//declare arrays and index
AdHocIndex = 0;
AdHocDescArr = string[];
AdHocBillingMethodArr = string[];
AdHocQtyArr = string[];
AdHocTotalPriceArr = string[];
ConfigAttrDict=dict("string");
SCDocNum = string[];
ContPDocNum = string[];
ContainerArr = string[];
RatePerHaulArr = string[];
ContTypeArr = string[];
CompactorValArr = string[];
TotalServiceTimeArr = string[];
TonsPerHaulArr = string[];
DisposalSiteArr = string[];
CostPerHaulArr = string[];
newPriceDict = dict("string");
newFeesDict = dict("string");
oldContainerArray = string[];
NewContainerArray = string[];
oldSizeArray = string[];
newSizeArray = string[];
oldNbrArray = string[];
newNbrArray = string[];
oldWtArray = string[];
newWtArray = string[];
oldFreqArray = string[];
newFreqArray = string[];
oldPriceArray = string[];
oldFeesArray = string[];
newPriceArray = string[];
newFeesArray = string[];

//start loop
for line in line_process{
	//models
	if(NOT isnumber(line._parent_doc_number)){
		put(ConfigAttrDict, line._document_number, line._config_attributes);
	}
	//line items
	if(isnumber(line._parent_doc_number)){
		//Ad Hoc Items
		if(line._parent_line_item == "Ad-Hoc Line Items"){
			AdHocDescArr[AdHocIndex] = line.description_line;
			AdHocBillingMethodArr[AdHocIndex] = line.billingType_line;
			AdHocQtyArr[AdHocIndex] = string(line._price_quantity);
			AdHocTotalPriceArr[AdHocIndex] = string(line.sellPrice_line);
			AdHocIndex = AdHocIndex + 1;
		}
		//Service Changes
		elif(line.activity_line == "Service level change"){
			if(findinarray(SCDocNum, line._parent_doc_number) == -1){
				append(SCDocNum, line._parent_doc_number);
			}
			//if monthly fee, then populate new price array
			if(line.billingType_line == "Monthly"){
				put(newPriceDict,line._parent_doc_number, string(line.sellPrice_line));
				put(newFeesDict, line._parent_doc_number, string(line.frfAmountSell_line + line.erfAmountSell_line + adminRate_quote));
			}
		}
		//container
		else{
			if(findinarray(ContPDocNum, line._parent_doc_number) == -1){
				print "in the else we get these descriptions:";
				print line.description_line;
				append(ContPDocNum,line._parent_doc_number);		
				
				//LARGE_CONTAINER
				if(line.billingType_line == "Per Haul"){
					print "we have a large container";
					append(ContainerArr, line.description_line);
					append(RatePerHaulArr, string(line.perHaulRate_line));
					append(ContTypeArr, line._model_name);
					//append(CompactorValArr, "Value");
					//append(TotalServiceTimeArr, "Value");
					//append(DisposalSiteArr, "Value");
					append(CostPerHaulArr, string(line.totalFloorPrice_line));
					
					//populate compactor value
					
					append(CompactorValArr, getconfigattrvalue(line._parent_doc_number, "compactorValue"));
					append(DisposalSiteArr, substring(getconfigattrvalue(line._parent_doc_number, "site_disposalSite"), 0, 49));
					append(TotalServiceTimeArr, getconfigattrvalue(line._parent_doc_number, "adjustedTotalTime_l"));
					append(TonsPerHaulArr, getconfigattrvalue(line._parent_doc_number, "estTonsHaul_l"));  					
					
				}
				//SMALL_CONTAINER
				else{
					print "we have a small container";
					append(ContainerArr, line.description_line);
					append(RatePerHaulArr, string(line.perHaulRate_line));
					append(ContTypeArr, line._model_name);
					append(CompactorValArr, "NA");
					append(TotalServiceTimeArr, "Value");
					//append(DisposalSiteArr, "NA");
					append(CostPerHaulArr, "NA");
					append(TonsPerHaulArr, "NA");
					
					if(getconfigattrvalue(line._parent_doc_number, "wasteCategory") == "Solid Waste"){
						append(DisposalSiteArr, getconfigattrvalue(line._parent_doc_number, "polygonRegion"));
					}
					else{
						append(DisposalSiteArr, " ");
					}
				}
				
				
			}
		}
	}
}
//end 20141211 line level loop


//Declaration of tags
START_B_TAG = "<b>";
START_U_TAG = "<u";
END_B_TAG = "</b>";
END_U_TAG = "</u>";
EMPTY_P_TAG = "<p></p>";
END_P_TAG = "</p>";

//20141210 Get Full Names
ApproverFullName = "";
SubmittedByFullName = "";

NameRecordSet = BMQL("SELECT First_Name, Last_Name, User_Login FROM User_Hierarchy WHERE User_Login LIKE $Approver OR User_Login LIKE $SubmittedBy");

for eachName in NameRecordSet{
	Login = get(eachName, "User_Login");
	if(Login  == Approver){
		ApproverFullName = get(eachName, "First_Name") + " " + get(eachName, "Last_Name");
	}
	if(Login == SubmittedBy){
		SubmittedByFullName = get(eachName, "First_Name") + " " + get(eachName, "Last_Name");
	}
}
//end Get Full Names

// Start the email body here
emailBody = "<ht"+ "ml><body>";

emailBody =	emailBody + "<table style='padding-left:35px;font-family:tahoma;'><tbody><tr><td bgcolor='00B8FF' align='right' style='color:#FFFFFF;width:535px;height:40px;'><b>QUOTE APPROVAL REQUIRED&nbsp;&nbsp;</b></td></tr></tbody></table>";
// Image tag for Approval email
emailBody =	emailBody + "<img style='padding-left:200px;' src='https://" + _system_company_name + ".bigmachines.com/bmfsweb/testrepublicservices/image/RP_HorizontalLatest.jpeg'/>";
emailBody =	emailBody + "<br>";
emailBody =	emailBody + "<br>";
emailBody =	emailBody + "<p style='padding-left:35px;font: 16px Calibri'></p>";
emailBody =	emailBody + "<p style='padding-left:35px;font: 16px Calibri'>Dear " + ApproverFullName +",</p>";
emailBody =	emailBody + "<p style='padding-left:35px;font: 16px Calibri'>Quote "+ quoteNumber_quote+ ", " + siteName_quote + " requires your approval for the following reason(s): </p>";
// Display Reason Description - For level 1 and 2 this will be based on the approver the input attribute will differ
emailBody =	emailBody + "<p style='padding-left:35px;font: 16px Calibri'><b> " + ReasonDescription + " </b></p>";

emailBody =	emailBody + "<p style='padding-left:35px;font: 16px Calibri'><u><i>Quote Detail:</i></u></p>";
submittersComments = "";
if(NOT isnull(SubmitComment)){
	submittersComments = SubmitComment;
}


// Display Basic information of quote like Submitter details, sales activity, division number, competitor etc.
emailBody = emailBody + "<table style=\"padding-left:35px;font: 16px Calibri;\">"
						+ "<tr>"
							+ "<td style=\"padding-left:35px;font: 16px Calibri;\">Submitted By: "+SubmittedByFullName +"</td></tr>"
							+ "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">Submitted Date: "+submittedDate_quote +"</td></tr>"
							+ "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">Comments: "+submittersComments +"</td></tr>"
							+ "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">Approvals Required: "+approvalReasonDisplayWithColorTA_quote +"</td></tr>" 		
							+ "<tr><td><br/></td></tr>"
							+ "<tr><td><br/></td></tr>"
							+ "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">Quote Description: "+quoteDescription_quote +"</td></tr>"
							+ "<tr><td><br/></td></tr>"
							+ "<tr><td><br/></td></tr>"
							+ "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">Sales Activity: "+salesActivity_quote +"</td></tr>"
							+ "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">Division Number: "+division_quote +"</td></tr>"
							+ "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">Customer Name: "+_quote_process_billTo_company_name +"</td></tr>"
							+ "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">Competitor: "+competitor_quote +"</td></tr>";
//20141211 Email revamp
emailBody = emailBody + "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">Industry: "+industry_quote +"</td></tr>"
							+ "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">Segment: "+segment_quote +"</td></tr>"
							+ "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">Term: "+initialTerm_quote +"</td></tr>";
							
//================== Rate Restriction ======================================//
emailBody = emailBody + "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">Rate Restriction: " + " " +"</td></tr>";

if(customerRateRestriction_quote == false){
	emailBody = emailBody + "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">No Rate Restriction" + "</td></tr>";
}

if(year1Rate_quote == "" AND year2Rate_quote == "" AND year3Rate_quote == "" AND afterYear4_quote == "" AND customerRateRestriction_quote == true){
	emailBody = emailBody + "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">Rate Firm Until " + afterYear1Date_quote + "</td></tr>";
}
if(customerRateRestriction_quote == true AND year1Rate_quote <> ""){
	emailBody = emailBody + "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">" + year1RatePrint_quote + " " + afterYear1Date_quote +"</td></tr>";
}
if(customerRateRestriction_quote == true AND year1Rate_quote == "" AND year2Rate_quote == "" AND year3Rate_quote == "" AND afterYear4_quote == ""){
	if(isnumber(initialTerm_quote)){
		if(atoi(initialTerm_quote) >= 24){
			emailBody = emailBody + "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">Rate Firm for the 1st year of the agreement.</td></tr>";
		}
	}
}
if(customerRateRestriction_quote == true AND year2Rate_quote <> ""){
	emailBody = emailBody + "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">" + year2RatePrint_quote + " " + afterYear2Date_quote +"</td></tr>";
}
if(customerRateRestriction_quote == true AND year3Rate_quote <> ""){
	emailBody = emailBody + "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">" + year3RatePrint_quote + " " + afterYear3Date_quote +"</td></tr>";
}
if(customerRateRestriction_quote == true AND afterYear4_quote <> ""){
	emailBody = emailBody + "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">" + year4RatePrint_quote + " " + afterYear4Date_quote +"</td></tr>";
}
//================= end Rate Restriction ====================================//


//end Email revamp							
							
emailBody = emailBody + "</table>";	

//Applying break tags whenever necessary 
emailBody =	emailBody + "<br>";
emailBody =	emailBody + "<br>";

//20141211  Container Details Table
ContIndex = 0;
if(NOT isempty(ContPDocNum)){
	emailBody =	emailBody + "<p style='padding-left:35px;font: 16px Calibri'>Container Details:</p>";
	emailBody = emailBody + "<table border=\"1\" bordercolor=\"000000\"  cellspacing=\"0\" cellpadding=\"3\" >"
						+ "<tr>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Container</b></td>"	
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Compactor Asset Value</b></td>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Total Service Time</b></td>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Rate Per Haul</b></td>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Cost Per Haul</b></td>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Tons Per Haul</b></td>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Disposal Site</b></td>"
						+ "</tr>";
	
	for eachCont in ContPDocNum{
		emailBody = emailBody + "<tr>" 
			+ "<td align=\"left\" bgcolor=\"FFFFFF\" nowrap style=\"color:#000000;\"><b>" + ContainerArr[ContIndex] + "</b></td>"
			+ "<td align=\"right\" bgcolor=\"FFFFFF\" nowrap style=\"color:#000000;\"><b>" + CompactorValArr[ContIndex] + "</b></td>"
			+ "<td align=\"right\" bgcolor=\"FFFFFF\" nowrap style=\"color:#000000;\"><b>" + TotalServiceTimeArr[ContIndex] + "</b></td>"
			+ "<td align=\"right\" bgcolor=\"FFFFFF\" nowrap style=\"color:#000000;\"><b>" + RatePerHaulArr[ContIndex] + "</b></td>"
			+ "<td align=\"right\" bgcolor=\"FFFFFF\" nowrap style=\"color:#000000;\"><b>" + CostPerHaulArr[ContIndex] + "</b></td>"
			+ "<td align=\"right\" bgcolor=\"FFFFFF\" nowrap style=\"color:#000000;\"><b>" + TonsPerHaulArr[ContIndex] + "</b></td>"
			+ "<td align=\"right\" bgcolor=\"FFFFFF\" nowrap style=\"color:#000000;\"><b>" + DisposalsiteArr[ContIndex] + "</b></td>"
			+ "</tr>";
		ContIndex = ContIndex + 1;
	}	
		

	emailBody = emailBody + "</table>";
}

//Applying break tags whenever necessary 
emailBody =	emailBody + "<br>";
emailBody =	emailBody + "<br>";

//end 20141211

// Display the prices in a table format
emailBody =	emailBody + "<p style='padding-left:35px;font: 16px Calibri'>Quote Totals:</p>";
emailBody = emailBody + "<table border=\"1\" bordercolor=\"000000\"  cellspacing=\"0\" cellpadding=\"3\" >"
						+ "<tr>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b></b></td>"	
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Cost Price</b></td>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Floor Price</b></td>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Average Price</b></td>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Target Price</b></td>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Proposed Price</b></td>"
							/*+ "<td align=\"center\" bgcolor=\"00B8FF\" style=\"color:#FFFFFF;width:80px;\"><b>ERF</b></td>"
							+ "<td align=\"center\" bgcolor=\"00B8FF\" style=\"color:#FFFFFF;width:80px;\"><b>FRF</b></td>"
							+ "<td align=\"center\" bgcolor=\"00B8FF\" style=\"color:#FFFFFF;width:80px;\"><b>Total</b></td>"*/
						+ "</tr>";
						if(commercialExists_quote){
							emailBody = emailBody +  "<tr>"
								+ "<td bgcolor=\"FFFFFF\" align=\"right\" nowrap style=\"color:#000000;\">" +  "Small Containers" + "</td>"
								+ "<td bgcolor=\"FFFFFF\" align=\"right\" nowrap style=\"color:#000000;\">" +  formatascurrency(smallMonthlyTotalFloor_quote,"USD") + "</td>"
								+ "<td bgcolor=\"FFFFFF\" align=\"right\" nowrap style=\"color:#000000;\">" +  formatascurrency(smallMonthlyTotalBase_quote,"USD") + "</td>"
								+ "<td bgcolor=\"FFFFFF\" align=\"right\" nowrap style=\"color:#000000;\">" +  formatascurrency(smallMonthlyTotalTarget_quote,"USD") + "</td>"
								+ "<td bgcolor=\"FFFFFF\" align=\"right\" nowrap style=\"color:#000000;\">" +  formatascurrency(smallMonthlyTotalStretch_quote,"USD") + "</td>"
								+ "<td bgcolor=\"FFFFFF\" align=\"right\" nowrap style=\"color:#000000;\">" +  formatascurrency(smallMonthlyTotalProposed_quote,"USD") + "</td>"
							+ "</tr>";
						}
						if(industrialExists_quote){
							emailBody = emailBody +  "<tr>"
								+ "<td bgcolor=\"FFFFFF\" align=\"right\" nowrap style=\"color:#000000;\">" +  "Large Containers" + "</td>"
								+ "<td bgcolor=\"FFFFFF\" align=\"right\" nowrap style=\"color:#000000;\">" +  formatascurrency(largeMonthlyTotalFloor_quote,"USD") + "</td>"
								+ "<td bgcolor=\"FFFFFF\" align=\"right\" nowrap style=\"color:#000000;\">" +  formatascurrency(largeMonthlyTotalBase_quote,"USD") + "</td>"
								+ "<td bgcolor=\"FFFFFF\" align=\"right\" nowrap style=\"color:#000000;\">" +  formatascurrency(largeMonthlyTotalTarget_quote,"USD") + "</td>"
								+ "<td bgcolor=\"FFFFFF\" align=\"right\" nowrap style=\"color:#000000;\">" +  formatascurrency(largeMonthlyTotalStretch_quote,"USD") + "</td>"
								+ "<td bgcolor=\"FFFFFF\" align=\"right\" nowrap style=\"color:#000000;\">" +  formatascurrency(largeMonthlyTotalProposed_quote,"USD") + "</td>"
							+ "</tr>";
						}
						emailBody = emailBody +  "<tr>"
							+ "<td bgcolor=\"FFFFFF\" align=\"right\" nowrap style=\"color:#000000;\">" +  "Fees" + "</td>"
							+ "<td bgcolor=\"FFFFFF\" align=\"right\" nowrap style=\"color:#000000;\">" +  formatascurrency(totalERFFRFAdminFloorAmount_quote,"USD") + "</td>"
							+ "<td bgcolor=\"FFFFFF\" align=\"right\" nowrap style=\"color:#000000;\">" +  formatascurrency(totalERFFRFAdminBaseAmount_quote,"USD") + "</td>"
							+ "<td bgcolor=\"FFFFFF\" align=\"right\" nowrap style=\"color:#000000;\">" +  formatascurrency(totalERFFRFAdminTargetAmount_quote,"USD") + "</td>"
							+ "<td bgcolor=\"FFFFFF\" align=\"right\" nowrap style=\"color:#000000;\">" +  formatascurrency(totalERFFRFAdminStretchAmount_quote,"USD") + "</td>"
							+ "<td bgcolor=\"FFFFFF\" align=\"right\" snowrap  tyle=\"color:#000000;\">" +  formatascurrency(totalERFFRFAdminSellAmount_quote,"USD") + "</td>"
							+ "</tr>"
						+ "<tr>"
							/*+ "<td bgcolor=\"003c69\" align=\"right\" style=\"color:#FFFFFF;\">" +  formatascurrency(monthlyTotalBase,"USD") + "</td>"
							+ "<td bgcolor=\"003c69\" align=\"right\" style=\"color:#FFFFFF;\">" +  formatascurrency(monthlyTotalTarget,"USD") + "</td>"
							+ "<td bgcolor=\"003c69\" align=\"right\" style=\"color:#FFFFFF;\">" +  formatascurrency(monthlyTotalStretch,"USD") + "</td>"*/
							+ "<td bgcolor=\"00B8FF\" align=\"right\" style=\"color:#FFFFFF;\">" +  "Total Estimated Amount" + "</td>"
							+ "<td bgcolor=\"00B8FF\" align=\"right\" style=\"color:#FFFFFF;\">" +  formatascurrency(grandTotalFloor_quote,"USD") + "</td>"
							+ "<td bgcolor=\"00B8FF\" align=\"right\" style=\"color:#FFFFFF;\">" +  formatascurrency(grandTotalBase_quote,"USD") + "</td>"
							+ "<td bgcolor=\"00B8FF\" align=\"right\" style=\"color:#FFFFFF;\">" +  formatascurrency(grandTotalTarget_quote,"USD") + "</td>"
							+ "<td bgcolor=\"00B8FF\" align=\"right\" style=\"color:#FFFFFF;\">" +  formatascurrency(grandTotalStretch_quote,"USD") + "</td>"
							+ "<td bgcolor=\"00B8FF\" align=\"right\" style=\"color:#FFFFFF;\">" +  formatascurrency(grandTotalSell_quote,"USD") + "</td>"
							/*+ "<td bgcolor=\"00B8FF\" align=\"right\" style=\"color:#FFFFFF;\">" +  formatascurrency(frfTotalSell_quote,"USD") + "</td>"
							+ "<td bgcolor=\"00B8FF\" align=\"right\" style=\"color:#FFFFFF;\">" +  formatascurrency(erfTotalSell_quote,"USD") + "</td>"
							+ "<td bgcolor=\"00B8FF\" align=\"right\" style=\"color:#FFFFFF;\">" +  formatascurrency(erfTotalSell_quote,"USD") + "</td>"*/
							+ "</tr></table>";
emailBody =	emailBody + "<p style='padding-left:35px;font: 16px Calibri'>Financial Values:</p>";
emailBody =	emailBody + financialSummaryForEmailTemplate_quote;
emailBody =	emailBody + "<br/>";
emailBody =	emailBody + "<p style='padding-left:35px;font: 16px Calibri'>Service Details:</p>";
emailBody =	emailBody + lineItemGridForEmailTemplate_quote;
emailBody =	emailBody + "<br/>";

//20141211  Additional Items Table
//create Additional Item table if we have adhoc items
if(NOT isempty(AdHocDescArr)){
	emailBody =	emailBody + "<p style='padding-left:35px;font: 16px Calibri'>Additional Items:</p>";
	emailBody = emailBody + "<table border=\"1\" bordercolor=\"000000\"  cellspacing=\"0\" cellpadding=\"3\" >"
						+ "<tr>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Description</b></td>"	
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Quantity</b></td>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Total Price</b></td>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Billing Method</b></td>"
						+ "</tr>";
	
	//populate rows
	eachAHDIndex = 0;
	for eachAHD in AdHocDescArr{
		
		emailBody = emailBody + "<tr>"
									+ "<td align=\"left\" bgcolor=\"FFFFFF\" nowrap style=\"color:#000000;\">" + AdHocDescArr[eachAHDIndex] + "</td>"
									+ "<td align=\"center\" bgcolor=\"FFFFFF\" nowrap style=\"color:#000000;\">" + AdHocQtyArr[eachAHDIndex] + "</td>"
									+ "<td align=\"right\" bgcolor=\"FFFFFF\" nowrap style=\"color:#000000;\">" + "$" + AdHocTotalPriceArr[eachAHDIndex] + "</td>"
									+ "<td align=\"center\" bgcolor=\"FFFFFF\" nowrap style=\"color:#000000;\">" + AdHocBillingMethodArr[eachAHDIndex] + "</td>"
							  + "</tr>";
		eachAHDIndex = eachAHDIndex + 1;
	}					
						
	emailBody = emailBody + "</table>";
}

//Applying break tags whenever necessary 
emailBody =	emailBody + "<br>";
emailBody =	emailBody + "<br>";

//end 20141211	

//20141211  Container Comparison Table
ConfigAttrStr = "";
ConfigAttrArr = string[];
NConfigAttrStr = "";
NConfigAttrArr = string[];
if(NOT isempty(SCDocNum)){
	emailBody =	emailBody + "<p style='padding-left:35px;font: 16px Calibri'>Container Comparison:</p>";
	
	//headers for old containers
	emailBody = emailBody + "<table border=\"1\" bordercolor=\"000000\"  cellspacing=\"0\" cellpadding=\"3\" >"
						+ "<tr>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Old Container</b></td>"	
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Size</b></td>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Nbr</b></td>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Waste Type</b></td>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Frequency</b></td>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>PRICE</b></td>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Fees</b></td>"
						+ "</tr>";
	
	for eachSC in SCDocNum{
		if(containskey(ConfigAttrDict, eachSC)){
			divison_config = getconfigattrvalue(eachSC, "division_config");
			quantity_readOnly = getconfigattrvalue(eachSC, "quantity_readOnly");
			wasteType_readOnly = getconfigattrvalue(eachSC, "wasteType_readOnly");
			containerSize_readOnly = getconfigattrvalue(eachSC, "containerSize_readOnly");
			liftsPerContainer_readOnly = getconfigattrvalue(eachSC, "liftsPerContainer_readOnly");
			wasteType_sc = getconfigattrvalue(eachSC, "wasteType_sc");
			quantity_sc = getconfigattrvalue(eachSC, "quantity_sc");
			containerSize_sc = getconfigattrvalue(eachSC, "containerSize_sc");
			liftsPerContainer_sc = getconfigattrvalue(eachSC, "liftsPerContainer_sc");
			hasCompactor_readOnly = getconfigattrvalue(eachSC, "hasCompactor_readOnly");
			containerCode_readOnly = getconfigattrvalue(eachSC, "containerCode_readOnly");
			containerCodes_SC = getconfigattrvalue(eachSC, "containerCodes_SC");
			
			bothContainers = util.getExcangeDescription(divison_config, quantity_readOnly, wasteType_readOnly, containerSize_readOnly, liftsPerContainer_readOnly, wasteType_sc, quantity_sc, containerSize_sc, liftsPerContainer_sc, hasCompactor_readOnly, containerCode_readOnly, containerCodes_SC);
			bothContainersArray = split(bothContainers, "|^|");
			
			append(oldContainerArray, bothContainersArray[0]);
			append(newContainerArray, bothContainersArray[1]);
			
			append(oldSizeArray, containerSize_readOnly);
			append(newSizeArray, containerSize_sc);
			append(oldNbrArray, quantity_readOnly);
			append(newNbrArray, quantity_sc);
			append(oldWtArray, wasteType_readOnly);
			append(newWtArray, wasteType_sc);
			append(oldFreqArray, liftsPerContainer_readOnly);
			append(newFreqArray, liftsPerContainer_sc);
			append(oldPriceArray, getconfigattrvalue(eachSC, "monthlyRevenue_sc"));
			append(oldFeesArray, getconfigattrvalue(eachSC, "totalWithFees_sc"));
			if(containskey(newPriceDict, eachSC)){
				append(newPriceArray, get(newPriceDict, eachSC));
			}
			else{
				append(newPriceArray, "No New Price");
			}
			if(containskey(newFeesDict, eachSC)){
				append(newFeesArray, get(newFeesDict, eachSC));
			}
			else{
				append(newFeesArray, "No New Fee");
			}
			
		}
	}
	
	indexOld = 0;
	for eachSCOld in SCDocNum{		
		emailBody = emailBody + "<tr>"
				+ "<td align=\"right\" bgcolor=\"FFFFFF\"nowrap style=\"color:#000000;\">" + oldContainerArray[indexOld] + "</td>"
				+ "<td align=\"right\" bgcolor=\"FFFFFF\"nowrap style=\"color:#000000;\">" + oldSizeArray[indexOld] + "</td>"
				+ "<td align=\"right\" bgcolor=\"FFFFFF\"nowrap style=\"color:#000000;\">" + oldNbrArray[indexOld] + "</td>"
				+ "<td align=\"right\" bgcolor=\"FFFFFF\"nowrap style=\"color:#000000;\">" + oldWTArray[indexOld] + "</td>"
				+ "<td align=\"right\" bgcolor=\"FFFFFF\"nowrap style=\"color:#000000;\">" + oldFreqArray[indexOld] + "</td>"
				+ "<td align=\"right\" bgcolor=\"FFFFFF\"nowrap style=\"color:#000000;\">" + oldPriceArray[indexOld] + "</td>"
				+ "<td align=\"right\" bgcolor=\"FFFFFF\"nowrap style=\"color:#000000;\">" + oldFeesArray[indexOld] + "</td>"
				+ "</tr>"; 
		indexOld = indexOld + 1;
	}
	
	//Headers for new containers
	emailBody = emailBody + "<tr>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>New Container</b></td>"	
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Size</b></td>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Nbr</b></td>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Waste Type</b></td>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Frequency</b></td>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>PRICE</b></td>"
							+ "<td align=\"center\" bgcolor=\"003c69\" nowrap style=\"color:#FFFFFF;width:80px;\"><b>Fees</b></td>"
						+ "</tr>";
	//rows for new containers
	
	indexNew = 0;
	for NeachSC in SCDocNum{
			
			emailBody = emailBody + "<tr>"
					+ "<td align=\"right\" bgcolor=\"FFFFFF\"nowrap style=\"color:#000000;\">" + newContainerArray[indexNew] + "</td>"
					+ "<td align=\"right\" bgcolor=\"FFFFFF\"nowrap style=\"color:#000000;\">" + newSizeArray[indexNew] + "</td>"
					+ "<td align=\"right\" bgcolor=\"FFFFFF\"nowrap style=\"color:#000000;\">" + newNbrArray[indexNew] + "</td>"
					+ "<td align=\"right\" bgcolor=\"FFFFFF\"nowrap style=\"color:#000000;\">" + newWTArray[indexNew] + "</td>"
					+ "<td align=\"right\" bgcolor=\"FFFFFF\"nowrap style=\"color:#000000;\">" + newFreqArray[indexNew] + "</td>"
					+ "<td align=\"right\" bgcolor=\"FFFFFF\"nowrap style=\"color:#000000;\">" + "$" + newPriceArray[indexNew] + "</td>"
					+ "<td align=\"right\" bgcolor=\"FFFFFF\"nowrap style=\"color:#000000;\">" + "$" + newFeesArray[indexNew] + "</td>"
				+ "</tr>";
			
			indexNew = indexNew + 1;
		
	}
	//end of rows for new containers
	emailBody = emailBody + "</table>";
}
						
						

//Applying break tags whenever necessary 
emailBody =	emailBody + "<br>";
emailBody =	emailBody + "<br>";

//end 20141211	
	
	
	//:document.bmDocForm.document_id.value=4653823;document.bmDocForm.action_id.value=4654396;document.bmDocForm.version_id.value=5095445;document.bmDocForm.document_number.value=-1;document.bmDocForm.id.value=5581344;bmSubmitForm("/commerce/buyside/document.jsp", document.bmDocForm, null, "performAction");		
//version_id = "4748762";
//    action_id = "4654396";
//	document_id ="4653823";
//    url = "https://testrepublicservices.bigmachines.com/commerce/buyside/document.jsp?formaction=performAction&id=" + _system_buyside_id + "&version_id=" + version_id + "&action_id=" + action_id + "&document_id=" + document_id + "&document_number=-1";
// Provide the user a transaction link to the quote so that he can click on it and approve 
emailBody = emailBody +  "<p style='padding-left:35px;font: 14px Calibri'> Please reply to this e-mail with the word " 
						+ "<b>Approve</b> or "
						+ "<b>Reject</b>; or by clicking the following link to approve within the BigMachines tool:"
						+ "<a href='"+ TransactionURL + "'>"
						+ TransactionURL + "</a></p>";
emailBody = emailBody + "</body></ht" + "ml>";

// Use the below statement when an output PDF needs to be attached
//xslName = "quote_pdf_bmClone_21";return emailBody + "$,$" + xslName;

return emailBody;
