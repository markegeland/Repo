/* 
================================================================================
Name:   Recommendation Rule - Box Mail Product Array Load - Box Mail Product Weight
Author:   Mark Egeland
Create date:  20150715
Description:  #710 Loads all of the product weights from the BoxMailBackWeights data table
        
Input:      N/A
                    
Output:     result(product weights)

Updates:    
=====================================================================================================
*/
result = Float[];

query = bmql("SELECT weight FROM BoxMailBackWeights");

for each in query{
	append(result, getFloat(each, "weight"));
}

return result;