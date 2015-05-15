/*
04/28/2015 - Sara - #533 Fix the Adhoc line iem zeroing out Issue SR# 3-10641454161
*/
ret = ""; 

for line in line_process{ 
if (line._price_configurable_price <> line._price_list_price_each){ 
ret = ret + line._document_number + "~_price_configurable_price~" + string(line._price_list_price_each) + "|"; 
} 
else{ 
ret = ret + line._document_number + "~_price_configurable_price~" + string(line._price_configurable_price) + "|"; 
} 
} 

return ret;