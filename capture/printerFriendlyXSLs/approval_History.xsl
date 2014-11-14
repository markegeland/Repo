
<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:java="http://xml.apache.org/xslt/java" exclude-result-prefixes="java">
	<!-- update these if some of the history is not showing -->
	<xsl:variable name="requestVarName" select="'request_approval_submit_quote'"/>
	<xsl:variable name="approveVarName" select="'approve_submit_quote'"/>
	<xsl:variable name="reviseVarName" select="'revise_submit_quote'"/>
	<xsl:variable name="rejectVarName" select="'reject_submit_quote'"/>
	<xsl:template match="/">
		<html>
			<body>
			    <h2>Approval Log</h2>
				<xsl:apply-templates select="//change_item[@action_var_name = $requestVarName or @action_var_name = $approveVarName or @action_var_name = $reviseVarName or @action_var_name = $rejectVarName]"/>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="change_item">
		<xsl:variable name="attr" select="name()"/>
		<table border="0">
			<tr>
				<xsl:if test="@action_var_name=$reviseVarName">
					<td class="form-input" cellpadding="0" cellspacing="0">
						<b>REVISION 					
							  Created By  <xsl:value-of select="@user_first_name"/><xsl:text> </xsl:text><xsl:value-of select="@user_last_name"/> on <xsl:value-of select="@change_date"/>
						</b>
					</td>
				</xsl:if>
			</tr>				
		</table>
		<table width="100%" border="0" cellspacing="0" cellpadding="2">
			<xsl:if test="@action_var_name!=$reviseVarName">
				<tr>
					<td width="110"><font face="verdana" size="1">User:</font></td>
					<td>
						<font face="verdana" size="2" color="navy">
							<xsl:value-of select="@user_first_name"/><xsl:text> </xsl:text><xsl:value-of select="@user_last_name"/>
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
				<xsl:if test="@perform_comment != ''">
					<tr>
						<td width="110"><font face="verdana" size="1">User Comments:</font></td>
						<td>
							<font face="verdana" size="2" color="navy">
								<xsl:value-of select="@perform_comment"/>
							</font>
						</td>
						<td></td>
					</tr>
				</xsl:if>
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
		<p/>
	</xsl:template>
</xsl:stylesheet>