/*
=======================================================================================================================
       Name: financialDetailView_html_quote
Description: Gets and styles the HTML table used to populate financialDetailView_html_quote
        
  Input: None     
                    
 Output: String with HTML table styled for the Pricing financial detail view table output

Updates: 20150212 - John Palubinskas - #68 removed styles from financialDetailViewWithColor, so need to add table 
                                       styles here.
=======================================================================================================================
*/
htmlString = commerce.financialDetailViewWithColor("Black");

htmlString = replace(htmlString,"<table>","<table class='financial-details-table'>");

return htmlString;