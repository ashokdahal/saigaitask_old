<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet 
xmlns:jmx="http://xml.kishou.go.jp/jmaxml1/"
xmlns:jmx_ib="http://xml.kishou.go.jp/jmaxml1/informationBasis1/" 
xmlns:jmx_seis="http://xml.kishou.go.jp/jmaxml1/body/seismology1/" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" indent="yes" encoding="UTF-8"/>

<xsl:template match="/">

<!--Control-->
	<!-- タイトル -->
	<xsl:value-of select="jmx:Report/jmx:Control/jmx:Title"/>
<!-- 改行 -->
<xsl:text>
</xsl:text>
	<!--発表日時-->
	<xsl:value-of select="translate(substring(jmx:Report/jmx:Control/jmx:DateTime,1,4),'0123456789', '0123456789')"/>
	<xsl:text>/</xsl:text>
	<xsl:value-of select="translate(substring(jmx:Report/jmx:Control/jmx:DateTime,6,1),'0123456789', '0123456789')"/>
	<xsl:value-of select="translate(substring(jmx:Report/jmx:Control/jmx:DateTime,7,1),'0123456789', '0123456789')"/>
	<xsl:text>/</xsl:text>
	<xsl:value-of select="translate(substring(jmx:Report/jmx:Control/jmx:DateTime,9,1),'0123456789', '0123456789')"/>
	<xsl:value-of select="translate(substring(jmx:Report/jmx:Control/jmx:DateTime,10,1),'0123456789', '0123456789')"/>
	<xsl:text> </xsl:text>
	<xsl:value-of select="translate(substring(jmx:Report/jmx:Control/jmx:DateTime,12,2)+9,'0123456789', '0123456789')"/>
	<xsl:text>:</xsl:text>
	<xsl:value-of select="translate(substring(jmx:Report/jmx:Control/jmx:DateTime,15,2),'0123456789', '0123456789')"/>
	<xsl:text>:</xsl:text>
	<xsl:value-of select="translate(substring(jmx:Report/jmx:Control/jmx:DateTime,18,2),'0123456789', '0123456789')"/>
<!-- 改行 -->
<xsl:text>
</xsl:text>
<xsl:choose>
	<xsl:when test="jmx:Report/jmx:Control/jmx:Title = '震源・震度に関する情報'">
		<!--領域名-->
		<xsl:value-of select="jmx:Report/jmx_seis:Body/jmx_seis:Earthquake/jmx_seis:Hypocenter/jmx_seis:Area/jmx_seis:Name" />
		<xsl:text>:</xsl:text>
		<xsl:value-of select="jmx:Report/jmx_seis:Body/jmx_seis:Earthquake/jmx_seis:Hypocenter/jmx_seis:Area/jmx_seis:Code" />
		<xsl:text>:</xsl:text>
		<xsl:value-of select="jmx:Report/jmx_seis:Body/jmx_seis:Earthquake/jmx_seis:Hypocenter/jmx_seis:Area/jmx_seis:Code/@type" />
<!-- 改行 -->
<xsl:text>
</xsl:text>
	</xsl:when>
	<xsl:when test="jmx:Report/jmx:Control/jmx:Title = '津波警報・注意報・予報a'">
		<xsl:for-each select="jmx:Report/jmx_seis:Body/jmx_seis:Tsunami/jmx_seis:Forecast/jmx_seis:Item" >
			<!--領域名-->
			<xsl:value-of select="jmx_seis:Area/jmx_seis:Name" />
			<xsl:text>:</xsl:text>
			<xsl:value-of select="jmx_seis:Area/jmx_seis:Code" />
			<xsl:text>:</xsl:text>
			<xsl:value-of select="jmx_seis:Category/jmx_seis:Kind/jmx_seis:Name" />
<!-- 改行 -->
<xsl:text>
</xsl:text>
		</xsl:for-each>
	</xsl:when>		
	<xsl:otherwise>
		<!--Headline-->
		<xsl:for-each select="jmx:Report/jmx_ib:Head/jmx_ib:Headline/jmx_ib:Information">
			<!--Informationのtype-->
			<xsl:for-each select="jmx_ib:Item">
				<!--領域名-->
				<xsl:for-each select="jmx_ib:Areas/jmx_ib:Area">
					<xsl:value-of select="jmx_ib:Name" />
					<xsl:text>:</xsl:text>
					<xsl:value-of select="jmx_ib:Code" />
					<xsl:text>:</xsl:text>
					<xsl:choose>
						<xsl:when test="contains(jmx_ib:Areas/@codeType, '／')">
							<xsl:value-of select="substring-after(../../jmx_ib:Areas/@codeType, '／')" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="../../jmx_ib:Areas/@codeType" />
						</xsl:otherwise>
					</xsl:choose>
<xsl:text>
</xsl:text>
				</xsl:for-each>
<!-- 改行 -->
<xsl:text>
</xsl:text>
			</xsl:for-each>
		</xsl:for-each>
	</xsl:otherwise>
</xsl:choose>

</xsl:template>

</xsl:stylesheet>
