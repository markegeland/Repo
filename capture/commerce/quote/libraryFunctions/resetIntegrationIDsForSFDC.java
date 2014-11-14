results = "";
if (len(crmOpportunityId_quote) > 0) {
	for line in line_process {
		if (startswith(line.syncId_line, crmOpportunityId_quote + "-" + line._document_number) == false) {
        		results = results + line._document_number + "~syncId_line~";
			results = results + crmOpportunityId_quote + "-" + line._document_number + "-" + line._part_number + "|";
		}
	}
}

for line in line_process {
	results = results + line._document_number + "~externalId_line~";
	results = results + line._document_number + "-" + _system_buyside_id + "|";
}


return results;