#! /bin/bash
node_num=1715255
input_dir='input/flickr'
output_dir='output/flickr/0_99'
path_test_data=$input_dir/flickr_0_01.edgelist
path_train_data=$input_dir/flickr_0_99.edgelist

neg_ratio=10 # used for link Predication
topk=40 # used for node_recommendation
threshold=0.01 # used for sampling. 1% of the node pairs are used.
iter_num=10 # train iterations for sampling processes.
thread_num=16 # num of threads for sampling based methods and calculating similarity scores.
learning_rate=0.02 # learning rate for sgdmf.

. ./funcs.sh
. ./compare.sh
