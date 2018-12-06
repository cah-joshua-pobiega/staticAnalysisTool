#!/bin/bash

set -e -x # fail fast
export TERM=${TERM:-dumb}
resource_dir=$(dirname $0)
echo ${resource_dir}
mkdir sonarqube-analysis-input/build
mkdir sonarqube-analysis-input/build/jacoco
cd project-source/sample-rest-api/

./gradlew clean test jacocoTestReport

cp ./build/jacoco/* ../../sonarqube-analysis-input/build/jacoco
echo "test reports"
cat ../../sonarqube-analysis-input/build/jacoco/test.exec
ln -sf ../project-source ../sonarqube-analysis-input


