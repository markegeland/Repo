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
		deliveryComments = replace(getconfigattrvalue(line._document_number,"deliveryNotes_s"),"&","and");
		serviceComments = replace(getconfigattrvalue(line._document_number,"serviceNotes_s"),"&","and");
		written = false; 
	}elif(written == false){
		if(deliveryComments <> "" AND NOT isnull(deliveryComments)){
			description = line._part_desc;
			allDeliveryComments = allDeliveryComments + description + " - " + deliveryComments + "\n";
			htmlDeliveryComments = htmlDeliveryComments + "<p>" + description + " - " + deliveryComments + "</p>";
		}
		if(serviceComments <> "" AND NOT isnull(serviceComments)){
			description = line._part_desc;
			allServiceComments = allServiceComments + description + " - " + serviceComments+ "\n";
			htmlServiceComments = htmlServiceComments + "<p>" + description + " - " + serviceComments+ "</p>";
		}
		written = true;
	}
}

result = result + "1~deliveryNotes_quote~" + allDeliveryComments + "|";
result = result + "1~deliveryNotesRichText_quote~" + htmlDeliveryComments + "|";
result = result + "1~serviceNotes_quote~" + allServiceComments + "|";
result = result + "1~serviceNotesRichText_quote~" + htmlServiceComments +"|";
return result;