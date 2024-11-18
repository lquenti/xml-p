(:
Give for each country the sum of the population of its neighbors (in descending order, with those
countries with no neighbors coming last)
:)
for $c in /*/country
let $neighbourpop := sum($c/border/id(@country)/population[last()])
order by $neighbourpop descending
return <country
  name="{$c/name[last()]}"
  neighbour_population="{$neighbourpop}"
/>
