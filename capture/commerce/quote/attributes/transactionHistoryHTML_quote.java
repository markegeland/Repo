containerGroupForTransaction = containerGroupForTransaction_quote;
if(containerGroupForTransaction_quote == ""){
	accountStatusRecordSet = bmql("SELECT Container_Grp_Nbr FROM Account_Status WHERE infopro_acct_nbr = $_quote_process_customer_id  AND Site_Nbr = $siteNumber_quote");
	for each in accountStatusRecordSet{
		containerGroupForTransaction = get(each,"Container_Grp_Nbr");
		break;
	}
}


// display the container group details from Account_Sales_Hist data table into an html table
headerStyle = "style='text-align:center;background-color:#DBEFFF;width: 200px'";
TableHeaders = string[]{"Transaction","Base","Amount","Percent", "Change in Units","ERF","FRF","Effective"}; // all table headers defined here
finalString="<table cellspacing='0' width='100%'><th style='text-align:center width:100%' colspan='8'>STR Transaction History (Last 2 Years)</th></table>";
finalString = finalString+"<div style='max-height: 100px; overflow-y: scroll;'><table style='border:1pt solid black;font-size:8pt;font-family: Tahoma,Arial,Helvetica,sans-serif;' cellspacing='0'>";
finalString = finalString + "<tbody>";
finalString = finalString + "<tr>";
tabledata = "style='border:1px solid grey;text-align:center'"; // used for the td's
typeTableData = "style='border:1px solid grey;text-align:center;width:35%'";	
dateSeperator="/";
delimeter1="-";
tempDate="";
tabledataForPrices="style='border:1px solid grey;text-align:right'";
for eachTHeading in TableHeaders{
	finalString = finalString + "<td "+headerStyle+">"+eachTHeading+"</td>";	
}
finalString = finalString + "</tr>";

tableBody = "";
count = 0;
if(_quote_process_customer_id <> "" AND SiteNumber_quote <> "" AND containerGroupForTransaction <> ""){
	resultSet= bmql("select infopro_acct_nbr,Eff_Dt_SK,Txn_Reason_Desc,Is_ERF_On,Is_FRF_On, Monthly_Sales_Amt,monthly_yard_cnt,Mon_Sales_Change_Amt, Mon_Yard_Change_Cnt from Account_Sales_Hist where infopro_acct_nbr = $_quote_process_customer_id AND Site_Nbr = $SiteNumber_quote AND Container_Grp_Nbr = $containerGroupForTransaction ORDER BY Eff_Dt_SK DESC"); // filtering based on 1)sitenumber,2)container group selected and 3)customer id

	for each in resultSet{

		count = count + 1;	
		infoPro = get(each,"infopro_acct_nbr");
		effDateStr = get(each,"Eff_Dt_SK");
        futureRateClass = "";

		//if the date in data table is empty or "9999-12-31" then display the effdate as empty
		if(effDateStr <> "" AND effDateStr <> "9999-12-31"){ 
			// date will be entered in 2013-01-01 format in the data table ,and the display format is MM/dd/yyyy
			effectiveDate = strtojavadate(effDateStr, "yyyyMMdd");
			effectiveDateFormatted = datetostr(effectiveDate, substring(_system_user_date_pref, 0, 10));
			
			tempDate= effectiveDateFormatted; // month + dateSeperator + day + dateSeperator + year; //MM/dd/yyyy

            if(effectiveDate > getDate()){ // is this a future rate?
                futureRateClass = "class='strHistFutureRate' ";
            }
		}else{
			tempDate="&nbsp;";
		}

		type = get(each,"Txn_Reason_Desc");
		baseRate = getfloat(each,"Monthly_Sales_Amt");
		amount = getfloat(each,"Mon_Sales_Change_Amt");
		totalYards = get(each,"monthly_yard_cnt");
		monthlyYardChangeCnt = get(each,"Mon_Yard_Change_Cnt");
		percent = 0.0;

        // to be calculated as amount/(base-amount)
		if(amount <> 0 AND baseRate <> 0){
    		originalAmount = (baseRate - amount);
    		if(originalAmount <> 0) {
    			percent = amount/originalAmount;
    		}
    		percent = round(percent * 100,2);
        }
		 
		erf = get(each,"Is_ERF_On");
		frf = get(each,"Is_FRF_On");
			
		details = "<td "+typeTableData+ ">" + type + "</td>" + 
				  "<td "+tabledataForPrices+">" + formatascurrency(baseRate,"USD") + "</td>" + 
				  "<td "+tabledataForPrices+">" + formatascurrency(amount,"USD") + "</td>" + 
				  "<td "+tabledataForPrices+">" + string(percent)+"%"+ "</td>" + 
			      "<td "+tabledataForPrices+">" + monthlyYardChangeCnt + "</td>" + 
				  "<td "+tabledata+">" + erf + "</td>" + 
			      "<td "+tabledata+">" + frf + "</td>" +  
				  "<td "+futureRateClass + tabledata +">" + tempDate +"</td>";

		if(count % 2 == 0){
			tableBody = tableBody + "<tr style='background-color: #F0FAFD;border:1pt solid grey;'>" + details + "</tr>"; 
		}else{
			tableBody = tableBody + "<tr style='border:1pt solid grey;'>" + details + "</tr>"; 
		}
		
	}
	finalString = finalString + tableBody;
	finalString = finalString + "</tbody>";
	finalString = finalString + "</table></div>";
}

if(tableBody == ""){
	return "<font color=\"red\" size=\"3\">No Transactions are Available</font>"; // when there is no details fetched from table then display this
}
return finalString;