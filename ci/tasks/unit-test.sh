#!/bin/bash

set -e -x # fail fast
export TERM=${TERM:-dumb}
resource_dir=$(dirname $0)
echo ${resource_dir}
mkdir build-out

cur_dir=$(pwd)
cd project-source/sample-rest-api

#build android app

$ANDROID_HOME/tools/bin/sdkmanager "build-tools;25.0.2" "build-tools;27.0.3" "platforms;android-27" "ndk-bundle"
./gradlew clean jacocoTestReport # use this for testing only

# cp app/build/outputs/apk/*.apk ${cur_dir}/build-out/mobile-app-1.0.apk





