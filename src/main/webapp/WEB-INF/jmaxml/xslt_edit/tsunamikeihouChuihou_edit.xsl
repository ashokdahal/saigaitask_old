<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet 
xmlns:jmx="http://xml.kishou.go.jp/jmaxml1/"
xmlns:jmx_ib="http://xml.kishou.go.jp/jmaxml1/informationBasis1/" 
xmlns:jmx_seis="http://xml.kishou.go.jp/jmaxml1/body/seismology1/" 
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

	<!-- エリア,type=label,levelflag=1_label -->	
	<xsl:for-each select="jmx:Report/jmx_ib:Head/jmx_ib:Headline/jmx_ib:Information/jmx_ib:Item">
		<!-- 警報・注意報,type=comboboxsave,levelflag=1_field,groupid=1 -->
		<xsl:value-of select="jmx_ib:Kind/jmx_ib:Name" />

		<!-- 警報・注意報コード,type=combobox,levelflag=1_field,dbuid=dbcategorykeihoucode,groupid=1 -->
		<xsl:value-of select="jmx_ib:Kind/jmx_ib:Code" />

		<!--領域名コード,type=label,levelflag=1_1_label-->
		<xsl:for-each select="jmx_ib:Areas/jmx_ib:Area">
			<!--領域名,type=comboboxsave,levelflag=1_1_field,groupid=3-->
			<xsl:value-of select="jmx_ib:Name" />	
			<!--コード,type=combobox,levelflag=1_1_field,dbuid=dbtatsunamiareacode,groupid=3-->	
			<xsl:value-of select="jmx_ib:Code" />
		</xsl:for-each>
		
	</xsl:for-each>

	<!-- 予報,type=label,levelflag=2_label -->	
	<xsl:for-each select="jmx:Report/jmx_seis:Body/jmx_seis:Tsunami/jmx_seis:Forecast/jmx_seis:Item">
		<!-- 警報・注意報,type=comboboxsave,levelflag=2_field,groupid=2 -->
		<xsl:value-of select="jmx_seis:Category/jmx_seis:Kind/jmx_seis:Name" />

		<!-- 警報・注意報コード,type=combobox,levelflag=2_field,dbuid=dbcategorykeihoucode,groupid=2 -->
		<xsl:value-of select="jmx_seis:Category/jmx_seis:Kind/jmx_seis:Code" />


		<!--領域名,type=comboboxsave,levelflag=2_field,groupid=4 -->
		<xsl:value-of select="jmx_seis:Area/jmx_seis:Name" />
		<!--領域名コード,type=combobox,levelflag=2_field,dbuid=dbtatsunamiareacode,groupid=4-->
		<xsl:value-of select="jmx_seis:Area/jmx_seis:Code" />
		
	</xsl:for-each>
</xsl:template>

</xsl:stylesheet>
