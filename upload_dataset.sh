#!/bin/sh
hdfs dfs -mkdir /input
hdfs dfs -put ./dataset/u100kdata.csv /input
hdfs dfs -put ./dataset/u100kitem.csv /input
hdfs dfs -put ./dataset/u100kdata-updating.csv /input
hdfs dfs -put ./dataset/u100kdata-simulation_input.csv /input
hdfs dfs -rm -r /trained-model
