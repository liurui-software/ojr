#!/bin/bash
#  (c) Copyright IBM Corp. 2024
#  (c) Copyright Instana Inc.

result=$(cd $1 &&
  export INFORMIXDIR=$2 &&
  export ONCONFIG=$3 &&
  export INFO RMIXSERVER=$4 &&
  export PATH=$INFORMIXDIR/bin:$PATH &&
  export INFORMIXSQLHOSTS=$INFORMIXDIR/etc/$5 &&
  ./onstat -T | awk '{print $7}' | awk '{sum+=$0;}END{print sum;}')

echo $result
