Steps:
1. copy `dynamic-country.xsl` & `generate-pages.xsl` to `~/public_html/`
2. add the mondial files (`mondial.dtd`  `mondial.xml`  `mondial.xsd`) to `~/public_html/`
3. Download Saxon and unzip it to ~/public_html/Saxon`
4. Create all static sites: `java -cp Saxon/saxon-he-12.5.jar net.sf.saxon.Transform -xsl:generate-pages.xsl -s:mondial.xml`
5. Dynamic sites are not working correctly for every browser I've tested. Therefore the current population is static.