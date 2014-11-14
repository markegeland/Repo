headerStyle = "style='text-align:center;background-color:#DBEFFF;width: 200px'";
TableHeaders = string[]{"Info Pro","Effective Date","Txn Reason","ERF","FRF"};
finalString = "<table style='border:1pt solid black;font-size:8pt;font-family: Tahoma,Arial,Helvetica,sans-serif;' cellspacing='0'>";
finalString = finalString + "<tbody>";
//finalString = finalString + "<tr style='border:1pt solid grey;'>";
finalString = finalString + "<th style='text-align:center' colspan='5'>Transaction History</th>"; 
//finalString = finalString + "</tr>";
finalString = finalString + "<tr>";
tabledata = "style='border:1px solid grey'";

for eachTHeading in TableHeaders{
	finalString = finalString + "<td "+headerStyle+">"+eachTHeading+"</td>";	
}
finalString = finalString + "</tr>";

tableBody = "";
count = 0;
if(_quote_process_customer_id <> "" AND SiteNumber_quote <> "" AND containerGroupForTransaction_quote <> ""){
	resultSet= bmql("select infopro_acct_nbr,Eff_Dt_SK,Txn_Reason_Desc,FRF_Pct,ERF_Pct from Account_Sales_Hist where infopro_acct_nbr = $_quote_process_customer_id AND Acct_Nbr = $SiteNumber_quote AND Container_Grp_Nbr = $containerGroupForTransaction_quote");

	for each in resultSet{
		count = count + 1;	
		infoPro = get(each,"infopro_acct_nbr");
		effDate = get(each,"Eff_Dt_SK");
		txnReason = get(each,"Txn_Reason_Desc");
		erf = get(each,"ERF_Pct");
		frf = get(each,"FRF_Pct");
				
		details = "<td "+tabledata+">" + infoPro + "</td>" + "<td "+tabledata+">" + effDate + "</td>" + "<td "+tabledata+">" + txnReason + "</td>" + "<td "+tabledata+">" + erf + "</td>" + "<td "+tabledata+">" + frf + "</td>" ;
		if(count % 2 == 0){
			tableBody = tableBody + "<tr style='background-color: #F0FAFD;border:1pt solid grey;'>" + details + "</tr>"; //infoPro + effDate + txnReason + erf + frf 
		}else{
			tableBody = tableBody + "<tr style='border:1pt solid grey;'>" + details + "</tr>"; //infoPro + effDate + txnReason + erf + frf 
		}
		
	}
	finalString = finalString + tableBody;
	finalString = finalString + "</tbody>";
	finalString = finalString + "</table>";
}

if(tableBody == ""){
		return "<font color=\"red\" size=\"3\">No Transaction are Available</font>";
}
return finalString;