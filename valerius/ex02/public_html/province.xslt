<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- Add parameters for country and province IDs -->
    <xsl:param name="country_code"/>
    <xsl:param name="province_id"/>
    
    <!-- Match root element -->
    <xsl:template match="/">
        <!-- Only process the province with matching ID -->
        <xsl:apply-templates select="//province[@id=$province_id]"/>
    </xsl:template>

    <xsl:template match="province">
        <html>
            <head>
                <title><xsl:value-of select="name"/> - <xsl:value-of select="//country[@car_code=$country_code]/name"/></title>
            </head>
            <body>
                <a href="../index.html">Back to Country</a>
                <h1><xsl:value-of select="name"/></h1>

                <!-- Basic Information -->
                <h2>Basic Information</h2>
                <ul>
                    <li>Area: <xsl:value-of select="area"/> km²</li>
                    <li>Capital: <xsl:value-of select="//city[@id=current()/@capital]/name[1]"/></li>
                    <li>Population: <xsl:value-of select="population[last()]"/> 
                        (<xsl:value-of select="population[last()]/@year"/>)</li>
                </ul>

                <!-- Cities -->
                <h2>Cities</h2>
                <ul>
                    <xsl:for-each select="city">
                        <li>
                            <a href="{@id}/index.html">
                                <xsl:value-of select="name[1]"/>
                                <xsl:if test="population">
                                    - Population: <xsl:value-of select="population[last()]"/>
                                    (<xsl:value-of select="population[last()]/@year"/>)
                                </xsl:if>
                            </a>
                        </li>
                    </xsl:for-each>
                </ul>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet> 