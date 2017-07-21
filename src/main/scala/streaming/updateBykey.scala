package streaming

import com.typesafe.config.ConfigFactory
import org.apache.spark.{HashPartitioner, SparkConf}
import org.apache.spark.streaming.{Milliseconds, StreamingContext}

/**
  * Created by root on 17-7-20.
  */
object updateBykey {

  def main(args: Array[String]): Unit = {
    /**
      * Load configuration file
      */
    implicit val conf = ConfigFactory.load()

    /**
      * Initialize SparkConf
      */
    val sparkConf = new SparkConf()
      .setAppName(conf.getString("spark.appName"))
      .setMaster(conf.getString("spark.master"))

    /**
      * Initialize SteamingContext
      */
    val IntervalBatch = Milliseconds(20000)
    val ssc = new StreamingContext(sparkConf, IntervalBatch)

    val updateFunc = (values: Seq[Int], state: Option[Int]) => {

      /**
        * Calculate sum of current count
        */
      val currentCount = values.sum

      /**
        * Get state previous count
        */
      val previousCount = state.getOrElse(0)

      /**
        * Get value of currentCount add previousCount
        */
      Some(currentCount + previousCount)
    }

    val newUpdateFunc = (iterator: Iterator[(String, Seq[Int], Option[Int])]) => {
      iterator.flatMap(t => updateFunc(t._2, t._3).map(s => (t._1, s)))
    }

    val partitioner = new HashPartitioner(ssc.sparkContext.defaultParallelism)

    ssc.checkpoint("/usr/local/src/checkpoint")

    val initialRDD = ssc.sparkContext.parallelize(List(("key", 1), ("value", 1)))

    val input = ssc.socketTextStream("localhost", 8888)
    val pair = input.flatMap(_.split(" ")).map(word => (word, 1))
    val wordCount = pair.updateStateByKey(newUpdateFunc, partitioner, true, initialRDD)

    wordCount.print()

    ssc.start()
    ssc.awaitTermination()

  }

}
