package streaming

import com.typesafe.config.ConfigFactory
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by root on 17-7-11.
  */


object SourceHdfs {

  /**
    * Definition HDFS checkpoint directory address
    */
  val checkpointDirectory = "hdfs://master:9000/sparkStreaming/Checkpoint_Data"

  def main(args: Array[String]): Unit = {

    /**
      * Either recreate a StreamingContext from checkpoint data or create a new StreamingContext.
      */
    val context = StreamingContext.getOrCreate(checkpointDirectory, createContext _)

    /**
      * Configure inputDStream source that HDFS address
      * No Receiver, SparkStreaming application monitor batch by timer
      */
    val DStream = context.textFileStream("hdfs://master:9000/quality/clipper_erp/2017-07-11")

    val wordCount = DStream.flatMap(_.split(" ")).map(word => (word, 1)).reduceByKey(_+_)

    wordCount.print()

    context.start()
    context.awaitTermination()

  }

  /**
    * Create spark streamingContext function for getOrCreate method
    */
  def createContext(): StreamingContext ={

    implicit val conf = ConfigFactory.load()
    val sparkConf = new SparkConf()
      .setAppName(conf.getString("spark.appName"))
      .setMaster(conf.getString("spark.master"))

    val ssc = new StreamingContext(sparkConf, Seconds(10))
    ssc.checkpoint(checkpointDirectory)
    ssc
  }

}
