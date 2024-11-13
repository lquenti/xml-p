for $o in /*/organization,
    $member_without_neighbouring_members in (
  (: for each member :)
  for $c in $o/members[@type="member"]/id(@country)
  where (
    (: check that all neighbours are not :)
    every $neighbour in $c/border/id(country)
    satisfies not (
      (: the member of the organization :)
      every $c2 in $o/members[@type="member"]/id(@country)
      satisfies $neighbour != $c2
    )
  )
  return $c
)
return <debug c="{$member_without_neighbouring_members/name[last()]}" o="{$o/name[last()]}" />

(: TODO replace debug :)

