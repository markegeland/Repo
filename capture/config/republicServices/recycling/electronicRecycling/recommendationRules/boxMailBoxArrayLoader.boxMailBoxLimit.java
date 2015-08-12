/* 
================================================================================
Name:   Recommendation Rule - Box Mail Box Array Load - Box Mail Box Limit
Author:   Mark Egeland
Create date:  20150716
Description:  #710 Loads all of the box weight limits from the BoxMailBackBoxInfo data table
        
Input:      N/A
                    
Output:     result(box weight limits)

Updates:    20150730 - Mark Egeland - #710 Update query to exclude asset registration
=====================================================================================================
*/
result = Float[];

query = bmql("SELECT weightLimit FROM BoxMailBackBoxInfo WHERE weightLimit <> -1");

for each in query{
	append(result, getFloat(each, "weightLimit"));
}

return result;