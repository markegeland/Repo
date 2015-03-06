/* 
=====================================================================================================
       Name: setCoverLetterStyle util function - Set Cover Letter Style
     Author: John Palubinskas
Create date: 1 Mar 2015
Description: Returns the value used to set the coverLetterStyle_quote attribute.  
             Determined by whether or not the division offers recycling, the market segment chosen, 
             and if it's a new or existing customer.
        
Input:      division - String - Lawson division number
            salesActivity - String - Existing Customer, or any of the New values
            segment - String - Environmental, Performance, Price
                    
Output:     String container the coverLetterStyle_quote value to set.

Updates:    
        
=====================================================================================================
*/

res = "new-generic"; // default to new-generic

recycling = false;
is_recycling = 0;
divRecyclingRecordSet = bmql("SELECT Is_Recycling FROM Div_Waste_Types WHERE division = $division");

for divRecyclingRecord in divRecyclingRecordSet {
  is_recycling = getint(divRecyclingRecord, "Is_Recycling");
  if(is_recycling == 1){
    recycling = true;
    break;
  }
}

if(salesActivity == "Existing Customer"){
  if(recycling){
    if(segment == "Environmental"){ res = "existing-environmental"; }
    if(segment == "Performance"){ res = "existing-performance"; }
    if(segment == "Price"){ res = "existing-price"; }
  }
  else{
    res = "existing-generic";
  }
}
else{ // new customer
  if(recycling){
    if(segment == "Environmental"){ res = "new-environmental"; }
    if(segment == "Performance"){ res = "new-performance"; }
    if(segment == "Price"){ res = "new-price"; }
  }
}

return res;