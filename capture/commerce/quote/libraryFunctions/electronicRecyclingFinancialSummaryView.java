/*
=======================================================================================================================
Name:        electronicRecyclingFinancialSummaryView
Author:   	Mark Egeland
Create date:  20152008

Description: Creates the Electronic Recycling Summary View table in HTML
        
Input:       N/A
                    
Output:      String (financial summary HTML)

Updates:     
    
=======================================================================================================================
*/

returnStr = "";

//Combine the HTML code into a string for storage in an attribute
returnStr = returnStr   + "<table class='rs_table_style' width=\"100%\" cellpadding=\"3\" cellspacing=\"0\" style=\"font-family:Open Sans;\">"
						+ "<tr class='rs_table_style'>"//Start of labels row
						+ "<td align=\"center\" style='border-bottom: thin solid #3D9BCB;color:#000059;'></td>";

returnStr = returnStr 	+ "<td align=\"center\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Electronic Recycling</td>";
returnStr = returnStr + "<td align=\"center\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Total</td>";

returnStr = returnStr   + "</tr>" //End of labels row
						+ "<tr class='rs_tr_td_style'>" //Start of Revenue row
						+ "<td>Revenue</td>";

returnStr = returnStr   + "<td align=\"center\">" + formatascurrency(electronicRecyclingTotalProposed_quote,"USD") + "</td>";
returnStr = returnStr + "<td align=\"center\">" + formatascurrency(electronicRecyclingTotalProposed_quote, "USD") + "</td>";

returnStr = returnStr   + "</tr>" //End of Revenue row
						+ "<tr  class='rs_tr_td_style'>" //Start of Expense row
						+ "<td >Expense</td>";

returnStr = returnStr   + "<td  align=\"center\">" + formatascurrency(electronicRecyclingTotalFloor_quote, "USD") + "</td>";
returnStr = returnStr + "<td  align=\"center\">" + formatascurrency(electronicRecyclingTotalFloor_quote, "USD") + "</td>";

returnStr = returnStr   + "</tr>" //End of Expense row
						+ "</table>";

//=============================== End - Building HTML String ===============================//

return returnStr;