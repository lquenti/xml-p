#!/bin/bash

target_dir="public_html/countries"
mondial_file="../../mondial.xml"
xpath_countries="//country/@car_code"

# Create main countries directory
mkdir -p "$target_dir"

# Make index.html
xsltproc public_html/index.xslt "$mondial_file" > "$target_dir/../index.html"

# Get all car codes
car_codes=$(xq -x "$xpath_countries" "$mondial_file")

# Process each country
for code in $car_codes; do
    echo "Processing country: $code"
    # Create country directory
    country_dir="$target_dir/$code"
    mkdir -p "$country_dir"
    
    # Generate country-specific HTML file
    xsltproc --stringparam country_code "$code" \
        -o "$country_dir/index.html" \
        public_html/country.xslt \
        "$mondial_file"

    # Get province IDs for this country
    xpath_provinces="//country[@car_code='$code']/province/@id"
    province_ids=$(xq -x "$xpath_provinces" "$mondial_file")

    # Process each province
    for prov_id in $province_ids; do
        echo "  Processing province: $prov_id"
        # Get province name for directory
        xpath_prov_name="//province[@id='$prov_id']/name/text()"
        prov_name=$(xq -x "$xpath_prov_name" "$mondial_file" | tr ' ' '_')
        
        # Create province directory
        province_dir="$country_dir/$prov_name"
        mkdir -p "$province_dir"
        
        # Generate province-specific HTML file
        xsltproc --stringparam country_code "$code" \
            --stringparam province_id "$prov_id" \
            -o "$province_dir/index.html" \
            public_html/province.xslt \
            "$mondial_file"
    done
done