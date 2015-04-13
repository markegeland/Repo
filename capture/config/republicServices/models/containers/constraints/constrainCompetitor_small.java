// 20150330 - John Palubinskas - #449 added for competitor rewrite

allowedCompArr = string[];
allowedCompetitors = "|^||^|"; // need to add a blank delimiter in order to get a blank menu item

// Get the division's competitors from div_competitor_adj data table
competitorRecSet = bmql("SELECT Competitor_Cd, competitor, infopro_reg FROM div_competitor_adj WHERE division = $division_config");

for each in competitorRecSet{
    competitor = get(each, "Competitor_Cd");
    region = get(each, "infopro_reg");
    append(allowedCompArr, competitor + region);
}
allowedCompetitors = allowedCompetitors + join(allowedCompArr, "|^|");

return allowedCompetitors;
