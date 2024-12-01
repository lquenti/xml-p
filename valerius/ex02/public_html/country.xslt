<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="country">
        <html>
            <head>
                <title><xsl:value-of select="name"/> (<xsl:value-of select="@car_code"/>)</title>
            </head>
            <body>
                <h1><xsl:value-of select="name"/> (<xsl:value-of select="@car_code"/>)</h1>
                
                <!-- Basic Information -->
                <h2>Basic Information</h2>
                <ul>
                    <li>Local Name: <xsl:value-of select="localname"/></li>
                    <li>Area: <xsl:value-of select="@area"/> km²</li>
                    <li>Capital: <xsl:value-of select="//city[@id=current()/@capital]/name[1]"/></li>
                    <li>Population: <xsl:value-of select="population[last()]"/> 
                        (<xsl:value-of select="population[last()]/@year"/>)</li>
                </ul>

                <!-- Demographics -->
                <h2>Demographics</h2>
                <h3>Ethnic Groups</h3>
                <ul>
                    <xsl:for-each select="ethnicgroup">
                        <li><xsl:value-of select="@percentage"/>% <xsl:value-of select="text()"/></li>
                    </xsl:for-each>
                </ul>

                <h3>Languages</h3>
                <ul>
                    <xsl:for-each select="language">
                        <li><xsl:value-of select="@percentage"/>% <xsl:value-of select="text()"/></li>
                    </xsl:for-each>
                </ul>

                <!-- Wikipedia Link -->
                <p>
                    <a href="https://en.wikipedia.org/wiki/{name}">
                        Visit Wikipedia page for <xsl:value-of select="name"/>
                    </a>
                </p>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet> 