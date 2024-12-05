<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml" indent="yes" />

<xsl:template match="mondial">
  <waters>
    <xsl:apply-templates select="sea"/>
    <xsl:apply-templates select="/*/lake[not(to)]"/>
  </waters>
</xsl:template>

<xsl:template match="sea">
  <xsl:variable name="sea_id" select="@id" />
  <sea>    
    <name>
      <xsl:value-of select="name[last()]" />
    </name>
      <xsl:for-each select="/*/river[./to/@water = $sea_id]" >
        <xsl:sort select="/estuary/elevation" />
        <xsl:call-template name="river"/>
      </xsl:for-each>
  </sea>
</xsl:template>

<xsl:template name="river">
<xsl:variable name="river_id" select="@id" />
<river>
  <name>
    <xsl:value-of select="name[last()]" />
  </name>
  <length>
    <xsl:value-of select="length" />
  </length>
  <xsl:for-each select="/*/river[./to/@water = $river_id]">
    <xsl:sort select="/estuary/elevation" />
    <xsl:call-template name="river" />
  </xsl:for-each>
  <xsl:for-each select="/*/lake[./to/@water = $river_id]">
    <xsl:sort select="/elevation" />
    <xsl:call-template name="lake" />
  </xsl:for-each>
</river>
</xsl:template>

<xsl:template name="lake">
<xsl:variable name="lake_id" select="@id" />
  <lake>
    <name>
      <xsl:value-of select="name" />
    </name>
    <xsl:for-each select="/*/river[./to/@water = $lake_id]">
      <xsl:sort select="/estuary/elevation" />
      <xsl:call-template name="river" />
    </xsl:for-each>
    <xsl:for-each select="/*/lake[./to/@water = $lake_id]">
      <xsl:sort select="/elevation" />
      <xsl:call-template name="lake" />
    </xsl:for-each>
  </lake>
</xsl:template>

<!-- Lakes that do not end in seas -->
<xsl:template match="/*/lake[not(to)]">
<xsl:variable name="lake_id" select="@id" />
  <lake>
    <name>
      <xsl:value-of select="name" />
    </name>
    <!-- They also need those which flow into them -->
    <xsl:for-each select="/*/river[./to/@water = $lake_id]">
      <xsl:sort select="/estuary/elevation" />
      <xsl:call-template name="river" />
    </xsl:for-each>
    <xsl:for-each select="/*/lake[./to/@water = $lake_id]">
      <xsl:sort select="/elevation" />
      <xsl:call-template name="lake" />
    </xsl:for-each>
  </lake>
</xsl:template>

<!-- rivers that just end -->
<xsl:template match="/*/river[not(to)]">
<xsl:variable name="river_id" select="@id" />
<river>
  <name>
    <xsl:value-of select="name[last()]" />
  </name>
  <length>
    <xsl:value-of select="length" />
  </length>
  <xsl:for-each select="/*/river[./to/@water = $river_id]">
    <xsl:sort select="/estuary/elevation" />
    <xsl:call-template name="river" />
  </xsl:for-each>
  <xsl:for-each select="/*/lake[./to/@water = $river_id]">
    <xsl:sort select="/elevation" />
    <xsl:call-template name="lake" />
  </xsl:for-each>
</river>
</xsl:template>

</xsl:stylesheet>
