#!/bin/bash
#  (c) Copyright IBM Corp. 2024
#  (c) Copyright Instana Inc.

result=$(cd $1 &&
  export INFORMIXDIR=$2 &&
  export ONCONFIG=$3 &&
  export INFO RMIXSERVER=$4 &&
  export PATH=$INFORMIXDIR/bin:$PATH &&
  export INFORMIXSQLHOSTS=$INFORMIXDIR/etc/$5 &&
  ./onstat -g his 1 | head -n 10 | awk '{a[NR]=$0} END{print a[NR-1]}' | awk '{print $4}')

echo $result
