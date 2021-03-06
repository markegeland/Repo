/*Disposal sites are filtered by Container type, equipment size, Waste Type, and Unit of Measure
Disposal site time is modified by paper work, disposal ticket signature
Overriding site total time updates large container pricing
so any of these changes should rerun disposal sites rule - prepare a delimited string to save current selections

Container type$_$equipment size$_$Waste Type$_$paper work$_$disposal ticket signature
*/
DELIM = "$_$";
tempArray = string[];
append(tempArray, containerType_l);
append(tempArray, equipmentSize_l);
append(tempArray, wasteType);
append(tempArray, unitOfMeasure);
append(tempArray, string(additionalPaperwork_l));
append(tempArray, string(disposalTicketSignature_l));


retStr = join(tempArray, DELIM);

return retStr;