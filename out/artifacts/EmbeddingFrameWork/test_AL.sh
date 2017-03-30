#! /bin/bash
node_num=456
input_dir='input/AL'
output_dir='output/AL'
path_test_data=$input_dir/AL_test.edgelist
path_train_data=$input_dir/AL_train.edgelist

neg_ratio=10 # used for link Predication
topk=10 # used for node_recommendation
threshold=0.01 # used for sampling. 1% of the node pairs are used.
iter_num=1 # train iterations for sampling processes.
thread_num=16 # num of threads for sampling based methods and calculating similarity scores.
learning_rate=0.02 # learning rate for sgdmf.

. ./funcs.sh
#. ./compare.sh
echo "*********************************************************"
path_source_vec=$sgd_mf_source_vec
path_dest_vec=$sgd_mf_dest_vec
java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar EmbeddingTools.SgdMF --path_train_data $path_train_data --path_source_vec $path_source_vec --path_dest_vec $path_dest_vec --node_num 456 --neg_sample 5 --layer_size 64 --iter $iter_num --learning_rate $learning_rate --threshold $threshold $1
path_dest_vec="no_input_dest"
embedding_node_rec
echo "*********************************************************"
