// 20150325 - John Palubinskas - #449 remove check for new from competitor

//if there are no models/parts added to the quote, then the previous action should take to customer site
if(NOT(commercialExists_quote) AND NOT(industrialExists_quote)){
  if(lower(salesActivity_quote) == "new/new" OR lower(salesActivity_quote) == "existing customer"){
    return true;
  }
}	
return false;