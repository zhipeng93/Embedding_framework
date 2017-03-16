package EmbeddingTools;

import java.io.IOException;

/**
 * perform matrix factorization on each different tasks to verify the correctness of my finding.
 */
abstract class MatrixFactorFramework extends EmbeddingBase{
    public MatrixFactorFramework(String []argv) throws IOException{
        super(argv);
    }

    static double sum_loss = 0;

    /**
     * parameters for adam(a good solution for sgd)
     */
     double mt = 0, vt = 0;
     double last_ms[] = new double[this.layer_size],
             last_vs[] = new double[this.layer_size];

    double last_mt[] = new double[this.layer_size],
            last_vt[] = new double[this.layer_size];
     double mt_head = 0, vt_head = 0;

     double beta1 = 0.9, beta2 = 0.999;
     double beta1t = 0.9, beta2t = 0.999; //math.pow(0.9, t)
    static double eps = 1e-6;



    @Override
    void generateEmbeddings(){
        for(int iter = 0; iter < ITER_NUM; iter ++) {
            sum_loss = 0;
//            rio -= 0.001;
            for (int i = 0; i < node_num; i++) {
                double sim[] = singleSourceScore(i);//source[i] * dest[j] = sim[j]
                for (int j = 0; j < node_num; j++) {
//                    if(sim[j] == 0)
//                        continue;// only used in recommendation.
                    mfSgd(source_vec[i], dest_vec[j], sim[j]);
                }
            }
            System.out.printf("%d iteration, sum loss is %f\n", iter, sum_loss);
        }
    }

    abstract double []singleSourceScore(int qv);
    void mfSgd(double s[], double t[], double value){
        /**
         * min ||s * t - f||2, deriv(s) = 2 * tmp * t, deriv(t) = 2 * tmp * s
         */
        double part_gd = 2 * (vecMultiVec(s, t) - value);

        for(int i = 0; i < layer_size; i++){
            double tmp = s[i];
            s[i] -= part_gd * rio * t[i];
            t[i] -= part_gd * rio * tmp;
        }
//         adam solution
//        for(int i=0; i < layer_size; i++){
//            /**
//             * update t[i]
//             */
//            double tmp = t[i];
//            mt = beta1 * last_ms[i] + (1 - beta1) * part_gd * s[i];
//            vt = beta2 * last_vs[i] + (1 - beta2) *
//                    Math.pow(part_gd * s[i], 2);
//            last_ms[i] = mt;
//            last_vs[i] = vt;
//            mt_head = mt / (1 - beta1t);
//            vt_head = vt / (1 - beta2t);
//            t[i] -= rio * mt_head / (Math.sqrt(vt_head) + eps);
//
//            /**
//             * update s[i]
//             */
//            mt = beta1 * last_mt[i] + (1-beta1) * part_gd * tmp;
//            vt = beta2 * last_vt[i] + (1-beta2) *
//                    Math.pow(part_gd * tmp, 2);
//            last_mt[i] = mt;
//            last_vt[i] = vt;
//            mt_head = mt / (1 - beta1t);
//            vt_head = vt / (1 - beta2t);
//            s[i] -= rio * mt_head / (Math.sqrt(vt_head) + eps);
//
//        }
//        beta2t *= beta2;
//        beta1t *= beta1;
        sum_loss += Math.pow(vecMultiVec(s, t) - value, 2);
    }


}