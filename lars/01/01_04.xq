(:
Give the lowest mountain which is the highest one on its continent
:)
(
  for $cont in /*/continent
  let $highest_mountain := (
    for $m in /*/mountain
    let $max_elev := max(
      for $m2 in /*/mountain
      where $m2/id(@country)/encompassed/id(@continent) = $cont
      return $m2/elevation
    )
    where $m/id(@country)/encompassed/id(@continent) = $cont and
      $m/elevation = $max_elev
    return $m
  )
  order by $highest_mountain/elevation descending
  return $highest_mountain
)[last()]
