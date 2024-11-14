(: 
For each island that is shared between two or more countries, give the name, the area, and the number of countries.
:)
for $i in /*/island
let $c_on_i := $i/id(@country)
where count($c_on_i) > 1
order by $i/name/text()
return <island name="{$i/name[last()]/text()}" 
               count="{count($c_on_i)}"
               area="{$i/area/text()}"
       >
  {
    for $c in $c_on_i return <country 
      name="{$c/name[last()]/text()}"
      area="{$c/@area}"
    /> 
  }
</island>
