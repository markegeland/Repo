/* 
BML util lib : getXMLNodesByTag
args:
   XML - String - xml to be parsed
   tag - String - value to get
return:
   values - String[] of values found in tag

*/

// initalize empty string array
result = String[];

// maximum number of elements
loop = split(xml,"<" + tag);
rest = xml;

// list all possible types of tags
attrTag = "<" + tag + " ";
noAttrTag = "<" + tag + ">";
emptyTag = "<" + tag + "/>";
endTag = "</" + tag + ">";

nodeTypes = String[]{attrTag,noAttrTag,emptyTag};
nodeTypeRange = range(sizeofarray(nodeTypes));

for i in loop 
{
	nextNodeIndex = -1;
	nextNodeType = -1;
	
	for j in nodeTypeRange 
	{
		index = find(rest,nodeTypes[j]);
		if(index > -1 and (nextNodeIndex == -1 or nextNodeIndex > index)) 
		{
			nextNodeIndex = index;
			nextNodeType = j;
		}
	}
	
	if(nextNodeIndex == -1) 
	{break;}
	
	if(nodeTypes[nextNodeType] == emptyTag) 
	{
		endIndex = nextNodeIndex + len(emptyTag);
	}
	else 
	{
		endIndex = find(rest,endTag);
		if(endIndex == -1) 
		{break;}
		endIndex = endIndex + len(endTag);
	}
	
	openTagEndIndex = nextNodeIndex;
	xmlSubstring = substring(rest,nextNodeIndex,endIndex);
	
	if(nextNodeType == 0 OR nextNodeType == 1)
	{
		openTagEndIndex = find(xmlSubstring,">") + nextNodeIndex +1;
	}
	
	closeTagStartIndex = endIndex - (len(tag)+3);
	
	append(result,substring(rest,openTagEndIndex,closeTagStartIndex));
	rest = substring(rest,endIndex);
}
return result;