for $sea in /waters/sea[
  name="Mediterranean Sea" or name="Black Sea" or name="North Sea"
]
return 
<sea name="{$sea}">
  {
    for $prov in $sea//province
    return <province name="{$prov/text()}"/>
  }
</sea>
