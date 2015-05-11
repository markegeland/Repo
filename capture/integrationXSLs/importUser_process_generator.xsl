<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version='1.0'>
	<xsl:output method="xml"/>
	<xsl:variable name="userId" select="/transaction/user_info/user_id"/>
	<xsl:strip-space elements="*"/>
	<xsl:template match="/">
		<!-- Begin SOAP XML -->
		<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
			<soap:Header>
				<SessionHeader xmlns="urn:partner.soap.sforce.com">
					<sessionId><xsl:value-of select="/transaction/user_info/session_id"/></sessionId>
				</SessionHeader>
				<CallOptions xmlns="urn:partner.soap.sforce.com">
					<client>BigMachinesLFE/1.0</client>
				</CallOptions>
			</soap:Header>
			<soap:Body>
				<query xmlns="urn:partner.soap.sforce.com">
					<queryString>
						select Id, Username, FirstName, LastName, Title, Phone, Email, MobilePhone from User 
						where Id='<xsl:value-of select="$userId"/>'
					</queryString>
				</query>
			</soap:Body>
		</soap:Envelope>
		<!-- End SOAP XML -->
	</xsl:template>
</xsl:stylesheet>