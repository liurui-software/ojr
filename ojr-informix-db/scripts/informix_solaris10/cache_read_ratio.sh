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
  ./onstat -p |head -n 6| awk '{a[NR]=$0} END{print a[NR]}'| awk '{print $4}')

echo $result|cut -d'.' -f 1