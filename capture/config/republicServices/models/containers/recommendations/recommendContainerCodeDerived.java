//Changed to prevent incorrect occurences of Hand Pickup while retaining the possiblities for non .5 sized hand pickups for existing customers.  Assumes no new business will have non .5 sized hand pickup.
retStr  = "";
// Change the Compactor from Boolean to String.

compactorInt = 0;
if(compactor){
	
	compactorInt = 1;//Parts DB has 0 for compactor part and "1" for non-compactor part
}
//Added as part of cart pricing to pull a specific code from Div_Container_Size 
containerCodes = split(routeTypeDervied, "^_^");

codeRecords = BMQL("SELECT container_cd FROM Div_Container_Size WHERE division = $division_config AND containerSize = $containerSize AND has_compactor = $compactorInt AND container_cd IN $containerCodes");
print codeRecords;

for record in codeRecords{
	if(get(record, "container_cd") <> "HP"){
		retStr = get(record, "container_cd");
	}elif(containerSize == ".5"){ 
		retStr = "HP";
	}
}

return retStr;