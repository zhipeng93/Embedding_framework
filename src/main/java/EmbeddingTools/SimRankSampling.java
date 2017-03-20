package EmbeddingTools;


import SimMeasures.PersonalizedPageRank;
import SimMeasures.SimRank;

import java.io.IOException;

public class SimRankSampling extends SamplingFrameWork{
    SimRank simrank;
    public SimRankSampling(String []argv) throws IOException{
        super(argv);
        simrank = new SimRank(train_graph, node_num);
    }

    double[] singleSourceSim(int qv){
        return simrank.singleSourceSim(qv);
    }


    public static void main(String []args) throws IOException{
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_source_vec", "res/arxiv_trainout_ppr_embedding_source_vec",
                "--path_dest_vec", "res/arxiv_trainout_ppr_embedding_dest_vec",
                "--node_num", "5242",
                "--layer_size", "64",
                "--neg_sample", "5",
                "--iter", "10",
                "--learning_rate", "0.015f",
                "--debug"
        };

        if(EmbeddingBase.TEST_MODE)
            new SimRankSampling(argv).run();
        else
            new SimRankSampling(args).run();
    }
}