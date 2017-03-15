package JudgeTools;

import com.beust.jcommander.Parameter;

import java.io.IOException;
import java.util.EnumMap;

/**
 * For link predication, we use the embeddings trained from training_data to predicate the links that would exist
 * possibly in the test data.
 * We sample negative edges that do not exist in both train data and test data, the number of negative edges is
 * ratio * #test_edges.
 * For these positive and negative samples, we calulate AUC.
 */
public class EmbeddingsLinkPred extends LinkPred{
    @Parameter(names = "--path_source_vec", description = "path of source_embedding_vecs")
     String path_source_vec;

    @Parameter(names = "--path_dest_vec", description = "if not specified, this is a symmetric embedding")
     String path_dest_vec;

    double source_vec[][];
    double dest_vec[][];
    public EmbeddingsLinkPred(String []argv) throws IOException{
        super(argv);
        source_vec = read_embeddings(path_source_vec);
        if(is_directed_embedding())
            dest_vec = read_embeddings(path_dest_vec);
    }


    final String NO_DEST_VEC = "no_input_dest";

    boolean is_directed_embedding(){
        if(path_dest_vec.equals("") || path_dest_vec.equals(NO_DEST_VEC))
            return false;
        else
            return true;
    }

    @Override
    double calculateScore(int from, int to) {
        if(is_directed_embedding()){
            return vec_multi_vec(source_vec[from], source_vec[to]);
        }
        else{
            return vec_multi_vec(source_vec[from], dest_vec[to]);
        }
    }

    public static void main(String[] args) throws IOException {
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_test_data", "data/arxiv_adj_test.edgelist",
                "--path_source_vec", "res/arxiv_trainout_ppr_embedding_source_vec",
                "--path_dest_vec", "no_input_dest",
                "--negative_ratio", "10",
                "--debug",
                "--node_num", "5242",
        };

        if(JudgeBase.TEST_MODE)
            new EmbeddingsLinkPred(argv).run();
        else
            new EmbeddingsLinkPred(args).run();
    }
}