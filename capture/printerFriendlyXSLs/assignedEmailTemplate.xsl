
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:java="http://xml.apache.org/xslt/java" exclude-result-prefixes="java">	
<!-- Email template for sending it to the people ,who are assigned to claim the ownership of a particular quote -->
	<xsl:variable name="main_doc" select="/transaction/data_xml/document[@data_type='0']"/>
	<xsl:variable name="sub_doc" select="/transaction/data_xml"/>
	<xsl:template match="/">
		<html>
			<body>
			<p> This Quote has been assigned to you. Please follow the below details for claiming ownership.</p>
				<table border="0">
					<tr>
						<td width="30%"> <b> Quote Number  </b></td> <td> <xsl:value-of select="$main_doc/quoteNumber_quote"/> </td>
						
					</tr>	
					<tr>
						<td width="30%"> <b> Customer Information  </b> </td> <td> <xsl:text> TBD </xsl:text> </td>
						<!-- We have to recheck what is the customer info needed to send here -->
					</tr>			
					<tr>
						<td width="30%"> <b> Quick link  </b></td> <td> <xsl:value-of select="$main_doc/linkToDocument_quote"/></td>	
					</tr>				
				</table>
			<p> Please forward to the appropriate Rep.</p>
			</body>
		</html>
	</xsl:template>	
</xsl:stylesheet>