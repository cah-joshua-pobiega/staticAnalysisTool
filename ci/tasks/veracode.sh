#!/bin/bash

set -e -x # fail fast
export TERM=${TERM:-dumb}
resource_dir=$(dirname $0)
echo ${resource_dir}

#parameters to pass --- > appName ; busUnit; team;  filePath
echo "Hi, we got here in Veracode land."
# DATE=`date +"%Y-%m-%d-%H%M"`
# appName=$1
# busUnit=$2
# team=$3
# filePath=$4
# apiID=$5
# apiKey=$6
#
# /usr/bin/java -j staticAnalysisWrapper  "$appName" "$busUnit" "$team" "$filePath" "$apiID" "$apiKey"
