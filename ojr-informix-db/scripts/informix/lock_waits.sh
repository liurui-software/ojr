#!/bin/bash
#  (c) Copyright IBM Corp. 2024
#  (c) Copyright Instana Inc.

result=$(cd $1 &&
  export INFORMIXDIR=$2 &&
  export ONCONFIG=$3 &&
  export INFORMIXSERVER=$4 &&
  export PATH=$INFORMIXDIR/bin:$PATH &&
  export INFORMIXSQLHOSTS=$INFORMIXDIR/etc/$5 &&
  ./onstat -p |head -n 20| awk '{a[NR]=$0} END{print a[NR-1]}'| awk '{print $2}')

echo $result