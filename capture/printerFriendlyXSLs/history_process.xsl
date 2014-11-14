
<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:java="http://xml.apache.org/xslt/java" exclude-result-prefixes="java">
	<xsl:template match="/">
		<html>
			<body>
				<table>
					<tr>
						<td>
							<b>REVISION # 1.0</b>
						</td>
					</tr>
				</table>
			</body>
		</html>
		<xsl:apply-templates select="//change_item"/>
	</xsl:template>
	<xsl:template match="change_item">
		<xsl:variable name="attr" select="name()"/>
		<table border="0">
			<tr>
				<xsl:if test="@action_name='Revise'">
					<td class="form-input" cellpadding="0" cellspacing="0">
						<b>REVISION # 					
							<xsl:value-of select="*/@new_value"/>  Created By  <xsl:value-of select="@user_first_name"/> 
							<xsl:value-of select="@user_last_name"/> on <xsl:value-of select="@change_date"/>
						</b>
					</td>
				</xsl:if>
			</tr>				
		</table>
		<table width="100%" border="0" cellspacing="0" cellpadding="2">
			<xsl:if test="@action_name!='Revise'">
				<tr>
					<td width="110"><font face="verdana" size="1">User:</font></td>
					<td>
						<font face="verdana" size="2" color="navy">
							<xsl:value-of select="@user_first_name"/> <xsl:value-of select="@user_last_name"/>
						</font>
					</td>
					<td></td>
				</tr>
				<tr>
					<td width="110"><font face="verdana" size="1">Action Taken:</font></td>
					<td>
						<font face="verdana" size="2" color="navy">
							<xsl:value-of select="@action_name"/>
						</font>
					</td>
					<td></td>
				</tr>
				<tr>
					<td width="110"><font face="verdana" size="1">Action Date:</font></td>
					<td>
						<font face="verdana" size="2" color="navy">
							<xsl:value-of select="@change_date"/>
						</font>
					</td>
					<td></td>
				</tr>					
			</xsl:if>
		</table>
		<table width="100%">
			<tr>
				<td width="20"></td>
				<td>
					<table width="100%" border="1" cellspacing="0" cellpadding="2">
						<xsl:if test="not(*)">
							<tr>
								<td align="center">
									<font face="verdana" size="2" color="green">
										<xsl:text> No changes recorded for this action  </xsl:text>
									</font>
								</td>
							</tr>
						</xsl:if>
						<xsl:if test="*">
							<tr>
								<th><font face="verdana" size="2"><xsl:text>Attribute</xsl:text></font></th>
								<th width="35%"><font face="verdana" size="2"><xsl:text>Original Value</xsl:text></font></th>
								<th width="35%"><font face="verdana" size="2"><xsl:text>New Value</xsl:text></font></th>
							</tr>
							<xsl:apply-templates select="*"/>
						</xsl:if>
					</table>
				</td>
				<td width="20"></td>
			</tr>
		</table>
		<p/>
	</xsl:template>
	<xsl:template match="*">
			<xsl:variable name="attr" select="name()"/>
			<tr>
				<td>
					<font face="verdana" size="1">
						<xsl:value-of select="//process/document/attribute[@var_name=$attr]/@label"/>
					</font>
				</td>
				<td>
					<font face="verdana" size="2" color="gray">
						<xsl:if test="@old_value!=''">
							<xsl:value-of select="@old_value"/>
						</xsl:if>
						<xsl:if test="@old_value=''">
							<i><xsl:text>-nothing-</xsl:text></i>
						</xsl:if>
					</font>
				</td>
				<td>
					<font face="verdana" size="2" color="green">
						<xsl:if test="@new_value!=''">
							<xsl:value-of select="@new_value"/>
						</xsl:if>
						<xsl:if test="@new_value=''">
							<i><xsl:text>-nothing-</xsl:text></i>
						</xsl:if>
					</font>
				</td>
			</tr>
	</xsl:template>
</xsl:stylesheet>