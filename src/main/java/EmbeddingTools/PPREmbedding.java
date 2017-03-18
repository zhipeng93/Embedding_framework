package EmbeddingTools;

import com.beust.jcommander.Parameter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * This is a sgd, but the order of updating source(w) and dest(con) is not suitable.
 * In this code, root node is updated in a batch manner while dest is updated every time.
 * The batch here means **one** word pair together with **multiple negative pairs**.
 */

/**
 * Use different number for nodes with different degrees to represent
 * the different number of random paths, auc increases but recall
 * decreases. WHy?
 */

public class PPREmbedding extends EmbeddingBase {
    /**
     * parameters for APP
     */
    @Parameter(names = "--jump_factor", description = "jump factor of random walks")
    private double jump_factor = 0.15f;

    @Parameter(names = "--max_step", description = "max steps for the random walk with restart")
    public static int max_step = 10;

    @Parameter(names = "--sample", description = "number of samples for each node")
    public static int SAMPLE = 200;

    public PPREmbedding(String []argv) throws IOException{
        super(argv);
    }
     /* used for adaptive learning rate of sgd*/


    /* random generator for sampling */
    public static Random r = new Random(0);

    /* used for measuring the likelihood of sgd. */
    static double sum_gd = 0;

    /* variables for testing the time consuming of each part.*/
    long sample_time = 0;
    long gd_time = 0;
    int total_sum = 0;

    public static void main(String[] args) throws NumberFormatException, IOException {
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_source_vec", "res/arxiv_trainout_ppr_embedding_source_vec",
                "--path_dest_vec", "res/arxiv_trainout_ppr_embedding_dest_vec",
                "--node_num", "5242",
                "--layer_size", "64",
                "--neg_sample", "5",
                "--iter", "10",
                "--learning_rate", "0.0025f",
                "--jump_factor", "0.15f",
                "--max_step", "10",
                "--sample", "200",
                "--debug",
        };
        if(EmbeddingBase.TEST_MODE){
            new PPREmbedding(argv).run();
        }
        else
            new PPREmbedding(args).run();
    }
    @Override
    void generateEmbeddings() throws IOException{
        /**
         * This code needs reconstruction.
         */
        generate_embeddings();

    }

    int sampleAnPositiveEdge(int start){
        long time_sample_start = System.nanoTime();

        int s = max_step;
        int id = -1;
        ArrayList<Integer> tmp_adj = train_graph[start];
        /**
         Here we need to sample the random walk with restart,
         Which is very expensive because it has to walk on the graph in
         mulltiple steps, here less than *step = 10*.
         */
        while (s-- > 0) {
            double jump = r.nextDouble();
            if (jump < jump_factor) {
                break;
            } else if (tmp_adj.size() == 0) {
                // the random walk stops here. =diff=
                break;
            } else {
                id = tmp_adj.get(r.nextInt(tmp_adj.size()));
                tmp_adj = train_graph[id];
            }
        }

        long time_sample_end = System.nanoTime();
        sample_time += time_sample_end - time_sample_start;
        return id;
    }

    int sampleAnNegativeEdge(int start){
        return r.nextInt(node_num);
    }
    public void generate_embeddings() throws IOException {
        for (int kk = 0; kk < ITER_NUM; kk++) {
            sum_gd = 0;
            for (int root = 0; root < node_num; root++) {
                ArrayList<Integer> adjs = train_graph[root];
                if (adjs == null || adjs.size() == 0)
                    continue;
                int root_size = adjs.size();
                int sample_num = (int)(SAMPLE * Math.sqrt(root_size) / 10);
                total_sum += sample_num;
                sample_num = SAMPLE;
                for (int i = 0; i < sample_num; i++) {
                    int id = sampleAnPositiveEdge(root);
                    /**
                     * For each positve sample, there are #neg negative samples. The
                     * computations occur is [(3*dim) multi-s + (2*dim) add-s] * #(neg + 1),
                     * which is O(5k*(neg+1))
                     */
                    if (id != -1) {
                        /**
                         * Use edge(root, id) to update the loss function.
                         */
                        double weight = 0;
                        double[] e = new double[layer_size];
                        updateVector(source_vec[root], dest_vec[id], 1, weight, e);

                        for (int j = 0; j < neg; j++) {
                            int nid = sampleAnNegativeEdge(root);
                            if (nid == root)
                                continue;
                            weight = 0;
                            updateVector(source_vec[root], dest_vec[nid], 0, weight, e);
                        }

                        for (int k = 0; k < layer_size; k++)
                            source_vec[root][k] += e[k];
                    }
                }
            }
            if(debug) {
                System.out.println("iter:" + kk + ":sum_gradient:" + sum_gd);
                System.out.printf("total samples %d\n", total_sum);
            }
        }
        System.out.printf("APP::sample time: %f, gd time: %f\n", sample_time / 1e9, gd_time / 1e9);
    }

    private void updateVector(double[] w, double[] c, int label, double weight,
                              double[] e) {
        /** use edge (root, dest) to update source(root), target(dest), which
         * are w[] and c[] respectively.*/
        long time_gd_start = System.nanoTime();
        double tmp_g = getSigmoid(vecMultiVec(w, c)) - label;
        sum_gd += Math.abs(tmp_g);
        for (int i = 0; i < w.length; i++) {
            double tmp_c = c[i];
            double tmp_w = w[i];
            e[i] -= rio * tmp_g * tmp_c;
            c[i] -= rio * tmp_g * tmp_w;
        }
        long time_gd_end = System.nanoTime();
        gd_time += time_gd_end - time_gd_start;
    }

}