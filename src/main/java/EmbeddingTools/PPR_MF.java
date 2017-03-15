package EmbeddingTools;

import SimMeasures.PersonalizedPageRank;

import java.io.IOException;

public class PPR_MF extends MatrixFactorFramework{
    PersonalizedPageRank ppr;
    public PPR_MF() throws IOException{
        ppr = new PersonalizedPageRank(train_graph, node_num);
    }
    @Override
    double[] singleSourceScore(int qv){
        double sim[] = ppr.singleSourceSim(qv);
        normalize(sim);
        return sim;
    }

    void normalize(double []s){
        double tail;
        for(int i = 0; i < s.length; i++){
            tail = Math.max(1e-10, s[i]);
            s[i] = Math.log(tail / neg);
        }
    }
    public static void main(String args[]) throws IOException{
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_source_vec", "res/arxiv_trainout_ppr_embedding_source_vec",
                "--path_dest_vec", "res/arxiv_trainout_ppr_embedding_dest_vec",
                "--node_num", "5242",
                "--layer_size", "64",
                "--neg_sample", "5",
                "--iter", "3",
        };
        PPR_MF pmf = new PPR_MF();
        if(pmf.TEST_MODE)
            pmf.run(argv);
        else pmf.run(args);

    }
}