#!/bin/bash

set -e -x # fail fast
export TERM=${TERM:-dumb}
resource_dir=$(dirname $0)
echo ${resource_dir}
echo 'Executing dependeny check...:  ' 

cd temp-storage
#execute analysis and put the log into scan-results dir
java -jar ../nexus-security-tools/nexus-iq-cli*.jar -i test_task -e -s https://nexusiq.cardinalhealth.net -a ${SONATYPE_IQ_USR}:${SONATYPE_IQ_PWD} *.jar  2>&1 | tee ../scan-results/log.txt