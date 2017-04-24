#! /bin/bash
node_num=2900
input_dir='input/OD'
output_dir='output/OD'
path_test_data=$input_dir/OD_train.edgelist
path_train_data=$input_dir/OD_test.edgelist

neg_ratio=10 # used for link Predication
topk=10 # used for node_recommendation
threshold=0.01 # used for sampling. 1% of the node pairs are used.
iter_num=10 # train iterations for sampling processes.
topk_sampling=20 # if this is set as 0, threshold is used.
thread_num=16 # num of threads for sampling based methods and calculating similarity scores.
learning_rate=0.02 # learning rate for sgdmf.

. ./funcs.sh
. ./compare.sh
