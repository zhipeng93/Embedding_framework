package EmbeddingTools;


import SimMeasures.CycleEmbedding;
import SimMeasures.Katz;
import SimMeasures.PersonalizedPageRank;

import java.io.IOException;

public class CycleSampling extends SamplingFrameWork{
    CycleEmbedding cycleEmbedding;
    public CycleSampling(String []argv) throws IOException{
        super(argv);
        cycleEmbedding = new CycleEmbedding(path_source_vec,
                path_dest_vec, node_num);
    }

    double[] singleSourceSim(int qv){
        return cycleEmbedding.singleSourceSim(qv);
    }


    public static void main(String []args) throws IOException{
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_source_vec", "res/arxiv_cycle_source_vec",
                "--path_dest_vec", "res/arxiv_cycle_dest_vec",
                "--node_num", "5242",
                "--layer_size", "64",
                "--neg_sample", "5",
                "--iter", "10",
                "--learning_rate", "0.02f",
                "--debug"
        };

        if(EmbeddingBase.TEST_MODE)
            new CycleSampling(argv).run();
        else
            new CycleSampling(args).run();
    }
}