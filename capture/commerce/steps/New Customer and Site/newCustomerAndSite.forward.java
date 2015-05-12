res = "";
SALES_REP_PROFILE = "Sales Rep";
MANAGER_PROFILE = "Manager";
EXEC_PROFILE = "Exec Manager";

SALES_REP_PROFILE_SFDC = "All - SFDC";
MANAGER_PROFILE_SFDC = "All - SFDC";
EXEC_PROFILE_SFDC = "All - SFDC";

if(sourceSystem_quote <> "SFDC") {
	res = res + "d0SalesReps" + "~" + _system_supplier_company_name + "~" + SALES_REP_PROFILE + "|" ;
	res = res + "d0Managers" + "~" + _system_supplier_company_name + "~" + MANAGER_PROFILE + "|" ;
	res = res + "d0ExecManagers" + "~" + _system_supplier_company_name + "~" + EXEC_PROFILE + "|" ;

	if(divisionSalesGroup_quote <> "") {
		res = res + divisionSalesGroup_quote + "~" + _system_supplier_company_name + "~" + SALES_REP_PROFILE + "|" ;
	}
	if(divisionManagerGroup_quote <> "") {
		res = res + divisionManagerGroup_quote + "~" + _system_supplier_company_name + "~" + MANAGER_PROFILE + "|" ;
	}
	if(divisionExecManagerGroup_quote <> "") {
		res = res + divisionExecManagerGroup_quote + "~" + _system_supplier_company_name + "~" + EXEC_PROFILE + "|" ;
	}
}
else {
	res = res + "d0SalesReps" + "~" + _system_supplier_company_name + "~" + SALES_REP_PROFILE_SFDC + "|" ;
	res = res + "d0Managers" + "~" + _system_supplier_company_name + "~" + MANAGER_PROFILE_SFDC + "|" ;
	res = res + "d0ExecManagers" + "~" + _system_supplier_company_name + "~" + EXEC_PROFILE_SFDC + "|" ;

	if(divisionSalesGroup_quote <> "") {
		res = res + divisionSalesGroup_quote + "~" + _system_supplier_company_name + "~" + SALES_REP_PROFILE_SFDC + "|" ;
	}
	if(divisionManagerGroup_quote <> "") {
		res = res + divisionManagerGroup_quote + "~" + _system_supplier_company_name + "~" + MANAGER_PROFILE_SFDC + "|" ;
	}
	if(divisionExecManagerGroup_quote <> "") {
		res = res + divisionExecManagerGroup_quote + "~" + _system_supplier_company_name + "~" + EXEC_PROFILE_SFDC + "|" ;
	}
}
return res;