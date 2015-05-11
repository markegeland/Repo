/* SalesEngine Commerce Process > Quote > Visual Workflow
** Advanced Default	

** PARAMETERS:
** _system_current_step_var - Current step in the workflow process
** DESCRIPTION:
** Function to display a visual workflow break-down of where in the approval process the Quote is
*/

ret = "<img src='/bmfsweb/" + lower(_system_supplier_company_name) + "/image/images/";

if(_system_current_step_var == "start_step" OR _system_current_step_var == "pending_process"){
	ret = ret + "approval-workflow-in-progress";
}
elif(_system_current_step_var == "underManagerReview_process"){
	ret = ret + "approval-workflow-pending-approval";
}
elif(_system_current_step_var == "approved_process" OR _system_current_step_var == "submitted_process"){
	ret = ret + "approval-workflow-approved";
}
else{
	ret = ret + "approval-workflow-in-progress";
}

ret = ret + ".gif' alt='Broken Visual Workflow' />";

return ret;