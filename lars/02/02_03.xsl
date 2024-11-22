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

- TODO: Wie kann ich in XSL ne 2. Datei einbinden?

-->


<xsl:output method="text" indent="no" />

<xsl:template match="PLAY">
\documentclass{article}
\begin{document}
  <xsl:for-each select="./ACT">
\section{<xsl:value-of select="TITLE"/>}
    <xsl:for-each select="./SCENE">
      <xsl:for-each select="./*">
        <!-- either tite, stagedir, or speech, see notes -->
        <xsl:choose>
          <xsl:when test='local-name() = "TITLE"'>
\subsection{<xsl:value-of select="."/>}
          </xsl:when>
          <xsl:when test='local-name() = "STAGEDIR"'>
\begin{quote}
<xsl:value-of select="."/>
\end{quote}
          </xsl:when>
          <xsl:otherwise> <!-- local-name() = "Speech" -->
            <xsl:variable name="speaker" select="./SPEAKER/text()"/>
\begin{description}
            <xsl:for-each select="./LINE">
\item[<xsl:value-of select="$speaker"/>] <xsl:value-of select="."/>
            </xsl:for-each>
\end{description}
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:for-each>
\end{document}
</xsl:template>

</xsl:stylesheet>
