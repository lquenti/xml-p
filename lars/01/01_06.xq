(:
Give the smallest (wrt. number of members) organization O_1 which is not
covered by any other organization O_2 (i.e. for all other organizations O_2,
O_1 has at least one member which is not a member of O_2)
:)
for $o1 in /*/organization
let $every_except_o1 := (for $o3 in /*/organization where $o3 != $o1 return $o3)
where (
  (: for every other organization :)
  every $o2 in $every_except_o1
  satisfies (
    (: there is some country :)
    some $c1 in $o1/members[@type="member"]/id(@country)
    satisfies (
      (: that is not in our organization :)
      every $c2 in $o2/members[@type="member"]/id(@country)
      satisfies $c1 != $c2
    )
  )
)
return $o1/name
