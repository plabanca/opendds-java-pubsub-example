#!/usr/bin/env bash
export DYLD_LIBRARY_PATH=${DYLD_LIBRARY_PATH}:$ACE_ROOT/lib:$DDS_ROOT/lib:$TEST_ROOT/nexmatix
# -r: Set RELIABLE_RELIABILITY_QOS
# WAIT_FOR_ACKS = -w
$JAVA_HOME/bin/java -ea -classpath $TEST_ROOT/lib/subvalve.jar:$TEST_ROOT/lib/nexmatix.jar:$DDS_CLASSPATH SubValve -DCPSBit 0 -DCPSConfigFile rtps_disc.ini -r -w

