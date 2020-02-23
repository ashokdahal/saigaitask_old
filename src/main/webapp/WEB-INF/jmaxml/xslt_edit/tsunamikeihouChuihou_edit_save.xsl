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
		<Information type="津波予報領域表現">
		<xsl:for-each select="ReportRoot/Report_Head_Headline_Information_Item">
			<Item>
				<Kind>
					<Name>
						<xsl:value-of select="Report_Head_Headline_Information_Item_Kind_Name"/>
					</Name>
					<Code>
						<xsl:value-of select="Report_Head_Headline_Information_Item_Kind_Code"/>
					</Code>
				</Kind>
				<Areas codeType="津波予報区">
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
	<Tsunami>
	<Forecast>
		<CodeDefine>
			<Type xpath="Item/Area/Code">津波予報区</Type>
			<Type xpath="Item/Category/Kind/Code">警報等情報要素／津波警報・注意報・予報</Type>
			<Type xpath="Item/Category/LastKind/Code">警報等情報要素／津波警報・注意報・予報</Type>
		</CodeDefine>
		<xsl:for-each select="ReportRoot/Report_Body_Tsunami_Forecast_Item">
		<Item>
			<Area>
				<Name>
					<xsl:value-of select="Report_Body_Tsunami_Forecast_Item_Area_Name" />
				</Name>
				<Code>
					<xsl:value-of select="Report_Body_Tsunami_Forecast_Item_Area_Code" />
				</Code>
			</Area>
			<Category>
				<Kind>
					<Name>
						<xsl:value-of select="Report_Body_Tsunami_Forecast_Item_Category_Kind_Name" />
					</Name>
					<Code>
						<xsl:value-of select="Report_Body_Tsunami_Forecast_Item_Category_Kind_Code" />
					</Code>
				</Kind>
			</Category>
		</Item>
		</xsl:for-each>
	</Forecast>
	</Tsunami>
</Body>
</Report>

</xsl:template>

</xsl:stylesheet>
