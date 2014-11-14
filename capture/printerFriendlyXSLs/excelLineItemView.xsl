
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:output method="html" encoding="UTF-8" indent="yes"/>
	<xsl:decimal-format name="american" decimal-separator="." grouping-separator=","/>
	<xsl:decimal-format name="euro" decimal-separator="," grouping-separator="."/>
	<xsl:variable name="main_doc" select="/transaction/data_xml/document[@data_type=0]"/>
	<xsl:variable name="numOfLineItems" select="count(/transaction/data_xml/document[@data_type='2' or @data_type='3'])"/>
	<!-- formula, quote values -->
	<xsl:variable name="calculate_by" select="'quote values'"/>
	<!--xsl:variable xmlns:str="http://exslt.org/strings" name="optionalArray" select="str:split('false,true', ',')"/>
	<xsl:variable xmlns:str="http://exslt.org/strings" name="miscArray" select="str:split('1,2', ',')"/-->

	<xsl:variable xmlns:str="http://exslt.org/strings" name="adHocArray" select="str:split('1,2,3,4,5,6', ',')"/>
	<xsl:template match="/">
		<xsl:variable name="outputCurrency" select="$main_doc/outputCurrency_quote"/>
		<xsl:variable name="conversionRate" select="$main_doc/conversionRate_quote"/>
		<html>
			<head>

		</head>
			<body>
				<!-- xsl:text disable-output-escaping="no" xml:space="preserve" -->
				<!--img alt="" src="" /-->
				<!-- output currency, and output language should be incorporated -->
				<!-- Address information, tables nested to show side by side -->
				<!--table>
					<tbody>
						<tr>
							<td>
								<table>
									<tbody>
										<xsl:apply-templates select="$main_doc/billTo/*" mode="address-set"/>
									</tbody>
								</table>
							</td>
							<td colspan="2"/>
							<td>
								<table>
									<tbody>
										<xsl:apply-templates select="$main_doc/shipTo/*" mode="address-set"/>
									</tbody>
								</table>
							</td>
						</tr>
					</tbody>
				</table>
				<hr/-->
				<table>
					<tbody>
						<tr>
							<td colspan="4">
								<h2>
									<xsl:value-of select="$main_doc/quoteNumber_quote/@label"/>:</h2>
							</td>
							<td colspan="5">
								<h2>
									<xsl:value-of select="$main_doc/quoteNumber_quote"/>
								</h2>
							</td>
						</tr>
					</tbody>
				</table>
				<table border="0.5pt solid black">
					<tbody>
						<tr style="background-color: #cccccc">
							<!-- To avoid additional translations for multi-language, labels will be pulled from the App -->
							<xsl:variable name="get-labels_doc" select="/transaction/data_xml/document[_sequence_number = 1]"/>
							<th>#</th>
							<th>
								<xsl:value-of select="$get-labels_doc/optional_line/@label"/>
							</th>
							<th>
								<xsl:value-of select="$get-labels_doc/customGroup_line/@label"/>
							</th>
							<th>
								<xsl:value-of select="$get-labels_doc/price/_price_quantity/@label"/>
							</th>
							<th>
								<xsl:value-of select="$get-labels_doc/part/_part_number/@label"/>
							</th>
							<th>
								<xsl:value-of select="$get-labels_doc/part/_part_desc/@label"/>
							</th>
							<th>
								<xsl:value-of select="$get-labels_doc/feeType_line/@label"/>
							</th>
							<th>
								<xsl:value-of select="$get-labels_doc/costEa_line/@label"/>
							</th>
							<th>
								<xsl:value-of select="$get-labels_doc/listPrice_line/@label"/>
							</th>
							<th>
								<xsl:value-of select="$get-labels_doc/extendedList_line/@label"/>
							</th>
							<th>
								<xsl:value-of select="$get-labels_doc/discount2_line/@label"/> %
							</th>
							<th>
								<xsl:value-of select="$get-labels_doc/extendedDiscount_line/@label"/>
							</th>
							<th>
								<xsl:value-of select="$get-labels_doc/netPriceEach_line/@label"/>
							</th>
							<th>
								<xsl:value-of select="$get-labels_doc/extendedNetPrice_line/@label"/>
							</th>
							<th>
								<xsl:value-of select="$get-labels_doc/netMarginPercent_line/@label"/>
							</th>
							<th>
								<xsl:value-of select="$get-labels_doc/netMarginAmt_line/@label"/>
							</th>
						</tr>
						<xsl:for-each select="/transaction/data_xml/document[@data_type='2' or @data_type='3']">
							<xsl:if test="$calculate_by='quote values'">
								<tr>
									<td>
										<xsl:value-of select="position()"/>
									</td>
									<td>
										<xsl:value-of select="optional_line/@display_value"/>
									</td>
									<td>
										<xsl:value-of select="customGroup_line"/>
									</td>
									<td>
										<xsl:value-of select="price/_price_quantity"/>
									</td>
									<td>
										<xsl:value-of select="part/_part_number"/>
										<xsl:value-of select="model/_model_name"/>
									</td>
									<td>
										<xsl:value-of select="part/_part_desc"/>
									</td>
									<td>
										<xsl:value-of select="feeType_line"/>
									</td>
									<td>
										<xsl:call-template name="BMI_universalFormatPrice">
											<xsl:with-param name="price" select="costEa_line"/>
											<xsl:with-param name="currency" select="$outputCurrency"/>
											<xsl:with-param name="multiplier" select="$conversionRate"/>
										</xsl:call-template>
									</td>
									<td>
										<xsl:call-template name="BMI_universalFormatPrice">
											<xsl:with-param name="price" select="listPrice_line"/>
											<xsl:with-param name="currency" select="$outputCurrency"/>
											<xsl:with-param name="multiplier" select="$conversionRate"/>
										</xsl:call-template>
									</td>
									<td>
										<xsl:call-template name="BMI_universalFormatPrice">
											<xsl:with-param name="price" select="extendedList_line"/>
											<xsl:with-param name="currency" select="$outputCurrency"/>
											<xsl:with-param name="multiplier" select="$conversionRate"/>
										</xsl:call-template>
									</td>
									<td>
										<xsl:value-of select="discount2_line"/>
									</td>
									<td>
										<xsl:call-template name="BMI_universalFormatPrice">
											<xsl:with-param name="price" select="extendedDiscount_line"/>
											<xsl:with-param name="currency" select="$outputCurrency"/>
											<xsl:with-param name="multiplier" select="$conversionRate"/>
										</xsl:call-template>
									</td>
									<td>
										<xsl:call-template name="BMI_universalFormatPrice">
											<xsl:with-param name="price" select="netPriceEach_line"/>
											<xsl:with-param name="currency" select="$outputCurrency"/>
											<xsl:with-param name="multiplier" select="$conversionRate"/>
										</xsl:call-template>
									</td>
									<td>
										<xsl:call-template name="BMI_universalFormatPrice">
											<xsl:with-param name="price" select="extendedNetPrice_line"/>
											<xsl:with-param name="currency" select="$outputCurrency"/>
											<xsl:with-param name="multiplier" select="$conversionRate"/>
										</xsl:call-template>
									</td>
									<td>
										<xsl:value-of select="netMarginPercent_line"/> %
									</td>
									<td>
										<xsl:call-template name="BMI_universalFormatPrice">
											<xsl:with-param name="price" select="netMarginAmt_line"/>
											<xsl:with-param name="currency" select="$outputCurrency"/>
											<xsl:with-param name="multiplier" select="$conversionRate"/>
										</xsl:call-template>
									</td>
								</tr>
							</xsl:if>
						</xsl:for-each>
						<xsl:for-each select="$adHocArray">
							<xsl:variable name="adHocProdName" select="concat('miscChargeProd', ., '_quote')"/>
							<xsl:variable name="adHocExists" select="$main_doc/*[name()= $adHocProdName] != ''"/>
							<xsl:variable name="adHocPos" select="."/>
							<xsl:if test="$calculate_by='quote values' and $adHocExists">
								<tr>
									<td>
										<xsl:value-of select="position() + $numOfLineItems"/>
									</td>
									<td>
										<xsl:variable name="adHocAttr" select="concat('miscOptional', $adHocPos, '_quote')"/>
										<xsl:value-of select="$main_doc/*[name()= $adHocAttr]/@display_value"/>
									</td>
									<td>
										<xsl:variable name="adHocAttr" select="concat('miscChargeGroup', $adHocPos, '_quote')"/>
										<xsl:value-of select="$main_doc/*[name()= $adHocAttr]"/>
									</td>
									<td>
										<xsl:variable name="adHocAttr" select="concat('miscChargeQty', $adHocPos, '_quote')"/>
										<xsl:value-of select="$main_doc/*[name()= $adHocAttr]"/>
									</td>
									<td>
										<xsl:variable name="adHocAttr" select="concat('miscChargeProd', $adHocPos, '_quote')"/>
										<xsl:value-of select="$main_doc/*[name()= $adHocAttr]"/>
									</td>
									<td>
										<xsl:variable name="adHocAttr" select="concat('miscChargeDesc', $adHocPos, '_quote')"/>
										<xsl:value-of select="$main_doc/*[name()= $adHocAttr]"/>
									</td>
									<td>
										<xsl:variable name="adHocAttr" select="concat('miscChargeFeeType', $adHocPos, '_quote')"/>
										<xsl:value-of select="$main_doc/*[name()= $adHocAttr]"/>
									</td>
									<td> </td>
									<td>
										<xsl:variable name="adHocAttr" select="concat('miscChargeList', $adHocPos, '_quote')"/>
										<xsl:call-template name="BMI_universalFormatPrice">
											<xsl:with-param name="price" select="$main_doc/*[name()= $adHocAttr]"/>
											<xsl:with-param name="currency" select="$outputCurrency"/>
											<xsl:with-param name="multiplier" select="$conversionRate"/>
										</xsl:call-template>
									</td>
									<td>
										<xsl:variable name="adHocAttr" select="concat('miscChargeList', $adHocPos, '_quote')"/>
										<xsl:variable name="adHocMultiplier" select="concat('miscChargeQty', $adHocPos, '_quote')"/>
										
										<xsl:variable name="adHocAttrVal" select="$main_doc/*[name()= $adHocAttr]"/>
										<xsl:variable name="adHocMultiplierVal" select="$main_doc/*[name()= $adHocMultiplier]"/>
										
										<xsl:call-template name="BMI_universalFormatPrice">
											<xsl:with-param name="price" select="$adHocAttrVal * $adHocMultiplierVal"/>
											<xsl:with-param name="currency" select="$outputCurrency"/>
											<xsl:with-param name="multiplier" select="$conversionRate"/>
										</xsl:call-template>
									</td>
									<td>
										<xsl:variable name="adHocAttr" select="concat('miscChargeDiscountPercent', $adHocPos, '_quote')"/> %
										<xsl:value-of select="$main_doc/*[name()= $adHocAttr]"/>
									</td>
									<td>
										<xsl:variable name="adHocList" select="concat('miscChargeList', $adHocPos, '_quote')"/>
										<xsl:variable name="adHocExtNet" select="concat('miscExtCharge', $adHocPos, '_quote')"/>
										<xsl:variable name="adHocMultiplier" select="concat('miscChargeQty', $adHocPos, '_quote')"/>
										
										<xsl:variable name="adHocListVal" select="$main_doc/*[name()= $adHocList]"/>
										<xsl:variable name="adHocExtNetVal" select="$main_doc/*[name()= $adHocExtNet]"/>
										<xsl:variable name="adHocMultiplierVal" select="$main_doc/*[name()= $adHocMultiplier]"/>
										
										<xsl:variable name="adHocValue">
											<xsl:value-of select="$adHocListVal * $adHocMultiplierVal -  $adHocExtNetVal" />
										</xsl:variable>
										
										<xsl:call-template name="BMI_universalFormatPrice">
											<xsl:with-param name="price" select="$adHocValue"/>
											<xsl:with-param name="currency" select="$outputCurrency"/>
											<xsl:with-param name="multiplier" select="$conversionRate"/>
										</xsl:call-template>
									</td>
									<td>
										<xsl:variable name="adHocAttr" select="concat('miscCharge', $adHocPos, '_quote')"/>
										<xsl:call-template name="BMI_universalFormatPrice">
											<xsl:with-param name="price" select="$main_doc/*[name()= $adHocAttr]"/>
											<xsl:with-param name="currency" select="$outputCurrency"/>
											<xsl:with-param name="multiplier" select="$conversionRate"/>
										</xsl:call-template>
									</td>
									<td>
										<xsl:variable name="adHocAttr" select="concat('miscExtCharge', $adHocPos, '_quote')"/>
										<xsl:call-template name="BMI_universalFormatPrice">
											<xsl:with-param name="price" select="$main_doc/*[name()= $adHocAttr]"/>
											<xsl:with-param name="currency" select="$outputCurrency"/>
											<xsl:with-param name="multiplier" select="$conversionRate"/>
										</xsl:call-template>
									</td>
									<td>	</td>
									<td> </td>
								</tr>
							</xsl:if>
						</xsl:for-each>
					</tbody>
				</table>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="*" mode="address-set">
		<xsl:variable name="field-var-name" select="name()"/>
		<xsl:variable name="field-label" select="concat(@label, ': ')"/>
		<xsl:variable name="field-value" select="."/>
		<xsl:variable name="value-populated" select="string-length($field-value)"/>
		<xsl:if test="position() = 1">
			<xsl:variable name="address-set-label" select="../@label"/>
			<tr>
				<th colspan="5">
					<xsl:value-of select="$address-set-label"/>
				</th>
			</tr>
		</xsl:if>
		<xsl:choose>
			<!-- when to open with row and data - print label (first name and address1)-->
			<xsl:when test="contains($field-var-name, 'first_name') or (contains($field-var-name, 'address') and not (contains($field-var-name, 'address_2'))) ">
				<xsl:text disable-output-escaping="yes" xml:space="preserve"><![CDATA[<tr><td colspan="2">]]></xsl:text>
				<xsl:value-of select="$field-label"/>
				<xsl:text disable-output-escaping="yes" xml:space="preserve"><![CDATA[</td><td colspan="3">]]></xsl:text>
				<xsl:value-of select="$field-value"/>
			</xsl:when>
			<!-- when to open with row and data - no label (city)-->
			<xsl:when test=" contains($field-var-name, 'city') ">
				<xsl:text disable-output-escaping="yes" xml:space="preserve"><![CDATA[<tr><td colspan="2"></td><td colspan="3">]]></xsl:text>
				<xsl:value-of select="$field-value"/>
			</xsl:when>
			<!-- when to remain open with row and data - no label - print comma (state,)-->
			<xsl:when test="contains($field-var-name, 'state')">
				<xsl:if test="$value-populated &gt; 0">
					<xsl:value-of select="concat(', ', $field-value)"/>
				</xsl:if>
			</xsl:when>
			<!-- when to remain open with row and data - no label - no comma (zipcode) -->
			<xsl:when test="contains($field-var-name, 'zip')">
				<xsl:if test="$value-populated &gt; 0">
					<xsl:value-of select="concat(' ', $field-value)"/>
				</xsl:if>
			</xsl:when>
			<!-- when to  close row and data - no label-->
			<xsl:when test="contains($field-var-name, 'last_name') or contains($field-var-name, 'country')">
				<xsl:value-of select="concat(' ',$field-value)"/>
				<xsl:text disable-output-escaping="yes" xml:space="preserve"><![CDATA[</td></tr>]]></xsl:text>
			</xsl:when>
			<!-- when to  close row and data - no label - no comma (address 2)-->
			<xsl:when test="contains($field-var-name, 'address_2')">
				<xsl:if test="$value-populated &gt; 0">
					<xsl:value-of select="concat(', ', $field-value)"/>
				</xsl:if>
				<xsl:text disable-output-escaping="yes" xml:space="preserve"><![CDATA[</td></tr>]]></xsl:text>
			</xsl:when>
			<!-- open and close - no label (company name 2) -->
			<xsl:when test="contains($field-var-name, 'company_name_2') and $value-populated &gt; 0">
				<tr>
					<td colspan="2"> </td>
					<td colspan="3">
						<xsl:value-of select="$field-value"/>
					</td>
				</tr>
			</xsl:when>
			<!-- otherwise open and close with row and data - print label -->
			<xsl:when test="$value-populated &gt; 0">
				<tr>
					<td colspan="2">
						<xsl:value-of select="$field-label"/>
					</td>
					<td colspan="3">
						<xsl:value-of select="$field-value"/>
					</td>
				</tr>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="BMI_universalFormatPrice">
		<xsl:param name="price"/>
		<xsl:param name="currency"/>
		<xsl:param name="multiplier" select="1"/>
		<xsl:param name="showCents" select="true()"/>
		<xsl:param name="showCurrencySymbol" select="true()"/>
		<xsl:variable name="safePrice">
			<xsl:variable name="priceWithoutMult">
				<xsl:choose>
					<xsl:when test="$price!='NaN'">
						<xsl:choose>
							<xsl:when test="$currency='EUR'">
								<xsl:call-template name="BMI_replaceSubstring">
									<xsl:with-param name="base_string" select="$price"/>
									<xsl:with-param name="string_to_replace">,</xsl:with-param>
									<xsl:with-param name="string_to_replace_with" select="'.'"/>
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$price"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>0</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="not($showCents)">
					<xsl:value-of select="round($priceWithoutMult*$multiplier)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$priceWithoutMult*$multiplier"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="format">
			<xsl:choose>
				<xsl:when test="$showCents">
					<xsl:choose>
						<xsl:when test="$currency='EUR'">
							<xsl:value-of disable-output-escaping="yes" select="string('#.##0,00')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of disable-output-escaping="yes" select="string('#,##0.00')"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="$currency='EUR'">
							<xsl:value-of disable-output-escaping="yes" select="string('#.##0')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of disable-output-escaping="yes" select="string('#,##0')"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$currency='USD'">
				<!-- US dollar -->
				<xsl:choose>
					<xsl:when test="$showCurrencySymbol">
						<xsl:value-of select="format-number($safePrice,concat('$',$format),'american')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="format-number($safePrice,$format,'american')"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$currency='EUR'">
				<!-- euro -->
				<xsl:choose>
					<xsl:when test="$showCurrencySymbol">
						<fo:inline font-family="Helvetica">&#8364;</fo:inline>
						<xsl:value-of select="format-number($safePrice,$format,'euro')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="format-number($safePrice,$format,'euro')"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$currency='GBP'">
				<!-- pound -->
				<xsl:choose>
					<xsl:when test="$showCurrencySymbol">
						<fo:inline font-family="Helvetica">&#163;</fo:inline>
						<xsl:value-of select="format-number($safePrice,$format,'american')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="format-number($safePrice,$format,'american')"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$currency='CAD' or $currency='AUD' or $currency='NZD'">
				<!-- canadian dollar, australian dollar, new zealand dollar -->
				<xsl:choose>
					<xsl:when test="$showCurrencySymbol">
						<xsl:value-of select="format-number($safePrice,concat('$',$format),'american')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="format-number($safePrice,$format,'american')"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$currency='CHF'">
				<!-- swiss franc -->
				<xsl:choose>
					<xsl:when test="$showCurrencySymbol">
                        CHF <xsl:value-of select="format-number($safePrice,$format,'american')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="format-number($safePrice,$format,'american')"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$currency='BRL'">
				<!-- brazilian real -->
				<xsl:choose>
					<xsl:when test="$showCurrencySymbol">
                        R<xsl:value-of select="format-number($safePrice,concat('$',$format),'american')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="format-number($safePrice,$format,'american')"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$currency='CNY'">
				<!-- chinese yuan renminbi -->
				<xsl:choose>
					<xsl:when test="$showCurrencySymbol">
						<fo:inline font-family="MSGothic">&#20803;</fo:inline>
						<xsl:value-of select="format-number($safePrice,$format,'american')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="format-number($safePrice,$format,'american')"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$currency='RS'">
				<!-- rupee -->
				<xsl:choose>
					<xsl:when test="$showCurrencySymbol">
                        Rp <xsl:value-of select="format-number($safePrice,$format,'american')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="format-number($safePrice,$format,'american')"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$currency='JPY'">
				<!-- japanese yen -->
				<xsl:choose>
					<xsl:when test="$showCurrencySymbol">
						<fo:inline font-family="MSGothic">&#165;</fo:inline>
						<xsl:value-of select="format-number($safePrice,$format,'american')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="format-number($safePrice,$format,'american')"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$currency='KRW'">
				<!-- south korean won -->
				<xsl:choose>
					<xsl:when test="$showCurrencySymbol">
						<fo:inline font-family="MSGothic">&#8361;</fo:inline>
						<xsl:value-of select="format-number($safePrice,$format,'american')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="format-number($safePrice,$format,'american')"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$currency='MXN'">
				<!-- mexico peso -->
				<xsl:choose>
					<xsl:when test="$showCurrencySymbol">
						<xsl:value-of select="format-number($safePrice,concat('$',$format),'american')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="format-number($safePrice,$format,'american')"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$currency='ARS'">
				<!-- argentinian austral -->
				<xsl:choose>
					<xsl:when test="$showCurrencySymbol">
						<fo:inline text-decoration="line-through">A</fo:inline>
						<xsl:value-of select="format-number($safePrice,$format,'american')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="format-number($safePrice,$format,'american')"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$currency='RMB'">
				<!-- china renminbi -->
				<xsl:choose>
					<xsl:when test="$showCurrencySymbol">
                        RMB <xsl:value-of select="format-number($safePrice,$format,'american')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="format-number($safePrice,$format,'american')"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$showCurrencySymbol">
						<xsl:value-of select="$currency"/>
						<xsl:value-of select="format-number($safePrice,$format,'american')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="format-number($safePrice,$format,'american')"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="BMI_universalNumber">
		<xsl:param name="val"/>
		<xsl:param name="numFormat"/>
		<xsl:param name="formatForDisplay" select="''"/>
		<xsl:variable name="realNumber">
			<xsl:choose>
				<xsl:when test="$numFormat = 'EUR'">
					<xsl:variable name="reverseSeperators" select="translate($val,',.','.,')"/>
					<xsl:call-template name="BMI_replaceSubstring">
						<xsl:with-param name="base_string" select="$reverseSeperators"/>
						<xsl:with-param name="string_to_replace">,</xsl:with-param>
						<xsl:with-param name="string_to_replace_with" select="''"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="number($val) != 'NaN' ">
					<xsl:value-of select="$val"/>
				</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$formatForDisplay='USD'">
				<xsl:value-of select="format-number($realNumber,'#,##0.00','american')"/>
			</xsl:when>
			<xsl:when test="$formatForDisplay='EUR'">
				<xsl:value-of select="format-number($realNumber,'#.###,00','euro')"/>
			</xsl:when>
			<xsl:when test="$formatForDisplay='GBP'">
				<xsl:value-of select="format-number($realNumber,'#,##0.00','american')"/>
			</xsl:when>
			<xsl:when test="$formatForDisplay='CAD'">
				<xsl:value-of select="format-number($realNumber,'#,##0.00','american')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$realNumber"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="BMI_replaceSubstring">
		<xsl:param name="base_string"/>
		<xsl:param name="string_to_replace"/>
		<xsl:param name="string_to_replace_with"/>
		<xsl:variable name="resultString" select="concat(substring-before($base_string,$string_to_replace),$string_to_replace_with,substring-after($base_string,$string_to_replace))"/>
		<xsl:choose>
			<xsl:when test="string-length($resultString) = string-length($string_to_replace_with)">
				<xsl:value-of select="$base_string"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="BMI_replaceSubstring">
					<xsl:with-param name="base_string" select="$resultString"/>
					<xsl:with-param name="string_to_replace" select="$string_to_replace"/>
					<xsl:with-param name="string_to_replace_with" select="$string_to_replace_with"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
