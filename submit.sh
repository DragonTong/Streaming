#!/bin/bash
set -e 

/usr/local/spark/bin/spark-submit --class streaming.WordCount target/spark.streaming.pro-1.0-SNAPSHOT.jar
