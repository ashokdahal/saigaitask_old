<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
xmlns:jmx="http://xml.kishou.go.jp/jmaxml1/"
xmlns:jmx_ib="http://xml.kishou.go.jp/jmaxml1/informationBasis1/" 
xmlns:jmx_eb="http://xml.kishou.go.jp/jmaxml1/elementBasis1/" 
xmlns:jmx_seis="http://xml.kishou.go.jp/jmaxml1/body/seismology1/" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" encoding="UTF-8" indent="yes" />

<xsl:param name="code"></xsl:param>

<xsl:template match="/">

<!-- 管理部の翻訳 -->
    <xsl:value-of select="jmx:Report/jmx:Control/jmx:Title" />
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

<!-- ヘッダ部の翻訳 -->
    <xsl:if test="jmx:Report/jmx_ib:Head/jmx_ib:Serial!=''">第<xsl:value-of select="jmx:Report/jmx_ib:Head/jmx_ib:Serial" />報<xsl:text>\n</xsl:text>
    </xsl:if>

  <xsl:if test="jmx:Report/jmx_ib:Head/jmx_ib:Headline/jmx_ib:Text!=''">
        <xsl:value-of select="jmx:Report/jmx_ib:Head/jmx_ib:Headline/jmx_ib:Text" />
        <!-- 改行 -->
        <xsl:text>\n</xsl:text>
  </xsl:if>

  <!-- 震度の予測 -->
  <xsl:if test="jmx:Report/jmx_seis:Body/jmx_seis:Intensity/jmx_seis:Forecast!=''">［最大予測震度］<xsl:call-template name="ForecastIntChange">
              <xsl:with-param name="From" select="jmx:Report/jmx_seis:Body/jmx_seis:Intensity/jmx_seis:Forecast/jmx_seis:ForecastInt/jmx_seis:From" />
              <xsl:with-param name="To" select="jmx:Report/jmx_seis:Body/jmx_seis:Intensity/jmx_seis:Forecast/jmx_seis:ForecastInt/jmx_seis:To" />
            </xsl:call-template>
        <!-- 改行 -->
        <xsl:text>\n</xsl:text>
  </xsl:if>

  <xsl:if test="jmx:Report/jmx_seis:Body/jmx_seis:Intensity/jmx_seis:Forecast/jmx_seis:Pref!=''">

      <xsl:for-each select="jmx:Report/jmx_seis:Body/jmx_seis:Intensity/jmx_seis:Forecast/jmx_seis:Pref">
	    <xsl:if test="jmx_seis:Area/jmx_seis:Code=$code">［<xsl:value-of select="jmx_seis:Area/jmx_seis:Name" />］<xsl:call-template name="ForecastIntChange">
              <xsl:with-param name="From" select="jmx_seis:Area/jmx_seis:ForecastInt/jmx_seis:From" />
              <xsl:with-param name="To" select="jmx_seis:Area/jmx_seis:ForecastInt/jmx_seis:To" />
            </xsl:call-template>
        </xsl:if>
      </xsl:for-each>

  </xsl:if>

</xsl:template>

<!-- ============================ -->
<!-- 改行支援テンプレート　　　　 -->
<!-- ============================ -->
<xsl:template name="mkBR">
  <xsl:param name="value">dummy</xsl:param>
  <xsl:choose>
    <xsl:when test="contains($value, '&#xA;')">
      <xsl:value-of select="substring-before($value, '&#xA;')" />
      <br/>
      <xsl:call-template name="mkBR">
        <xsl:with-param name="value" select="substring-after($value, '&#xA;')" />
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$value" />
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ============================ -->
<!-- 震度変換表示テンプレート　　 -->
<!-- ============================ -->
<xsl:template name="SIchange">
  <xsl:param name="SI">dummy</xsl:param>
  <xsl:choose>
    <xsl:when test="contains($SI,'-')">
      <xsl:value-of select="translate($SI,'-','弱')" />
    </xsl:when>
    <xsl:when test="contains($SI,'+')">
      <xsl:value-of select="translate($SI,'+','強')" />
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$SI" />
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ============================ -->
<!-- 予測震度変換表示テンプレート -->
<!-- ============================ -->
<xsl:template name="ForecastIntChange">
  <xsl:param name="From">dummy</xsl:param>
  <xsl:param name="To">dummy</xsl:param>
  <xsl:choose>
    <xsl:when test="contains($From,$To)">
      <xsl:call-template name="SIchange">
        <xsl:with-param name="SI" select="$From" />
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="$To='over'">
      <xsl:call-template name="SIchange">
        <xsl:with-param name="SI" select="$From" />
      </xsl:call-template>
      <xsl:text>程度以上</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="SIchange">
        <xsl:with-param name="SI" select="$From" />
      </xsl:call-template>
      <xsl:text>から</xsl:text>
      <xsl:call-template name="SIchange">
        <xsl:with-param name="SI" select="$To" />
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ============================ -->
<!-- 日付表示テンプレート         -->
<!-- ============================ -->
<xsl:template name="DispDateTime">
  <xsl:param name="Jikan">dummy</xsl:param>
  <xsl:param name="Significant">yyyy-mm-ddThh:mm</xsl:param>
  <xsl:param name="TimeZone">dummy</xsl:param>
  <xsl:if test="contains($Significant,'yyyy')">
    <xsl:choose>
      <xsl:when test="$TimeZone='UTC'">
        <xsl:value-of select="substring($Jikan,1,4)" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>平成</xsl:text>
        <xsl:value-of select="substring($Jikan,1,4) - 1988 " />
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>年</xsl:text>
  </xsl:if>
  <xsl:if test="contains($Significant,'-mm') or contains($Significant,'mm-')">
    <xsl:value-of select="substring($Jikan,6,2)" />
    <xsl:text>月</xsl:text>
  </xsl:if>
  <xsl:if test="contains($Significant,'dd')">
    <xsl:value-of select="substring($Jikan,9,2)" />
    <xsl:text>日</xsl:text>
  </xsl:if>
  <xsl:if test="contains($Significant,'hh')">
    <xsl:value-of select="substring($Jikan,12,2)" />
    <xsl:text>時</xsl:text>
  </xsl:if>
  <xsl:if test="contains($Significant,':mm') or contains($Significant,'mm:')">
    <xsl:value-of select="substring($Jikan,15,2)" />
    <xsl:text>分</xsl:text>
  </xsl:if>
  <xsl:if test="contains($Significant,'ss')">
    <xsl:value-of select="substring($Jikan,18,2)" />
    <xsl:text>秒</xsl:text>
  </xsl:if>
  <xsl:if test="$TimeZone='UTC'">
    <xsl:text>（ＵＴＣ）</xsl:text>
  </xsl:if>
</xsl:template>

<!-- ============================ -->
<!-- 全角スペース表示テンプレート -->
<!-- ============================ -->
<xsl:template name="blank1"><xsl:text>　</xsl:text></xsl:template>
<xsl:template name="blank2"><xsl:text>　　</xsl:text></xsl:template>
<xsl:template name="blank3"><xsl:text>　　　</xsl:text></xsl:template>
<xsl:template name="blank4"><xsl:text>　　　　</xsl:text></xsl:template>
<xsl:template name="blank5"><xsl:text>　　　　　</xsl:text></xsl:template>
<xsl:template name="blank6"><xsl:text>　　　　　　</xsl:text></xsl:template>
<xsl:template name="blank7"><xsl:text>　　　　　　　</xsl:text></xsl:template>
<xsl:template name="blank8"><xsl:text>　　　　　　　　</xsl:text></xsl:template>

</xsl:stylesheet>
