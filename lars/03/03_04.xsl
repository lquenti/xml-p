<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml" indent="yes" />

<!--
  Strategy:
  - [x] By default, copy everything
  - [x] Add catalonia
  - [x] Move //province[@id="prov-Spain-11"]/city to //country[@car_code="CAT"]/city
  - [x] Replace all //located[@country="E" and @province="prov-Spain-11"] with
    <located country="CAT" />
  - [ ] Bsp Garonne: source/located
  - [ ] mehrere provinzen: (todo IDREF) Mediter. Sea
  - [ ] Organizations
  - [ ] Population/Area/Religion/Border... (s. catdata.xml) wird vermindert
    - [ ] Border <=0 fällt weg
    - [ ] Border ist symmetrisch
  - [ ] NICHT die ID ändern wo Spain einfach encoded ist
-->

<!-- Identity template: copy all text nodes, elements and attributes -->   
<xsl:template match="@*|node()">
  <xsl:choose>
    <!-- This doesn't cover multiple provinces but that would be too iffy -->
    <!-- also note that while this is hard ocded here, I could've just copy 
         pasted the stuff from below to make it dynamic -->
    <xsl:when test="name() = 'located' and @country='E' and @province='prov-Spain-11'">
      <located country='CAT'/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:copy>
        <xsl:apply-templates select="@*|node()" />
      </xsl:copy>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- Add Catalonia -->
<xsl:template match="mondial">
  <xsl:variable name="catdata">
    <!-- The problem is as follows
      If I just add doc('./catdata.xml') as a variable, I can not easily
      add stuff INSIDE of it. So instead I manually decompose it into the
      outer tag (i.e. the <country> element) and its inner. Thus, I can
      put stuff within my bastarded <xsl:element>, which *looks* like
      the outer element of the doc()
    -->
    <xsl:variable name="data" select="doc('./catdata.xml')"/>

    <xsl:variable name="outer_nodename" select="$data/*/name()"/>
    <xsl:variable name="outer_attrs" select="$data/*/@*"/>
    <xsl:variable name="inner_stuff" select="$data/*/*"/>

    <xsl:element name="{$outer_nodename}">
      <xsl:copy-of select="$outer_attrs"/>
      <xsl:copy-of select="$inner_stuff"/>

      <!-- Add the cities, but replace the country and province -->
      <xsl:for-each select="//city[@province='prov-Spain-11']">
        <city 
          id="{@id}"
          country="{$data/*/@car_code}"
        >
          <xsl:copy-of select="./*"/>
        </city>
      </xsl:for-each>
    </xsl:element>
  </xsl:variable>
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
    <xsl:copy-of select="$catdata"/>
  </xsl:copy>
</xsl:template>

<!-- //*[id(@country) = /mondial/country[@car_code="E"] and ./located/@province = "prov-Spain-11" ] -->
<!-- Hierfür alle nicht-country-attribute übernehmen und bei country vielleicht  -->
<!-- übernehmen (bsp Mittelmeer) -->

<!-- replace the original Catalonia with nothing -->
<xsl:template match="country[@car_code='E']/province[@id='prov-Spain-11']"/>

</xsl:stylesheet>
