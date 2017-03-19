package EmbeddingTools;


import SimMeasures.Combination;

import java.io.IOException;

public class CombinationSampling extends SamplingFrameWork{
    Combination combination;
    public CombinationSampling(String []argv) throws IOException{
        super(argv);
        combination = new Combination(train_graph, node_num);
        rio = 0.02;
    }

    double[] singleSourceSim(int qv){
        return combination.singleSourceSim(qv);
    }


    public static void main(String []args) throws IOException{
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_source_vec", "res/arxiv_cycle_source_vec",
                "--path_dest_vec", "res/arxiv_cycle_dest_vec",
                "--node_num", "5242",
                "--layer_size", "64",
                "--neg_sample", "5",
                "--iter", "10",
                "--debug"
        };

        if(EmbeddingBase.TEST_MODE)
            new CombinationSampling(argv).run();
        else
            new CombinationSampling(args).run();
    }
}