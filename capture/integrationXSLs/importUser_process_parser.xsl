<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sf2="urn:partner.soap.sforce.com" xmlns:sf="urn:sobject.partner.soap.sforce.com">
	<xsl:output method="xml"/> 
	<xsl:template match="*">
		<data_xml>
			<xsl:if test="string-length(//sf:Id) > 0">
				<document document_var_name="quote_process">
					<crmUserId_quote><xsl:value-of select="//sf:Id" /></crmUserId_quote>
					<preparedByName_quote><xsl:value-of select="//sf:FirstName" />&#160;<xsl:value-of select="//sf:LastName" /></preparedByName_quote>
					<preparedByTitle_quote><xsl:value-of select="//sf:Title" /></preparedByTitle_quote>
					<preparedByPhone_quote><xsl:value-of select="//sf:Phone" /></preparedByPhone_quote>
					<preparedByCell_quote><xsl:value-of select="//sf:MobilePhone" /></preparedByCell_quote>
					<preparedByEmail_quote><xsl:value-of select="//sf:Email" /></preparedByEmail_quote>
				</document>
			</xsl:if>
		</data_xml>
	</xsl:template> 
</xsl:stylesheet> 
