#!/usr/bin/env bash
set -euo pipefail
command -v java >/dev/null || { echo 'Java is required (21+)'; exit 1; }
command -v mvn >/dev/null || { echo 'Maven 3.9+ is required'; exit 1; }
java -version
mvn -version
mvn clean verify
