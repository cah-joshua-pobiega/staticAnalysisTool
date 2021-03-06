#!/bin/bash

set -e -x # fail fast
export TERM=${TERM:-dumb}
resource_dir=$(dirname $0)
echo ${resource_dir}

mkdir sonarqube-analysis-input/build
mkdir sonarqube-analysis-input/build/jacoco
mkdir build-out/version
cd project-source/ea_veracodewrapper/

#build veracodeWrapper
./gradlew clean build
echo "Coping results"
#  move files to appropiate directories
cp -R build/libs/staticAnalysisWrapper-*.jar ../../build-out
cp -R ./* ../../sonarqube-analysis-input/
cp -R ../version/number ../../build-out/version/number
# cp ./build/jacoco/* ../../sonarqube-analysis-input/build/jacoco
# echo "TEMP" > ../../build-out/version/number
# echo "TEMP" > ../../build-out/version/number
