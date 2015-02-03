#!/bin/bash
set -e
export PATH=$PATH:$GOROOT/bin:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools:$ANT_HOME/bin
./w_ts_ip
case $1 in
 dev)
  ant clear_cls stop_ts start_ts uninstall emma debug install test fetch-test-report stop_ts
 ;;
 pub)
  ant pub.jar
 ;;
 *) 
  ant clear_cls stop_ts start_ts uninstall emma debug install test fetch-test-report stop_ts
esac
