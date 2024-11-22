<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html" indent="yes" />

<xsl:template match="mondial">
  <html>
    <body>
      <table>
        <tr>
          <th>River</th>
          <th>System Length</th>
        </tr>
        <xsl:for-each select="/*/river[./to/@water = /*/sea/@id]">
          <xsl:variable name="river_id" select="@id" />
          <tr>
            <td><xsl:value-of select="name"/></td>
            <td>
              <xsl:variable name="lens">
                <xsl:call-template name="river"/>
              </xsl:variable>
              <xsl:value-of select="sum(for $x in $lens/length return number($x))"/>
            </td>
          </tr>
        </xsl:for-each>
      </table>
    </body>
  </html>
</xsl:template>

<xsl:template name="river">
  <xsl:variable name="river_id" select="@id" />
  <length>
    <xsl:value-of select="length"/>
  </length>
  <xsl:for-each select="/*/lake[./to/@water = $river_id]">
    <xsl:call-template name="lake"/>
  </xsl:for-each>
  <xsl:for-each select="/*/river[./to/@water = $river_id]">
    <xsl:call-template name="river"/>
  </xsl:for-each>
</xsl:template>

<xsl:template name="lake">
  <xsl:variable name="lake_id" select="@id" />
  <!-- lakes do not have a length, they are just "in-between"... -->
  <xsl:for-each select="/*/lake[./to/@water = $lake_id]">
    <xsl:call-template name="lake"/>
  </xsl:for-each>
  <xsl:for-each select="/*/river[./to/@water = $lake_id]">
    <xsl:call-template name="river"/>
  </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
