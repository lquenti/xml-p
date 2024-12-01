<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="2.0">

<!-- Define HTML output format -->
<xsl:output method="html" indent="yes" name="html" encoding="UTF-8"/>

<!-- Root template: Iterate over all countries -->
<xsl:template match="/">
    <xsl:for-each select="//country">
        <xsl:call-template name="country">
            <xsl:with-param name="entity" select="."/>
        </xsl:call-template>
    </xsl:for-each>
</xsl:template>

<!-- Define base URL for resources -->
<xsl:variable name="base_url" select="concat('', 'mondial/')"/>

<!-- Template to handle individual countries -->
<xsl:template name="country">
    <xsl:param name="entity"/>
    
    <!-- Paths and file locations -->
    <xsl:variable name="c_path" select="$entity/@car_code"/>
    <xsl:variable name="countryfile" select="concat($base_url, $c_path, '/index.html')"/>
    <xsl:variable name="capital_id" select="$entity/@capital"/>
    <xsl:variable name="capital_province_id" select="$entity/id(@capital)/id(@province)/@id"/>
    <xsl:variable name="capital_path" select="concat($capital_province_id, '/', $capital_id)"/>
    
    <!-- Generate country-specific HTML -->
    <xsl:result-document href="{$countryfile}" format="html">
        <html>
        <head>
            <title>Country: <xsl:value-of select="$entity/name[1]"/></title>
            <style>
                table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
                th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                th { background-color: #00ffff; }
                tr:nth-child(even) { background-color: #f9f9f9; }
            </style>
        </head>
        <body>
            <!-- Country details table -->
            <h1>Country Details</h1>
            <table>
                <tr>
                    <th>Country Name</th>
                    <th>Local Name</th>
                    <th>Last Population</th>
                    <th>Size (kmÃ‚Â²)</th>
                    <th>Capital</th>
                </tr>
                <tr>
                    <td><xsl:value-of select="$entity/name[1]/text()"/></td>
                    <td><xsl:value-of select="$entity/localname[1]/text()"/></td>
                    <td><xsl:value-of select="$entity/population[last()]/text()"/></td>
                    <td><xsl:value-of select="$entity/@area"/> kmÃ‚Â²</td>
                    <td>
                        <a href="{$capital_path}">
                            <strong>
                                <xsl:value-of select="$entity/id(@capital)/name[1]/text()"/>
                            </strong>
                        </a>
                    </td>
                </tr>
            </table>
            
            <!-- Provinces table -->
            <h2>Provinces</h2>
            <table>
                <tr>
                    <th>Province Name</th>
                </tr>
                <xsl:for-each select="province">
                    <xsl:variable name="full_url" select="concat('', ./@id)"/>
                    <tr>
                        <xsl:call-template name="province">
						    <xsl:with-param name="path" select="$c_path"/>
						    <xsl:with-param name="entity2" select="."/>
					    </xsl:call-template>
                        <td>
                            <a href="{$full_url}">
                                <xsl:value-of select="./name[1]/text()"/>
                            </a>
                        </td>
                    </tr>
                </xsl:for-each>
            </table>

            <!-- Cities table -->
            <h2>Cities</h2>
            <table>
                <tr>
                    <th>City Name</th>
                </tr>
                <xsl:for-each select=".//city">
                    <xsl:variable name="province_id" select="concat(./@province, '/')"/>
                    <xsl:variable name="citylink" select="concat($province_id, ./@id)"/>
                    <tr>
                        <td>
                            <a href="{$citylink}">
                                <xsl:value-of select="./name[1]/text()"/>
                            </a>
                        </td>
                    </tr>
                </xsl:for-each>
            </table>
        </body>
        </html>
    </xsl:result-document>

    <!-- Dynamic Site -->
    <xsl:variable name="countryxml" select="concat($base_url, $c_path, '.xml')" />
    <xsl:result-document href="{$countryxml}"  format="html">
        <xsl:processing-instruction name="xml-stylesheet">
            <xsl:text>type="text/xsl" href="../dynamic-country.xsl"?</xsl:text>
        </xsl:processing-instruction>
        <country>
            <name><xsl:value-of select="$entity/name[1]"/></name>
            <localname><xsl:value-of select="$entity/localname[1]"/></localname>
            <last_population><xsl:value-of select="$entity/population[last()]"/></last_population>
            <growth_rate><xsl:value-of select="($entity/@population_growth,1)[1]"/></growth_rate>
            <last_population_year>
                <xsl:value-of select="$entity/population[last()]/@year"/>
            </last_population_year>
            <capital>
                <xsl:value-of select="$entity/id(@capital)/name[1]"/>
            </capital>
            <provinces>
                <xsl:for-each select="province">
                    <province>
                        <name><xsl:value-of select="name[1]"/></name>
                    </province>
                </xsl:for-each>
            </provinces>
            <cities>
                <xsl:for-each select=".//city">
                    <city>
                        <name><xsl:value-of select="name[1]"/></name>
                        <population><xsl:value-of select="population[last()]"/></population>
                    </city>
                </xsl:for-each>
            </cities>
        </country>
    </xsl:result-document>
</xsl:template>

<xsl:template name="province">
    <xsl:param name="path"/>
    <xsl:param name="entity2"/>
    
    <!-- Define the output path for the province file -->
    <xsl:variable name="p_path" select="concat($path, '/', $entity2/@id)"/>
    <xsl:variable name="provincefile" select="concat($base_url, $p_path, '/index.html')"/>

    <!-- Generate HTML for the province -->
    <xsl:result-document href="{$provincefile}" format="html">
        <html>
        <head>
            <title>Province: <xsl:value-of select="$entity2/name[1]"/></title>
            <style>
                table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
                th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                th { background-color: #00ffff; }
                tr:nth-child(even) { background-color: #f9f9f9; }
            </style>
        </head>
        <body>
            <h1>Province Details</h1>
            <table>
                <tr>
                    <th>Province Name</th>
                    <th>Local Name</th>
                    <th>Last Population</th>
                    <th>Size (kmÃ‚Â²)</th>
                </tr>
                <tr>
                    <td><xsl:value-of select="$entity2/name[1]/text()"/></td>
                    <td><xsl:value-of select="$entity2/localname[1]/text()"/></td>
                    <td><xsl:value-of select="$entity2/population[last()]/text()"/></td>
                    <td><xsl:value-of select="$entity2/area"/> kmÃ‚Â²</td>
                </tr>
            </table>

            <!-- Cities in the province -->
            <h2>Cities in Province</h2>
            <table>
                <tr>
                    <th>City Name</th>
                </tr>
                <xsl:for-each select="city">
                    <tr>
                        <xsl:call-template name="city">
                            <xsl:with-param name="path2" select="$p_path"/>
                            <xsl:with-param name="entity3" select="."/>
                        </xsl:call-template>
                        <xsl:variable name="citylink" select="concat('', ./@id)"/>	
                        <td>
                            <a href="{$citylink}">
                                <xsl:value-of select="./name[1]/text()"/>
                            </a>
                        </td>
                    </tr>
                </xsl:for-each>
            </table>
        </body>
        </html>
    </xsl:result-document>
</xsl:template>

<xsl:template name="city">
    <xsl:param name="path2"/>
    <xsl:param name="entity3"/>

    <!-- Define the output path for the city file -->
    <xsl:variable name="city_path" select="concat($path2, '/', $entity3/@id)"/>
    <xsl:variable name="cityfile" select="concat($base_url, $city_path, '/index.html')"/>

    <!-- Generate HTML for the city -->
    <xsl:result-document href="{$cityfile}" format="html">
        <html>
        <head>
            <title>City: <xsl:value-of select="$entity3/name[1]"/></title>
            <style>
                table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
                th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                th { background-color: #00ffff; }
                tr:nth-child(even) { background-color: #f9f9f9; }
            </style>
        </head>
        <body>
            <h1>City Details</h1>
            <table>
                <tr>
                    <th>City Name</th>
                    <th>Last Population</th>
                </tr>
                <tr>
                    <td><xsl:value-of select="$entity3/name[1]/text()"/></td>
                    <td><xsl:value-of select="$entity3/population[last()]"/></td>
                </tr>
            </table>

            <!-- Headquarter of organizations -->
            <h2>Headquarter of</h2>
            <table>
                <tr>
                    <th>Organization Name</th>
                </tr>
                <xsl:for-each select="//organization[$entity3/id(@country)/id(@memberships) = . and id(@headq) = $entity3]">
                    <tr>
                        <td>
                            <xsl:value-of select="./name[1]/text()"/>
                        </td>
                    </tr>
                </xsl:for-each>
            </table>

            <!-- Relations to province and country -->
            <h2>City Relation</h2>
            <table>
                <tr>
                    <th>City</th>
                    <th>Province</th>
                    <th>Country</th>
                </tr>
                <tr>
                    <td><xsl:value-of select="$entity3/name[1]/text()"/></td>
                    <td>
                        <xsl:if test="@province">
                            <a href="../">
                                <xsl:value-of select="id(@province)/name[1]/text()"/>
                            </a>
                        </xsl:if>
                    </td>
                    <td>
                        <a href="../../">
                            <xsl:value-of select="id(@country)/name[1]/text()"/>
                        </a>
                    </td>
                </tr>
            </table>
        </body>
        </html>
    </xsl:result-document>
</xsl:template>
</xsl:stylesheet>
