#!/bin/sh
spark-submit --class "TrainALSModel" --master spark://master:7077  trainals_2.11-1.0.jar True
spark-submit --class "RecommendALS" --master spark://master:7077  alsmovierec_2.11-1.0.jar 10
