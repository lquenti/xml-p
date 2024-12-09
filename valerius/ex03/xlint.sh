#!/bin/bash

echo "3.4"
xmllint --noout --valid output_3_4.xml && echo "Verification success" || echo "Failed"

echo "3.5"
xmllint --noout --valid output_3_5.xml && echo "Verification success" || echo "Failed"
