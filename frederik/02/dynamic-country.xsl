<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:math="http://www.w3.org/2005/xpath-functions/math"
    version="2.0">
    <xsl:template match="/country">
        <html>
        <head>
            <title><xsl:value-of select="name"/></title>
        </head>
        <body>
            <h1>Country: <xsl:value-of select="name"/></h1>
            <p>Local Name: <xsl:value-of select="localname"/></p>
            <p>Last Population: <xsl:value-of select="last_population"/></p>
            <p>Capital: <xsl:value-of select="capital"/></p>

            <xsl:variable name="last_population" select="last_population"/>
            <xsl:variable name="growth_rate" select="growth_rate"/>
            <xsl:variable name="last_year" select="last_population_year"/>
            <xsl:variable name="current_year" select="2024"/>
            <xsl:variable name="years_elapsed" select="$current_year - $last_year"/>
            <!-- math:pow is not working in Firefox -->
            <!--xsl:variable name="current_population" select="$last_population * math:pow(1 + $growth_rate div 100, $years_elapsed)"/-->
            <xsl:variable name="current_population" select="$last_population"/>
            <p>Estimated Current Population: 
                <xsl:value-of select="format-number($current_population, '#,###')"/>
            </p>
            <h2>Provinces</h2>
            <ul>
                <xsl:for-each select="provinces/province">
                    <li>
                        <xsl:value-of select="name"/>
                    </li>
                </xsl:for-each>
            </ul>
            <h2>Cities</h2>
            <ul>
                <xsl:for-each select="cities/city">
                    <li>
                        <xsl:value-of select="name"/> - Population: <xsl:value-of select="population"/>
                    </li>
                </xsl:for-each>
            </ul>
        </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
