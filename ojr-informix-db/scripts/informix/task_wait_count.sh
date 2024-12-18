#!/bin/bash
#  (c) Copyright IBM Corp. 2024
#  (c) Copyright Instana Inc.

result=$(cd $1 &&
  export INFORMIXDIR=$2 &&
  export ONCONFIG=$3 &&
  export INFO RMIXSERVER=$4 &&
  export PATH=$INFORMIXDIR/bin:$PATH &&
  export INFORMIXSQLHOSTS=$INFORMIXDIR/etc/$5 &&
  ./onstat -g wai | awk 'BEGIN {RS=""; FS="\n"} NR==2 {linecount = NF -2; if (linecount>0) print linecount; else print 0}')

echo $result