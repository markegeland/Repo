/*
=======================================================================================================================
Name:        setRateRestrictions
Author:      John Palubinskas
Create date: 10 Feb 2015

Description: Commerce function to set all rate restriction attributes.  Runs in postPricingFormulas so that we have
             the rate restriction data for approval emails as well as the doc engine.
        
Input:       effectiveServiceDate_quote
             year1Rate_quote, year2Rate_quote, year3Rate_quote, year4Rate_quote
                    
Output:      String (documentNumber + "~" + attributeVariableName + "~" + value + "|")

Updates:     

=======================================================================================================================
*/
res = "";

// Format for printable rate restrictions is 'Increase 5%' or 'CPI Increase'
rateArray = string[]{year1Rate_quote, year2Rate_quote, year3Rate_quote, afterYear4_quote};

i=0;
for rate in rateArray{
    rate = upper(rate);
    if (find(rate, "CPI")> -1){
        rate = rate + " Increase";
    }
    elif(find(rate, "%")> -1){
        rate = "Increase " + rate;
    }
    elif(rate <> ""){
        rate = "Increase " + rate + "%";
    }
    else{
        rate = "";
    }
    rateArray[i] = rate;
    i = i + 1;
}

// Format for dates for rate restrictions after year 1 is MM/YYYY
effectiveYear = atoi(substring(effectiveServiceDate_quote, 0, 4));
afterYear1Date = substring(effectiveServiceDate_quote, 5, 7) + "/" + string(effectiveYear + 1);
afterYear2Date = substring(effectiveServiceDate_quote, 5, 7) + "/" + string(effectiveYear + 2);
afterYear3Date = substring(effectiveServiceDate_quote, 5, 7) + "/" + string(effectiveYear + 3);
afterYear4Date = substring(effectiveServiceDate_quote, 5, 7) + "/" + string(effectiveYear + 4);

res = res + "1~year1RatePrint_quote~" + rateArray[0] + "|"
          + "1~year2RatePrint_quote~" + rateArray[1] + "|"
          + "1~year3RatePrint_quote~" + rateArray[2] + "|"
          + "1~year4RatePrint_quote~" + rateArray[3] + "|"
          + "1~afterYear1Date_quote~" + afterYear1Date + "|"
          + "1~afterYear2Date_quote~" + afterYear2Date + "|"
          + "1~afterYear3Date_quote~" + afterYear3Date + "|"
          + "1~afterYear4Date_quote~" + afterYear4Date + "|";
return res;