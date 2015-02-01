#!/bin/bash
export GOPATH=`pwd`:$GOPATH
go build -o TServer org.cny.amf.ts/main
