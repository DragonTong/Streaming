package streaming

import com.typesafe.config.ConfigFactory
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Milliseconds, StreamingContext}
import org.apache.spark.streaming.flume.FlumeUtils

/**
  * Created by root on 17-7-18.
  */
object pollingFlume {

  def main(args: Array[String]): Unit = {

    implicit val conf = ConfigFactory.load()

    val sparkConf = new SparkConf()
      .setAppName(conf.getString("spark.appName"))
      .setMaster(conf.getString("spark.master"))

    val IntervalBatch = Milliseconds(20000)
    val ssc = new StreamingContext(sparkConf, IntervalBatch)

    val flumeStream = FlumeUtils.createPollingStream(ssc, "192.168.0.156", 4545)

    flumeStream.map(e=> new String(e.event.getBody.array())).print()

    ssc.start()
    ssc.awaitTermination()

  }

}
