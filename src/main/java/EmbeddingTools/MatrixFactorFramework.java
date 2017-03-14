package EmbeddingTools;

import java.io.IOException;

/**
 * perform matrix factorization on each different tasks to verify the correctness of my finding.
 */
abstract class MatrixFactorFramework extends EmbeddingBase{
    public MatrixFactorFramework() throws IOException{
        train_graph = readEdgeListFromDisk(path_train_data, node_num);
        source_vec = new double[node_num][layer_size];
        dest_vec = new double[node_num][layer_size];
    }
    @Override
    void init() throws IOException{
//        train_graph = readEdgeListFromDisk(path_train_data, node_num);
//        source_vec = new double[node_num][layer_size];
//        dest_vec = new double[node_num][layer_size];
    }

    @Override
    void generateEmbeddings(){
        for(int iter = 0; iter < ITER_NUM; iter ++) {
            for (int i = 0; i < node_num; i++) {
                double sim[] = singleSourceScore(i);//source[i] * dest[j] = sim[j]
                for (int j = 0; j < node_num; j++) {
                    mfSgd(source_vec[i], dest_vec[j], sim[j]);
                }
            }
        }
    }

    abstract double []singleSourceScore(int qv);
    void mfSgd(double s[], double t[], double value){
        /**
         * min ||s * t - f||2, deriv(s) = 2 * tmp * t, deriv(t) = 2 * tmp * s
         */
        double part_gd = 2 * (vecMultiVec(s, t) - value);
        for(int i = 0; i < node_num; i++){
            double tmp = s[i];
            s[i] -= part_gd * rio * t[i];
            t[i] -= part_gd * rio * tmp;
        }
    }

}