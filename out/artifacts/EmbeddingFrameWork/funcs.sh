#! /bin/bash

# paras
app_source_vec=$output_dir/APP_source.vec
app_dest_vec=$output_dir/APP_dest.vec
dp_vec=$output_dir/dp.vec
line_1_vec=$output_dir/line.vec1
line_2_vec=$output_dir/line.vec2
katz_source_vec=$output_dir/katz_source.vec
katz_dest_vec=$output_dir/katz_dest.vec
simrank_source_vec=$output_dir/simrank_source.vec
simrank_dest_vec=$output_dir/simrank__dest.vec
common_neighbors_source_vec=$output_dir/common_neighbors_source.vec
common_neighbors_dest_vec=$output_dir/common_neighbors_dest.vec
jaccard_source_vec=$output_dir/jaccard_source.vec
jaccard_dest_vec=$output_dir/jaccard_dest.vec
aa_source_vec=$output_dir/aa_source.vec
aa_dest_vec=$output_dir/aa_dest.vec
ppr_sampling_source_vec=$output_dir/ppr_sampling_source.vec
ppr_sampling_dest_vec=$output_dir/ppr_sampling_dest.vec
ppr_mf_source_vec=$output_dir/pprmf_source.vec
ppr_mf_dest_vec=$output_dir/pprmf_dest.vec
jaccard_mf_source_vec=$output_dir/jaccardmf_source.vec
jaccard_mf_dest_vec=$output_dir/jaccardmf_dest.vec
#group_info=$input_dir/flickr.group

# Judge Tools
embedding_link_pred(){
    java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar JudgeTools.EmbeddingsLinkPred --path_train_data $path_train_data --path_test_data $path_test_data --path_source_vec $path_source_vec --node_num $node_num --path_dest_vec $path_dest_vec --negative_ratio $neg_ratio $1
}

embedding_node_rec(){
    java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar JudgeTools.EmbeddingsNodeRec --thread_num $thread_num --path_train_data $path_train_data --path_test_data $path_test_data --path_source_vec $path_source_vec --path_dest_vec $path_dest_vec --topk $topk --node_num $node_num $1
}
embedding_multilabel_classification(){
    python MultiLabelClassification_group.py $path_source_vec $group_info 
}

common_neighbors_node_rec(){
    java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar JudgeTools.CommonNeighborsNodeRec --thread_num $thread_num --path_train_data $path_train_data --path_test_data $path_test_data --topk $topk --node_num $node_num $1
}

common_neighbors_link_pred(){
    java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar JudgeTools.CommonNeighborsLinkPred --path_train_data $path_train_data --path_test_data $path_test_data --negative_ratio $neg_ratio --node_num $node_num $1
}

jaccard_node_rec(){
    java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar JudgeTools.JaccardNodeRec --thread_num $thread_num --path_train_data $path_train_data --path_test_data $path_test_data --topk $topk --node_num $node_num $1
}

jaccard_link_pred(){
    java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar JudgeTools.JaccardLinkPred --path_train_data $path_train_data --path_test_data $path_test_data --negative_ratio $neg_ratio --node_num $node_num $1
}
aa_node_rec(){
    java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar JudgeTools.AANodeRec --thread_num $thread_num --path_train_data $path_train_data --path_test_data $path_test_data --topk $topk --node_num $node_num $1
}

aa_link_pred(){
    java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar JudgeTools.AALinkPred --path_train_data $path_train_data --path_test_data $path_test_data --negative_ratio $neg_ratio --node_num $node_num $1
}

katz_node_rec(){
    java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar JudgeTools.KatzNodeRec --thread_num $thread_num --path_train_data $path_train_data --path_test_data $path_test_data --topk $topk --node_num $node_num $1
}

rkatz_node_rec(){
    java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar JudgeTools.ReverseKatzNodeRec --thread_num $thread_num --path_train_data $path_train_data --path_test_data $path_test_data --topk $topk --node_num $node_num $1
}

ppr_node_rec(){
    java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar JudgeTools.PPRNodeRec --thread_num $thread_num --path_train_data $path_train_data --path_test_data $path_test_data --topk $topk --node_num $node_num $1
}

