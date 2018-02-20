#!/bin/bash


javac -cp '.:javax.json-1.1.jar' Grabber.java

java -cp '.:javax.json-1.1.jar' Grabber
