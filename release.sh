#!/bin/bash

mvn release:prepare  -Darguments="-DskipTests"

# Create a temp file
tmpfile=$(mktemp /tmp/release-noqm.XXXXXX)

echo "# NOQM Release v$1" > "$tmpfile"

echo "" >> "$tmpfile"
echo "## Fixes" >> "$tmpfile"
echo "" >> "$tmpfile"
curl -H "Authorization: token $GITHUB_TOKEN" "https://api.github.com/repos/sim51/NOQM/issues?milestone=$1&state=closed&sort=updated&labels=bug" | jq -r '.[] | [(.number), .title] | @tsv' | sed s/^/#/g >> "$tmpfile"

echo "" >> "$tmpfile"
echo "## Improvements" >> "$tmpfile"
echo "" >> "$tmpfile"
curl -H "Authorization: token $GITHUB_TOKEN" "https://api.github.com/repos/sim51/NOQM/issues?milestone=$1&state=closed&sort=updated&labels=enhancement" | jq -r '.[] | [(.number), .title] | @tsv' | sed s/^/#/g >> "$tmpfile"


# retrieve last tag if not set
latest=$(curl -H "Authorization: token $GITHUB_TOKEN" "https://api.github.com/repos/sim51/NOQM/tags" | jq -r '.[0].name')
echo "" >> "$tmpfile"
echo "## Commits" >> "$tmpfile"
echo "" >> "$tmpfile"
git log "v$1".."$latest" --oneline >> "$tmpfile"

# Create the github release notes
# based on https://www.npmjs.com/package/github-release-cli
github-release upload \
  --owner=sim51 \
  --repo=NOQM \
  --tag="v$1" \
  --name="v$1" \
  --body="$(cat $tmpfile)" \
  target/noqm-*

# Perform the release
mvn release:perform -Darguments="-DskipTests -Dmaven.deploy.skip=true"
