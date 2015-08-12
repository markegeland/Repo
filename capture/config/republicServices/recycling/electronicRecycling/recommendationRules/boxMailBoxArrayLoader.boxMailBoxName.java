/* 
================================================================================
Name:   Recommendation Rule - Box Mail Box Array Load - Box Mail Box Name
Author:   Mark Egeland
Create date:  20150716
Description:  #710 Loads all of the box names from the BoxMailBackBoxInfo data table
        
Input:      N/A
                    
Output:     result(box names)

Updates:    20150730 - Mark Egeland - #710 Update query to exclude asset registration
=====================================================================================================
*/
result = String[];

query = bmql("SELECT box FROM BoxMailBackBoxInfo WHERE weightLimit <> -1");

for each in query{
	append(result, get(each, "box"));
}

return result;