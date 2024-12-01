<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- Add parameters for country code and city ID -->
    <xsl:param name="country_code"/>
    <xsl:param name="city_id"/>
    
    <!-- Match root element -->
    <xsl:template match="/">
        <!-- Only process the city with matching ID -->
        <xsl:apply-templates select="//city[@id=$city_id]"/>
    </xsl:template>

    <xsl:template match="city">
        <html>
            <head>
                <title><xsl:value-of select="name[1]"/> - <xsl:value-of select="//country[@car_code=$country_code]/name"/></title>
            </head>
            <body>
                <a href="../index.html">Back to Province</a>
                <h1><xsl:value-of select="name[1]"/></h1>

                <!-- Basic Information -->
                <h2>Basic Information</h2>
                <ul>
                    <li>Latitude: <xsl:value-of select="latitude"/>°</li>
                    <li>Longitude: <xsl:value-of select="longitude"/>°</li>
                    <xsl:if test="elevation">
                        <li>Elevation: <xsl:value-of select="elevation"/> m</li>
                    </xsl:if>
                    <li>Population: <xsl:value-of select="population[last()]"/> 
                        (<xsl:value-of select="population[last()]/@year"/>)</li>
                </ul>

                <!-- Geographic Features -->
                <xsl:if test="located_at">
                    <h2>Geographic Features</h2>
                    <ul>
                        <xsl:for-each select="located_at">
                            <li>Located at <xsl:value-of select="@watertype"/>: 
                                <xsl:value-of select="@sea|@lake|@river"/>
                            </li>
                        </xsl:for-each>
                    </ul>
                </xsl:if>

                <!-- Population History -->
                <h2>Population History</h2>
                <ul>
                    <xsl:for-each select="population">
                        <li><xsl:value-of select="@year"/>: <xsl:value-of select="text()"/>
                            <xsl:if test="@measured"> (<xsl:value-of select="@measured"/>)</xsl:if>
                        </li>
                    </xsl:for-each>
                </ul>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet> 