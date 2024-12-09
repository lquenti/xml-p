#!/bin/bash

echo "3.4"
xmllint --noout --valid output.xml && echo "Verification success" || echo "Failed"