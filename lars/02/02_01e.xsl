<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml" indent="yes" />

<xsl:template match="mondial">
  <waters>
    <xsl:apply-templates select="sea"/>
  </waters>
</xsl:template>

<xsl:template match="sea">
  <xsl:variable name="sea_id" select="@id" />
  <sea>    
    <name>
      <xsl:value-of select="name[last()]" />
    </name>
    <!-- Enough if any has a legative lat (i.e the minimum)-->
    <xsl:if test="0 > min(for $x in .//latitude return number($x))">
      <negative-lat/>
    </xsl:if>
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
    <!-- Split up into country and provinces because its easier -->
    <!-- Countries for (b), provinces for (c) -->
    <countries>
      <xsl:for-each select="id(@country)">
        <country>
          <xsl:value-of select="name[last()]" />
        </country>
      </xsl:for-each>
    </countries>
    <provinces>
      <xsl:for-each select=".//*[@province]/id(@province)">
        <province>
          <xsl:value-of select="name[last()]"/>
        </province>
      </xsl:for-each>
    </provinces>
    <!-- Enough if any has a legative lat (i.e the minimum)-->
    <xsl:if test="0 > min(for $x in .//latitude return number($x))">
      <negative-lat/>
    </xsl:if>
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
    <!-- Split up into country and provinces because its easier -->
    <!-- Countries for (b), provinces for (c) -->
    <countries>
      <xsl:for-each select="id(@country)">
        <country>
          <xsl:value-of select="name[last()]" />
        </country>
      </xsl:for-each>
    </countries>
    <provinces>
      <xsl:for-each select=".//*[@province]/id(@province)">
        <province>
          <xsl:value-of select="name[last()]"/>
        </province>
      </xsl:for-each>
    </provinces>
    <!-- Enough if any has a legative lat (i.e the minimum)-->
    <xsl:if test="0 > min(for $x in .//latitude return number($x))">
      <negative-lat/>
    </xsl:if>
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

</xsl:stylesheet>
