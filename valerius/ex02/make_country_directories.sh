#!/bin/bash

target_dir="public_html/countries"
mondial_file="../../mondial.xml"
xpath="//country/@car_code"

# Create main countries directory
mkdir -p "$target_dir"

# Get all car codes
car_codes=$(xq -x "$xpath" "$mondial_file")

# Process each country
for code in $car_codes; do
    # Create country directory
    country_dir="$target_dir/$code"
    mkdir -p "$country_dir"
    
    # Generate country-specific HTML file
    xsltproc --stringparam country_code "$code" \
        -o "$country_dir/index.html" \
        public_html/country.xslt \
        "$mondial_file"
done