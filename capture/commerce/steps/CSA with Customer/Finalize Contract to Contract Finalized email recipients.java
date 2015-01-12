/******************************************************************************
 *
 * Step: CSA with Customer - Finalize - Finalize Contract to Contract Finalized
 *
 * 20141207 John Palubinskas: Fix 2014 R1 upgrade issue where Cc fields no longer work.
 *                            Adding Transaction Creator (preparedByEmail_quote) to Recipient list.
 *
 ******************************************************************************/
emailListArr = string[];

if (orderManagementEmail_quote <> "") {
  append(emailListArr, orderManagementEmail_quote);
}
if (preparedByEmail_quote <> "") {
  append(emailListArr, preparedByEmail_quote);
}

emailListStr = join(emailListArr,",");

return emailListStr;