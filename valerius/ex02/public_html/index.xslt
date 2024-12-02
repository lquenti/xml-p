<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/mondial">
        <html>
            <head>
                <title>Countries</title>
            </head>
            <body>
                <h1>Countries</h1>
                <ul>
                    <xsl:for-each select="//country">
                        <li>
                            <a href="/countries/{@car_code}/index.html">
                                <xsl:value-of select="name"/> (<xsl:value-of select="@car_code"/>)
                            </a>
                        </li>
                    </xsl:for-each>
                </ul>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>