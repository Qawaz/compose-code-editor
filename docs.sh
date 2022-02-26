#!/bin/bash

# Fail on any error
set -ex

# Clear out the old API docs
[ -d docs/api ] && rm -r docs/api

# Build the docs with dokka
./gradlew dokkaHtmlMultiModule --stacktrace

# Copy README.md to docs
cp README.md ./docs/README.md

# Deploy Docs
mkdocs gh-deploy --force