rppr_node_rec(){
    java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar JudgeTools.ReversePPRNodeRec --thread_num $thread_num --path_train_data $path_train_data --path_test_data $path_test_data --topk $topk --node_num $node_num $1
}
simrank_node_rec(){
    java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar JudgeTools.SimRankNodeRec --thread_num $thread_num --path_train_data $path_train_data --path_test_data $path_test_data --topk $topk --node_num $node_num $1
}

# Embedding Tools
ppr_embedding(){
    java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar EmbeddingTools.PPREmbedding --path_train_data $path_train_data --path_source_vec $path_source_vec --path_dest_vec $path_dest_vec --node_num $node_num --neg_sample 5 --layer_size 64 --sample 200 --max_step 10 --iter $iter_num --jump_factor 0.2f --learning_rate 0.01 $1
}
ppr_sampling(){
    java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar EmbeddingTools.PPRSampling --path_train_data $path_train_data --path_source_vec $path_source_vec --path_dest_vec $path_dest_vec --node_num $node_num --neg_sample 5 --layer_size 64 --iter $iter_num --learning_rate $learning_rate --threshold $threshold  --thread_num $thread_num --topk $topk_sampling $1
}
common_neighbors_sampling(){
    java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar EmbeddingTools.CommonNeighborsSampling --path_train_data $path_train_data --path_source_vec $path_source_vec --path_dest_vec $path_dest_vec --node_num $node_num --neg_sample 5 --layer_size 64 --iter $iter_num --learning_rate $learning_rate  --threshold $threshold --thread_num $thread_num --topk $topk_sampling $1
}
jaccard_sampling(){
    java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar EmbeddingTools.JaccardSampling --path_train_data $path_train_data --path_source_vec $path_source_vec --path_dest_vec $path_dest_vec --node_num $node_num --neg_sample 5 --layer_size 64 --iter $iter_num --learning_rate $learning_rate  --threshold $threshold --thread_num $thread_num --topk $topk_sampling $1
}
katz_sampling(){
    java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar EmbeddingTools.KatzSampling --path_train_data $path_train_data --path_source_vec $path_source_vec --path_dest_vec $path_dest_vec --node_num $node_num --neg_sample 5 --layer_size 64 --iter $iter_num --learning_rate $learning_rate --threshold $threshold --thread_num $thread_num --topk $topk_sampling $1
}
simrank_sampling(){
    java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar EmbeddingTools.SimRankSampling --path_train_data $path_train_data --path_source_vec $path_source_vec --path_dest_vec $path_dest_vec --node_num $node_num --neg_sample 5 --layer_size 64 --iter $iter_num --learning_rate $learning_rate --threshold $threshold --thread_num $thread_num --topk $topk_sampling $1
}

aa_sampling(){
    java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar EmbeddingTools.AASampling --path_train_data $path_train_data --path_source_vec $path_source_vec --path_dest_vec $path_dest_vec --node_num $node_num --neg_sample 5 --layer_size 64 --iter $iter_num --learning_rate $learning_rate --threshold $threshold --thread_num $thread_num --topk $topk_sampling $1
}

ppr_mf(){
    java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar EmbeddingTools.PPRMF --path_train_data $path_train_data --path_source_vec $path_source_vec --path_dest_vec $path_dest_vec --node_num $node_num --neg_sample 5 --layer_size 64 --iter $iter_num --learning_rate $learning_rate --threshold $threshold $1
}
jaccard_mf(){
    java -cp EmbeddingFrameWork.jar:jcommander-1.60.jar:ejml-0.25.jar EmbeddingTools.JaccardMF --path_train_data $path_train_data --path_source_vec $path_source_vec --path_dest_vec $path_dest_vec --node_num $node_num --neg_sample 5 --layer_size 64 --iter $iter_num --learning_rate $learning_rate --threshold $threshold $1
}
# cd $EMBEDDING_HOME/deepwalk
deepwalk(){
    cd $EMBEDDING_HOME/deepwalk
    python __main__.py --input $input_data --format edgelist --output $out_dir/dp.vec --window-size 6 --number-walks 80 --representation-size 64 --workers 1 --walk-length 10 --max-memory-data-size 100000000000
}

# cd $EMBEDDING_HOME/LINE/linux
line_order1(){
    ./line -train $input_data -output $out_dir/line.vec1 --size 64 -order 1 -threads 16 -sample 40
}
line_order2(){
    ./line -train $input_data -output $out_dir/line.vec2 --size 64 -order 2 -threads 16 -samples 40 
}

