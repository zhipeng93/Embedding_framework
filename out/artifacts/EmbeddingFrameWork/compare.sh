#! /bin/bash

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
rkatz_node_rec --debug

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
echo "ppr_embedding"
ppr_embedding --debug

path_dest_vec="no_input_dest"
echo "ppr embedding node_rec"
embedding_node_rec --debug

path_source_vec=$ppr_sampling_source_vec
path_dest_vec=$ppr_sampling_dest_vec
echo "embedding"
ppr_sampling --debug

path_dest_vec="no_input_dest"
echo "ppr sampling node_rec"
embedding_node_rec --debug

echo "ppr node_rec"
ppr_node_rec --debug
rppr_node_rec --debug

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
echo "*********************************************************"

echo "*********************************************************"
path_source_vec=$sgd_mf_source_vec
path_dest_vec=$sgd_mf_dest_vec
sgd_mf
path_dest_vec="no_input_dest"
embedding_node_rec
echo "*********************************************************"
