<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    
    <!-- Copy all nodes and attributes by default -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- Special handling for country names -->
    <xsl:template match="/mondial/country/name">
        <name>
            <xsl:value-of select="concat(., ' (', count(preceding-sibling::country), ')')"/>
        </name>
    </xsl:template>
    
</xsl:stylesheet> 