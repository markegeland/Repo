<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<!-- PLEASE ONLY MODIFY THE XSL WITHIN THE SECTION MARKED MODIFY -->
	<!-- BigMachines' Customer Service is only responsible for issues resulting from modifications within this section -->
	<xsl:output method="xml"/>
	<!-- data_type=0 : quote-level node -->
	<!-- data_type=2 : model-level node -->
	<!-- data_type=3 : line item node   -->
	<xsl:variable name="main_doc" select="/transaction/data_xml/document[@data_type=0]"/>
	<xsl:variable name="startPos" select="0"/>
	<xsl:variable name="endPos" select="200"/>
	<xsl:strip-space elements="*"/>

	<!-- Main Template -->
	<xsl:template match="/">
		<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
			<soap:Header>
				<SessionHeader xmlns="urn:partner.soap.sforce.com">
					<sessionId><xsl:value-of select="/transaction/user_info/session_id"/></sessionId>
				</SessionHeader>
				<CallOptions xmlns="urn:partner.soap.sforce.com">
					<client>BigMachinesLFE/1.0</client>
					<defaultNamespace>BigMachines</defaultNamespace>
				</CallOptions>
			</soap:Header>
			<soap:Body soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
				<upsert xmlns="urn:partner.soap.sforce.com">
					<ExternalIDFieldName>BigMachines__External_Id__c</ExternalIDFieldName>
					<xsl:choose>
						<xsl:when test="(string-length($main_doc/crmQuoteId_quote) = 15 or string-length($main_doc/crmQuoteId_quote) = 18)            and count(/transaction/data_xml/document[@data_type=3 and optional_line = 'false']) &gt; $startPos and count(/transaction/data_xml/document[@data_type=3 and optional_line = 'false']) &lt;= 1000">
							<xsl:call-template name="upsertLines"/>
						</xsl:when>
						<xsl:otherwise><_BM_IGNORE_PARTNER/></xsl:otherwise>	
					</xsl:choose>
				</upsert>
			</soap:Body>
		</soap:Envelope>
	</xsl:template>

	<!-- template: upsertLines - this template creates new or modifies existing line items in SFDC -->
	<xsl:template name="upsertLines">
		<xsl:for-each select="/transaction/data_xml/document[@data_type=3 and optional_line = 'false'][position() &gt; $startPos and position() &lt;= $endPos]">
			<xsl:sort select="_sequence_number" data-type="number"/>
			<xsl:variable name="sub_doc" select="."/>
			<!-- ************************************ MODIFY START ************************************ -->
			<xsl:variable name="name">
				<xsl:choose>
					<xsl:when test="string-length($sub_doc/_part_number) &gt; 1"><xsl:value-of select="$sub_doc/_part_number"/></xsl:when>
					<xsl:otherwise><xsl:value-of select="$sub_doc/_model_name"/></xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="desc">
				<xsl:choose>
					<xsl:when test="string-length($sub_doc/description_line) &gt; 1"><xsl:value-of select="$sub_doc/description_line"/></xsl:when>
					<xsl:otherwise><xsl:value-of select="$sub_doc/_model_product_line_name"/></xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="price">
				<xsl:choose>
					<xsl:when test="$sub_doc/sellPrice_line != ''">
						<xsl:value-of select="$sub_doc/sellPrice_line"/>
					</xsl:when>
					<xsl:otherwise>0</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<sObjects xmlns="urn:sobject.partner.soap.sforce.com">
				<type>Quote_Product__c</type>
				<External_Id__c><xsl:value-of select="$sub_doc/externalId_line"/></External_Id__c>
				<Quote__c><xsl:value-of select="$main_doc/crmQuoteId_quote"/></Quote__c>
				<Name><xsl:value-of select="$name"/></Name>
				<Description__c><xsl:value-of select="$desc"/></Description__c>
			
				<Quantity__c><xsl:value-of select="$sub_doc/_price_quantity"/></Quantity__c>
				<Capture_Total__c><xsl:value-of select="$sub_doc/totalPrice_line"/></Capture_Total__c>
				<Capture_Cost__c><xsl:value-of select="$sub_doc/totalFloorPrice_line"/></Capture_Cost__c>
				<Capture_Floor__c><xsl:value-of select="$sub_doc/totalBasePrice_line"/></Capture_Floor__c>
				<Capture_Average__c><xsl:value-of select="$sub_doc/totalTargetPrice_line"/></Capture_Average__c>
				<Capture_Target__c><xsl:value-of select="$sub_doc/totalStretchPrice_line"/></Capture_Target__c>
				<Sales_Price__c><xsl:value-of select="$price"/></Sales_Price__c>
				<Capture_FRF_Amount__c><xsl:value-of select="$sub_doc/frfAmountSell_line"/></Capture_FRF_Amount__c>
				<Capture_ERF_Amount__c><xsl:value-of select="$sub_doc/erfAmountSell_line"/></Capture_ERF_Amount__c>
				<Capture_Billing_Method__c><xsl:value-of select="$sub_doc/billingType_line"/></Capture_Billing_Method__c>
				
				<Prep_Delete__c>false</Prep_Delete__c>
				<Synchronization_Id__c><xsl:value-of select="$sub_doc/syncId_line"/></Synchronization_Id__c>
				
			</sObjects>
			<!-- ************************************ MODIFY END ************************************ -->
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>