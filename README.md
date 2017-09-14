## opendds-java-pubsub-example

1. Download and build OpenDDS

```
$ cd
$ wget https://github.com/objectcomputing/OpenDDS/releases/download/DDS-3.11/OpenDDS-3.11.tar.gz
$ tar -xvzf OpenDDS-3.11.tar.gz
$ cd OpenDDS-3.11
$ ./configure --java --no-tests
$ make -k
```
2. Download and build this example

```
$ cd ../opendds-java-pubsub-example
$ . ../OpenDDS-3.11/setenv.sh
$ ./genprj.sh
$ make
```
