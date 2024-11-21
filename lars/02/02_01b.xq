(: Call with:
saxonXQ lars/02/02_01b.xq | xq
:)

(: find all that are missing in my xml :)
let $mondial := doc("../../mondial.xml"),
    $my_xml := doc("./02_01a.xml")
for $water in $mondial/*/(sea|river|lake)
where not($water/name = $my_xml//name)
return $water

(: Missing: Those that end in a lake I think :)
