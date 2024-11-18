(:
Compute all pairs (country, organization) s.t. the country does not belong to the organization,
but all its neighbors belong to the organization.
:)
for $o in /*/organization,
    $c in /*/country
let
  $orga_members := $o/members/id(@country)
where
  every $n in $c/border/id(@country)
  satisfies $n=$orga_members and
  not($c = $orga_members)
return <result c="{$c/name[last()]/text()}" o="{$o/name[last()]}"/>
