<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet
xmlns:jmx="http://xml.kishou.go.jp/jmaxml1/"
xmlns:jmx_ib="http://xml.kishou.go.jp/jmaxml1/informationBasis1/"
xmlns:jmx_seis="http://xml.kishou.go.jp/jmaxml1/body/seismology1/"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" indent="yes" encoding="UTF-8"/>

<xsl:template match="/">

<Report xmlns="http://xml.kishou.go.jp/jmaxml1/" xmlns:jmx="http://xml.kishou.go.jp/jmaxml1/">
<Control>
	<Title>
		<xsl:value-of select="ReportRoot/Report_Control_Title"/>
	</Title>
	<DateTime>
		<xsl:value-of select="ReportRoot/Report_Control_DateTime"/>
	</DateTime>
</Control>
<Head xmlns="http://xml.kishou.go.jp/jmaxml1/informationBasis1/">
	<Title>
		<xsl:value-of select="ReportRoot/Report_Head_Title"/>
	</Title>
	<Headline>
		<Text>
			<xsl:value-of select="ReportRoot/Report_Head_Headline_Text"/>
		</Text>
		<Information type="震源・震度に関する情報（細分区域）">
		<xsl:for-each select="ReportRoot/Report_Head_Headline_Information_Item">
			<Item>
				<Kind>
				<Name>
					<xsl:value-of select="Report_Head_Headline_Information_Item_Kind_Name"/>
				</Name>
				</Kind>
				<Areas codeType="地震情報／細分区域">
					<xsl:for-each select="Report_Head_Headline_Information_Item_Areas_Area">
					<Area>
						<Name>
							<xsl:value-of select="Report_Head_Headline_Information_Item_Areas_Area_Name"/>
						</Name>
						<Code>
							<xsl:value-of select="Report_Head_Headline_Information_Item_Areas_Area_Code"/>
						</Code>
					</Area>
					</xsl:for-each>
				</Areas>
			</Item>
		</xsl:for-each>
		</Information>
	</Headline>
</Head>
<Body xmlns="http://xml.kishou.go.jp/jmaxml1/body/seismology1/" xmlns:jmx_eb="http://xml.kishou.go.jp/jmaxml1/elementBasis1/">
	<Earthquake>
      <OriginTime><xsl:value-of select="ReportRoot/Report_Body_Earthquake_OriginTime"/></OriginTime>
      <ArrivalTime><xsl:value-of select="ReportRoot/Report_Body_Earthquake_ArrivalTime"/></ArrivalTime>
      <Hypocenter>
        <Area>
          <Name>
          	<xsl:value-of select="ReportRoot/Report_Body_Earthquake_Hypocenter_Area_Name"/>
          </Name>
        </Area>
      </Hypocenter>
      <jmx_eb:Magnitude type="Mj" description="Ｍ５．３">
      	<xsl:value-of select="ReportRoot/Report_Body_Earthquake_Magnitude" />
      </jmx_eb:Magnitude>
    </Earthquake>
  
	<Intensity>
	<Observation>

		<xsl:for-each select="ReportRoot/Report_Body_Intensity_Observation_Pref">
		<Pref>
			<Name>
				<xsl:value-of select="Report_Body_Intensity_Observation_Pref_Name"/>
			</Name>
			<Code>
				<xsl:value-of select="Report_Body_Intensity_Observation_Pref_Code"/>
			</Code>
			<MaxInt>
				<xsl:value-of select="Report_Body_Intensity_Observation_Pref_MaxInt"/>
			</MaxInt>

			<xsl:for-each select="Report_Body_Intensity_Observation_Pref_Area">
			<Area>
				<Name>
					<xsl:value-of select="Report_Body_Intensity_Observation_Pref_Area_Name"/>
				</Name>
				<Code>
					<xsl:value-of select="Report_Body_Intensity_Observation_Pref_Area_Code"/>
				</Code>
				<MaxInt>
					<xsl:value-of select="Report_Body_Intensity_Observation_Pref_Area_MaxInt"/>
				</MaxInt>

				<xsl:for-each select="Report_Body_Intensity_Observation_Pref_Area_City">
				<City>
					<Name>
						<xsl:value-of select="Report_Body_Intensity_Observation_Pref_Area_City_Name"/>
					</Name>
					<Code>
						<xsl:value-of select="Report_Body_Intensity_Observation_Pref_Area_City_Code"/>
					</Code>
					<MaxInt>
						<xsl:value-of select="Report_Body_Intensity_Observation_Pref_Area_City_MaxInt"/>
					</MaxInt>

					<xsl:for-each select="Report_Body_Intensity_Observation_Pref_Area_City_IntensityStation">
					<IntensityStation>
						<Name>
							<xsl:value-of select="Report_Body_Intensity_Observation_Pref_Area_City_IntensityStation_Name"/>
						</Name>
						<Code>
							<xsl:value-of select="Report_Body_Intensity_Observation_Pref_Area_City_IntensityStation_Code"/>
						</Code>
						<Int>
							<xsl:value-of select="Report_Body_Intensity_Observation_Pref_Area_City_IntensityStation_Int"/>
						</Int>
					</IntensityStation>
					</xsl:for-each>
				</City>
				</xsl:for-each>
			</Area>
			</xsl:for-each>
		</Pref>
		</xsl:for-each>
	</Observation>
	</Intensity>
</Body>
</Report>

</xsl:template>

</xsl:stylesheet>
