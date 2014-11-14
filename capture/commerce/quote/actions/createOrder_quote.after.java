// Create Order - Advanced Modification

result = "";
delimiter = "~";
delimiterItem = "|";

attrVarValue = String[][] { {"status_quote", "Creating Order"},
			    {"orderedDate_quote",_system_date}
                          };
                          
for eachAttr in attrVarValue {
  result = result + _quote_process_document_number + delimiter + eachAttr[0] + delimiter + eachAttr[1] + delimiterItem;  
} 

return result;