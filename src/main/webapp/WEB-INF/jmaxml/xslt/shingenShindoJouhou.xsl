<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet 
xmlns:jmx="http://xml.kishou.go.jp/jmaxml1/"
xmlns:jmx_ib="http://xml.kishou.go.jp/jmaxml1/informationBasis1/" 
xmlns:jmx_eb="http://xml.kishou.go.jp/jmaxml1/elementBasis1/" 
xmlns:jmx_seis="http://xml.kishou.go.jp/jmaxml1/body/seismology1/" 
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
	<!-- 改行 -->
	<xsl:text>\n</xsl:text>
	<!-- 震央地名 -->
	<xsl:text>震央地名:</xsl:text>
	<xsl:value-of select="jmx:Report/jmx_seis:Body/jmx_seis:Earthquake/jmx_seis:Hypocenter/jmx_seis:Area/jmx_seis:Name" />
	<!-- 改行 -->
	<xsl:text>\n</xsl:text>
	<!-- マグニチュード -->
	<xsl:text>マグニチュード:</xsl:text>
	<xsl:value-of select="jmx:Report/jmx_seis:Body/jmx_seis:Earthquake/jmx_eb:Magnitude" />
	<!-- 改行 -->
	<xsl:text>\n</xsl:text>
	<!-- エリアと震度 -->	
	<xsl:for-each select="jmx:Report/jmx_ib:Head/jmx_ib:Headline/jmx_ib:Information/jmx_ib:Item">
		<!-- 震度 -->
		<xsl:value-of select="jmx_ib:Kind/jmx_ib:Name" />
		<!-- コロン -->
		<xsl:text>:</xsl:text>
		<!--領域名-->
		<xsl:for-each select="jmx_ib:Areas/jmx_ib:Area">
			<xsl:value-of select="jmx_ib:Name" />
			<!-- カンマ+半角スペース -->
			<xsl:text>, </xsl:text>
		</xsl:for-each>
	<!-- 改行 -->
	<xsl:text>\n</xsl:text>
	</xsl:for-each>

</xsl:template>

</xsl:stylesheet>
