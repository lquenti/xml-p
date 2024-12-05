<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!--
Write an XSLT stylesheet that creates a LATEX file hamlet.tex from hamlet.xml:
- Act headers and Scene headers as section/subsections,
- Act and Scene intros, and stage directives as quote environments,
- Speeches as items in a description environment, with the name of the person 
  as item label,
- XSL programming: for all mentions of country names and city names that exist
  in Mondial, print these names in red

Notes:
  - There are no ACT intros since this is empty
    /PLAY/ACT/*[local-name() != "SCENE" and local-name() != "TITLE"]
  - Analagously:
    /PLAY/ACT/SCENE/*[
      local-name() != "TITLE" and
      local-name() != "STAGEDIR" and
      local-name() != "SPEECH"
    ]
  - I know that it doesn't work with multi-word names such as "Las Vegas" but
    this is the conceptually cleaner approach.

xQuery: doc()
-->


<xsl:output method="text" indent="no" />

<xsl:template match="PLAY">
  <xsl:variable name="mondial_names" 
  select="doc('../../mondial.xml')/*/country/(. | ./city | ./province/city)/name/text()" />

  <!-- <xsl:variable name="" select="doc(mondial)//city|country" /> -->
\documentclass{article}
\usepackage{xcolor}
\begin{document}
  <xsl:for-each select="./ACT">
\section{
<xsl:call-template name="check_against_mondial">
  <xsl:with-param name="mondial_names" select="$mondial_names" />
  <xsl:with-param name="text" select="TITLE" />
</xsl:call-template>
}
    <xsl:for-each select="./SCENE">
      <xsl:for-each select="./*">
        <!-- either tite, stagedir, or speech, see notes -->
        <xsl:choose>
          <xsl:when test='local-name() = "TITLE"'>
\subsection{
<xsl:call-template name="check_against_mondial">
  <xsl:with-param name="mondial_names" select="$mondial_names" />
  <xsl:with-param name="text" select="." />
</xsl:call-template>
}
          </xsl:when>
          <xsl:when test='local-name() = "STAGEDIR"'>
\begin{quote}
<xsl:call-template name="check_against_mondial">
  <xsl:with-param name="mondial_names" select="$mondial_names" />
  <xsl:with-param name="text" select="." />
</xsl:call-template>
\end{quote}
          </xsl:when>
          <xsl:otherwise> <!-- local-name() = "Speech" -->
            <xsl:variable name="speaker" select="./SPEAKER/text()"/>
\begin{description}
            <xsl:for-each select="./LINE">
\item[<xsl:value-of select="$speaker"/>] 
<xsl:call-template name="check_against_mondial">
  <xsl:with-param name="mondial_names" select="$mondial_names" />
  <xsl:with-param name="text" select="." />
</xsl:call-template>
            </xsl:for-each>
\end{description}
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:for-each>
\end{document}
</xsl:template>

<xsl:template name="check_against_mondial">
  <xsl:param name="mondial_names" />
  <xsl:param name="text" />
  <xsl:for-each select="tokenize($text, ' ')">
    <xsl:choose>
      <xsl:when test=". = $mondial_names">\textcolor{red}{<xsl:value-of select="."/>} &#160;</xsl:when>
      <xsl:otherwise><xsl:value-of select="."/> &#160;</xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
