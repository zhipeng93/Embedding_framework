package EmbeddingTools;

import SimMeasures.PersonalizedPageRank;
import org.ejml.alg.dense.decomposition.svd.implicitqr.SvdImplicitQrAlgorithm;
import org.ejml.data.DenseMatrix64F;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

import java.io.IOException;

public class SvdMF extends MatrixFactorFramework{
    PersonalizedPageRank ppr;
    public SvdMF(String []argv) throws IOException{
        super(argv);
        ppr = new PersonalizedPageRank(train_graph, node_num);
        score = new double[node_num][node_num];
    }

    double score[][];
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

    @Override
    void generateEmbeddings(){
        // generate the embeddings and store them in
        // source_vec and dest_vec
        genScores();
        ApplySVD();

    }
    void genScores(){
        /* generate score[][] */
        double tmp[];
        for(int i = 0; i < node_num; i ++) {
            tmp = ppr.singleSourceSim(i);
            sppmi(ppr.singleSourceSim(i));
            score[i] = tmp;

        }
    }
    void sppmi(double[] a){
        double pmi=0;
        for(int i=0; i<a.length; i++){
            if(a[i] < 1e-9)
                pmi = 0;
            else
                pmi = Math.max(Math.log(a[i] * node_num / neg), 0);
            a[i] = pmi;
        }
    }
    void ApplySVD(){
        /* apply SVD to score, and store u\sqrt(\sigma) in source_vec
        \sqrt(sigma)v in dest_vec
         */
//        DenseMatrix64F dmf = new DenseMatrix64F(score);
//        SimpleMatrix simpleMatrix = SimpleMatrix.wrap(dmf);
//        SimpleSVD svd = simpleMatrix.svd();
//        SimpleMatrix u = svd.getU();
//        SimpleMatrix v = svd.getV();

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

        if(EmbeddingBase.TEST_MODE)
            new SvdMF(argv).run();
        else
            new SvdMF(args).run();

    }
}