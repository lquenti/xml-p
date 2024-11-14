(:
Claim: Most 13th’s of a month are fridays (counting since the introduction of the Gregorian Calendar by jumping from October 4th, 1582 directly to October 15th, 1582).
Refute or prove this claim using XQuery programming.

After thinking a bit, this is my approach:
1. start at 15.10.1582
2. We know this was a Thursday, i.e. 16.10.1582 was a Friday
2. Go through each day, check if its a 13., if not go to next
3. If it is a 13., check its weekday by mod 16.10.1582

See: http://www.datypic.com/books/xquery/chapter20.html
:)

declare function local:generate_until($d as xs:date, $end as xs:date) as xs:date* {
  if ($d >= $end) then
    ()
  else if (day-from-date($d) = 13) then 
    ($d, local:generate_until($d + xs:dayTimeDuration("P28D"), $end))
  else 
    local:generate_until($d + xs:dayTimeDuration("P1D"), $end)
};

(: We have to split it due to stack overflow :)
let $some_friday := xs:date("1582-10-16"),
    $seventeenth_century := local:generate_until(xs:date("1582-10-16"), xs:date("1600-01-01")),
    $eighteenth_century := local:generate_until(xs:date("1600-01-01"), xs:date("1700-01-01")),
    $nineteenth_century := local:generate_until(xs:date("1800-01-01"), xs:date("1900-01-01")),
    $twentieth_century := local:generate_until(xs:date("1900-01-01"), xs:date("2000-01-01")),
    $twentyfirst_century := local:generate_until(xs:date("2000-01-01"), current-date()),
    $all_thirteen := ($seventeenth_century, $eighteenth_century, $nineteenth_century, $twentieth_century, $twentyfirst_century)
for $t in $all_thirteen
return 
  ($t, <result day="{$t-$some_friday}" />)
