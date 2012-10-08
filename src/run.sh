#!/usr/bin/env bash

# remove previous class
rm probingNet/*.class 2> /dev/null

javac probingNet/main.java
java probingNet.packageTrain
