#!/bin/bash


javac -cp '.:javax.json-1.1.jar' *.java

java -cp '.:javax.json-1.1.jar' DataReader
