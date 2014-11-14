quote_process_customer_id = "";
SiteNumber_quote = "";
containerGroupForTransaction_quote = "";
returnDict = dict("string");
if(containskey(inputDict, "_quote_process_customer_id")){
	quote_process_customer_id = get(inputDict, "_quote_process_customer_id");
}
if(containskey(inputDict, "SiteNumber_quote")){
	SiteNumber_quote = get(inputDict, "SiteNumber_quote");
}
if(containskey(inputDict, "containerGroupForTransaction_quote")){
	containerGroupForTransaction_quote = get(inputDict, "containerGroupForTransaction_quote");
}
headerStyle = "style='text-align:center;background-color:#DBEFFF;width: 200px'";
TableHeaders = string[]{"Container Group", "Qty","Size","Lifts Per Period", "Has Compactor?", "Est. Monthly Rev","Total with Fees"}; // all table headers defined here
finalString="<table cellspacing='0' width='100%'><th style='text-align:center width:100%' colspan='6'>Container</th></table>";
finalString = finalString+"<table style='border:1pt solid black;font-size:8pt;font-family: Tahoma,Arial,Helvetica,sans-serif;' cellspacing='0'>";
finalString = finalString + "<tbody>";
//finalString = finalString + "<th style='text-align:center' colspan='6'>Container Details</th>"; 
finalString = finalString + "<tr>";
for eachTHeading in TableHeaders{
	finalString = finalString + "<td "+headerStyle+">"+eachTHeading+"</td>";	
}
finalString = finalString + "</tr>";
tableBody = "";
count = 0;
tabledata="";
accountData = "";
containersDataArray = string[];
DELIM = "$_$";
DELIM1 = "@_@";
pickupPeriodUnit = "week";
/*We don't need Pickup_Period_Unit column anymore in account_statu table because it's value is always "week", so just hard-coding the local variable here*/
print "---here--";
print "SiteNumber_quote"; print SiteNumber_quote;
print "quote_process_customer_id"; print quote_process_customer_id;
if(quote_process_customer_id <> "" AND SiteNumber_quote <> "" ){
	resultSet= bmql("select Container_Grp_Nbr, monthly_rate, container_cnt, Container_Size, Pickup_Period_Length, period, Pickup_Per_Tot_Lifts, is_erf_charged, is_frf_charged, frf_rate_pct, erf_rate_pct,is_erf_on_frf, has_Compactor, Lift_Days, waste_type from Account_Status where infopro_acct_nbr = $quote_process_customer_id AND Site_Nbr = $siteNumber_quote");
	print "resultSet";print resultSet;
// all the container details are fetched from the table Account_Status data table based on customer Id and Site number
	for each in resultSet{
		rowDataArray = string[];
		count = count + 1;	
		containerGroup = get(each,"Container_Grp_Nbr");
		containerCnt = getFloat(each,"container_cnt");
		qty = string(containerCnt);
		size = get(each,"Container_Size");
		pickupPeriodLength = get(each,"Pickup_Period_Length");
		pickupPerTotLifts = getFloat(each,"Pickup_Per_Tot_Lifts");
		period = getFloat(each,"period");
		Lift_Days = get(each,"Lift_Days");
		waste_type = get(each,"waste_type");
		
		frequency = "";
		hasCompactor = get(each,"has_Compactor");
		
		hasCompactorFlag = "No";
		if(hasCompactor == "1"){
			hasCompactorFlag = "Yes";
		}
			
		if(lower(pickupPeriodUnit) == "week"){
			if(pickupPeriodLength == "2"){
				frequency = "EOW";
			}
			elif(pickupPeriodLength == "4"){
				frequency = "Every 4 weeks";
			}
			elif(pickupPeriodLength == "1"){
				frequencyFloat = pickupPerTotLifts/(containerCnt * period);
				frequencyInt = integer(frequencyFloat);
				if(frequencyInt == 1){
					frequency = "1 X Week";
				}
				elif(frequencyInt == 2){
					frequency = "2 X Week";
				}
				elif(frequencyInt == 3){
					frequency = "3 X Week";
				}
				elif(frequencyInt == 4){
					frequency = "4 X Week";
				}
				elif(frequencyInt == 5){
					frequency = "5 X Week";
				}
				elif(frequencyInt == 6){
					frequency = "6 X Week";
				}
				elif(frequencyInt == 7){
					frequency = "7 X Week";
				}
			}
		}
		
		append(rowDataArray, "containerGroup:"+containerGroup);
		append(rowDataArray, "containerCnt:"+string(containerCnt));
		append(rowDataArray, "size:"+size);
		append(rowDataArray, "pickupPeriodLength:"+pickupPeriodLength);
		append(rowDataArray, "pickupPerTotLifts:"+string(pickupPerTotLifts));
		append(rowDataArray, "period:"+string(period));
		append(rowDataArray, "hasCompactor:"+hasCompactor);
		append(rowDataArray, "Lift_Days:"+Lift_Days);
		append(rowDataArray, "waste_type:"+waste_type);
		append(rowDataArray, "frequency:"+replace(replace(frequency," ", ""),"X", "/"));
		recordStr = join(rowDataArray, DELIM);
		append(containersDataArray, recordStr);
		
		if(containerGroup == containerGroupForTransaction_quote){ // highlight the table row with red when it matches with selecter container group
			tabledata = "style='border:2px solid black;text-align:center'";
		}else{
			tabledata = "style='border:1px solid grey;text-align:center'";
		}
		estiMonthlyRev = getfloat(each, "monthly_rate");
		estFees = estiMonthlyRev * (1 + getfloat(each,"frf_rate_pct")/100 * getint(each,"is_frf_charged") * (1 + getfloat(each,"is_erf_on_frf") * getfloat(each,"erf_rate_pct")/100 * getfloat(each,"is_erf_charged")) + getfloat(each,"erf_rate_pct")/100 * getint(each,"is_erf_charged"));
	
		//details = "<td "+tabledata+">" + containerGroup + "</td>" +
		details = "<td id='grp"+containerGroup+"'"+tabledata+">" + containerGroup + "</td>" +
					"<td "+tabledata+">" + qty + "</td>" + 
					"<td "+tabledata+">" + size + "</td>" + 
					"<td "+tabledata+">" + frequency + "</td>"  + 
					"<td "+tabledata+">" + hasCompactorFlag + "</td>" + 
					"<td "+tabledata+">" + (formatascurrency(estiMonthlyRev, "USD")) + "</td>" + 
					"<td "+tabledata+">" + (formatascurrency(estFees,"USD")) + "</td>";
		if(count % 2 == 0){
			tableBody = tableBody + "<tr style='background-color: #F0FAFD;border:1pt solid grey;'>" + details + "</tr>"; 
		}else{
			tableBody = tableBody + "<tr style='border:1pt solid grey;'>" + details + "</tr>"; 
		}
		
	}
	finalString = finalString + tableBody;
	finalString = finalString + "</tbody>";
	finalString = finalString + "</table>";
}
if(sizeofarray(containersDataArray) > 0){
	accountData = join(containersDataArray, DELIM1); 
}

if(tableBody == ""){
		finalString = "<font color=\"red\" size=\"3\">No Transactions are Available</font>";
}
put(returnDict, "html", finalString);
put(returnDict, "text", accountData);

return returnDict;