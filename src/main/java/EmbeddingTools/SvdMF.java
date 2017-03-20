package EmbeddingTools;

import SimMeasures.PersonalizedPageRank;
import org.ejml.alg.dense.decomposition.svd.implicitqr.SvdImplicitQrAlgorithm;
import org.ejml.data.DenseMatrix64F;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

import java.io.IOException;
import java.io.PrintStream;

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
    void generateEmbeddings() throws IOException{
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
    void ApplySVD() throws IOException{
        /* apply SVD to score, and store u\sqrt(\sigma) in source_vec
        \sqrt(sigma)v in dest_vec
         */
        DenseMatrix64F dmf = new DenseMatrix64F(score);
        SimpleMatrix simpleMatrix = SimpleMatrix.wrap(dmf);
        SimpleSVD svd = simpleMatrix.svd();

        SimpleMatrix u = svd.getU().extractMatrix(0, node_num, 0, layer_size);
        SimpleMatrix v = svd.getV().extractMatrix(0, node_num, 0, layer_size);
        SimpleMatrix w = svd.getW().extractMatrix(0, layer_size, 0, layer_size);
        // x = U * W * V^T

        for(int i=0; i< layer_size; i++){
            w.set(i, i, Math.sqrt(w.get(i, i)));
        }
        SimpleMatrix u_w = u.mult(w);
        SimpleMatrix w_v = w.mult(v.transpose());
//        (u_w.mult(w_v)).print();
        saveAsEmbeddings(u_w.getMatrix(), path_source_vec);
        saveAsEmbeddings(w_v.getMatrix(), path_dest_vec);
    }
    void saveAsEmbeddings(DenseMatrix64F A , String fileName)
            throws IOException{
        PrintStream fileStream = new PrintStream(fileName);

        fileStream.print(A.getNumRows()+" ");
        fileStream.println(A.getNumCols());
        for( int i = 0; i < A.numRows; i++ ) {
            fileStream.print(i+" ");
            for( int j = 0; j < A.numCols; j++ ) {
                fileStream.print(A.get(i,j)+" ");
            }
            fileStream.println();
        }
        fileStream.close();
    }
    public static void main(String args[]) throws IOException{
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_source_vec", "res/arxiv_trainout_ppr_embedding_source_vec",
                "--path_dest_vec", "res/arxiv_trainout_ppr_embedding_dest_vec",
                "--node_num", "5242",
                "--layer_size", "64",
                "--neg_sample", "5",
                "--learning_rate", "0.015f",
                "--iter", "3",
        };
        SvdMF svdmf;
        if(EmbeddingBase.TEST_MODE)
            svdmf = new SvdMF(argv);
        else
            svdmf = new SvdMF(args);

        // This is not consistent with other methods that starts with run()
        svdmf.generateEmbeddings();

    }
}