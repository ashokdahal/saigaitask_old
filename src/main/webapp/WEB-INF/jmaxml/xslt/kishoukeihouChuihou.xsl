<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet 
xmlns:jmx="http://xml.kishou.go.jp/jmaxml1/"
xmlns:jmx_ib="http://xml.kishou.go.jp/jmaxml1/informationBasis1/" 
xmlns:jmx_seis="http://xml.kishou.go.jp/jmaxml1/body/seismology1/" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" indent="yes" encoding="UTF-8"/>

<xsl:param name="code"></xsl:param>

<xsl:template match="/">
	<xsl:apply-templates select="jmx:Report/jmx:Control" />
	<!-- ヘッダ -->
	<xsl:apply-templates select="jmx:Report/jmx_ib:Head" />
	<!-- 警報種別 -->
	<xsl:apply-templates select="jmx:Report/jmx_ib:Head/jmx_ib:Headline" />
</xsl:template>

<!--Control-->
<xsl:template match="jmx:Control">
	<!-- タイトル -->
	<xsl:value-of select="jmx:Title"/>
	<!-- 改行 -->
	<xsl:text>\n</xsl:text>
	<!--発表日時-->
	<xsl:value-of select="translate(substring(jmx:DateTime,1,4),'0123456789', '0123456789')"/>
	<xsl:text>/</xsl:text>
	<xsl:value-of select="translate(substring(jmx:DateTime,6,1),'0123456789', '0123456789')"/>
	<xsl:value-of select="translate(substring(jmx:DateTime,7,1),'0123456789', '0123456789')"/>
	<xsl:text>/</xsl:text>
	<xsl:value-of select="translate(substring(jmx:DateTime,9,1),'0123456789', '0123456789')"/>
	<xsl:value-of select="translate(substring(jmx:DateTime,10,1),'0123456789', '0123456789')"/>
	<xsl:text> </xsl:text>
	<!-- 時間はGMTなので+9する -->
	<xsl:value-of select="translate(substring(jmx:DateTime,12,2)+9,'0123456789', '0123456789')"/>
	<xsl:text>:</xsl:text>
	<xsl:value-of select="translate(substring(jmx:DateTime,15,2),'0123456789', '0123456789')"/>
	<xsl:text>:</xsl:text>
	<xsl:value-of select="translate(substring(jmx:DateTime,18,2),'0123456789', '0123456789')"/>
	<!-- 改行 -->
	<xsl:text>\n</xsl:text>
</xsl:template>

<xsl:template match="jmx_ib:Head">
	<!-- タイトル-->
	<xsl:value-of select="jmx_ib:Title"/>
	<!-- 改行 -->
	<xsl:text>\n</xsl:text>
</xsl:template>

	<xsl:template match="jmx_ib:Headline">
		<xsl:for-each select="jmx_ib:Information[@type='気象警報・注意報（市町村等）']/jmx_ib:Item">
			<xsl:if test="jmx_ib:Areas/jmx_ib:Area/jmx_ib:Code=$code">
			<!-- 特別警報 -->
				<xsl:choose>
				<!--発表中の警報がある場合-->
					<xsl:when test="count(jmx_ib:Kind/jmx_ib:Name[contains(.,'特別')!=0])>0">
						<xsl:text> ［特別警報］</xsl:text>
						<xsl:for-each select="jmx_ib:Kind[contains(jmx_ib:Name,'特別')!=0]">
								<xsl:value-of select="substring-before(jmx_ib:Name, '特')" />
							<!-- Conditionがある場合-->
								<xsl:if test="count(jmx_ib:Condition)!= 0">
									<xsl:text>（</xsl:text>
									<xsl:value-of select="jmx_ib:Condition" />
									<xsl:text>）</xsl:text>
								</xsl:if>
							<xsl:if test="position() != last()">
								<xsl:text>，</xsl:text>
							</xsl:if>
						</xsl:for-each>
					</xsl:when>
				<!-- 発表中の警報がない場合 -->
					<xsl:otherwise>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text></xsl:text>
			<!--警報-->
				<xsl:choose>
				<!--発表中の警報がある場合-->
					<xsl:when test="count(jmx_ib:Kind/jmx_ib:Name[contains(.,'警')!=0 and contains(.,'特別')=0])>0">
						<xsl:text> ［警報］</xsl:text>
						<xsl:for-each select="jmx_ib:Kind[contains(jmx_ib:Name,'警')!=0 and contains(.,'特別')=0]">
								<xsl:value-of select="substring-before(jmx_ib:Name, '警')" />
							<!-- Conditionがある場合-->
								<xsl:if test="count(jmx_ib:Condition)!= 0">
									<xsl:text>（</xsl:text>
									<xsl:value-of select="jmx_ib:Condition" />
									<xsl:text>）</xsl:text>
								</xsl:if>
							<xsl:if test="position() != last()">
								<xsl:text>，</xsl:text>
							</xsl:if>
						</xsl:for-each>
					</xsl:when>
				<!-- 発表中の警報がない場合 -->
					<xsl:otherwise>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text></xsl:text>
			<!-- 注意報 -->
				<xsl:choose>
				<!-- 発表中の注意報がある場合 -->
					<xsl:when test="count(jmx_ib:Kind/jmx_ib:Name[contains(.,'注')!=0])>0">
						<xsl:text> ［注意報］</xsl:text>
						<xsl:for-each select="jmx_ib:Kind[contains(jmx_ib:Name,'注')!=0]">
								<xsl:value-of select="substring-before(jmx_ib:Name, '注')" />
							<xsl:if test="position() != last()">
								<xsl:text>，</xsl:text>
							</xsl:if>
						</xsl:for-each>
					</xsl:when>
				<!-- 発表中の注意報がない場合 -->
					<xsl:otherwise>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
		</xsl:for-each>
		<xsl:text>\n</xsl:text>
		<!-- テキスト -->
		<xsl:value-of select="jmx_ib:Text"/>
	</xsl:template>


</xsl:stylesheet>
