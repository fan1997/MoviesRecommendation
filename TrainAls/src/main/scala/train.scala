import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.recommendation.ALS
import org.apache.spark.mllib.recommendation.Rating
import org.apache.spark.mllib.evaluation.RegressionMetrics
import org.apache.spark.mllib.evaluation.RankingMetrics
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
object TrainALSModel {
  def main(args: Array[String]) {
    //参数
    val save_bool = args(0).toString()
    val sc = new SparkContext("local[2]", "Train ALS Model for Movies Recommendation")
    sc.setLogLevel("ERROR")
    // val rawData = sc.textFile("/input/u100k.data")
    // val rawRatings = rawData.map(_.split("\t").take(3))
    val rawData = sc.textFile("/input/u100kdata-updating.csv")
    val rawRatings = rawData.map(_.split(",").take(3))
    val ratings = rawRatings.map {case Array(user, movie, rating) => Rating(user.toInt, movie.toInt, rating.toDouble)}
    println("Training ALS Model ...")
    val model = ALS.train(ratings, 50, 10, 0.01)
    if(save_bool == "True")
        model.save(sc, "/trained-model/trained_model_100k")
    // Calculate MSE
    val usersProducts = ratings.map {case Rating(user, product, rating) => (user, product)}
    val predictions = model.predict(usersProducts).map {case Rating(user, product, rating) => ((user, product), rating)}
    val ratingsAndPredictions = ratings.map {case Rating(user, product, rating) =>
      ((user, product), rating)}.join(predictions)
    // Calculate MSE with MLlib
    val predictedAndTrue = ratingsAndPredictions.map {
      case ((user, product), (actual, predicted)) => (actual, predicted)}
    val regressionMetrics = new RegressionMetrics(predictedAndTrue)
    println("Training Finished!, Evaluting...")
    println("MLlib MSE = " + regressionMetrics.meanSquaredError)
    println("MLlib RMSE = " + regressionMetrics.rootMeanSquaredError)
  }
}
