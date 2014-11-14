result = true;
count = 0;

competitorCodesRecs =  bmql("SELECT DISTINCT Competitor_Cd FROM div_competitor_adj  WHERE division = $division_quote AND competitor = $competitor_quote");

for rec in competitorCodesRecs {
	count = count + 1;
	print rec;
}

if (count > 1){
	result = false;
}

return result;