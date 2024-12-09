(: To validate your result first, list all countries (ordered descending by area) that are located to
   more than 90% on islands. :)

for $country in //country

let $general_island_area := 
    sum(
        //island[
            @country = $country/@car_code and 
            count(id(@country)) = 1
        ]/area/text()
    )

let $islands := //island[./id(@country) = $country]

let $shared_island_parts :=
    (:
    for $island in $islands
    let $shared_percentage := xs:decimal($island/owned_by[id(@country) = $country]/@area/string()) div 100
    return $island/area/text() * $shared_percentage
    :)
    for $island in $islands
    return xs:decimal($island/owned_by[id(@country) = $country]/@area/string()) (:div $island/area:)

let $shared_island_area := sum($shared_island_parts)
let $country_island_area := sum($general_island_area) + sum($shared_island_area)

where $country_island_area >= 0.9 * xs:decimal($country/@area/string())
order by $country/number(@area) descending

return (<country show_island_area="1">
        name = {$country/name/text()}
        area = {$country/@area/string()}
        island_area = {$general_island_area}
        country_island_area = {$country_island_area}
        shared_island_area = {$shared_island_area}
        </country>,'')
