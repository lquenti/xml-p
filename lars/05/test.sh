curl -X POST http://localhost:8080/reverseserver_war_exploded/reverse \
  -H "Content-Type: application/xml" --data-binary @- <<EOF
<country car_code='D'>
    <name>Germany</name>
    <population year="2011">80219695</population>
</country>
EOF
