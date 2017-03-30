#! /bin/bash
node_num=10312
input_dir='input/blogcatalog/'
output_dir='output/blogcatalog/0_9'
path_train_data=$input_dir/blogcatalog_0_9.edgelist
path_test_data=$input_dir/blogcatalog_0_1.edgelist
#group_info=$input_dir/blogcatalog.group
neg_ratio=10 # used for link Predication
topk=40 # used for node_recommendation
threshold=0.01 # used for sampling. 1% of the node pairs are used.
iter_num=10 # train iterations for sampling processes.
thread_num=16 # num of threads for sampling based methods and calculating similarity scores.
learning_rate=0.02 # learning rate for sgdmf.

. ./funcs.sh
. ./compare.sh
