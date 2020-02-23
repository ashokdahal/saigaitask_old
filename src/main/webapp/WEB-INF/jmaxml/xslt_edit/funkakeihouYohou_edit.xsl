<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet 
xmlns:jmx="http://xml.kishou.go.jp/jmaxml1/"
xmlns:jmx_ib="http://xml.kishou.go.jp/jmaxml1/informationBasis1/" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" indent="yes" encoding="UTF-8"/>

<xsl:template match="/">

	<!--, タイトル（Control）,type=inputstring -->
	<xsl:value-of select="jmx:Report/jmx:Control/jmx:Title"/>
	<!--発表日時,type=datetime-->
	<xsl:value-of select="jmx:Report/jmx:Control/jmx:DateTime"/>

	<!-- タイトル（Head),type=inputstring -->
	<xsl:value-of select="jmx:Report/jmx_ib:Head/jmx_ib:Title"/>
	<!-- テキスト,type=inputstring -->
	<xsl:value-of select="jmx:Report/jmx_ib:Head/jmx_ib:Headline/jmx_ib:Text"/>
	<!-- トリガー情報,type=label,levelflag=1_label -->
	<xsl:for-each select="jmx:Report/jmx_ib:Head/jmx_ib:Headline/jmx_ib:Information/jmx_ib:Item">

		<!-- 種別,type=comboboxsave,levelflag=1_1_field,groupid=1 -->
		<xsl:value-of select="jmx_ib:Kind/jmx_ib:Name"/>
	
		<!-- 種別Code,type=combobox,levelflag=1_1_field,dbuid=dbvolcanotypecode,groupid=1 -->
		<xsl:value-of select="jmx_ib:Kind/jmx_ib:Code"/>
	
		<!-- 火山名,type=comboboxsave,levelflag=1_1_field,groupid=2 -->
		<xsl:value-of select="jmx_ib:Areas/jmx_ib:Area/jmx_ib:Name"/>		

		<!-- 火山Code,type=combobox,levelflag=1_1_field,dbuid=dbvolcanoareacode,groupid=2 -->
		<xsl:value-of select="jmx_ib:Areas/jmx_ib:Area/jmx_ib:Code"/>

	</xsl:for-each>
	
</xsl:template>

</xsl:stylesheet>
