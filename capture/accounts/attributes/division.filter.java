/* 
================================================================================
Name:        Accounts Division Filter
Author:      ???
Create date: ??? 
Description: Filters the InfoPro Division list based on the user's groups.
        
Input:       division_quote: String - Division of quote
                    
Output:      String (documentNumber + "~" + attributeVariableName + "~" + value + "|")

Updates:     20141106 - John Palubinskas - updated to sort the division list array
             20141218 - John Palubinskas - fix for 2014 R1 upgrade, split function
=====================================================================================================
*/
groupsArr = string[];
retStrArr = string[];
lawsonDivArr = string[];
infoProArr = string[][];
divisionStr = "";
corporateUser = false;
retStr = "";

if(_group_var_name <> ""){
    groupsArr = split(_group_var_name, "~");
}

//This works for Non-zero division groups where division Number will always have 4 characters
for eachGrp in groupsArr{
    if(len(eachGrp) >= 5){

        eachGrpCharArr = split(eachGrp, "");

        if(isnumber(eachGrpCharArr[1]) AND isnumber(eachGrpCharArr[2]) AND isnumber(eachGrpCharArr[3]) AND isnumber(eachGrpCharArr[4])){ 
            divisionStr = eachGrpCharArr[1] + eachGrpCharArr[2] + eachGrpCharArr[3] + eachGrpCharArr[4]; 
        }else{
            if(eachGrpCharArr[1] == "0"){
                corporateUser  = true;
            }
        }

        if(isnumber(divisionStr)){
            if(findinarray(lawsonDivArr, divisionStr) == -1){
                append(lawsonDivArr, divisionStr);
            }   
        }
    }   
}

if(NOT(corporateUser)){
    divisionArr = string[];
    selectColumnsArr = string[]{"infoProDivision"};
    whereClauseColumnsArr = string[];
    whereClauseCompsArr = string[];
    whereClauseValuesArr = lawsonDivArr;
    for each in lawsonDivArr{
        append(whereClauseColumnsArr, "lawsonDivisionNumber");
        append(whereClauseCompsArr, "=");
    }
    whereClauseUseORs = true;
    infoProArr = gettabledata("Division_Mapping", selectColumnsArr, whereClauseColumnsArr, whereClauseValuesArr, whereClauseCompsArr, whereClauseUseORs);

    for eachRow in infoProArr{
        for eachCol in eachRow{
            if(findinarray(divisionArr, eachCol) == -1){
                append(divisionArr, eachCol);
            }
        }
    }
    retStrArr = sort(divisionArr, "asc", "text");
}

print("corporateUser: " + string(corporateUser));

if(corporateUser){
    divisionArr = string[];
    infoProRecSet = bmql("SELECT DISTINCT infoProDivision FROM Division_Mapping");

    for each in infoProRecSet{
        infoProDiv = get(each, "infoProDivision");
        if (findinarray(retStrArr, infoProDiv) == -1)
        {
            append(divisionArr , infoProDiv);
        }
    }
    retStrArr = sort(divisionArr, "asc", "text");
}

if(NOT(isempty(retStrArr))){
    retStr = join(retStrArr, ",");
}

return retStr;