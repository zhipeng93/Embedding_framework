package EmbeddingTools;

import SimMeasures.JaccardCoeff;
import SimMeasures.PersonalizedPageRank;

import java.io.IOException;
import java.util.LinkedList;

/**
 * The result is not as good as PPR. Also it's worse than APP.
 * The learning rate is hard to tune, usually by grid search, for normal
 * sgd, 0.015 is a good choice: 0.94auc, 0.48recall.
 *
 * For adam, 0.0025 is a good choice: 0.95auc, 0.53recall.
 * Adam takes a lot of time.
 * Also, we cannot ignore the small values since it's different from the
 * MF in recommendation tasks.
 *
 * Without ignoring the zero values, the time is:
 * 10s/epoch, adam 30s/epoch
 *
 * If loss of sgd fluctuates, the learning rate is too large.
 *
 * The best for PPR is 0.73 for recall.
 * The best for APP is 0.68 for recall.
 */
public class JaccardMF extends MatrixFactorFramework{
    JaccardCoeff jaccardCoeff;
    public JaccardMF(String []argv) throws IOException{
        super(argv);
        jaccardCoeff = new JaccardCoeff(train_graph, node_num);
    }

    @Override
    double []singleSourceScore(int qv){
        double tmp[] = jaccardCoeff.singleSourceSim(qv);
//        sppmi(tmp);
        return tmp;
    }

    void sppmi(double[] a){
        double pmi=0;
        for(int i=0; i<a.length; i++){
            if(a[i] < 1e-20)
                //seldom shows up.
                pmi = -10;
            else
                pmi = Math.log(a[i] * node_num / neg);
//                pmi = Math.max(Math.log(a[i]), 0);

                a[i] = pmi;
        }
    }
    public static void main(String []args) throws IOException{
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_source_vec", "res/arxiv_trainout_ppr_embedding_source_vec",
                "--path_dest_vec", "res/arxiv_trainout_ppr_embedding_dest_vec",
                "--node_num", "5242",
                "--layer_size", "64",
                "--neg_sample", "5",
                "--iter", "10",
                "--learning_rate", "0.2f",
                "--threshold", "0.01",
                "--debug",
        };

        if(EmbeddingBase.TEST_MODE)
            new JaccardMF(argv).run();
        else
            new JaccardMF(args).run();

    }

}