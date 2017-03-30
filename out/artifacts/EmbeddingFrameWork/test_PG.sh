#! /bin/bash
node_num=6301
input_dir='input/PG'
output_dir='output/PG'
path_test_data=$input_dir/PG_test.edgelist
path_train_data=$input_dir/PG_train.edgelist

neg_ratio=10 # used for link Predication
topk=10 # used for node_recommendation
threshold=0.01 # used for sampling. 1% of the node pairs are used.
iter_num=1 # train iterations for sampling processes.
thread_num=16 # num of threads for sampling based methods and calculating similarity scores.
learning_rate=0.02 # learning rate for sgdmf.

. ./funcs.sh
. ./compare.sh