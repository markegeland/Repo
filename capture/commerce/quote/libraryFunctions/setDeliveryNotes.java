/*20150421 - Gaurav Dawar - #539 - Fixed the XML Document pull error
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