<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html" indent="yes" />

<xsl:template match="waters">
  <html>
    <body>
      <tr>
        <th>River</th>
        <th>System Length</th>
      </tr>
      <xsl:for-each select="/*/sea">
        <tr>
          <td><xsl:value-of select="./name"/></td>
          <!-- number() can only be applied to a scalar -->
          <td>
            <!-- TODO: Returns NaN sometimes? -->
            <xsl:value-of select="sum(for $x in .//length return number($x))"/>
          </td>
        </tr>
      </xsl:for-each>
    </body>
  </html>
</xsl:template>

</xsl:stylesheet>
