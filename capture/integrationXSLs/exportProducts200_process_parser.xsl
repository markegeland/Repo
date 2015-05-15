<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sf2="urn:partner.soap.sforce.com" xmlns:sf="urn:sobject.partner.soap.sforce.com">
	<xsl:output method="xml"/>
	<xsl:variable name="ids" select="//sf2:id"/>
	<xsl:variable name="errormsg" select="//sf2:message"/>
	<xsl:template match="/">
		<response>
			<id_field_value>
				<xsl:text>~</xsl:text>
				<xsl:for-each select="$ids">
					<xsl:value-of select="."/><xsl:text>~</xsl:text>
				</xsl:for-each>
			</id_field_value>
			<xsl:for-each select="$errormsg">
				<error>
					<xsl:value-of select="."/>
				</error>
			</xsl:for-each>
		</response>
	</xsl:template>
</xsl:stylesheet>
