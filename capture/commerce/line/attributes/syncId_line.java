syncId = "";

if (len(crmOpportunityId_quote) > 0) {
	syncId = crmOpportunityId_quote + "-" + _document_number + "-" + _part_number;
}

return syncId;