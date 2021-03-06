<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sf2="urn:partner.soap.sforce.com" xmlns:sf="urn:sobject.partner.soap.sforce.com">
	<xsl:variable name="main_doc" select="/transaction/data_xml/document[@data_type=0]"/>
	<xsl:output method="xml"/>

	<xsl:template match="*">
		<!-- grab oppId from result as best determination if result contained data; crmOpportunityId_quote may have invalid value -->
		<xsl:variable name="year" select="substring(//sf:CloseDate,1,4)"/>
		<xsl:variable name="month" select="substring(//sf:CloseDate,6,2)"/>
		<xsl:variable name="day" select="substring(//sf:CloseDate,9,2)"/>		
		<data_xml>
			<xsl:if test="string-length(//sf:Name) &amp;gt; 0">
				<document document_var_name="quote_process">
					<sfdcOpportunityStringValidation_quote>
						Area!nv!area_quote!nv!<xsl:value-of select="substring(//sf:FS_Area__c,1,3)"/>!&amp;!
						Lawson Division!nv!division_quote!nv!<xsl:value-of select="//sf:Lawson_Division__r/sf:Name"/>!&amp;!
						<!--InfoPro Division!nv!infoProDivision_quote!nv!<xsl:value-of select="//sf:InfoPro_Number__c"/>!&amp;! -->
						Sales Activity!nv!salesActivity_quote!nv!<xsl:choose><xsl:when test="//sf:Sales_Activity__c = 'New'">New/New</xsl:when><xsl:otherwise><xsl:value-of select="//sf:Sales_Activity__c"/></xsl:otherwise></xsl:choose>!&amp;!
						Industry!nv!industry_quote!nv!<xsl:value-of select="//sf:Industry__c"/>!&amp;!
					</sfdcOpportunityStringValidation_quote>
				</document>
			</xsl:if>
		</data_xml>
	</xsl:template>
</xsl:stylesheet>