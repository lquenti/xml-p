echo "3.4"
xmllint --noout --valid output_3_4.xml && echo "Verification success" || echo "Failed"

echo "3.5"
xmllint --noout --valid output_3_5.xml && echo "Verification success" || echo "Failed"

echo "3.6"
xmllint --noout --valid output_3_6.xml && echo "Verification success" || echo "Failed"

#echo "3.6a"
#xmllint --noout --valid output_3_6a.xml && echo "Verification success" || echo "Failed"

echo "3.7"
xmllint --noout --valid output_3_7.xml && echo "Verification success" || echo "Failed"