package EmbeddingTools;

import com.beust.jcommander.Parameter;

import java.io.IOException;
import java.util.Arrays;

/**
 * perform matrix factorization on each different tasks to verify the correctness of my finding.
 */
abstract class MatrixFactorFramework extends EmbeddingBase{
    public MatrixFactorFramework(String []argv) throws IOException{
        super(argv);
    }
    @Parameter(names = "--threshold", description = "threshold for sampling framework")
    protected static double threshold;

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


    double getThresholdBysketch(double ratio){
        /**
         * get the a threshold that satisfies:
         * 1. scores larger than the threshold are preserved;
         * 2. The number of the preserved node-pairs / |NODE| = ratio.
         *
         * This is also consistent with sgns, which only concerns the
         * frequently word-context pairs. Selddom-occur ones are not
         * computed.
         */
        int sample_node_num = 100;
        int sample_score_each_node = 100;
        double scores[] = new double[100 * 100];
        int idx = 0;
        for(int i = 0; i < sample_node_num; i++){
            int qv = random.nextInt(node_num);
            double rs[] = singleSourceScore(qv);
            for(int j = 0; j< sample_score_each_node; j ++) {
                int tmp = random.nextInt(node_num);
                scores[idx++] = rs[tmp];
            }
        }
        Arrays.sort(scores);
        double threshold = scores[(int)((scores.length - 1) * (1 - ratio))] + 1e-20;
        /* "-1" to prevent indexOutOfBounds, 1e-20 to prevent case: 0 > 0 */
        return threshold;
    }

    @Override
    void generateEmbeddings() throws IOException{
        double cut_threshold = getThresholdBysketch(threshold);
        System.out.printf("threshold is %f\n", cut_threshold);
        for(int iter = 0; iter < ITER_NUM; iter ++) {
            sum_loss = 0;
//            rio -= 0.001;
            for (int i = 0; i < node_num; i++) {
                double sim[] = singleSourceScore(i);//source[i] * dest[j] = sim[j]
                for (int j = 0; j < node_num; j++) {
                    if(sim[j] < cut_threshold)
                        continue;// only used in recommendation.
                        // here we follow word2vec: also ignore not-frequent ones.
                    else {
                        double norm_score = Math.log(sim[j] * node_num / neg);
                        mfSgd(source_vec[i], dest_vec[j], norm_score);
                    }
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