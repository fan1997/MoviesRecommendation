import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.recommendation.ALS
import org.apache.spark.mllib.recommendation.Rating
import org.apache.spark.mllib.evaluation.RegressionMetrics
import org.apache.spark.mllib.evaluation.RankingMetrics
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
import scala.collection.mutable.ArrayBuffer

object RecommendALS{
  def main(args: Array[String]) {
    val sc = new SparkContext("local[2]", "Recommend Movies through trained als model")
    sc.setLogLevel("ERROR")
    // val rawData = sc.textFile("/input/u100k.data")
    // val rawRatings = rawData.map(_.split("\t").take(3))
    // val rawData = sc.textFile("/input/u100kdata.csv")
    // val rawRatings = rawData.map(_.split(",").take(3))
    val rawData = sc.textFile("/input/u100kdata-updating.csv")
    val rawRatings = rawData.map(_.split(",").take(3))
    val ratings = rawRatings.map {case Array(user, movie, rating) => Rating(user.toInt, movie.toInt, rating.toDouble)}
    val model = MatrixFactorizationModel.load(sc, "/trained-model/trained_model_100k")
    // Make recommendations to user
    val userId = args(0).toInt
    val K = 10
    // Check the recommendation movies' name
    val movies = sc.textFile("/input/u100kitem.csv")
    val titles = movies.map(line => line.split("\\|")).map(fields => (fields(0).toInt, fields(1))).collectAsMap()
    val moviesForUser = ratings.keyBy(_.user).lookup(userId)
    // println(ratings.size())
    println("\n")
    println("Welcome to use our Recommendation system, Dear No." + args(0) + "!")
    println("\n")
    println("You have watched "+ moviesForUser.size.toString() + " movies!")
    println("\n")
    println("Your favorite movies are:  ")
    val moviesForUserall = moviesForUser.map(rating => (titles(rating.product)))
    val moviesForUserfav = moviesForUser.sortBy(-_.rating).take(5).map(rating => (titles(rating.product), rating.rating))
    var countfav : Int = 0; 
    for(x <- moviesForUserfav){
      countfav=countfav+1
      println(countfav +": "+ x) 
    }
    println("\n")
    val topKRecs = model.recommendProducts(userId, K + moviesForUser.size)
    println("We recommend these new movies for you, have fun!")
    val finalrec = topKRecs.map(rating => (titles(rating.product), rating.rating))
    val finalrec_withoutratings = topKRecs.map(rating => (titles(rating.product)))
    var count : Int = 0; 
    for(x <- finalrec_withoutratings){
      if((moviesForUserall.contains(x) != true) &&( count < K.toInt)){
          count = count + 1
          println(count +": "+ x) 
      }   
    }
  }
}
