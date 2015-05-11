<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml"/>
	<xsl:variable name="main_doc" select="/transaction/data_xml/document[@data_type=0]"/>
	<xsl:variable name="bmTransId" select="/transaction/@id"/>
	<xsl:strip-space elements="*"/>

	<!-- template: main -->
	<xsl:template match="/">
		<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
			<soap:Header>
				<SessionHeader xmlns="urn:partner.soap.sforce.com">
					<sessionId>
						<xsl:value-of select="/transaction/user_info/session_id"/>
					</sessionId>
				</SessionHeader>
				<CallOptions xmlns="urn:partner.soap.sforce.com">
					<client>BigMachinesLFE/1.0</client>
					<defaultNamespace>BigMachines</defaultNamespace>
				</CallOptions>
			</soap:Header>
			<soap:Body soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
				<upsert xmlns="urn:partner.soap.sforce.com">	
					<externalIDFieldName>BigMachines__Transaction_Id__c</externalIDFieldName>
					<xsl:call-template name="upsertQuote"/>
				</upsert>
			</soap:Body>
		</soap:Envelope>
	</xsl:template>

	<!-- template: upsertQuote - this template creates a new or modifies an existing quote in SFDC -->
	<xsl:template name="upsertQuote">
		<!-- ************************************ MODIFY START ************************************ -->
		<sObjects xmlns="urn:sobject.partner.soap.sforce.com">
			<type>BigMachines__Quote__c</type>
			<Name>
				<xsl:value-of select="$main_doc/quoteNumber_quote"/>
			</Name>
			<BigMachines__Transaction_Id__c>
				<xsl:value-of select="$bmTransId"/>
			</BigMachines__Transaction_Id__c>
			<!-- Only run the upserts if sfdcRunUpserts_quote == true. Check the attribute default of this attribute to see how it is set.-->
			<xsl:if test="$main_doc/sfdcRunUpserts_quote = 'true'">
				<BigMachines__Opportunity__c>
					<xsl:value-of select="$main_doc/crmOpportunityId_quote"/>
				</BigMachines__Opportunity__c>
				<BigMachines__Account__c>
					<xsl:value-of select="$main_doc/_customer_id"/>
				</BigMachines__Account__c>
				<BigMachines__Description__c>
					<xsl:value-of select="$main_doc/quoteDescription_quote"/>
				</BigMachines__Description__c>
				<BigMachines__Status__c>
					<xsl:value-of select="$main_doc/status_quote"/>
				</BigMachines__Status__c>
				<BigMachines__Total__c>
					<xsl:value-of select="$main_doc/total_quote"/>
				</BigMachines__Total__c>
				<BigMachines_Opportunity_Read_Only__c>
					<xsl:value-of select="$main_doc/opportunityReadOnly_quote"/>
				</BigMachines_Opportunity_Read_Only__c>
				<BigMachines_Opportunity_Stage__c>
					<xsl:value-of select="$main_doc/opportunityStage_quote"/>
				</BigMachines_Opportunity_Stage__c>
				<BigMachines__Pricebook_Id__c>
					<xsl:value-of select="$main_doc/_partner_price_book_id"/>
				</BigMachines__Pricebook_Id__c>
				<BigMachines__Prep_Sync__c>true</BigMachines__Prep_Sync__c>

				<!-- Start Totals -->
				<Capture_Monthly_Total__c>
					<xsl:value-of select="$main_doc/grandTotalSell_quote"/>
				</Capture_Monthly_Total__c>
				<Capture_Monthly_Small_Base_Rate__c>
					<xsl:value-of select="$main_doc/smallMonthlyTotalProposed_quote"/>
				</Capture_Monthly_Small_Base_Rate__c>
				<Capture_Monthly_Large_Haul_Charge__c>
					<xsl:value-of select="$main_doc/largeMonthlyTotalHaulProposed_quote"/>
				</Capture_Monthly_Large_Haul_Charge__c>
				<Capture_Monthly_Large_Disposal_Charge__c>
					<xsl:value-of select="$main_doc/largeMonthlyTotalDisposalProposed_quote"/>
				</Capture_Monthly_Large_Disposal_Charge__c>
				<Capture_Monthly_Large_Rental_Charge__c>
					<xsl:value-of select="$main_doc/largeMonthlyTotalRentalProposed_quote"/>
				</Capture_Monthly_Large_Rental_Charge__c>
				<Capture_Monthly_ERF_FRF_Total__c>
					<xsl:value-of select="$main_doc/erfAndFrfTotalSell_quote"/>
				</Capture_Monthly_ERF_FRF_Total__c>
				<Capture_Admin_Rate__c>
					<xsl:value-of select="$main_doc/adminRate_quote"/>
				</Capture_Admin_Rate__c>
				<Capture_One_Time_Charge_Subtotal__c>
					<xsl:value-of select="$main_doc/deliveryChargeSubtotal_quote"/>
				</Capture_One_Time_Charge_Subtotal__c>
				<Capture_One_Time_ERF_FRF_Total__c>
					<xsl:value-of select="$main_doc/deliveryERFandFRFTotal_quote"/>
				</Capture_One_Time_ERF_FRF_Total__c>
				<Capture_One_Time_Total__c>
					<xsl:value-of select="$main_doc/oneTimeTotalDeliveryAmount_quote"/>
				</Capture_One_Time_Total__c>
				<!--4/20 DS-->
				<Capture_Monthly_Small_Total_Floor__c>
					<xsl:value-of select="$main_doc/smallMonthlyTotalFloor_quote"/>
				</Capture_Monthly_Small_Total_Floor__c>
				<Capture_Monthly_Small_Total_Base__c>
					<xsl:value-of select="$main_doc/smallMonthlyTotalBase_quote"/>
				</Capture_Monthly_Small_Total_Base__c>
				<Capture_Monthly_Small_Total_Target__c>
					<xsl:value-of select="$main_doc/smallMonthlyTotalTarget_quote"/>
				</Capture_Monthly_Small_Total_Target__c>
				<Capture_Monthly_Small_Total_Stretch__c>
					<xsl:value-of select="$main_doc/smallMonthlyTotalStretch_quote"/>
				</Capture_Monthly_Small_Total_Stretch__c>
				<Capture_Monthly_Small_Total_Price_w_Fees__c>
					<xsl:value-of select="$main_doc/smallMonthlyTotalPriceInclFees_quote"/>
				</Capture_Monthly_Small_Total_Price_w_Fees__c>
				<Capture_Monthly_Large_Total_Haul_Floor__c>
					<xsl:value-of select="$main_doc/largeMonthlyTotalHaulFloor_quote"/>
				</Capture_Monthly_Large_Total_Haul_Floor__c>
				<Capture_Monthly_Large_Haul_Base_Price__c>
					<xsl:value-of select="$main_doc/largeMonthlyHaulBasePrice_quote"/>
				</Capture_Monthly_Large_Haul_Base_Price__c>
				<Capture_Monthly_Large_Total_Haul_Target__c>
					<xsl:value-of select="$main_doc/largeMonthlyTotalHaulTarget_quote"/>
				</Capture_Monthly_Large_Total_Haul_Target__c>
				<Capture_Monthly_Large_Total_Haul_Stretch__c>
					<xsl:value-of select="$main_doc/largeMonthlyTotalHaulStretch_quote"/>
				</Capture_Monthly_Large_Total_Haul_Stretch__c>
				<Capture_Monthly_Large_Haul_Price_w_Fees__c>
					<xsl:value-of select="$main_doc/largeMonthlyHaulPriceInclFees_quote"/>
				</Capture_Monthly_Large_Haul_Price_w_Fees__c>
				<Capture_Monthly_Large_Disposal_Floor__c>
					<xsl:value-of select="$main_doc/largeMonthlyTotalDisposalFloor_quote"/>
				</Capture_Monthly_Large_Disposal_Floor__c>
				<Capture_Monthly_Large_Disposal_Base__c>
					<xsl:value-of select="$main_doc/largeMonthlyTotalDisposalBase_quote"/>
				</Capture_Monthly_Large_Disposal_Base__c>
				<Capture_Monthly_Large_Disposal_Target__c>
					<xsl:value-of select="$main_doc/largeMonthlyTotalDisposalTarget_quote"/>
				</Capture_Monthly_Large_Disposal_Target__c>
				<Capture_Monthly_Large_Disposal_Stretch__c>
					<xsl:value-of select="$main_doc/largeMonthlyTotalDisposalStretch_quote"/>
				</Capture_Monthly_Large_Disposal_Stretch__c>
				<Capture_Monthly_Large_Disp_Price_w_Fees__c>
					<xsl:value-of select="$main_doc/largeMonthlyDisposalPriceInclFees_quote"/>
				</Capture_Monthly_Large_Disp_Price_w_Fees__c>
				<Capture_Monthly_Large_Total_Rental_Floor__c>
					<xsl:value-of select="$main_doc/largeMonthlyTotalRentalFloor_quote"/>
				</Capture_Monthly_Large_Total_Rental_Floor__c>
				<Capture_Monthly_Large_Total_Rental_Base__c>
					<xsl:value-of select="$main_doc/largeMonthlyTotalRentalBase_quote"/>
				</Capture_Monthly_Large_Total_Rental_Base__c>
				<Capture_Monthly_Large_Rental_Target__c>
					<xsl:value-of select="$main_doc/largeMonthlyTotalRentalTarget_quote"/>
				</Capture_Monthly_Large_Rental_Target__c>
				<Capture_Monthly_Large_Rental_Stretch__c>
					<xsl:value-of select="$main_doc/largeMonthlyTotalRentalStretch_quote"/>
				</Capture_Monthly_Large_Rental_Stretch__c>
				<Capture_Monthly_Large_Rental_Price_Fees__c>
					<xsl:value-of select="$main_doc/largeMonthlyRentalPriceInclFees_quote"/>
				</Capture_Monthly_Large_Rental_Price_Fees__c>
				<Capture_Monthly_Total_Amount_w_Fees__c>
					<xsl:value-of select="$main_doc/totalMonthlyAmtInclFees_quote"/>
				</Capture_Monthly_Total_Amount_w_Fees__c>
				<Capture_Total_Floor__c>
					<xsl:choose>
						<xsl:when test="$main_doc/monthlyTotalFloor_quote != ''">
							<xsl:value-of select="$main_doc/monthlyTotalFloor_quote"/>
						</xsl:when>
						<xsl:otherwise>0.0</xsl:otherwise>
					</xsl:choose>
				</Capture_Total_Floor__c>
				<Capture_Total_Target__c>
					<xsl:choose>
						<xsl:when test="$main_doc/monthlyTotalTarget_quote != ''">
							<xsl:value-of select="$main_doc/monthlyTotalTarget_quote"/>
						</xsl:when>
						<xsl:otherwise>0.0</xsl:otherwise>
					</xsl:choose>
				</Capture_Total_Target__c>
				<Capture_Total_Base__c>
					<xsl:choose>
						<xsl:when test="$main_doc/monthlyTotalBase_quote != ''">
							<xsl:value-of select="$main_doc/monthlyTotalBase_quote"/>
						</xsl:when>
						<xsl:otherwise>0.0</xsl:otherwise>
					</xsl:choose>
				</Capture_Total_Base__c>
				<Capture_Total_Stretch__c>
					<xsl:choose>
						<xsl:when test="$main_doc/monthlyTotalStretch_quote != ''">
							<xsl:value-of select="$main_doc/monthlyTotalStretch_quote"/>
						</xsl:when>
						<xsl:otherwise>0.0</xsl:otherwise>
					</xsl:choose>
				</Capture_Total_Stretch__c>
				<Capture_Current_Invoice_Charges__c>
					<xsl:value-of select="$main_doc/grandTotalSell_quote"/>
				</Capture_Current_Invoice_Charges__c>
				<Capture_Monthly_Large_Total_Floor_Price__c>
					<xsl:value-of select="$main_doc/largeMonthlyTotalFloor_quote"/>
				</Capture_Monthly_Large_Total_Floor_Price__c>
				<Capture_Monthly_Large_Total_Base_Price__c>
					<xsl:value-of select="$main_doc/largeMonthlyTotalBase_quote"/>
				</Capture_Monthly_Large_Total_Base_Price__c>
				<Capture_Monthly_Large_Total_Target_Price__c>
					<xsl:value-of select="$main_doc/largeMonthlyTotalTarget_quote"/>
				</Capture_Monthly_Large_Total_Target_Price__c>
				<Capture_Monthly_Large_Stretch_Price__c>
					<xsl:value-of select="$main_doc/largeMonthlyTotalStretch_quote"/>
				</Capture_Monthly_Large_Stretch_Price__c>
				<Capture_Monthly_Large_Proposed_Price__c>
					<xsl:value-of select="$main_doc/largeMonthlyTotalProposed_quote"/>
				</Capture_Monthly_Large_Proposed_Price__c>
				<Capture_Monthly_Large_Total_Price_w_Fees__c>
					<xsl:value-of select="$main_doc/largeMonthlyTotalPriceInclFees_quote"/>
				</Capture_Monthly_Large_Total_Price_w_Fees__c>


				<!-- End Totals -->

			</xsl:if>
		</sObjects>
		<!-- ************************************ MODIFY END ************************************ -->
	</xsl:template>

</xsl:stylesheet>