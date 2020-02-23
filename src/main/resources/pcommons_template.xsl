<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" xmlns:pcsoap="http://soap.publiccommons.ne.jp/">
	<xsl:template match="/">
		<pcsoap:publish>
			<pcsoap:message>
				<xsl:apply-templates />
			</pcsoap:message>
		</pcsoap:publish>
		</xsl:template>
		<xsl:template match="@*|node()">
			<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>