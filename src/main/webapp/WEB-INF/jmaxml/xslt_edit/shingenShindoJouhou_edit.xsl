<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet
xmlns:jmx="http://xml.kishou.go.jp/jmaxml1/"
xmlns:jmx_ib="http://xml.kishou.go.jp/jmaxml1/informationBasis1/"
xmlns:jmx_eb="http://xml.kishou.go.jp/jmaxml1/elementBasis1/"
xmlns:jmx_seis="http://xml.kishou.go.jp/jmaxml1/body/seismology1/"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" indent="yes" encoding="UTF-8"/>

<xsl:template match="/">

	<!--, タイトル（Control）,type=inputstring -->
	<xsl:value-of select="jmx:Report/jmx:Control/jmx:Title"/>
	<!--発表日時,type=datetime-->
	<xsl:value-of select="jmx:Report/jmx:Control/jmx:DateTime"/>

	<!-- タイトル-（Head),type=inputstring -->
	<xsl:value-of select="jmx:Report/jmx_ib:Head/jmx_ib:Title"/>
	<!-- テキスト,type=inputstring -->
	<xsl:value-of select="jmx:Report/jmx_ib:Head/jmx_ib:Headline/jmx_ib:Text"/>
	<!--オリジナル日時,type=datetime-->
 	<xsl:value-of select="jmx:Report/jmx_seis:Body/jmx_seis:Earthquake/jmx_seis:OriginTime"/>
	<!--到着日時,type=datetime-->
 	<xsl:value-of select="jmx:Report/jmx_seis:Body/jmx_seis:Earthquake/jmx_seis:ArrivalTime"/>
	<!-- 震央地名,type=inputstring -->
	<xsl:value-of select="jmx:Report/jmx_seis:Body/jmx_seis:Earthquake/jmx_seis:Hypocenter/jmx_seis:Area/jmx_seis:Name" />
	<!-- マグニチュード,type=inputstring -->
	<xsl:value-of select="jmx:Report/jmx_seis:Body/jmx_seis:Earthquake/jmx_eb:Magnitude" />
	<!-- エリアと震度,type=label,levelflag=1_label -->
	<xsl:for-each select="jmx:Report/jmx_ib:Head/jmx_ib:Headline/jmx_ib:Information/jmx_ib:Item">
		<!-- 震度,type=inputstring,levelflag=1_field -->
		<xsl:value-of select="jmx_ib:Kind/jmx_ib:Name" />
		<!--エリア,type=label,levelflag=1_1_label-->
		<xsl:for-each select="jmx_ib:Areas/jmx_ib:Area">
		    <!--領域名,type=comboboxsave,levelflag=1_1_field,groupid=5-->
			<xsl:value-of select="jmx_ib:Name" />
		    <!--領域コード,type=combobox,dbuid=dbseismiccode,levelflag=1_1_field,groupid=5-->
			<xsl:value-of select="jmx_ib:Code" />

		</xsl:for-each>
	</xsl:for-each>

	<!--県,type=label,levelflag=2_label-->
	<xsl:for-each select="jmx:Report/jmx_seis:Body/jmx_seis:Intensity/jmx_seis:Observation/jmx_seis:Pref">
		<!--県,type=comboboxsave,levelflag=2_field,groupid=4-->
		<xsl:value-of select="jmx_seis:Name" />
		<!--県のコード,type=combobox,dbuid=dbprefcode,levelflag=2_field,groupid=4-->
		<xsl:value-of select="jmx_seis:Code" />
		<!--県の最大震度,type=combobox,dbuid=dbmaxquake,levelflag=2_field-->
		<xsl:value-of select="jmx_seis:MaxInt" />

		<!-- エリア,type=label,levelflag=2_1_label -->
		<xsl:for-each select="jmx_seis:Area">
			<!--エリアのコード,type=comboboxsave,levelflag=2_1_field,groupid=3-->
			<xsl:value-of select="jmx_seis:Name" />
			<!--エリアのコード,type=combobox,dbuid=dbseismiccode,levelflag=2_1_field,groupid=3-->
			<xsl:value-of select="jmx_seis:Code" />
			<!--エリアの最大震度,type=combobox,dbuid=dbmaxquake,levelflag=2_1_field-->
			<xsl:value-of select="jmx_seis:MaxInt" />

			<!-- 市区町村,type=label,levelflag=2_1_1_label -->
			<xsl:for-each select="jmx_seis:City">
				<!--市区町村,type=comboboxsave,levelflag=2_1_1_field,groupid=2-->
				<xsl:value-of select="jmx_seis:Name" />
				<!--市区町村のコード,type=combobox,dbuid=dbcitycode,levelflag=2_1_1_field,groupid=2-->
				<xsl:value-of select="jmx_seis:Code" />
				<!--市区町村の最大震度,type=combobox,dbuid=dbmaxquake,levelflag=2_1_1_field-->
				<xsl:value-of select="jmx_seis:MaxInt" />

				<!-- 市区町村内の観測点,type=label,levelflag=2_1_1_1_label -->
				<xsl:for-each select="jmx_seis:IntensityStation">
					<!--市区町村内の観測点,type=comboboxsave,levelflag=2_1_1_1_field,groupid=1-->
					<xsl:value-of select="jmx_seis:Name" />
					<!--市区町村内の観測点のコード,type=combobox,dbuid=dbseismiccode,levelflag=2_1_1_1_field,groupid=1-->
					<xsl:value-of select="jmx_seis:Code" />
					<!--市区町村内の観測点の最大震度,type=combobox,dbuid=dbmaxquake,levelflag=2_1_1_1_field-->
					<xsl:value-of select="jmx_seis:Int" />
				</xsl:for-each>

			</xsl:for-each>

		</xsl:for-each>

	</xsl:for-each>
</xsl:template>

</xsl:stylesheet>
