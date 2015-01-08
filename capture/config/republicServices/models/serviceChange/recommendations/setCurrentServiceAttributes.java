quantity = 0;

accountStatusRecs = bmql("SELECT container_cnt FROM Account_Status WHERE Container_Grp_Nbr = $containerGroup_config AND  infopro_acct_nbr = $accountNumber AND Site_Nbr = $siteNumber_config");

for rec in accountStatusRecs{
    quantity = getint(rec, "container_cnt");    
}

return quantity;