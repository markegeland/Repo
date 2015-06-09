<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sf2="urn:partner.soap.sforce.com" xmlns:sf="urn:sobject.partner.soap.sforce.com">
	<xsl:variable name="main_doc" select="/transaction/data_xml/document[@data_type=0]"/>
	<xsl:output method="xml"/>
	<xsl:template match="*">		
		<data_xml>
				<document document_var_name="quote_process">
					<!--Just take the first Record. In the future, when multi-site quoting is implemented, this will probably be updated to pull in all records and store it as a delimited string.-->
					<xsl:for-each select="//sf2:records[1]">
						<sfdcSiteStringValidation_quote>
							Site Address Line 1!nv!_siteAddress_quote_address!nv!<xsl:value-of select="//sf:Address_Line_1__c"/>!&amp;!
							Site Address Line 2!nv!_siteAddress_quote_address_2!nv!<xsl:value-of select="//sf:Address_Line_2__c"/>!&amp;!
							Site City!nv!_siteAddress_quote_city!nv!<xsl:value-of select="//sf:City__c"/>!&amp;!
							<!-- Ensure that the state is upper case. -->
							Site State!nv!_siteAddress_quote_state!nv!<xsl:value-of select="translate(//sf:State__c, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>!&amp;!
							Site Zip!nv!_siteAddress_quote_zip!nv!<xsl:value-of select="//sf:Postal_Code__c"/>!&amp;!
							Site Longitude!nv!siteLongitude!nv!<xsl:value-of select="//sf:Longitude__c"/>!&amp;!
							Site Latitude!nv!siteLatitude!nv!<xsl:value-of select="//sf:Latitude__c"/>!&amp;!
						</sfdcSiteStringValidation_quote>	
					</xsl:for-each>
				</document>
		</data_xml>
	</xsl:template>
</xsl:stylesheet>