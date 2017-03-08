package JudgeTools;
import java.io.IOException;
import java.util.*;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
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

    @Parameter(names = "--path_dest_vec",
            description = "path of dest_embedding_vecs, if not specified, this is a symmetric embedding")
     String path_dest_vec;

    double source_vec[][];
    double dest_vec[][];

    @Override
    double calculateScore(int from, int to) {
        if(path_dest_vec.equals("no_input_dest")){
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

        EmbeddingsLinkPred elp = new EmbeddingsLinkPred();
        JCommander jCommander;
        if(elp.TEST_MODE)
            jCommander =  new JCommander(elp, argv);
        else
            jCommander =  new JCommander(elp, args);

        if(elp.help){
            jCommander.usage();
            return;
        }
        elp.readEmbeddings();
        elp.calculateAUC();
    }

    void readEmbeddings() throws IOException {
        //source_vec = new double[node_num][layer_size];
        source_vec = JudgeUtils.read_embeddings(path_source_vec);
        if (!path_dest_vec.equals("no_input_dest")) {
            // there should be source_vec and dest_vec
            //dest_vec = new double[node_num][layer_size];
            dest_vec = JudgeUtils.read_embeddings(path_dest_vec);
        }
    }


    public double vec_multi_vec(double[] vi, double[] vj) {
        int len = vi.length;
        double score = 0;
        for (int kk = 0; kk < len; kk++) {
            score += vi[kk] * vj[kk];
        }
        return score;
    }
}