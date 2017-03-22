package EmbeddingTools;
import SimMeasures.AA;

import java.io.IOException;

public class AASampling extends SamplingFrameWork{
    AA aa;
    public AASampling(String []argv) throws IOException{
        super(argv);
        aa = new AA(train_graph, node_num);
    }

    double[] singleSourceSim(int qv){
        return aa.singleSourceSim(qv);
    }


    public static void main(String []args) throws IOException{
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_source_vec", "res/arxiv_trainout_ppr_embedding_source_vec",
                "--path_dest_vec", "res/arxiv_trainout_ppr_embedding_dest_vec",
                "--node_num", "5242",
                "--layer_size", "64",
                "--neg_sample", "5",
                "--learning_rate", "0.02f",
                "--iter", "10",
                "--threshold", "0.0005",
                "--debug"
        };

        if(EmbeddingBase.TEST_MODE)
            new AASampling(argv).run();
        else
            new AASampling(args).run();
    }
}