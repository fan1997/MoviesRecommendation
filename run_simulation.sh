#!/bin/sh
hdfs dfs -rm  /input/u100kdata-updating.csv
hdfs dfs -rm -r /trained-model/trained_model_100k
spark-submit --class "SparkSql" --master spark://master:7077  sqlmovie_2.11-1.0.jar
spark-submit --class "TrainALSModel" --master spark://master:7077  trainals_2.11-1.0.jar True
spark-submit --class "RecommendALS" --master spark://master:7077  alsmovierec_2.11-1.0.jar 10
