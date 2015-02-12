/* 
================================================================================
       Name:  lineItemGridForEmailTemplate
     Author:  John Palubinskas
Create date:  11 Feb 2015 (rewritten)
Description:  Formats the HTML Service Details table output in the approval email.
        
  Input:  None
                    
 Output:  String containing the Service Details table HTML

Updates:
        
================================================================================
*/

htmlStr = "";

// Header Row
htmlStr = "<table class='service-details'>"
        + "<tr>"
        + "<th>Service</th>"
        + "<th>Cost</th>"
        + "<th>Floor</th>"
        + "<th>Average</th>"
        + "<th>Target</th>"
        + "<th>Price</th>";
if(includeFRF_quote == "Yes"){
	htmlStr = htmlStr + "<th>FRF</th>";
}
if(includeERF_quote == "Yes"){
	htmlStr = htmlStr + "<th>ERF</th>";
}
htmlStr = htmlStr + "<th>Total</th></tr>";

// Service Detail rows
for line in line_process{
	if(line._part_number <> ""){
		htmlStr = htmlStr + "<tr>"
		                  + "<td>" + line.description_line + "</td> "
		                  + "<td>" + formatascurrency(line.totalFloorPrice_line, "USD") + "</td>"           // Cost
		                  + "<td>" + formatascurrency(line.totalBasePrice_line, "USD") + "</td>"            // Floor
		                  + "<td>" + formatascurrency(line.totalTargetPrice_line, "USD") + "</td>"          // Average
		                  + "<td>" + formatascurrency(line.totalStretchPrice_line, "USD") + "</td>"         // Target
		                  + "<td class='totals'>" + formatascurrency(line.sellPrice_line, "USD") + "</td>"; // Price

		if(includeFRF_quote == "Yes"){
			htmlStr = htmlStr + "<td>" + formatascurrency(line.frfAmountSell_line, "USD") + "</td>"; // FRF
		}
		if(includeERF_quote == "Yes"){
			htmlStr = htmlStr + "<td>" + formatascurrency(line.erfAmountSell_line, "USD") + "</td>"; // ERF
		}
		htmlStr = htmlStr + "<td class='totals'>" + formatascurrency(line.totalPrice_line, "USD") + "</td></tr>";  // Total
	}
}

htmlStr = htmlStr + "</table>";

return htmlStr;