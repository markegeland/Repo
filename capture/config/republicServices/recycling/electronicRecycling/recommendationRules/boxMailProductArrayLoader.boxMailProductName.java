/* 
================================================================================
Name:   Recommendation Rule - Box Mail Product Array Load - Box Mail Product Name
Author:   Mark Egeland
Create date:  20150715
Description:  #710 Loads all of the product names from the BoxMailBackWeights data table
        
Input:      N/A
                    
Output:     result(product names)

Updates:    
=====================================================================================================
*/
result = String[];

query = bmql("SELECT productName FROM BoxMailBackWeights");

for each in query{
	append(result, get(each, "productName"));
}

return result;