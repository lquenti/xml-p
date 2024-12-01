#!/bin/bash

# Ensure the script is executable
chmod +x "$(dirname "$0")/start_http_server.sh"

# Navigate to the public_html directory
cd "$(dirname "$0")/public_html"

# Start the Python HTTP server
python3 -m http.server 8080
