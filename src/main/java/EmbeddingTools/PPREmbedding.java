package EmbeddingTools;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.IOException;
import java.util.*;

/**
 * This is a sgd, but the order of updating source(w) and dest(con) is not suitable.
 * In this code, root node is updated in a batch manner while dest is updated every time.
 * The batch here means **one** word pair together with **multiple negative pairs**.
 */

public class PPREmbedding extends EmbeddingBase {
    /**
     * parameters for graphs
     */
    ArrayList<Integer> graph[];
    double[][] source_vec;
    double[][] dest_vec;
    /**
     * parameters for APP
     */
    @Parameter(names = "--jump_factor", description = "jump factor of random walks")
    private double jump_factor = 0.15f;

    @Parameter(names = "--max_step", description = "max steps for the random walk with restart")
    public static int max_step = 10;

    @Parameter(names = "--sample", description = "number of samples for each node")
    public static int SAMPLE = 200;

    /**
     * used for adaptive learning rate of sgd, may not change.
     */
    public static double alpha = 0.0025f;
    /**
     * random generator for sampling
     */
    public static Random r = new Random(0);
    /**
     * use to calculate e^i quickly
     */
    public static int MAX_EXP = 5;
    public static double[] expTable;
    public static int magic = 100;

    /**
     * used for measuring the likelihood of sgd.
     */
    static double sum_gd = 0;
    /**
     * variables for testing the time consuming of each part.
     */
    long sample_time = 0;
    long gd_time = 0;

    static {
        expTable = new double[1000];
        for (int i = 0; i < 1000; i++) {
            expTable[i] = Math.exp((i / 1000.0 * 2 - 1) * MAX_EXP); // Precompute the exp() table
            expTable[i] = expTable[i] / (expTable[i] + 1); // f(x) = x / (x + 1)
            // \sigmod_x = expTable[(int)((x + MAX_EXP) * (1000 / MAX_EXP / 2))];
            // if x \in [-MAX_EXP, MAX_EXP]
        }
    }

    public static void main(String[] args) throws NumberFormatException, IOException {
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_source_vec", "res/arxiv_trainout_ppr_embedding_source_vec",
                "--path_dest_vec", "res/arxiv_trainout_ppr_embedding_dest_vec",
                "--node_num", "5242",
                "--layer_size", "64",
                "--neg_sample", "5",
                "--iter", "10",
        };
        PPREmbedding pprEmbedding = new PPREmbedding();
        JCommander jCommander;
        if (pprEmbedding.TEST_MODE)
            jCommander = new JCommander(pprEmbedding, argv);
        else
            jCommander = new JCommander(pprEmbedding, args);

