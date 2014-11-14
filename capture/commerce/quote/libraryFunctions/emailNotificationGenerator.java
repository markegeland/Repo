/* 
	==========================================================================
	| COMMERCE BML LIBRARY													 |
	+------------------------------------------------------------------------+
	| emailNotificationGenerator											 |
	==========================================================================
	| Generates HTML email											 		|
	==========================================================================
*/

//Calculations
monthlyTotalBase = smallMonthlyTotalBase_quote + largeMonthlyTotalBase_quote;
monthlyTotalTarget = smallMonthlyTotalTarget_quote + largeMonthlyTotalTarget_quote;
monthlyTotalStretch = smallMonthlyTotalStretch_quote + largeMonthlyTotalStretch_quote;
monthlyTotalSell = smallMonthlyTotalProposed_quote + largeMonthlyTotalProposed_quote;
//Declaration of tags
START_B_TAG = "<b>";
START_U_TAG = "<u";
END_B_TAG = "</b>";
END_U_TAG = "</u>";
EMPTY_P_TAG = "<p></p>";
END_P_TAG = "</p>";

// Start the email body here
emailBody = "<html><body>";

emailBody =	emailBody + "<table style='padding-left:35px;font-family:tahoma;'><tbody><tr><td bgcolor='00B8FF' align='right' style='color:#FFFFFF;width:535px;height:40px;'><b>QUOTE APPROVAL REQUIRED&nbsp;&nbsp;</b></td></tr></tbody></table>";
// Image tag for Approval email
emailBody =	emailBody + "<img style='padding-left:200px;' src='http://" + _system_company_name + ".bigmachines.com/bmfsweb/testrepublicservices/image/RP_HorizontalLatest.jpeg'/>";
emailBody =	emailBody + "<br>";
emailBody =	emailBody + "<br>";
emailBody =	emailBody + "<p style='padding-left:35px;font: 16px Calibri'></p>";
emailBody =	emailBody + "<p style='padding-left:35px;font: 16px Calibri'>Dear " + Approver +",</p>";
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
							+ "<td style=\"padding-left:35px;font: 16px Calibri;\">Submitted By: "+SubmittedBy +"</td></tr>"
							+ "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">Submitted Date: "+submittedDate_quote +"</td></tr>"
							+ "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">Comments: "+submittersComments +"</td></tr>"
							+ "<tr><td><br/></td></tr>"
							+ "<tr><td><br/></td></tr>"
							+ "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">Quote Description: "+quoteDescription_quote +"</td></tr>"
							+ "<tr><td><br/></td></tr>"
							+ "<tr><td><br/></td></tr>"
							+ "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">Sales Activity: "+salesActivity_quote +"</td></tr>"
							+ "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">Division Number: "+division_quote +"</td></tr>"
							+ "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">Customer Name: "+_quote_process_billTo_company_name +"</td></tr>"
							+ "<tr><td style=\"padding-left:35px;font: 16px Calibri;\">Competitor: "+competitor_quote +"</td></tr>"
						+ "</table>";	

//Applying break tags whenever necessary 
emailBody =	emailBody + "<br>";
emailBody =	emailBody + "<br>";

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
	//javascript:document.bmDocForm.document_id.value=4653823;document.bmDocForm.action_id.value=4654396;document.bmDocForm.version_id.value=5095445;document.bmDocForm.document_number.value=-1;document.bmDocForm.id.value=5581344;bmSubmitForm("/commerce/buyside/document.jsp", document.bmDocForm, null, "performAction");		
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
emailBody = emailBody + "</body></html>";

// Use the below statement when an output PDF needs to be attached
//xslName = "quote_pdf_bmClone_21";return emailBody + "$,$" + xslName;

return emailBody ;