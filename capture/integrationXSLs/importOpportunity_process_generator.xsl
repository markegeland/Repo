<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
	<xsl:output method="xml"/>
	<xsl:variable name="main_doc" select="/transaction/data_xml/document[@data_type=0]"/>
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
						select
							Name,
							CloseDate,
							StageName,
							Probability,
							
							<!--Capture Screen 1-->
							FS_Area__c,
							InfoPro_Lawson_Division__r.Lawson_Division__r.Name,
							InfoPro_Number__c,
							Sales_Activity__c,
							
							<!--Capture Screen 2-->
							Industry__c,
							NAICSCode__r.NAICS_Code_No__c,
							SPW_Segment__c,
							Type,
							
							<!--Capture Screens 2 and 6-->
							Authorized_By__r.Name,
							Site_Contact__r.Name,
							Site_Contact__r.Title,
							Site_Contact__r.Email,
							Site_Contact__r.Phone,
							
							<!--Capture Screen 6-->
							Billing_Contact__r.Name,
							Billing_Contact__r.Email,
							Billing_Contact__r.Phone,
							Billing_Contact__r.MobilePhone
							
						from Opportunity 
						where Id='<xsl:value-of select="$main_doc/crmOpportunityId_quote"/>'
						<!--where Id=006L0000006gkGFIAY-->
					</queryString>
				</query>
			</soap:Body>
		</soap:Envelope>
		<!-- End SOAP XML -->
	</xsl:template>
</xsl:stylesheet>