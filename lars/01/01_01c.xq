(: 
c) Give the names of all cities that have more than 1000000 inhabitants and 
where some organization has its headquarter.

I interpret "have more than 1'000'000 inhabitants" as in "have more than 1mil 
IN THE LAST CENSUS".
Also I prefer the English name, which seems to be the last one.
:)
/*/country//city[
  population[last()]/text() > 1000000 and
  . = /*/organization/id(@headq)
]/name[last()]/text()

(: 
Altenative solution: Go through the origanizations
/*/organization/id(@headq)[population[last()]>1000000]/name[last()]/text()
:)
