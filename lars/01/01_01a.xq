(: 
a) Give the names of all countries where some organization has its headquarter.

This could also be done with `.//city`, but this is imo clearer that we do not 
pick up anything accidentally.
:)
/mondial/country[
  city = /mondial/organization/id(@headq) or 
  province/city = /mondial/organization/id(@headq)
]/name[last()]/text()
