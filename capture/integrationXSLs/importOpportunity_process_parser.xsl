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
			<xsl:if test="string-length(//sf:Name) &gt; 0">
				<document document_var_name="quote_process">
					<opportunityName_quote><xsl:value-of select="//sf:Name"/></opportunityName_quote>
					<opportunityStage_quote><xsl:value-of select="//sf:StageName"/></opportunityStage_quote>
					<probability><xsl:value-of select="//sf:Probability"/></probability>
					<closeDate><xsl:value-of select="$month"/>/<xsl:value-of select="$day"/>/<xsl:value-of select="$year"/></closeDate>
					
					<!--Capture Screen 1-->
					<area_quote><xsl:value-of select="substring(//sf:FS_Area__c,1,3)"/></area_quote>
					<division_quote><xsl:value-of select="//sf:Lawson_Division__r/sf:Name"/></division_quote>
					<salesActivity_quote>
						<xsl:choose>
							<xsl:when test="//sf:Sales_Activity__c = 'New'">New/New</xsl:when>
							<xsl:otherwise><xsl:value-of select="//sf:Sales_Activity__c"/></xsl:otherwise>
						</xsl:choose>
					</salesActivity_quote>
					
					<!--Capture Screen 2-->
					<industry_quote><xsl:value-of select="//sf:Industry__c"/></industry_quote>
					<naics_quote><xsl:value-of select="//sf:NAICSCode__r/sf:NAICS_Code_No__c"/></naics_quote>
					<segment_quote><xsl:value-of select="//sf:SPW_Segment__c"/></segment_quote>
					<accountType_quote><xsl:value-of select="//sf:Type"/></accountType_quote>
					
				</document>
			</xsl:if>
		</data_xml>
	</xsl:template>

</xsl:stylesheet>