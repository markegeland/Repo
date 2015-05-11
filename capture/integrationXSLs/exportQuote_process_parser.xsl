<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sf2="urn:partner.soap.sforce.com" xmlns:sf="urn:sobject.partner.soap.sforce.com">
	<xsl:output method="xml"/>
	<xsl:template match="/">
		<response>
			<id_field_value>
				<xsl:value-of select="//sf2:id"/>
			</id_field_value>
			<xsl:for-each select="//sf2:message">
				<error>
					<xsl:value-of select="."/>
				</error>
			</xsl:for-each>
		</response>
	</xsl:template>
</xsl:stylesheet>
