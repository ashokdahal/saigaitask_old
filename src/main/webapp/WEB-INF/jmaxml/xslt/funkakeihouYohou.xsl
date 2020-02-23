<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet 
xmlns:jmx="http://xml.kishou.go.jp/jmaxml1/"
xmlns:jmx_ib="http://xml.kishou.go.jp/jmaxml1/informationBasis1/" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" indent="yes" encoding="UTF-8"/>

<xsl:template match="/">

<!--Control-->
	<!-- タイトル -->
	<xsl:value-of select="jmx:Report/jmx:Control/jmx:Title"/>
	<!-- 改行 -->
	<xsl:text>\n</xsl:text>
	<!--発表日時-->
	<xsl:value-of select="translate(substring(jmx:Report/jmx:Control/jmx:DateTime,1,4),'0123456789', '0123456789')"/>
	<xsl:text>/</xsl:text>
	<xsl:value-of select="translate(substring(jmx:Report/jmx:Control/jmx:DateTime,6,1),'0123456789', '0123456789')"/>
	<xsl:value-of select="translate(substring(jmx:Report/jmx:Control/jmx:DateTime,7,1),'0123456789', '0123456789')"/>
	<xsl:text>/</xsl:text>
	<xsl:value-of select="translate(substring(jmx:Report/jmx:Control/jmx:DateTime,9,1),'0123456789', '0123456789')"/>
	<xsl:value-of select="translate(substring(jmx:Report/jmx:Control/jmx:DateTime,10,1),'0123456789', '0123456789')"/>
	<xsl:text> </xsl:text>
	<!-- 時間はGMTなので+9する -->
	<xsl:value-of select="translate(substring(jmx:Report/jmx:Control/jmx:DateTime,12,2)+9,'0123456789', '0123456789')"/>
	<xsl:text>:</xsl:text>
	<xsl:value-of select="translate(substring(jmx:Report/jmx:Control/jmx:DateTime,15,2),'0123456789', '0123456789')"/>
	<xsl:text>:</xsl:text>
	<xsl:value-of select="translate(substring(jmx:Report/jmx:Control/jmx:DateTime,18,2),'0123456789', '0123456789')"/>
	<!-- 改行 -->
	<xsl:text>\n</xsl:text>

<!-- Head -->

	<!-- タイトル-->
	<xsl:value-of select="jmx:Report/jmx_ib:Head/jmx_ib:Title"/>
	<!-- 改行 -->
	<xsl:text>\n</xsl:text>
	<!-- テキスト -->
	<xsl:value-of select="jmx:Report/jmx_ib:Head/jmx_ib:Headline/jmx_ib:Text"/>

</xsl:template>

</xsl:stylesheet>
