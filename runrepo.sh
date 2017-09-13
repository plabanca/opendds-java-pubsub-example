#!/usr/bin/env bash
rm $TEST_ROOT/repo.ior
$DDS_ROOT/bin/DCPSInfoRepo -NOBITS -ORBDebugLevel 10 -ORBLogFile $TEST_ROOT/DCPSInfoRepo.log -o $TEST_ROOT/repo.ior
