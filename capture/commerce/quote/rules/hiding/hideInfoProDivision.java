if(division_quote <> ""){
	divisionMappingRecordSet = bmql("SELECT DISTINCT infoProDivision FROM Division_Mapping WHERE lawsonDivisionNumber = $division_quote");
	size = 0;
	for each in divisionMappingRecordSet{
		size = size + 1;
		
	}
	
	if ( size <= 1){
		return true;
	}
}else{
	return true;
}
	return false;