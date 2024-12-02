for $p in fn:distinct-values(/waters/sea[
    name="Mediterranean Sea" or name="Black Sea" or name="North Sea"
  ]//province)
where (:$p in Europe (haben wir nicht aber "offensichtlich") and :)
 $p = /waters/sea[name="Mediterranean Sea"]//province and
 $p = /waters/sea[name="Black Sea"]//province and
 $p = /waters/sea[name="North Sea"]//province
return $p
