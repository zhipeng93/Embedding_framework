#! /bin/bash
node_num=6301
input_dir='input/PG'
output_dir='output/PG'
path_test_data=$input_dir/PG_test.edgelist
path_train_data=$input_dir/PG_train.edgelist

neg_ratio=10 # used for link Predication
topk=10 # used for node_recommendation
topk_sampling=10 # used for sampling, for each node, use topk_sampling candidates for each node.
threshold=0.005 # used for sampling. 1% of the node pairs are used.
iter_num=3 # train iterations for sampling processes.
thread_num=4 # num of threads for sampling based methods and calculating similarity scores.
learning_rate=0.02 # learning rate for sgdmf.
. ./funcs.sh

echo "*********************************************************"
path_source_vec=$katz_source_vec
path_dest_vec=$katz_dest_vec

#ppr_embedding --debug
path_dest_vec="no_input_dest"
#embedding_node_rec --debug

path_dest_vec=$katz_dest_vec
ppr_sampling --debug
path_dest_vec="no_input_dest"
embedding_node_rec --debug

#ppr_node_rec
#rppr_node_rec
