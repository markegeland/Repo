
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:variable name="main_doc" select="transaction/data_xml/document[@data_type=0]"/>
	<xsl:variable name="model_doc" select="/transaction/data_xml/document[@data_type=2]"/>
	<xsl:variable name="sub_doc" select="/transaction/data_xml/document[@data_type=2 or @data_type=3]"/>
	<xsl:variable name="supplier" select="$main_doc/supplierCompanyName_quote"/>
	<xsl:variable name="rsLogo" select="concat('http://',$supplier,'.bigmachines.com/bmfsweb/', $supplier, '/image/RP_HorizontalLatest.jpeg')"/>
	<xsl:variable name="TransactionURL" select = "$main_doc/linkToDocument_quote"/>
	<xsl:variable name="reasonDescription" select="$main_doc/reasonDescription_quote"/>
	 <xsl:output method="html" indent="yes"/> 
	<xsl:template match="/">
		<html>
			<body>
				<table style='padding-left:35px;font-family:tahoma;'>
					<!-- <tbody><tr><td bgcolor='00B8FF' align='right' style='color:#FFFFFF;width:535px;height:40px;'><b>QUOTE APPROVAL REQUIRED</b></td></tr></tbody> -->
					<tbody><tr><td bgcolor='00B8FF' align='right' style='color:#FFFFFF;width:535px;height:40px;'></td></tr></tbody>
				</table>
				<p><img style='padding-left:10px' src="{$rsLogo}"/></p>
				<br>
				</br>
				<p style='padding-left:35px;font: 16px Calibri'></p>
				<!-- <p style='padding-left:35px;font: 16px Calibri'>Quote <xsl:value-of select="$main_doc/quoteNumber_quote"/>, <xsl:value-of select="$main_doc/siteName_quote"/> requires your approval for the following reason(s): </p> -->
				<p style='padding-left:35px;font: 16px Calibri'>Quote <xsl:value-of select="$main_doc/quoteNumber_quote"/>, <xsl:value-of select="$main_doc/siteName_quote"/></p>
				<br></br>
				<p style='padding-left:35px;font: 16px Calibri'><b><xsl:value-of select="$reasonDescription"/></b></p>
				<br></br>
				<p style='padding-left:35px;font: 16px Calibri'><xsl:value-of select="$main_doc/quoteNumber_quote"/></p>
				<p style='padding-left:35px;font: 16px Calibri'><u><i>Quote Detail:</i></u></p>
				<!-- <table style='padding-left:35px;font: 16px Calibri'> -->
				<table style='font: 16px Calibri'>
				<tr>
				<td style='padding-left:35px;font: 16px Calibri'>Submitted By: <xsl:value-of select="$main_doc/_quote_process_submitted_by_submit_quote"/></td></tr>
				<tr>
				<td style='padding-left:35px;font: 16px Calibri'>Submitted Date: <xsl:value-of select="$main_doc/submittedDate_quote"/></td></tr>
				<!-- <tr><td style='padding-left:35px;font: 16px Calibri'>Comments: <xsl:value-of select="$main_doc/_quote_process_submit_comment"/></td></tr> -->
				<tr><td><br/></td></tr>
				<tr><td><br/></td></tr>
				<tr><td style='padding-left:35px;font: 16px Calibri'>Quote Description: <xsl:value-of select="$main_doc/quoteDescription_quote"/></td></tr>
				<tr><td><br/></td></tr>
				<tr><td><br/></td></tr>
				<tr><td style='padding-left:35px;font: 16px Calibri'>Sales Activity: <xsl:value-of select="$main_doc/salesActivity_quote"/></td></tr>
				<!-- <tr><td style='padding-left:35px;font: 16px Calibri'>Division Number: <xsl:value-of select="$main_doc/division_quote"/></td></tr> -->
				<tr><td style='padding-left:35px;font: 16px Calibri'>Division: <xsl:value-of select="$main_doc/division_quote"/></td></tr>
				<tr><td style='padding-left:35px;font: 16px Calibri'>Customer Name: <xsl:value-of select="$main_doc/_quote_process_billTo_company_name"/></td></tr>
				<tr><td style='padding-left:35px;font: 16px Calibri'>Current Sales Provider: <xsl:value-of select="$main_doc/competitor_quote"/></td></tr>
				</table>
				<br>
				</br>
				<p style='padding-left:35px;font: 16px Calibri'>Quote Totals:</p>
				
				<table border='1' bordercolor='000000'  cellspacing='0' cellpadding='3'>
				<tr>
				<td align='center' bgcolor='003c69' style='color:#FFFFFF;width:80px;'><b></b></td>
				<td align='center' bgcolor='003c69' style='color:#FFFFFF;width:80px;'><b>Cost Price</b></td>
				<td align='center' bgcolor='003c69' style='color:#FFFFFF;width:80px;'><b>Floor Price</b></td>
				<td align='center' bgcolor='003c69' style='color:#FFFFFF;width:80px;'><b>Average price</b></td>
				<td align='center' bgcolor='003c69' style='color:#FFFFFF;width:80px;'><b>Target Price</b></td>
				<td align='center' bgcolor='003c69' style='color:#FFFFFF;width:80px;'><b>Proposed Price</b></td>
				<!--<td align='center' bgcolor='00B8FF' style='color:#FFFFFF;width:80px;'><b>ERF</b></td>
				<td align='center' bgcolor='00B8FF' style='color:#FFFFFF;width:80px;'><b>FRF</b></td>
				<td align='center' bgcolor='00B8FF' style='color:#FFFFFF;width:80px;'><b>Total</b></td>-->
				</tr>
				<xsl:if test="$main_doc/commercialExists_quote = 'true'">
					<tr>
					<td align='center' bgcolor='FFFFFF' style='color:#000000;width:80px;'><b>Small Containers</b></td>
					<td align='right' bgcolor='FFFFFF' style='color:#000000;width:80px;'>$<xsl:value-of select="format-number($main_doc/smallMonthlyTotalFloor_quote, '#.##')"/></td>
					<td align='right' bgcolor='FFFFFF' style='color:#000000;width:80px;'>$<xsl:value-of select="format-number($main_doc/smallMonthlyTotalBase_quote, '#.##')"/></td>
					<td align='right' bgcolor='FFFFFF' style='color:#000000;width:80px;'>$<xsl:value-of select="format-number($main_doc/smallMonthlyTotalTarget_quote, '#.##')"/></td>
					<td align='right' bgcolor='FFFFFF' style='color:#000000;width:80px;'>$<xsl:value-of select="format-number($main_doc/smallMonthlyTotalStretch_quote, '#.##')"/></td>
					<td align='right' bgcolor='FFFFFF' style='color:#000000;width:80px;'>$<xsl:value-of select="format-number($main_doc/smallMonthlyTotalProposed_quote, '#.##')"/></td>
					</tr>
				</xsl:if>
				<xsl:if test="$main_doc/industrialExists_quote = 'true'">
					<tr>
					<td align='center' bgcolor='FFFFFF' style='color:#000000;width:80px;'><b>Large Containers</b></td>
					<td align='right' bgcolor='FFFFFF' style='color:#000000;width:80px;'>$<xsl:value-of select="format-number($main_doc/largeMonthlyTotalFloor_quote, '#.##')"/></td>
					<td align='right' bgcolor='FFFFFF' style='color:#000000;width:80px;'>$<xsl:value-of select="format-number($main_doc/largeMonthlyTotalBase_quote, '#.##')"/></td>
					<td align='right' bgcolor='FFFFFF' style='color:#000000;width:80px;'>$<xsl:value-of select="format-number($main_doc/largeMonthlyTotalTarget_quote, '#.##')"/></td>
					<td align='right' bgcolor='FFFFFF' style='color:#000000;width:80px;'>$<xsl:value-of select="format-number($main_doc/largeMonthlyTotalStretch_quote, '#.##')"/></td>
					<td align='right' bgcolor='FFFFFF' style='color:#000000;width:80px;'>$<xsl:value-of select="format-number($main_doc/largeMonthlyTotalProposed_quote, '#.##')"/></td>
					</tr>
				</xsl:if>
				<tr>
				<td align='center' bgcolor='FFFFFF' style='color:#000000;width:80px;'><b>Fees</b></td>
				<td align='right' bgcolor='FFFFFF' style='color:#000000;width:80px;'>$<xsl:value-of select="format-number($main_doc/erfAndFrfTotalFloor_quote, '#.##')"/></td>
				<td align='right' bgcolor='FFFFFF' style='color:#000000;width:80px;'>$<xsl:value-of select="format-number($main_doc/erfAndFrfTotalBase_quote, '#.##')"/></td>
				<td align='right' bgcolor='FFFFFF' style='color:#000000;width:80px;'>$<xsl:value-of select="format-number($main_doc/erfAndFrfTotalTarget_quote, '#.##')"/></td>
				<td align='right' bgcolor='FFFFFF' style='color:#000000;width:80px;'>$<xsl:value-of select="format-number($main_doc/erfAndFrfTotalStretch_quote, '#.##')"/></td>
				<td align='right' bgcolor='FFFFFF' style='color:#000000;width:80px;'>$<xsl:value-of select="format-number($main_doc/erfAndFrfTotalSell_quote, '#.##')"/></td>
				</tr>
				<tr>
				<td bgcolor='00B8FF' align='right' style='color:#FFFFFF;'><b>Total Estimated Amount</b></td>
				<td bgcolor='00B8FF' align='right' style='color:#FFFFFF;'>$<xsl:value-of select="format-number($main_doc/grandTotalFloor_quote, '#.##')"/></td>
				<td bgcolor='00B8FF' align='right' style='color:#FFFFFF;'>$<xsl:value-of select="format-number($main_doc/grandTotalBase_quote, '#.##')"/></td>
				<td bgcolor='00B8FF' align='right' style='color:#FFFFFF;'>$<xsl:value-of select="format-number($main_doc/grandTotalTarget_quote, '#.##')"/></td>
				<td bgcolor='00B8FF' align='right' style='color:#FFFFFF;'>$<xsl:value-of select="format-number($main_doc/grandTotalStretch_quote, '#.##')"/></td>
				<td bgcolor='00B8FF' align='right' style='color:#FFFFFF;'>$<xsl:value-of select="format-number($main_doc/grandTotalSell_quote, '#.##')"/></td>
				</tr></table>
				<br></br>
				<br></br>
				<p style='padding-left:35px;font: 14px Calibri'> You can review this quote within the BigMachines tool: <a href='{$TransactionURL}'><xsl:value-of select="$TransactionURL"/></a></p>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>