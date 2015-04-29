/* 
================================================================================
       Name:  setDeliveryNotes
     Author:  Aaron Quintanilla
Create date:  
Description:  Sets the delivery and service notes quote attributes from config
        
  Input:     deliveryNotes_line
             _config_attr_info
             _document_number
             _model_name
             _part_desc
                    
 Output:     quote level attributes
             deliveryNotes_quote
             deliveryNotesRichText_quote
             serviceNotes_quote
             serviceNotesRichText_quote

Updates:
    20150421 - Gaurav Dawar - #539 - Fixed the XML Document pull error
    20150423 - Aaron Quintanilla - #563 - Added check for blank config attributes since AdHoc won't have them
    20150428 - John Palubinskas - #549 Remove final line feed to prevent extra space when notes printed on CSA
    
================================================================================
*/

result = "";
deliveryComments = "";
serviceComments = "";
description = "";
written = true;
allDeliveryComments = "";
allServiceComments = "";
htmlDeliveryComments = "";
htmlServiceComments = "";

for line in line_process{
	if(line._model_name <> ""){
		if (find(line._config_attr_info, "deliveryNotes_s") <> -1){
			deliveryComments = replace(getconfigattrvalue(line._document_number,"deliveryNotes_s"),"&","and");
		}
		if (find(line._config_attr_info, "serviceNotes_s") <> -1){
			serviceComments = replace(getconfigattrvalue(line._document_number,"serviceNotes_s"),"&","and");
		}
		written = false; 
	}elif(written == false){
		if(deliveryComments <> "" AND NOT isnull(deliveryComments)){
			description = line._part_desc;
			allDeliveryComments = allDeliveryComments + description + " - " + deliveryComments + "\n";
			htmlDeliveryComments = htmlDeliveryComments + "<span>" + description + " - " + deliveryComments + "</span><br/>";
		}
		if(serviceComments <> "" AND NOT isnull(serviceComments)){
			description = line._part_desc;
			allServiceComments = allServiceComments + description + " - " + serviceComments+ "\n";
			htmlServiceComments = htmlServiceComments + "<span>" + description + " - " + serviceComments+ "</span><br/>";
		}
		written = true;
	}
}

// strip off the final /n since it leaves gaps in the CSA
if (allDeliveryComments <> "") { allDeliveryComments = substring(allDeliveryComments ,0,len(allDeliveryComments)-1); }
if (allServiceComments <> "") { allServiceComments = substring(allServiceComments ,0,len(allServiceComments )-1); }
if (htmlDeliveryComments <> "") { htmlDeliveryComments = substring(htmlDeliveryComments ,0,len(htmlDeliveryComments)-1); }
if (htmlServiceComments <> "") { htmlServiceComments = substring(htmlServiceComments ,0,len(htmlServiceComments )-1); }

result = result + "1~deliveryNotes_quote~" + allDeliveryComments + "|";
result = result + "1~deliveryNotesRichText_quote~" + htmlDeliveryComments + "|";
result = result + "1~serviceNotes_quote~" + allServiceComments + "|";
result = result + "1~serviceNotesRichText_quote~" + htmlServiceComments +"|";

return result;