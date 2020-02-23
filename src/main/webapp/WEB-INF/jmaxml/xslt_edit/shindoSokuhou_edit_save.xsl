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
      <Information type="震度速報">

		<xsl:for-each select="ReportRoot/Report_Head_Headline_Information_Item">
        <Item>
          <Kind>
            <Name>
				<xsl:value-of select="Report_Head_Headline_Information_Item_Kind_Name"/>
			</Name>

            <Condition>
				<xsl:value-of select="Report_Head_Headline_Information_Item_Kind_Condition"/>
			</Condition>
          </Kind>

          <Areas codeType="震度速報エリア情報">
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

  <Body xmlns="http://xml.kishou.go.jp/jmaxml1/body/seismology1/" xmlns:jmx_eb="http://xml.kishou.go.jp/jmaxml1/elementBasis1/" type="地方自治体情報">
  	<Intensity>
	 <Observation>
			
		<MaxInt>
			<xsl:value-of select="ReportRoot/Report_Body_Intensity_Observation_MaxInt"/>
		</MaxInt>			
	
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
            <Area codeType="震度速報エリア情報">
				<Name>
					<xsl:value-of select="Report_Body_Intensity_Observation_Pref_Area_Name"/>
				</Name>
				<Code>
					<xsl:value-of select="Report_Body_Intensity_Observation_Pref_Area_Code"/>
				</Code>
				<MaxInt>
					<xsl:value-of select="Report_Body_Intensity_Observation_Pref_Area_MaxInt"/>
				</MaxInt>
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
