# MoviesRecommendation

This is a project used for Movies Recommendation.

Main dataset: Movielens

Main tools: Hadoop + Spark mllib(ALS) + Spark sql

## Usage

we have 2 ways to try this project. One is simple in linux shell file. Another one is more detail.

## Shell file

#### Clone from github

```shell
git clone https://github.com/fan1997/MoviesRecommendation.git
cd MoviesRecommendation
chmod +x ./spark-setup.sh
chmod +x ./copy_files.sh
```

#### Setup spark

```shell
./spark-setup.sh
```

#### Copy files to master container

assume that master container id is 9524c9955ca2.

```shell
./copy_files.sh 9524c9955ca2
```

#### Enter the master container

```shell
docker exec -it 9524c9955ca2 /bin/bash
cd home
chmod +x run.sh
chmod +x run_simulation.sh
chmod +x upload_dataset.sh
```

#### Upload datasets to hdfs

```shell
./upload_dataset.sh
```

#### Train and recommend movies for a specific user NO.10

```shell
./run.sh
```

#### Run Simulations

we assume that user NO.10 has  seen the 10 movies we recommended for him, then we need to recommend another 10 movies for him.

```shell
./run_simulation.sh
```



### Detail

#### Clone from github

```shell
git clone https://github.com/fan1997/MoviesRecommendation.git
cd MoviesRecommendation
chmod +x ./spark-setup.sh
chmod +x ./copy_files.sh
```

#### Setup spark

```shell
cd ./spark-install-run
docker-compose up -d
docker ps
cd ..
```

#### Copy files to master container

assume that master container id is 9524c9955ca2.

```shell
docker cp ./MovieRecommendAls/target/scala-2.11/alsmovierec_2.11-1.0.jar 9524c9955ca2:/home
docker cp ./TrainAls/target/scala-2.11/trainals_2.11-1.0.jar 9524c9955ca2:/home
docker cp ./SqlMovie/target/scala-2.11/sqlmovie_2.11-1.0.jar 9524c9955ca2:/home
docker cp -a ./dataset/ 9524c9955ca2:/home
docker cp -a run_simulation.sh 9524c9955ca2:/home
docker cp -a run.sh 9524c9955ca2:/home
docker cp upload_dataset.sh 9524c9955ca2:/home
```

#### Enter the master container

```shell
docker exec -it 9524c9955ca2 /bin/bash
cd home
chmod +x run.sh
chmod +x run_simulation.sh
chmod +x upload_dataset.sh
```

#### Upload datasets to hdfs

```shell
hdfs dfs -mkdir /input
hdfs dfs -put ./dataset/u100kdata.csv /input
hdfs dfs -put ./dataset/u100kitem.csv /input
hdfs dfs -put ./dataset/u100kdata-updating.csv /input
hdfs dfs -put ./dataset/u100kdata-simulation_input.csv /input
hdfs dfs -rm -r /trained-model
```

#### Train and recommend movies for a specific user NO.10

```shell
spark-submit --class "TrainALSModel" --master spark://master:7077  trainals_2.11-1.0.jar True
spark-submit --class "RecommendALS" --master spark://master:7077  alsmovierec_2.11-1.0.jar 10
```

#### Run Simulations

we assume that user NO.10 has  seen the 10 movies we recommended for him, then we need to recommend another 10 movies for him.

```shell
hdfs dfs -rm  /input/u100kdata-updating.csv
hdfs dfs -rm -r /trained-model/trained_model_100k
spark-submit --class "SparkSql" --master spark://master:7077  sqlmovie_2.11-1.0.jar
spark-submit --class "TrainALSModel" --master spark://master:7077  trainals_2.11-1.0.jar True
spark-submit --class "RecommendALS" --master spark://master:7077  alsmovierec_2.11-1.0.jar 10
```

