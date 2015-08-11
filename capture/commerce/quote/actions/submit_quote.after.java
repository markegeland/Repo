// Added in Github on - 01/08/2015
// Author - Gaurav Dawar
// 20150320 - GD - #468 - Removed 4960 from the list
// 20150406 - John Palubinskas - #449 add call to setTransactionCode


// This Piece of code is created to remove the default CSAVersion="California" for 
// the divisions which has multiple CSAversions.
ret = ""; 
if(    (division_quote == "4091") 
    OR (division_quote == "3439") 
    OR (division_quote == "4323") 
    OR (division_quote == "4463") 
    OR (division_quote == "4259") 
    OR (division_quote == "3607") 
    OR (division_quote == "3614") 
    OR (division_quote == "3639") 
    OR (division_quote == "3776") 
    OR (division_quote == "3778") 
    OR (division_quote == "3753") 
    OR (division_quote == "3757") 
    OR (division_quote == "3777") 
    OR (division_quote == "3754") 
    OR (division_quote == "3752") 
    OR (division_quote == "3758") 
    OR (division_quote == "3760") 
    OR (division_quote == "4384") 
    OR (division_quote == "4972") 
    OR (division_quote == "4467") 
    OR (division_quote == "3220") 
    OR (division_quote == "3010") 
    OR (division_quote == "4971") 
    OR (division_quote == "4468") 
    OR (division_quote == "4922") 
    OR (division_quote == "4959") 
    OR (division_quote == "3895") 
    OR (division_quote == "3893") 
    OR (division_quote == "3840") 
    AND (cSAVersion_quote == "California") AND permExists == true){ 
    ret = ret + "1~cSAVersion_quote~|"; 
} 

if(    (division_quote == "4091") 
    OR (division_quote == "3439") 
    OR (division_quote == "4323") 
    OR (division_quote == "4463") 
    OR (division_quote == "4259") 
    OR (division_quote == "3607") 
    OR (division_quote == "3614") 
    OR (division_quote == "3639") 
    OR (division_quote == "3776") 
    OR (division_quote == "3778") 
    OR (division_quote == "3753") 
    OR (division_quote == "3757") 
    OR (division_quote == "3777") 
    OR (division_quote == "3754") 
    OR (division_quote == "3752") 
    OR (division_quote == "3758") 
    OR (division_quote == "3760") 
    OR (division_quote == "4384") 
    OR (division_quote == "4972") 
    OR (division_quote == "4467") 
    OR (division_quote == "3220") 
    OR (division_quote == "3010") 
    OR (division_quote == "4971") 
    OR (division_quote == "4468") 
    OR (division_quote == "4922") 
    OR (division_quote == "4959") 
    OR (division_quote == "3895") 
    OR (division_quote == "3893") 
    OR (division_quote == "3840") 
    AND (cSAVersion_quote == "California") AND tempExists == true){ 
    ret = ret + "1~cSAVersionTemp_quote~|"; 
} 

ret = ret + commerce.setStatus("submit");
ret = ret + commerce.postPricingFormulas("");
ret = ret + commerce.setTransactionCode();

return ret;