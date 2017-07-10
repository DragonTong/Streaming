package streaming

/**
  * Created by root on 17-7-10.
  */

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object WordCount {

  def main(args: Array[String]): Unit = {
    /**
      * Configure SparkConf
      */
    val conf = new SparkConf().setMaster("local[2]").setAppName("Streaming")

    /**
      * Configure SparkStreaming
      */
    val ssc = new StreamingContext(conf, Seconds(1))

    /**
      * Configure input stream data source and listen port
      */
    val input = ssc.socketTextStream("localhost", 8888)

    /**
      * Programming is based on DStream, DStream blog to RDD model
      */
    val wordcount = input.flatMap(_.split(" ")).map(word => (word, 1)).reduceByKey(_+_)

    /**
      * print don't trigger Job execute. method class such as print, saveAsTextFile, saveAsHadoopFiles,foreachRDD
      */
    wordcount.print()

    /**
      * Start up framework
      */
    ssc.start()
    ssc.awaitTermination()
  }
}
