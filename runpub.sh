#!/usr/bin/env bash
export DYLD_LIBRARY_PATH=${DYLD_LIBRARY_PATH}:$ACE_ROOT/lib:$DDS_ROOT/lib:$TEST_ROOT/nexmatix
$JAVA_HOME/bin/java -ea -classpath $TEST_ROOT/lib/pubvalve.jar:$TEST_ROOT/lib/nexmatix.jar:$DDS_CLASSPATH PubValve -DCPSBit 0 -DCPSConfigFile rtps_disc.ini -r -w
