<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sf2="urn:partner.soap.sforce.com" xmlns:sf="urn:sobject.partner.soap.sforce.com">
	<xsl:variable name="main_doc" select="/transaction/data_xml/document[@data_type=0]"/>
	<xsl:output method="xml"/>
	<xsl:template match="*">		
		<data_xml>
				<document document_var_name="quote_process">
					<!--Just take the first Record. In the future, when multi-site quoting is implemented, this will probably be updated to pull in all records and store it as a delimited string.-->
					<xsl:for-each select="//sf2:records[1]">
						<siteName_quote> <xsl:value-of select="//sf:Site_Name__c"/> </siteName_quote>
						<_siteAddress_quote_address> <xsl:value-of select="//sf:Address_Line_1__c"/> </_siteAddress_quote_address>
						<_siteAddress_quote_address_2> <xsl:value-of select="//sf:Address_Line_2__c"/> </_siteAddress_quote_address_2>
						<_siteAddress_quote_city> <xsl:value-of select="//sf:City__c"/> </_siteAddress_quote_city>
						<!-- Ensure that the state is upper case. -->
						<_siteAddress_quote_state> <xsl:value-of select="translate(//sf:State__c, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/></_siteAddress_quote_state>
						<_siteAddress_quote_zip> <xsl:value-of select="//sf:Postal_Code__c"/> </_siteAddress_quote_zip>
					</xsl:for-each>
				</document>
		</data_xml>
	</xsl:template>
</xsl:stylesheet>