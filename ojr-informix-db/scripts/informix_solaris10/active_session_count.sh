# !/bin/bash
#  (c) Copyright IBM Corp. 2024
#  (c) Copyright Instana Inc.

result=$(cd $1 &&
  INFORMIXDIR=$2 &&
  export INFORMIXDIR &&
  ONCONFIG=$3 &&
  export ONCONFIG &&
  INFORMIXSERVER=$4 &&
  export INFORMIXSERVER &&
  PATH=$INFORMIXDIR/bin:$PATH &&
  export PATH &&
  INFORMIXSQLHOSTS=$INFORMIXDIR/etc/$5 &&
  export INFORMIXSQLHOSTS &&
  ./onstat -g ses active | awk 'NR==2 {linecount = NF -2; if (linecount>0) print linecount; else print 0}')

echo $result