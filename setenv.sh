#!/usr/bin/env bash
export DANCE_ROOT=unused
export CIAO_ROOT=unused

export JAVA_PLATFORM=darwin
export JAVA_HOME=$(/usr/libexec/java_home)

#export JAVA_PLATFORM=linux
#export JAVA_HOME=~/.sdkman/candidates/java/current

export BASE_ROOT=~
export DDS_ROOT=$BASE_ROOT/OpenDDS-3.11
export ACE_ROOT=$DDS_ROOT/ACE_wrappers
export TAO_ROOT=$ACE_ROOT/TAO
export MPC_ROOT=$ACE_ROOT/MPC
export TEST_ROOT=$BASE_ROOT/opendds-java-pubsub-example
export PATH=${PATH}:$ACE_ROOT/bin:$DDS_ROOT/bin
export LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:$ACE_ROOT/lib:$DDS_ROOT/lib
export DYLD_LIBRARY_PATH=${DYLD_LIBRARY_PATH}:$LD_LIBRARY_PATH
export DDS_CLASSPATH=$DDS_ROOT/lib/i2jrt.jar:$DDS_ROOT/lib/OpenDDS_DCPS.jar
