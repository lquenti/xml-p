(: 
Give the names and longitude of all cities where on the 21st of September the sun raises later, but less than 10 minutes later than in Berlin.


According to
https://www.timeanddate.com/sun/germany/berlin?month=9&year=2024
the sun rises at the 21.09. at 06:51. 
I haven't found a formula, so I did "binary search on cities" to find the result.

As we know:
"Im Osten geht die Sonne auf, im Süden nimmt sie ihren Lauf, im Westen wird sie untergeh'n, im Norden ist sie nie zu seh'n"
Thus, it has to have a longitude smaller.

- Hannover was too extreme with 07:05
- Wolfsburg was perfect with 07:01 according to
https://www.timeanddate.com/sun/germany/wolfsburg?month=9&year=2024
which has a longitude of 10.780420

(as a debug, we sort them so that wolfsburg should be last :D)

Also, after a lot of debugging, I found out to number cast everything...
:)
for $c in /*/country//city
where (number($c/longitude)) < number(//city[name="Berlin"]/longitude)
    and (number($c/longitude)) >= 10.780420
order by $c/longitude descending
return <result c="{$c/name[last()]}" l="{$c/longitude}" />
