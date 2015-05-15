// 20150325 - John Palubinskas - #449 remove check for new from compeitior

if(lower(salesActivity_quote) == "new/new" OR existingCustomerWithNewSite_quote == true){
  return true;
}
return false;