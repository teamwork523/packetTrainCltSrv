#!/usr/bin/env bash

# ssh haokun@owl.eecs.umich.edu "rm -rf pktTrain/*"
if [ -z $1 ]
then
    echo "./push.sh [-e,-o]"
    exit 1
fi

if [ $1 = "-e" ]
then
    scp -r *.java haokun@ep2.eecs.umich.edu:~/pktTrain
elif [ $1 = "-o" ]
then
    scp -r *.java haokun@owl.eecs.umich.edu:~/pktTrain
fi
