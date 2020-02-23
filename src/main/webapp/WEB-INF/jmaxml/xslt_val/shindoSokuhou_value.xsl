<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet 
xmlns:jmx="http://xml.kishou.go.jp/jmaxml1/"
xmlns:jmx_ib="http://xml.kishou.go.jp/jmaxml1/informationBasis1/" 
xmlns:jmx_seis="http://xml.kishou.go.jp/jmaxml1/body/seismology1/" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" indent="yes" encoding="UTF-8"/>

<xsl:template match="/">

<!-- Body -->

	<!-- 県 -->
	<xsl:for-each select="jmx:Report/jmx_seis:Body/jmx_seis:Intensity/jmx_seis:Observation/jmx_seis:Pref">
		<!-- 県code -->
		<xsl:value-of select="jmx_seis:Code" />
		<!-- コロン -->
		<xsl:text>:</xsl:text>
		<!-- 県最大震度 -->
		<xsl:value-of select="jmx_seis:MaxInt" />
		<!-- 改行 -->
		<xsl:text>\n</xsl:text>
		<!--県内のエリアごとの情報-->
		<xsl:for-each select="jmx_seis:Area">
			<xsl:value-of select="jmx_seis:Code" />
			<!-- コロン -->
			<xsl:text>:</xsl:text>
			<!-- エリア最大震度 -->
			<xsl:value-of select="jmx_seis:MaxInt" />
			<!-- 改行 -->
			<xsl:text>\n</xsl:text>
		</xsl:for-each>
	</xsl:for-each>

</xsl:template>

</xsl:stylesheet>
