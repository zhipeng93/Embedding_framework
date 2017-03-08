package EmbeddingTools;

import java.awt.image.ImagingOpException;
import java.io.IOException;

public class RootedPageRank extends FrameWork{
    double[] singleSourceSim(int i){
        double res[] = new double[];
        return res;
    }
    double singlePairSim(int i, int j){
        return 0;
    }

    void normalize(double []sim_array){

    }

    public static void main(String []args) throws IOException{
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_source_vec", "res/arxiv_trainout_ppr_embedding_source_vec",
                "--path_dest_vec", "res/arxiv_trainout_ppr_embedding_dest_vec",
                "--node_num", "5242",
                "--layer_size", "64",
                "--neg_sample", "5",
                "--iter", "10",
        };
        new RootedPageRank().run(argv);
    }
}