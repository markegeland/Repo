regionArr = string[];
regionRecordSet = bmql("SELECT Region FROM CSACombo WHERE Division = $division_quote");
for eachRec in regionRecordSet{
	region = get(eachRec, "Region");
	if(region <> ""){
		append(regionArr, region);
	}	
}
print regionArr;
If(isempty(regionArr)){
	return true;
}
return false;