<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml" indent="yes" />

<!--
  Strategy:
  - [x] By default, copy everything
  - [x] Add catalonia
  - [x] Remove the original catalonia
  - Add from the original:
    - [x] area
    - [x] population
    - [x] `<encompassed continent="europe" percentage="100"/>`
    - [x] cities
  - Change Attributes with Spain
    - [x] Add another population for current year
    - [x] Border to catalonia
  - Change border
    - [x] Remove border from spain
    - [x] Update border from countries that neighbour spain
    - [x] Add catalonia to other countries
  - [ ] If you have something like
        `//*[id(@country) = $old_country and ./located/id(@province) = $old_province]`
        then
        - [ ] If it is the only province, remove the old country from the upper one
        - [ ] else, add catalonia
    - Note that there are no located without the country above, because
        let $located_in_country_elems := //*[
            id(@country) = //country[@car_code="E"] and 
            ./located[id(@province) = //province[@id="prov-Spain-11"]]
          ]/located[@country="E"]
        return //located[
          id(@province) = //province[@id="prov-Spain-11"] and 
          not(. = $located_in_country_elems)
        ]
      returns an empty query


  Notes:
  - <population_growth/> ignoriert da fummelig zu rechnen
  - <infant_mortality/> ignoriert da fummelig zu rechnen
  - <gdp*/> ignoriert da fummelig zu rechnen
  - <unemployment*/> ignoriert da fummelig zu rechnen
  - <ethnicgroup/> ist weird (daher ignoriert) da nicht normalisiert
    - CAT hat 100% "Mediterranean Nordic"
    - E hat 0% "Mediterranean Nordic"
  - <religion/> ist weird (daher ignoriert) da nicht normalisiert
    - CAT hat 52.4% "Roman Catholoic"
    - E hat 0% "Roman Cathonic" (es ist dort "Catholic")
  - <language/> ist weird (daher ignoriert) da nicht normalisiert
    - "Occitan" existiert nicht bei "E", obwohl es in "CAT" ist
      (Das könnte Rundungsfehler sein, aber das tu ich mir nicht an)
-->

<!-- The new country, unprocessed -->
<xsl:variable name="data" select="doc('./catdata.xml')"/>

<!-- The original province that now became a country -->
<xsl:variable name="original_province"
  select="/*/country/province[./name = $data/*/name]" />

<xsl:variable name="original_country"
  select="/*/country[./province = $original_province]" />

<xsl:template match="@*|node()">
  <xsl:choose>
    <xsl:when test=". = $original_country">
      <xsl:call-template name="process_original_country"/>
    </xsl:when>
      <!-- we can't use idref because we dont have a dtd for catdata -->
      <xsl:when test="name() = 'country' and $data/*/border/@country = ./@car_code">
      <xsl:call-template name="update_borders"/>
    </xsl:when>
    <xsl:when 
      test="id(@country) = $original_country and ./located[id(@province) = $original_province]">
      <xsl:call-template name="change_located_container"/>
    </xsl:when>
    <xsl:otherwise>
      <!-- Identity template: copy all text nodes, elements and attributes -->   
      <xsl:copy>
        <xsl:apply-templates select="@*|node()" />
      </xsl:copy>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- the new country element -->
<xsl:variable name="new_country_data">
  <!-- The problem is as follows
    If I just add doc('./catdata.xml') as a variable, I can not easily
    add stuff INSIDE of it. So instead I manually decompose it into the
    outer tag (i.e. the <country> element) and its inner. Thus, I can
    put stuff within my bastarded <xsl:element>, which *looks* like
    the outer element of the doc()
  -->

    <xsl:variable name="outer_nodename" select="$data/*/name()"/>
    <xsl:variable name="outer_attrs" select="$data/*/@*"/>
    <xsl:variable name="inner_stuff" select="$data/*/*"/>

    <xsl:element name="{$outer_nodename}">
      <!-- Add back the original stuff -->
      <xsl:copy-of select="$outer_attrs"/>
      <xsl:copy-of select="$inner_stuff"/>

      <!-- Add stuff that was in the old province -->
      <xsl:copy-of select="$original_province/area" />
      <xsl:copy-of select="$original_province/population" />

      <!-- Add stuff from country above -->
      <!-- Theoretically doesnt work everytime, but good enough -->
      <xsl:copy-of select="$original_country/encompassed" />

      <!-- Add the cities, but replace the country and province -->
      <xsl:for-each select="//city[id(@province) = $original_province]">
        <city 
          id="{@id}"
          country="{$data/*/@car_code}"
        >
          <xsl:copy-of select="./*"/>
        </city>
      </xsl:for-each>
    </xsl:element>
</xsl:variable>

<xsl:template name="process_original_country">
  <!-- same trick: decompose -->
  <xsl:variable name="outer_nodename" select="./name()"/>
  <xsl:variable name="outer_attrs" select="./@*"/>
  <xsl:variable name="inner_stuff_without_border" select="./*[name() != 'border']"/>
  <xsl:variable name="old_borders" select="./border"/>
  <xsl:variable name="country_car_code" select="./@car_code"/>

  <xsl:element name="{$outer_nodename}">
    <!-- Add back the original stuff -->
    <xsl:copy-of select="$outer_attrs"/>
    <xsl:copy-of select="$inner_stuff_without_border"/>

    <!-- add a new population -->
    <xsl:variable name="newest_country_pop" select="./population[last()]"/>
    <xsl:variable name="province_pop" 
      select="$original_province/population[last()]"/>
    <xsl:variable name="new_pop" 
      select="number($newest_country_pop/text()) - number($province_pop/text())"/>
    <population measured="computed" year="{year-from-dateTime(current-dateTime())}">
      <xsl:copy select="$new_pop"/>
    </population>

    <!-- Fix borders -->
    <xsl:for-each select="$old_borders">
      <xsl:choose>
        <xsl:when test="id($new_country_data/*/border/@country) = id(./@country)">
          <xsl:variable name="current" select="."/>
          <xsl:variable name="new_border_length" select="number(@length) - number($new_country_data/*/border[@country = $current/@country]/@length)"/>
          <xsl:if test="not($new_border_length = 0)">
            <border country="{$current/@country}" length="{$new_border_length}"/>
          </xsl:if>
        </xsl:when>
        <xsl:otherwise>
          <xsl:copy-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>

    <!-- Add new border to new country -->
    <border country="{$new_country_data/*/@car_code/string()}"
    length="{$new_country_data/*/border[./@country = $country_car_code]/@length/string()}"/>
  </xsl:element>
