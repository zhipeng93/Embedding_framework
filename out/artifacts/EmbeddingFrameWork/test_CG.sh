#! /bin/bash
node_num=5242
input_dir='input/CG'
output_dir='output/CG'
path_test_data=$input_dir/CG_test.edgelist
path_train_data=$input_dir/CG_train.edgelist

neg_ratio=10 # used for link Predication
topk=10 # used for node_recommendation
topk_sampling=20
threshold=0.01 # used for sampling. 1% of the node pairs are used.
iter_num=1 # train iterations for sampling processes.
thread_num=1 # num of threads for sampling based methods and calculating similarity scores.
learning_rate=0.02 # learning rate for sgdmf.

. ./funcs.sh
. ./compare.sh
