#!/bin/bash
#Package & Publish binary artifacts
set -e -x # fail fast
echo "it works!!!!"
export TERM=${TERM:-dumb}
resource_dir=$(dirname $0)

mv temp-storage/veracodeWrapper*.jar deploy-output/veracodeWrapper.jar
mkdir deploy-output/version
mv build-version/number deploy-output/version/number