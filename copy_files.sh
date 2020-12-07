#!/bin/sh
echo "$1"
docker cp ./MovieRecommendAls/target/scala-2.11/alsmovierec_2.11-1.0.jar $1:/home
docker cp ./TrainAls/target/scala-2.11/trainals_2.11-1.0.jar $1:/home
docker cp ./SqlMovie/target/scala-2.11/sqlmovie_2.11-1.0.jar $1:/home
docker cp -a ./dataset/ $1:/home
docker cp -a run_simulation.sh $1:/home
docker cp -a run.sh $1:/home
docker cp upload_dataset.sh $1:/home

