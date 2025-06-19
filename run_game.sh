#!/bin/bash

echo "Starting Art Heist Adventure game..."
echo ""
echo "To play: Type commands like 'look', 'go north', etc. after the '>' prompt."
echo ""

# Get the directory where the script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Clean and build the project with Gradle (with compatibility settings)
"$SCRIPT_DIR/gradlew" clean build

# Check if the build was successful
if [ $? -ne 0 ]; then
    echo "Error: Failed to build the game. Please check for compilation errors."
    exit 1
fi

# Run the game using the Gradle wrapper for compatibility
"$SCRIPT_DIR/gradlew" run -q
