package EmbeddingTools;


import SimMeasures.CoCitation;
import SimMeasures.PersonalizedPageRank;

import java.io.IOException;

public class CoCitationSampling extends SamplingFrameWork{
    CoCitation coCitation;
    public CoCitationSampling(String []argv) throws IOException{
        super(argv);
        coCitation = new CoCitation(train_graph, node_num);
        rio = 0.02;
    }

    double[] singleSourceSim(int qv){
        return coCitation.singleSourceSim(qv);
    }


    public static void main(String []args) throws IOException{
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_source_vec", "res/arxiv_trainout_ppr_embedding_source_vec",
                "--path_dest_vec", "res/arxiv_trainout_ppr_embedding_dest_vec",
                "--node_num", "5242",
                "--layer_size", "64",
                "--neg_sample", "5",
                "--iter", "10",
                "--debug"
        };

        if(EmbeddingBase.TEST_MODE)
            new PPRSampling(argv).run();
        else
            new PPRSampling(args).run();
    }
}