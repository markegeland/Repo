/*Updated for Large Existing */
regionArr = string[];
accTypeLine = "Temporary";
regionRecordSet = bmql("SELECT Region FROM CSACombo WHERE Division = $division_quote AND AccountType <> $accTypeLine");
for eachRec in regionRecordSet{
	region = get(eachRec, "Region");
	if(region <> ""){
		append(regionArr, region);
	}	
}
print regionArr;
If(isempty(regionArr) OR permExists == false){
	return true;
}else{
	return false;
}