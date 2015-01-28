/*
    BML Util: eval - To evaluate the condition is true or not based on the input attribute dictionary
    param:
      expression
      attr
    return: 
      String 
*/

debug = false;
wildcard = "NA";
null = "";
quote = "\"";
delim = "|^|";
returnVal = -1;
ORop = " || ";
ANDop = " && ";
plusOp = "+";
multOp = "*";
gtOp = ">";
ltOp = "<";
gteOp = ">=";
lteOp = "<=";
eqOp = "=";
neOp = "<>";
containOp = "contains";
containStrOp = "containsStr";
notContainOp = "notContains";
boolOps = String[]{gteOp,lteOp,neOp,gtOp,ltOp,eqOp,containOp,containStrOp,notContainOp};

if(expression == null) {
  return expression;
}
if (substring(expression,0,1) == quote and substring(expression,-1) == quote) {
  return substring(expression,1,-1);
}

clauses = split(expression,ANDop);
clauseVals = String[];

i = 0;
for clause in clauses {
  if(debug){print("AND-CLAUSE:" + clause);}
  clauseTrue = false;
  if(substring(clause,0,1) == quote and substring(clause,-1) == quote) {
    clauseTrue = true;
    clauseVals[i] = substring(clause,1,-1);
    i = i+1;
    continue;
  }
  subClauses = split(clause,ORop);
  subClauseVals = String[];
  j = 0;
  for subClause in subClauses {
    if(debug){print("   OR-CLAUSE: " + subClause);}
    if(substring(subclause,0,1) == quote and substring(subclause,-1) == quote) {
      j=j+1;
      continue;
    }
    comps = String[]{subClause};
    thisOp = null;
    for op in boolOps {
      if(find(subClause,op)>0) {
        thisOp = op;
        comps = split(subClause,op);
        break;
      }
    }
    k = 0;
    for comp in comps {
      if(debug){print("      Comp:"+comp);}
      valNum = 0;
      valStr = null;
      if(substring(comp,0,1) == quote and substring(comp,-1) == quote) {
        valStr = substring(comp,1,-1);
      } else {
        summands = split(comp,plusOp);
        if(debug){print "            SUMMANDS:" + join(summands,", ");}

        for summand in summands {
          if(summand == null) {
            continue;
          }
          if(isnumber(summand)) {
            valNum = valNum + atof(summand);
            continue;
          }
          mults = split(summand,multOp);
          mtotal = 1;
          if(debug){print "              MULTS:" + join(mults,",");}
          for mult in mults {
            mNum = 1;
            if(isnumber(mult)) {
              mtotal = mtotal * atof(mult);
              continue;
            }
            isVariable = containskey(attrs,mult);
            if(isVariable) {
              varVal = get(attrs,mult);
              if(isnumber(varVal)) {
                mNum = atof(varVal);
              }
              else {
                valStr = varVal;
              }
            }
            if(not(isVariable)) { 
              valStr = comp;
              break;
            }
            mtotal = mtotal * mNum;
          }
          valNum = valNum + mtotal;
        }
      }

      if(valStr == null ) {
        valStr = string(valNum);
      }
      if(debug){print("                VAL: " + valStr);}
      comps[k] = valStr;
      k = k+1;
    }
    leftSide = 0;
    numbers = false;
    rightSide = 0;
    //rightSideNum = false;
    
    if(thisOp == null) {
      // no comparator, return value of expression
      clauseTrue = true;
      return comps[0];
    }
    if(isnumber(comps[0])) {
      leftSide = atof(comps[0]);
      numbers = true;
    }
    if(numbers) {
      if(not(isnumber(comps[1]))) {	
        numbers = false;
      } else {
        rightSide = atof(comps[1]);
      }
    }
    //if(debug){print(comps[0] + thisOp + comps[1] + "?");}
    //clauseTrue = false;
    if(thisOp == gtOp) {
      if(numbers and leftSide > rightSide or
          (not(numbers) and comps[0] > comps[1])) {
        clauseTrue = true;
        break;
      }
      
    } elif (thisOp == ltOp) {
      //if(debug){print("ltOp");}
      if(numbers and leftSide < rightSide or
          (not(numbers) and comps[0] < comps[1])) {
        
        clauseTrue = true;
        break;
      }
      
    } elif (thisOp == lteOp) {
      if(numbers and leftSide <= rightSide or
          (not(numbers) and comps[0] <= comps[1])) {
        clauseTrue = true;
        break;
      }
      
    } elif (thisOp == gteOp) {
      if(numbers and leftSide >= rightSide or
          (not(numbers) and comps[0] >= comps[1])) {
        clauseTrue = true;
        break;
      }
      
    } elif (thisOp == eqOp) {
      //if(debug){print(comps[0] + thisOp + comps[1]);}
      if(numbers and leftSide == rightSide or
          (not(numbers) and comps[0] == comps[1])) {
        clauseTrue = true;
        break;
      }
      
    } elif (thisOp == neOp) {
      
      if(numbers and leftSide <> rightSide or
          (not(numbers) and comps[0] <> comps[1])) {
        clauseTrue = true;
        break;
      }
      
    }
    elif ( thisOp == containOp ) {
      comps0 = comps[0];
      comps0 = replace(comps0, ",", "~");
      
      list = split(comps0 , "~");
      list2 = split(comps[1], ",");
      /*
      print "###";
      print list[0];
      compVal = comps[0];
      print compVal;
      print len(compVal);
      print findinarray(list, compVal);
      print "###";
      // comment out here so that we can have multiple values in both side.
      if ( not(numbers) and findinarray(list, item) <> -1 ) {
          clauseTrue = true;
          break;
        }
      */
      for item in list2 {
        if ( not(numbers) and findinarray(list, item) <> -1 ) {
          clauseTrue = true;
          break;
        }
      }
      if ( clauseTrue ) {
        break;
      } 
    }
    elif ( thisOp == notContainOp ) {     // Newly added: not contains 
      comps0 = comps[0];
      comps0 = replace(comps0, ",", "~");
      
      list = split(comps0 , "~");
      list2 = split(comps[1], ",");

      clauseTrue = true;
      for item in list2 {
        if ( debug ) {
          print item + ": " + string(findinarray(list, item)) ;
        }
        if ( not(numbers) and findinarray(list, item) <> -1 ) {
          clauseTrue = false ;
        }
      }
      
      if ( clauseTrue ) {
        break;
      }       
    } 
    if ( debug ) { print("current truth: " + string(clauseTrue));}
    j = j+1;
  }
  if(not(clauseTrue)) {
    return "FALSE";
  }
}
return "TRUE";