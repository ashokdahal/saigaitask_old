<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet 
xmlns:jmx="http://xml.kishou.go.jp/jmaxml1/"
xmlns:jmx_ib="http://xml.kishou.go.jp/jmaxml1/informationBasis1/" 
xmlns:jmx_seis="http://xml.kishou.go.jp/jmaxml1/body/seismology1/" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" indent="yes" encoding="UTF-8"/>

<xsl:template match="/">

<!-- Head -->

	<!-- エリア -->	
	<xsl:for-each select="jmx:Report/jmx_ib:Head/jmx_ib:Headline/jmx_ib:Information/jmx_ib:Item">
		<!--領域名コード-->
		<xsl:for-each select="jmx_ib:Areas/jmx_ib:Area">
			<xsl:value-of select="jmx_ib:Code" />
			<!-- カンマ -->
			<xsl:text>,</xsl:text>
		</xsl:for-each>
		
		<!-- コロン -->
		<xsl:text>:</xsl:text>
		
		<!-- 警報・注意報コード -->
		<xsl:value-of select="jmx_ib:Kind/jmx_ib:Code" />
	<!-- 改行 -->
	<xsl:text>\n</xsl:text>
	</xsl:for-each>

	<xsl:for-each select="jmx:Report/jmx_seis:Body/jmx_seis:Tsunami/jmx_seis:Forecast/jmx_seis:Item">
		<!--領域名コード-->
		<xsl:value-of select="jmx_seis:Area/jmx_seis:Code" />
		
		<!-- コロン -->
		<xsl:text>:</xsl:text>
		
		<!-- 警報・注意報コード -->
		<xsl:value-of select="jmx_seis:Category/jmx_seis:Kind/jmx_seis:Code" />
	<!-- 改行 -->
	<xsl:text>\n</xsl:text>
	</xsl:for-each>

</xsl:template>

</xsl:stylesheet>
