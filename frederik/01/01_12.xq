(:<result>
   <total-fridays>1043</total-fridays>
   <total-thirteens>5307</total-thirteens>
   <proportion>20%</proportion>
</result>:)

declare namespace math="http://www.w3.org/2005/xpath-functions/math";

declare function local:day-of-week($year as xs:integer, $month as xs:integer, $day as xs:integer) as xs:string {
    let $d := (if ($month < 3) then ($year - 1) else $year)
    let $m := (if ($month < 3) then ($month + 12) else $month)
    let $k := $d mod 100
    let $j := floor($d div 100)
    let $dayOfWeek := (1 + floor(13 * ($m + 1) div 5) + $k + floor($k div 4) + floor($j div 4) - 2 * $j) mod 7
    return (if ($dayOfWeek = 0) then "Saturday" 
            else if ($dayOfWeek = 1) then "Sunday" 
            else if ($dayOfWeek = 2) then "Monday" 
            else if ($dayOfWeek = 3) then "Tuesday" 
            else if ($dayOfWeek = 4) then "Wednesday" 
            else if ($dayOfWeek = 5) then "Thursday" 
            else "Friday")
};

let $startYear := 1582
let $startMonth := 10
let $endYear := 2024
let $fridayCount := 
    for $year in ($startYear to $endYear)
    let $months := if ($year = $startYear) then ($startMonth to 12) else (1 to 12)
    return 
        sum(
            for $month in $months
            return
                let $day := 13
                let $dow := local:day-of-week($year, $month, $day)
                return if ($dow = "Friday") then 1 else 0
        )

let $totalCount := 
    for $year in ($startYear to $endYear)
    let $months := if ($year = $startYear) then ($startMonth to 12) else (1 to 12)
    return 
        count($months)

let $totalFridays := sum($fridayCount)
let $totalThirteens := sum($totalCount)

return 
    <result>
        <total-fridays>{$totalFridays}</total-fridays>
        <total-thirteens>{$totalThirteens}</total-thirteens>
        <proportion>{round($totalFridays div $totalThirteens * 100)}%</proportion>
    </result>