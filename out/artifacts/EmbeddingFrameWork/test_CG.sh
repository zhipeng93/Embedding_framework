#! /bin/bash
. ./funcs.sh
node_num=5242
input_dir='input/CG'
output_dir='output/CG'
path_test_data=$input_dir/CG_test.edgelist
path_train_data=$input_dir/CG_train.edgelist


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

threshold=0.01
iter_num=10
thread_num=16

echo "*********************************************************"
path_source_vec=$katz_source_vec
path_dest_vec=$katz_dest_vec
echo "embedding"
katz_sampling --debug

path_dest_vec="no_input_dest"
echo "katz embedding node_rec"
embedding_node_rec --debug

echo "kat node_rec"
katz_node_rec --debug

echo "*********************************************************"
path_source_vec=$simrank_source_vec
path_dest_vec=$simrank_dest_vec
echo "embedding"
simrank_sampling --debug

path_dest_vec="no_input_dest"
echo "simrank embedding node_rec"
embedding_node_rec --debug

echo "simrank node_rec"
simrank_node_rec --debug

echo "*********************************************************"
path_source_vec=$app_source_vec
path_dest_vec=$app_dest_vec
echo "embedding"
ppr_sampling --debug

path_dest_vec="no_input_dest"
echo "ppr embedding node_rec"
embedding_node_rec --debug

echo "ppr node_rec"
ppr_node_rec --debug

echo "*********************************************************"
path_source_vec=$co_citation_source_vec
path_dest_vec=$co_citation_dest_vec
echo "embedding"
cocitation_sampling --debug

path_dest_vec="no_input_dest"
echo "cocitation embedding node_rec"
embedding_node_rec --debug

echo "cocitation node_rec"
cocitation_node_rec --debug
echo "*********************************************************"

echo "*********************************************************"
path_source_vec=$aa_source_vec
path_dest_vec=$aa_dest_vec
echo "embedding"
aa_sampling --debug

path_dest_vec="no_input_dest"
echo "aa embedding node_rec"
embedding_node_rec --debug

echo "aa node_rec"
aa_node_rec --debug
#embedding_link_pred --debug
#embedding_node_rec --debug
#embedding_multilabel_classification
#cocitation_node_rec --debug
#cocitation_link_pred --debug
#katz_node_rec --debug
#ppr_node_rec --debug
#simrank_node_rec --debug

#ppr_embedding --debug
#ppr_sampling --debug
#svd_mf --debug
#sgd_mf --debug
