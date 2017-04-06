package EmbeddingTools;


import SimMeasures.JaccardCoeff;

import java.io.IOException;

public class JaccardSampling extends SamplingFrameWork{
    JaccardCoeff jaccardCoeff;
    public JaccardSampling(String []argv) throws IOException{
        super(argv);
        jaccardCoeff = new JaccardCoeff(train_graph, node_num);

    }

    double[] singleSourceSim(int qv){
        return jaccardCoeff.singleSourceSim(qv);
    }


    public static void main(String []args) throws IOException{
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_source_vec", "res/arxiv_trainout_ppr_embedding_source_vec",
                "--path_dest_vec", "res/arxiv_trainout_ppr_embedding_dest_vec",
                "--node_num", "5242",
                "--layer_size", "64",
                "--neg_sample", "5",
                "--threshold", "0.0005",
                "--iter", "10",
                "--learning_rate", "0.02f",
                "--thread_num", "16",
                "--debug"
        };

        if(EmbeddingBase.TEST_MODE)
            new JaccardSampling(argv).run();
        else
            new JaccardSampling(args).run();
    }
}