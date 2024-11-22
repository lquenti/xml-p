for $x in fn:distinct-values(//waters/sea[name = "Black Sea"]//country/text())
return <country name="{$x}"/>
