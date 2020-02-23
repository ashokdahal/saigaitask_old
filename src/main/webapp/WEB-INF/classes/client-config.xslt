<!--

Copyright 2004 Sun Microsystems, Inc. All rights reserved.
SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.

-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:param name="name"/>
  <xsl:param name="pass"/>
  <xsl:template match="/">
    <xwss:SecurityConfiguration dumpMessages="false"
      xmlns:xwss="http://java.sun.com/xml/ns/xwss/config">
      <xwss:UsernameToken name="{$name}" password="{$pass}"/>
    </xwss:SecurityConfiguration>
  </xsl:template>
</xsl:stylesheet>

