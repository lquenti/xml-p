(: 
e) Give the names of all cities where an organization has its headquarter, and 
which are the capital of a member country of this organization.
:)

/*/organization[
  @headq = members[@type="member"]/id(@country)/@capital
]/id(@headq)/name[last()]/text()