        if (pprEmbedding.help) {
            jCommander.usage();
            return;
        }
        pprEmbedding.readGraph();
        pprEmbedding.generate_embeddings();
        pprEmbedding.write_embeddings_to_disk();


    }

    public void rand_init(double[][] w) {
        for (int i = 0; i < w.length; i++) {
            double[] tmp = w[i];
            for (int j = 0; j < tmp.length; j++) {
                tmp[j] = (r.nextDouble() - 0.5) / this.layer_size;
            }
        }
    }

    public void generate_embeddings() throws IOException {

        source_vec = new double[node_num][layer_size];
        dest_vec = new double[node_num][layer_size];
        rand_init(source_vec);

        alpha = starting_alpha;
        for (int kk = 0; kk < ITER_NUM; kk++) {
            sum_gd = 0;
//			alpha = Math.max(0.0001, starting_alpha * (iter-kk)/iter);
            for (int root = 0; root < node_num; root++) {

                ArrayList<Integer> adjs = graph[root];
                if (adjs == null || adjs.size() == 0)
                    continue;
                for (int i = 0; i < SAMPLE; i++) {
                    // sampled: from a to b
                    int s = max_step;
                    int id = -1;
                    ArrayList<Integer> tmp_adj = adjs;
                    /**
                     Here we need to sample the random walk with restart,
                     Which is very expensive because it has to walk on the graph in
                     mulltiple steps, here less than *step = 10*.
                     */
                    long time_sample_start = System.nanoTime();
                    while (s-- > 0) {
                        double jump = r.nextDouble();
                        if (jump < jump_factor) {
                            break;
                        } else if (tmp_adj.size() == 0) {
                            // the random walk stops here. =diff=
                            break;
                        } else {
                            id = tmp_adj.get(r.nextInt(tmp_adj.size()));
                            tmp_adj = graph[id];
                        }
                    }
                    long time_sample_end = System.nanoTime();
                    sample_time += time_sample_end - time_sample_start;
                    /**
                     * For each positve sample, there are #neg negative samples. The
                     * computations occur is [(3*dim) multi-s + (2*dim) add-s] * #(neg + 1),
                     * which is O(5k*(neg+1))
                     */
                    long time_gd_start = System.nanoTime();
                    if (id != -1) {
                        /**
                         * Use edge(root, id) to update the loss function.
                         */
                        double weight = 0;
//                        double weight = graph[id].size() * 1.0 / magic;/* ?? */
                        double[] e = new double[layer_size];
                        // update as :word a, context b
                        updateVector(source_vec[root], dest_vec[id], 1, weight, e);

                        for (int j = 0; j < neg; j++) {
                            int nid = r.nextInt(node_num);
                            if (nid == root)
                                continue;
//                            List<Integer> adj = graph[nid];
                            weight = 0;
//                            if (adj != null && !adj.isEmpty())
//                                weight = graph[nid].size() * 1.0 / magic;
//                            else
//                                continue;
                            updateVector(source_vec[root], dest_vec[nid], 0, weight, e);
                        }

                        for (int k = 0; k < layer_size; k++)
                            source_vec[root][k] += e[k];
                    }
                    long time_gd_end = System.nanoTime();
                    gd_time += time_gd_end - time_gd_start;
                }
            }
            if(debug)
                System.out.println("iter:" + kk + ":sum_gradient:" + sum_gd);
        }
        System.out.printf("APP::sample time: %f, gd time: %f\n", sample_time / 1e9, gd_time / 1e9);
    }

    private void updateVector(double[] w, double[] c, int label, double weight,
                              double[] e) {
        /** use edge (root, dest) to update source(root), target(dest), which
         * are w[] and c[] respectively.*/
        double neg_g = calculateGradient(label, w, c, weight);
        for (int i = 0; i < w.length; i++) {
            double tmp_c = c[i];
            double tmp_w = w[i];
            e[i] += neg_g * tmp_c;
            c[i] += neg_g * tmp_w;
        }
    }

    private double calculateGradient(int label, double[] w, double[] c, double weight) {
        double f = 0, g;
        for (int i = 0; i < this.layer_size; i++)
            f += w[i] * c[i];
        double sigmoid = getSigmoid(f);

        g = (label - sigmoid) * alpha;
        /**
         * I do not understand what the author is computing.
         */
//        if (label == 1) {
//            global_likelihood += Math.log(sigmoid);
//        } else
//            global_likelihood += Math.log(1 - sigmoid);
        sum_gd += Math.abs(sigmoid - label);
        return g;
    }

    public double getSigmoid(double f) {
        if (f > MAX_EXP)
            return 1;
        else if (f < -MAX_EXP)
            return -1;
        else
            return expTable[(int) (f + MAX_EXP) * (1000 / MAX_EXP / 2)];
    }

    public void write_embeddings_to_disk() throws IOException {
        EmbeddingUtils.write_array_to_disk(path_source_vec, source_vec);
        EmbeddingUtils.write_array_to_disk(path_dest_vec, dest_vec);
    }

    public void readGraph() throws IOException {
        this.graph = EmbeddingUtils.readEdgeListFromDisk(path_train_data, node_num);
    }
}




