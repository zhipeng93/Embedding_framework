#! /bin/bash
node_num=6301
input_dir='input/CG'
output_dir='output/CG'
path_test_data=$input_dir/CG_test.edgelist
path_train_data=$input_dir/CG_train.edgelist

neg_ratio=10 # used for link Predication
topk=10 # used for node_recommendation
thread_num=1 # num of threads for sampling based methods and calculating similarity scores.
learning_rate=0.02 # learning rate for sgdmf.

topk_sampling=20 # used for sampling, for each node, use topk_sampling candidates for each node.
threshold=0.0008 # used for sampling. 1% of the node pairs are used.
iter_num=20 # train iterations for sampling processes.
. ./funcs.sh

echo "*********************************************************"
#jaccard_node_rec
path_source_vec=$katz_source_vec
path_dest_vec=$katz_dest_vec
#jaccard_mf
jaccard_sampling
#ppr_embedding --debug
path_dest_vec="no_input_dest"
embedding_node_rec --debug

#path_dest_vec=$katz_dest_vec
#katz_sampling --debug
#path_dest_vec="no_input_dest"
#embedding_node_rec --debug

#ppr_node_rec
#rppr_node_rec
