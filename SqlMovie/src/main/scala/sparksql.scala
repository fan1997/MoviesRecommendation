import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.recommendation.ALS
import org.apache.spark.mllib.recommendation.Rating
import org.apache.spark.mllib.evaluation.RegressionMetrics
import org.apache.spark.mllib.evaluation.RankingMetrics
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
import scala.collection.mutable.ArrayBuffer
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.{Encoder, Encoders, SparkSession}
import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql.DataFrame
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs._
object SparkSql{
  def merge(srcPath: String, dstPath: String): Unit =  {
   val hadoopConfig = new Configuration()
   val hdfs = FileSystem.get(hadoopConfig)
   FileUtil.copyMerge(hdfs, new Path(srcPath), hdfs, new Path(dstPath), true, hadoopConfig, null) 
   // the "true" setting deletes the source files once they are merged into the new output
  }

  def main(args: Array[String]) {
    
    val sc = new SparkContext("local[2]", "SRL MOVIE")
    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._
    sc.setLogLevel("ERROR")
    val rawData = sc.textFile("/input/u100kdata.csv")
    val rawRatings = rawData.map(_.split(",").take(3))
    val ratings = rawRatings.map {case Array(user, movie, rating) => Rating(user.toInt, movie.toInt, rating.toDouble)}
    val ratingsDF = ratings.toDF()
    val rawData_addi = sc.textFile("/input/u100kdata-simulation_input.csv")
    val rawRatings_addi = rawData_addi.map(_.split(",").take(3))
    val ratings_addi = rawRatings_addi.map {case Array(user, movie, rating) => Rating(user.toInt, movie.toInt, rating.toDouble)}
    val ratingsDF_addi = ratings_addi.toDF()
    // println(ratingsDF)
    // ratingsDF.show()
    val new_df = ratingsDF.union(ratingsDF_addi)
    // println(new_df.count())
    new_df.registerTempTable("ratingsTable")
     
    // sqlContext.sql("INSERT INTO ratingsTable VALUES (1, 1, 4.5)")
    println("Simulation running... , No.10 is watching the 10  movies that recommended for him.....")
    val fav_movies: DataFrame = sqlContext.sql("SELECT * FROM ratingsTable")
    val outputfile = "/input"  
    var filename = "u100kdata-updating.csv"
    var outputFileName = outputfile + "/temp_" + filename 
    var mergedFileName = outputfile + "/" + filename
    var mergeFindGlob  = outputFileName
    fav_movies.write
        .format("com.databricks.spark.csv")
        .option("header", "false")
        .mode("overwrite")
        .save(outputFileName)
    merge(mergeFindGlob, mergedFileName )
    fav_movies.unpersist()
    println("Simulation is completed, and new information has been writing to hdfs!")
  }
}
