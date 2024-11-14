(:
List the names of all organizations with at least one member country on each continent
:)
for $o in /*/organization
where (
  every $cont in /*/continent
  satisfies 
    $o/members[@type="member"]/id(@country)/encompassed/id(@continent) = $cont
)
return $o/name
