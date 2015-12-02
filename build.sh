#!/bin/bash
set -e
export PATH=$PATH:$GOROOT/bin:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools:$ANT_HOME/bin
if [ ! -d assets ]; then
 mkdir assets
fi
#./w_ts_ip
case $1 in
 dev)
  ant clear_cls stop_ts start_ts uninstall emma debug install test fetch-test-report stop_ts
 ;;
 pub)
  ant pub.jar
 ;;
 pub.ims)
  ant pub.jar
  cp ~/git/awf/build/awf/awf-v0.0.1.jar ~/svn/IMS/trunk/050IMS/IMS-A/libs/ 
;;
 *) 
  ant clear_cls stop_ts start_ts uninstall emma debug install test fetch-test-report stop_ts
esac
