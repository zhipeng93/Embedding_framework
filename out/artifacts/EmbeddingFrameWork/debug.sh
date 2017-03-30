#! /bin/bash
. ./funcs.sh
node_num=5242
input_dir='input/arxiv'
output_dir='output/arxiv'
path_test_data=$input_dir/arxiv_test.edgelist
path_train_data=$input_dir/arxiv_train.edgelist


app_source_vec=$output_dir/APP_source.vec
app_dest_vec=$output_dir/APP_dest.vec
dp_vec=$output_dir/dp.vec
line_1_vec=$output_dir/line.vec1
line_2_vec=$output_dir/line.vec2
katz_source_vec=$output_dir/katz_source.vec
katz_dest_vec=$output_dir/katz_dest.vec
simrank_source_vec=$output_dir/simrank_source.vec
simrank_dest_vec=$output_dir/simrank__dest.vec
co_citation_source_vec=$output_dir/co_citation_source.vec
co_citation_dest_vec=$output_dir/co_citation_dest.vec
aa_source_vec=$output_dir/aa_source.vec
aa_dest_vec=$output_dir/aa_dest.vec

ppr_sampling_source_vec=$output_dir/ppr_sampling_source.vec
ppr_sampling_dest_vec=$output_dir/ppr_sampling_dest.vec
neg_ratio=10
topk=10
#group_info=$input_dir/flickr.group

learning_rate=0.02
threshold=0.05
iter_num=10
thread_num=1
path_source_vec=$app_source_vec
path_dest_vec=$app_dest_vec
katz_node_rec --debug
#ppr_node_rec --debug
#sgd_mf
#ppr_sampling --debug
path_dest_vec="no_input_dest"
#embedding_node_rec
rppr_node_rec --debug
ppr_node_rec --debug
rkatz_node_rec --debug
