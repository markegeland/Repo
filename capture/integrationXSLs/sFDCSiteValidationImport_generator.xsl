<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
<xsl:output method="xml"/>
<xsl:variable name="main_doc" select="/transaction/data_xml/document[@data_type=0]"/>
<xsl:strip-space elements="*"/>
<xsl:template match="/">
<!--  Begin SOAP XML  -->
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
<soap:Header>
<SessionHeader xmlns="urn:partner.soap.sforce.com">
<sessionId>
<xsl:value-of select="/transaction/user_info/session_id"/>
</sessionId>
</SessionHeader>
<CallOptions xmlns="urn:partner.soap.sforce.com">
<client>BigMachinesLFE/1.0</client>
</CallOptions>
</soap:Header>
<soap:Body>
<query xmlns="urn:partner.soap.sforce.com">
<queryString>
	SELECT
		Address__r.Address_Line_1__c,
		Address__r.Address_Line_2__c,
		Address__r.City__c,
		Address__r.State__c,
		Address__r.Postal_Code__c,
		Address__r.Latitude__c,
		Address__r.Longitude__c 
	FROM Site__c
	WHERE Opportunity__c ='<xsl:value-of select="$main_doc/crmOpportunityId_quote"/>'
</queryString>
</query>
</soap:Body>
</soap:Envelope>
<!--  End SOAP XML  -->
</xsl:template>
</xsl:stylesheet>