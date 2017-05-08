#! /bin/bash
node_num=456
input_dir='input/AL'
output_dir='output/AL'
path_train_data=$input_dir/AL_train.edgelist
path_simi=$output_dir/rpr.simi
thread_num=1 # num of threads for sampling based methods and calculating similarity scores.
. ./funcs.sh
#. ./compare.sh
rpr_simi --debug
