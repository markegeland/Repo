/* 
================================================================================
       Name: recommendContainerCodeDerived
     Author: Aaron (I think...)
Create date: 2014 - carts implementation
Description: Returns the container code from Div_Container_Size based on inputs. 
        
      Input: division_config:  String  - Lawson division of quote
             compactor:        Boolean - true if part has a compactor
             containerSize:    String  - from container size drop down options
             routeTypeDerived: String  - array of route types delimited by ^_^
                    
     Output: String the derived container code

      Notes: If containerSize = .50, it is a 100 gallon cart.  If it is .5, then it is hand pickup.

    Updates: 20150109 - John Palubinskas - #78 Fix for HP container code not showing on CSA

================================================================================
*/

retStr  = "";

// Change the Compactor from Boolean to String.
compactorInt = 0;
if(compactor){
    compactorInt = 1;
}

// Handle containerSize field in Div_Container_Size being loaded with values rounded to two decimal places
containerSizeArr = split(containerSize,".");
containerSizeFormatted = containerSize;
if (sizeofarray(containerSizeArr) == 2 AND len(containerSizeArr[1]) == 1){
    containerSizeFormatted = containerSizeArr[0] + "." + containerSizeArr[1] + "0";
}

containerCodes = split(routeTypeDervied, "^_^");

codeRecords = BMQL("SELECT container_cd FROM Div_Container_Size WHERE division = $division_config AND containerSize = $containerSizeFormatted AND has_compactor = $compactorInt AND container_cd IN $containerCodes");

for record in codeRecords{
    if(get(record, "container_cd") <> "HP"){
        retStr = get(record, "container_cd");
        break;
    }
}

// Since the query above can potentially return multiple container codes, force HP for a .5 containerSize.
// If containerSize = .50, it is a 100 gallon cart.  If it is .5, then it is hand pickup.
if(containerSize == ".5"){ 
    retStr = "HP";
}

return retStr;