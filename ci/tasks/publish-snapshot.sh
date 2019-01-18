#!/bin/bash
#Package & Publish binary artifacts
set -e -x # fail fast
echo "it works!!!!"
export TERM=${TERM:-dumb}
resource_dir=$(dirname $0)

mv temp-storage/staticAnalysisWrapper*.jar deploy-output/staticAnalysisWrapper.jar
mkdir deploy-output/version
mv build-version/number deploy-output/version/number
