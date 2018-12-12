#!/bin/bash

set -e -x # fail fast
export TERM=${TERM:-dumb}
resource_dir=$(dirname $0)
echo ${resource_dir}

mkdir sonarqube-analysis-input/build
mkdir sonarqube-analysis-input/build/jacoco
mkdir build-out/version
cd project-source/ea_veracodeWrapper/

#build veracodeWrapper
./gradlew clean build
echo "Coping results"
#  move files to appropiate directories
cp -R build/libs/staticAnalysisWrapper-*.jar ../../build-out
# cp ./build/jacoco/* ../../sonarqube-analysis-input/build/jacoco
echo "TEMP-SNAPSHOT" > ../../build-out/version/number
# echo "TEMP" > ../../build-out/version/number
