(: FINALLY
Compute all pairs (country, organization) s.t. the country belongs to the 
organization, but all its neighbors do not belong to the organization.
:)
for $o in /*/organization,
    $c in $o/members[@type="member"]/id(@country)
where
  every $n in $c/border/id(@country)
  satisfies not($n=$o/members[@type="member"]/id(@country))
return <result c="{$c/name[last()]/text()}" o="{$o/name[last()]}"/>
