/* 
================================================================================
Name:    setChevron_er
Author:   Mark Egeland
Create date:  20150728
Description:  #710 Sets the header progress bar image
        
Input:      supplierCompany_config - String: holds the company name, to build the URL
                    
Output:    String: HTML to generate the progress bar image

Updates:    
=====================================================================================================
*/
return "<img src='/bmfsweb/"+lower(supplierCompany_config)+"/image/images/timeline_3.png' width='100%' alt='Broken Visual Workflow'/>";