#!/usr/bin/env bash
export LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:$ACE_ROOT/lib:$DDS_ROOT/lib:$TEST_ROOT/nexmatix
export DYLD_LIBRARY_PATH=$LD_LIBRARY_PATH
# -r: Set RELIABLE_RELIABILITY_QOS
# -w: WAIT_FOR_ACKS
$JAVA_HOME/bin/java -ea -classpath $TEST_ROOT/lib/subvalve.jar:$TEST_ROOT/lib/nexmatix.jar:$DDS_CLASSPATH SubValve -DCPSBit 0 -DCPSConfigFile rtps_disc.ini -r -w

