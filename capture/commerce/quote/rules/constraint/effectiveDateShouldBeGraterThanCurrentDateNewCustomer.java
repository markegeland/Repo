// 20150325 - John Palubinskas - #449 remove check for new from competitor

// Effective date should always be greater than today's date
// This rule should run only if CSA is selected to be printed/emailed
result = false;

if(_system_current_step_var == "generateDocuments" AND chooseCSA_quote)
{
    result = true;
}

return result;