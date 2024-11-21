All commands:
```
saxonXSL -s":./mondial.xml" -xsl":./lars/02/02_01a.xsl" -o":./lars/02/02_01a.xml"
saxonXQ lars/02/02_01b.xq | xq
saxonXSL -s":./mondial.xml" -xsl":./lars/02/02_01b.xsl" -o":./lars/02/02_01b.xml"
saxonXSL -s":./lars/02/02_01b.xml" -xsl":./lars/02/02_01c.xsl" -o":./lars/02/02_01c.xml"
```
