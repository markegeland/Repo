<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sf2="urn:partner.soap.sforce.com" xmlns:sf="urn:sobject.partner.soap.sforce.com">
	<xsl:variable name="main_doc" select="/transaction/data_xml/document[@data_type=0]"/>
	<xsl:output method="xml"/>

	<xsl:template match="*">
		<!-- grab oppId from result as best determination if result contained data; crmOpportunityId_quote may have invalid value -->
		
		<xsl:variable name="IsPrimaryQuote" select="//sf:BigMachines__Is_Primary__c"/>			
		<data_xml>
			<xsl:if test="string-length(//sf:BigMachines__Is_Primary__c) &gt; 0">
				<document document_var_name="quote_process">
					<crmisPrimary_quote><xsl:value-of select="$IsPrimaryQuote"/></crmisPrimary_quote>
				</document>
			</xsl:if>
		</data_xml>
	</xsl:template>

</xsl:stylesheet>