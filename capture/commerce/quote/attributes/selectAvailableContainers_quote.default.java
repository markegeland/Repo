// Populating the container group details as a dynamic dropdown value to use it in the backend
delimer="$$";		
result = "";
value="";
LARGE_CONTAINER = "Large Containers";
SMALL_CONTAINER = "Containers";
SERVICE_CHANGE = "Service Change";
haulsPerPeriod = "";
containerGroupArray = string[];

for line in line_process {
	containerGroup ="";
	wasteType = "";
	frequency = "";
	containerSize = "";
	quantity = "";
	
	if(line._parent_doc_number == ""){
		//Get Small Container
		if(line._model_name == SMALL_CONTAINER){
			if(NOT isnull(getconfigattrvalue(line._document_number, "wasteType"))){
				//wasteType = replace(getconfigattrvalue(line._document_number, "wasteType")," " , "&#32;");
				wasteType = getconfigattrvalue(line._document_number, "wasteType");
			}
			if(NOT isnull(getconfigattrvalue(line._document_number, "liftsPerContainer_s"))){
				frequency = getconfigattrvalue(line._document_number, "liftsPerContainer_s"); //Ex: 1/Week
			}
			if(NOT isnull(getconfigattrvalue(line._document_number, "containerSize"))){	
				containerSize = getconfigattrvalue(line._document_number, "containerSize");	
			}
			if(NOT isnull(getconfigattrvalue(line._document_number, "quantity"))){
				quantity=getconfigattrvalue(line._document_number, "quantity");
			}
		}
		//Get Service Change 
		elif(line._model_name == SERVICE_CHANGE){
			
			
			if(NOT isnull(getconfigattrvalue(line._document_number, "wasteType_readOnly"))){
				//wasteType = replace(getconfigattrvalue(line._document_number, "wasteType_readOnly")," " , "&#32;");
				wasteType = getconfigattrvalue(line._document_number, "wasteType_readOnly");
			}
			if(NOT isnull(getconfigattrvalue(line._document_number, "quantity_readOnly"))){
				quantity = getconfigattrvalue(line._document_number, "quantity_readOnly");
			}
			if(NOT isnull(getconfigattrvalue(line._document_number, "containerSize_readOnly"))){
				containerSize = getconfigattrvalue(line._document_number, "containerSize_readOnly");
			}
			if(NOT isnull(getconfigattrvalue(line._document_number, "liftsPerContainer_readOnly"))){
				frequency = getconfigattrvalue(line._document_number, "liftsPerContainer_readOnly");
			}
			
			if(NOT isnull(getconfigattrvalue(line._document_number, "wasteType_sc"))){
				wasteType_sc=getconfigattrvalue(line._document_number, "wasteType_sc");
				if(wasteType_sc <> "No Change"){
					wasteType = wasteType_sc;
				}
			}
			if(NOT isnull(getconfigattrvalue(line._document_number, "quantity_sc"))){
				quantity_sc=getconfigattrvalue(line._document_number, "quantity_sc");
				if(quantity_sc <> quantity){
					quantity = quantity_sc;
				}	
			}
			if(NOT isnull(getconfigattrvalue(line._document_number, "containerSize_sc"))){
				containerSize_sc=getconfigattrvalue(line._document_number, "containerSize_sc");
				if(containerSize_sc <> "No Change"){
					containerSize = containerSize_sc;
				}
			}
			if(NOT isnull(getconfigattrvalue(line._document_number, "liftsPerContainer_sc"))){
				liftsPerContainer_sc=getconfigattrvalue(line._document_number, "liftsPerContainer_sc");
				if(liftsPerContainer_sc <> "No Change"){
					frequency = liftsPerContainer_sc;
				}
			}
		}
		//Get Large Container
		elif(line._model_name == LARGE_CONTAINER){
			continue;
			//commented out 7/31 by Julie, large container shouldn't populate the dropdown list
			
			/*if(NOT isnull(getconfigattrvalue(line._document_number, "wasteType"))){
				//wasteType = replace(getconfigattrvalue(line._document_number, "wasteType")," " , "&#32;");
				wasteType = getconfigattrvalue(line._document_number, "wasteType");
			}
			
			if(NOT isnull(getconfigattrvalue(line._document_number, "haulsPerPeriod"))){
				frequency = getconfigattrvalue(line._document_number, "haulsPerPeriod");
			}
			
			if(NOT isnull(getconfigattrvalue(line._document_number, "equipmentSize_l"))){
				containerSize=getconfigattrvalue(line._document_number, "equipmentSize_l");
			}
			
			if(NOT isnull(getconfigattrvalue(line._document_number, "quantity"))){
				quantity=getconfigattrvalue(line._document_number, "quantity");
			}
			*/
			
		}
		containerSizeFloat = 0.0;
		if(isnumber(containerSize)){
			containerSizeFloat = atof(containerSize);
		}
		//Set the menu value
		//<option week="" waste$$2$$20.0$$1="" value="Solid">Solid Waste--2x20.0Yd--1/Week</option>
		containerGroup = wasteType + delimer + quantity + delimer + string(containerSizeFloat) + delimer + frequency;
		if(findinarray(containerGroupArray, containerGroup) == -1){
			append(containerGroupArray, containerGroup);
		}
	}
	
}
result = result+ "<select class='form-input' id='availableContainers'>";
//result = result + "<option>--Select--</option>"; //handled in Post Pricing recommending 1st model value
for each in containerGroupArray{
	itemArray = split(each, delimer);
	//Set the menu display value
	displayLabel = itemArray[0] + "--" +  itemArray[1] + "x" + itemArray[2] + "Yd" + "--" + itemArray[3];
	//Retain option selected 
	if(containerDetailsString_quote == each){
		result = result+ "<option value='"+each+"' selected>"+displayLabel+"</option>"	;	
	}
	else{
		result = result+ "<option value='"+each+"'>"+displayLabel+"</option>"	;	
	}
}
result = result+ "</select>";

print result;
return result;