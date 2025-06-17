#!/bin/bash
# Run the game using Gradle with all progress indicators suppressed
cd "$(dirname "$0")"
./gradlew run -q --console=plain --no-daemon
