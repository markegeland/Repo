/*
This function returns Line Item grid view in HTML format to be used in Approval Notifications
Prepared by: Srikar
*/
htmlStr = "";
//Define table Headers
headersArray = string[]{"Service", "Cost", "Floor", "Average", "Target", "Price", "FRF", "ERF", "Total"};

//Define styles to be used on header row and regular row
THSTYLE = "style='padding:3px;font-weight:bold;color:#FFFFFF;background-color:#003c69;border:thin solid #000000;font-family:tahoma;'";
TDSTYLE = "bgcolor='#FFFFFF' align='right' nowrap style='color:#000000;'";
TDPRICESTYLE = "style='padding:3px;border:thin solid #000000;text-align:right;background-color:#00B8FF;font-family:tahoma;width:COL_WIDTHpx;'";
TABLESTYLE = "border='1' bordercolor='000000'  cellspacing='0' cellpadding='3'";
 
//Define tags to be used in table structure
TABLESTART = "<TABLE " +TABLESTYLE+ ">";
TABLEEND = "</TABLE>";
TBODYSTART = "<TBODY>";
TBODYEND = "</TBODY>";
TRSTART = "<TR>";
TREND = "</TR>";
TDSTART = "<TD " +TDSTYLE+ ">";
TDEND = "</TD>"; 
THSTART = "<TH " +THSTYLE+ ">";
THEND = "</TH>";



htmlStr = htmlStr + TABLESTART + TRSTART;
numCols = 0;
//Begin table header creation
for header in headersArray{
	//Display ERF column only when applied
	if(header == "ERF"){
		if(includeERF_quote == "Yes"){
			htmlStr = htmlStr + THSTART + header + THEND;
			numCols = numCols + 1;
		}	
	}
	//Display FRF column only when applied
	elif(header == "FRF"){
		if(includeFRF_quote == "Yes"){
			htmlStr = htmlStr + THSTART + header + THEND;
			numCols = numCols + 1;
		}
	}
	else{
		htmlStr = htmlStr + THSTART + header + THEND;
		numCols = numCols + 1;
	}
}
//Reserve 20% for description and distribute rest equally among other columns
descColWidth = "160";
colWidth = "80";
totalWidth = 720; //Create a fixed width table
if(numCols > 0){
	colWidth = string((totalWidth - atoi(descColWidth))/(numCols - 1)); 
}
htmlStr = htmlStr + TREND;
//Ends table header

//Begin table body
htmlStr = htmlStr + TBODYSTART;
//Iterate on line items to append them to table body
for line in line_process{
	if(line._part_number <> ""){
		htmlStr = htmlStr + TRSTART;
		//Description column - change content alignment for this column
		htmlStr = htmlStr + replace(replace(TDSTART, "right", "left"), "COL_WIDTH", descColWidth) + line.description_line + TDEND; 
		//Cost column
		htmlStr = htmlStr + replace(TDSTART, "COL_WIDTH", colWidth) + formatascurrency(line.totalFloorPrice_line, "USD") + TDEND; 
		//Floor column
		htmlStr = htmlStr + replace(TDSTART, "COL_WIDTH", colWidth) + formatascurrency(line.totalBasePrice_line, "USD") + TDEND; 
		//Average column
		htmlStr = htmlStr + replace(TDSTART, "COL_WIDTH", colWidth) + formatascurrency(line.totalTargetPrice_line, "USD") + TDEND; 
		//Stretch column
		htmlStr = htmlStr + replace(TDSTART, "COL_WIDTH", colWidth) + formatascurrency(line.totalStretchPrice_line, "USD") + TDEND; 
		//Price column - Replace regular style with highlighter style
		htmlStr = htmlStr + replace(replace(TDSTART, TDSTYLE, TDPRICESTYLE), "COL_WIDTH", colWidth) + formatascurrency(line.sellPrice_line, "USD") + TDEND; 
		//FRF column
		if(includeFRF_quote == "Yes"){
			htmlStr = htmlStr + replace(TDSTART, "COL_WIDTH", colWidth) + formatascurrency(line.frfAmountSell_line, "USD") + TDEND; 
		}
		//ERF column
		if(includeERF_quote == "Yes"){
			htmlStr = htmlStr + replace(TDSTART, "COL_WIDTH", colWidth) + formatascurrency(line.erfAmountSell_line, "USD") + TDEND; 
		}
		//Total column - Replace regular style with highlighter style
		htmlStr = htmlStr + replace(replace(TDSTART, TDSTYLE, TDPRICESTYLE), "COL_WIDTH", colWidth) + formatascurrency(line.totalPrice_line, "USD") + TDEND; 
		htmlStr = htmlStr + TREND;
	}
}
//End iterate line items
htmlStr = htmlStr + TBODYEND + TABLEEND;
return htmlStr;