</xsl:template>

<xsl:template name="update_borders">
  <xsl:variable name="current_country" select="."/>
  <!-- same trick: decompose -->
  <xsl:variable name="outer_nodename" select="./name()"/>
  <xsl:variable name="outer_attrs" select="./@*"/>
  <xsl:variable name="inner_stuff_without_country_border" 
    select="./*[not(@country = $original_country/@car_code)]"/>

  <xsl:element name="{$outer_nodename}">
    <!-- Add back the original stuff -->
    <xsl:copy-of select="$outer_attrs"/>
    <xsl:copy-of select="$inner_stuff_without_country_border"/>

    <xsl:variable name="new_border_length"
      select="number($new_country_data/*/border[./@country = $current_country/@car_code]/@length)"/>

    <!-- if the border to the old country still exist, add it -->
    <xsl:variable name="border_remainder"
    select="number(./border[@country = $original_country/@car_code]/@length) - $new_border_length" />
    <xsl:if test="not($border_remainder = 0)">
      <border country="{$original_country/@car_code}"
      length="{$border_remainder}"/>
    </xsl:if>

    <!-- Add border to catalonia -->
    <border country="{$new_country_data/*/@car_code/string()}"
      length="{$new_border_length}"/>
  </xsl:element>
</xsl:template>

<xsl:template name="change_located_container">
  <xsl:choose>
    <!-- is our province the only province in it? -->
    <xsl:when test="count(id(./located[id(@country) = $original_country]/@province)) = 1">
      <!-- 
        - Replace spain with catalonia on country list
        - Fully remove the <located> element with spain in it
        - Add the located in catalonia
      -->
      <!-- Good old decompose -->
      <xsl:variable name="outer_nodename" select="./name()"/>

      <xsl:variable name="outer_attrs_without_country" select="./@*[not(./string() = 'country')]"/>
      <xsl:variable name="all_countries_except_spain_but_with_catalonia" 
      select="string-join((./id(@country)[not(@car_code = $original_country/@car_code)] | $new_country_data/*)/@car_code, ' ')"/>

      <xsl:variable name="inner_stuff_without_located_spain"
      select="./*[not(name() = 'located' and id(@country) = $original_country)] "/>

      <xsl:element name="{$outer_nodename}">
        <xsl:copy-of select="$outer_attrs_without_country"/>
        <xsl:attribute name="country">
          <xsl:copy-of select="$all_countries_except_spain_but_with_catalonia"/>
        </xsl:attribute>

        <!-- recursive application -->
        <!-- TODO einziger Bug: Rekursion klappt nicht, grep nach Ebro -->
        <xsl:for-each select="$inner_stuff_without_located_spain">
          <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
          </xsl:copy>
        </xsl:for-each>

        <located country='{$new_country_data/*/@car_code/string()}'/>
      </xsl:element>

    </xsl:when>
    <xsl:otherwise>
      <!-- 
        - Add catalonia to the country list (let spain there as well)
        - Remove the province from spains <located>
        - Add the located in catalonia
      -->
      <xsl:variable name="outer_nodename" select="./name()"/>

      <xsl:variable name="outer_attrs_without_country" select="./@*[not(./string() = 'country')]"/>
      <xsl:variable name="all_countries_with_catalonia" 
      select="string-join((./id(@country) | $new_country_data/*)/@car_code, ' ')"/>
      <xsl:variable name="inner_stuff_without_located_spain"
      select="./*[not(name() = 'located' and id(@country) = $original_country)] "/>

      <xsl:element name="{$outer_nodename}">
        <xsl:copy-of select="$outer_attrs_without_country"/>
        <xsl:attribute name="country">
          <xsl:copy-of select="$all_countries_with_catalonia"/>
        </xsl:attribute>

        <!-- recursive application -->
        <!-- TODO einziger Bug: Rekursion klappt nicht, grep nach Ebro -->
        <xsl:for-each select="$inner_stuff_without_located_spain">
          <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
          </xsl:copy>
        </xsl:for-each>

        <!-- remove the province from spains located -->
        <xsl:variable name="located_spain_provinces"
        select="./located[id(./@country) = $original_country]"/>
        <xsl:variable name="all_provinces_without_catalonia"
        select="string-join($located_spain_provinces/id(@province)[not(. = $original_province)]/@id, ' ')"/>
        <located country='{$original_country/@car_code}'
        province="{$all_provinces_without_catalonia}"/>

        <located country='{$new_country_data/*/@car_code/string()}'/>
      </xsl:element>
    </xsl:otherwise>
  </xsl:choose>

</xsl:template>

<!-- Entry point -->
<xsl:template match="mondial">

  <!-- Add the catdata at the bottom -->
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
    <xsl:copy-of select="$new_country_data"/>
  </xsl:copy>
</xsl:template>

<!-- replace the original Catalonia with nothing -->
<xsl:template match="country/province[. = $original_province]"/>

</xsl:stylesheet>
