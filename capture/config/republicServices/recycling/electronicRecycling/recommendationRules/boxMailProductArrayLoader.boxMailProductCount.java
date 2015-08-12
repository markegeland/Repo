/* 
================================================================================
Name:   Recommendation Rule - Box Mail Product Array Load - Box Mail Product Count
Author:   Mark Egeland
Create date:  20150715
Description:  #710 Determines the number of rows for the array based on entries in BoxMailBackWeights data table
        
Input:      N/A
                    
Output:     result(count of data table entries)

Updates:    
=====================================================================================================
*/
result = 0;

query = bmql("SELECT productName FROM BoxMailBackWeights");

for each in query{
	result = result + 1;
}

return result;