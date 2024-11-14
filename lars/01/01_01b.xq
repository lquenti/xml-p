(: 
b) Give the names of all countries where no organization has its headquarter.


as in a), I do not trust `//`

Note that `!=` would result in "for all not" not "not for all" 
:)

/*/country[not(
  city = /*/organization/id(@headq) or 
  province/city = /*/organization/id(@headq)
)]/name/text()
