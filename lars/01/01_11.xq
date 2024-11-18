(:
Write a recursive function that, given a river name, computes the total length of a river system of that river (i.e., its direct and indirect tributaries). Consider that rivers may flow into or through lakes.

Note that we need to have a context item because without we get
net.sf.saxon.trans.XPathException: Leading '/' selects nothing: the context item is absent

See: https://stackoverflow.com/questions/41701877/xpty0002-contextitem

Also has to be optional, otherwise
net.sf.saxon.trans.UncheckedXPathException: An empty sequence is not allowed as the result of a call to local:river_len
:)

(: Assuming that the river exists :)
declare function local:river_len($ctx as node(), $river_name as xs:string) as xs:decimal? {
  (: first find the river :)
  let $river := $ctx//*[name/text() = $river_name]
  return xs:decimal(number(if ($river/length) then number($river/length) else 0)) + sum(
    (: They could branch, I think rivers get bigger over time :)
    for $from in $ctx//*[to/id(@water) = $river]/name[last()]/text()
    return local:river_len($ctx, $from)
  )
};

(: Output for every river the total length of its network, in descending order :)

for $river in //river
let $len := local:river_len(/mondial, $river/name[last()]/text())
order by $len descending
return <river name="{$river/name/text()}" len="{$len}" />

