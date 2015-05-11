// 20150505 - John Palubinskas - #518 CRM integration

ret = "";
sourceSystem = "";
recordSet = bmql("SELECT sourceSystem_quote FROM commerce.quote_process ");
for record in recordSet{
	sourceSystem = get(record,"sourceSystem_quote");
}
if(sourceSystem == "SFDC"){
	ret = ret + "<style> #header-wrapper{display:none;}</style>";
	ret = ret + "<style type = \"text/css\">a[href=\"/commerce/buyside/commerce_manager.jsp?bm_cm_process_id=4653759&from_hp=true&_bm_trail_refresh_=true\"]{ display:none; } </style>";
}
return ret;