/* 
================================================================================
Name:   Recommendation Rule - Box Mail Box Array Load - Box Mail Box Count
Author:   Mark Egeland
Create date:  20150716
Description:  #710 Determines the number of rows for the array based on entries in BoxMailBackBoxInfo data table
        
Input:      N/A
                    
Output:     result(count of data table entries)

Updates:    20150730 - Mark Egeland - #710 Update query to exclude asset registration
=====================================================================================================
*/
result = 0;

query = bmql("SELECT box FROM BoxMailBackBoxInfo WHERE weightLimit <> -1");

for each in query{
	result = result + 1;
}

return result;