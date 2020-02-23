<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet 
xmlns:jmx="http://xml.kishou.go.jp/jmaxml1/"
xmlns:jmx_ib="http://xml.kishou.go.jp/jmaxml1/informationBasis1/" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" indent="yes" encoding="UTF-8"/>

<xsl:template match="/">

<!-- Head -->

	<!-- Item -->
	<!-- おそらく繰り返しはないだろうが念のため -->
	<xsl:for-each select="jmx:Report/jmx_ib:Head/jmx_ib:Headline/jmx_ib:Information/jmx_ib:Item">
		
		<!-- 火山Code -->
		<xsl:value-of select="jmx_ib:Areas/jmx_ib:Area/jmx_ib:Code"/>

		<!-- コロン -->
		<xsl:text>:</xsl:text>

		<!-- 種別Code -->
		<xsl:value-of select="jmx_ib:Kind/jmx_ib:Code"/>

		<!-- 改行 -->
		<xsl:text>\n</xsl:text>

	</xsl:for-each>

</xsl:template>

</xsl:stylesheet>
