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
      <Information type="土砂災害警戒情報">
	    
		<xsl:for-each select="ReportRoot/Report_Head_Headline_Information_Item">
        <Item>
          <Kind>
            <Name>
				<xsl:value-of select="Report_Head_Headline_Information_Item_Kind_Name"/>
			</Name>
            <Code>
				<xsl:value-of select="Report_Head_Headline_Information_Item_Kind_Code"/>
			</Code>
            <Condition>
				<xsl:value-of select="Report_Head_Headline_Information_Item_Kind_Condition"/>
			</Condition>
          </Kind>
          <Areas codeType="気象・地震・火山情報／市町村等">
		  
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
  <Body xmlns="http://xml.kishou.go.jp/jmaxml1/body/meteorology1/">
  </Body>
</Report>	

</xsl:template>

</xsl:stylesheet>
