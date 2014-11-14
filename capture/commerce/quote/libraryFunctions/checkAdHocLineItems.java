// Commerce Libraries > checkAdHocLineItems
// total ad-hoc quantities
adHocLineQtyTotal = miscChargeList1_quote + miscChargeList2_quote + miscChargeList3_quote + miscChargeList4_quote + miscChargeList5_quote + miscChargeList6_quote;
//of ad-hoc total is greater than zero flag is set to true
if ( adHocLineQtyTotal > 0 ) {
	return true;
}
return false;