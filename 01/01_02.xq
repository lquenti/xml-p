(:
a) State an XQuery query that returns for every country its radius; in 
descending order.

Again, use the last() name to get the English country name

TODO try me out, takes too long to evaluate online...

For what it can be useful:
A lower bound on the minimal distance for being conquered?
As conquering meant getting the capital in old days?
:)
declare namespace math="http://www.w3.org/2005/xpath-functions/math";

declare function local:distance($lat1, $lon1, $lat2, $lon2) as xs:double {
  let $deg2rad := (math:pi() div 180)
  return
  6370 * math:acos(
    math:cos($lat1 * $deg2rad) *
    math:cos($lat2 * $deg2rad) *
    math:cos(($lon1 - $lon2) * $deg2rad) + 
    math:sin($lat1 * $deg2rad) * 
    math:sin($lat2 * $deg2rad)
  )
};
for $c in /*/country
let $capital := $c/id(@capital),
    $cap_lat := $capital/latitude/text(),
    $cap_lon := $capital/longitude/text(),
    $radius := max(
      for $thing in //*[id(@country) = $c and latitude and longitude]
      return local:distance(
        $cap_lat,
        $cap_lon,
        $thing/latitude,
        $thing/longitude
      )
    )
order by $radius descending
return 
<country 
  name="{$c/name[last()]}"
  radius="{$radius}"
/>
