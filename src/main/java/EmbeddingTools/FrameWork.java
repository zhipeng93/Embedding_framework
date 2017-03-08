package EmbeddingTools;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Random;

abstract class FrameWork extends EmbeddingBase{
    /**
     * @param a
     * @param b
     * @return sim(a, b)
     */
    abstract double singlePair(int a, int b);

    /**
     * @param a
     * @return sim(a, x) in double[]
     */
    abstract double[] singleSourceSim(int a);

    ArrayList<Integer> [] read_graph(String path, int node_num) throws IOException{
        return EmbeddingUtils.readEdgeListFromDisk(path, node_num);
    }

    /**
     *
     * @param sim: the single source similarity array
     * @return normalized version of sim[], which can be a map from [0,1] to [-1, 1], or others.
     */
    abstract double[] normalize(double[] sim);

    ArrayList<Integer> [] graph; /* store the graph in an adjlist way */
    double source_vec[][]; /* store the source vectors */
    double dest_vec[][]; /* store the destination vectors */
    int ITER_NUM;

    Random random = new Random(System.currentTimeMillis());

    void rand_init(double[][] w) {

        for (int i = 0; i < w.length; i++) {
            double[] tmp = w[i];
            for (int j = 0; j < tmp.length; j++) {
                tmp[j] = (random.nextDouble() - 0.5) / this.layer_size;
            }
        }
    }
    void shuffle(int a[]){
        int count = a.length;
        for(int i = count; i > 1; i--){
            swap(a, i - 1, random.nextInt());
        }
    }
    void swap(int a[], int i, int j){
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }
    void generateEmbeddings(){
        int shuffle_dim1[] = new int[node_num];
        int shuffle_dim2[] = new int[node_num];


        for(int i = 0; i < node_num; i ++) {
            shuffle_dim1[i] = i;
            shuffle_dim2[i] = i;
        }

        double sim[] = new double[node_num];

        for(int iter = 0; iter < ITER_NUM; iter ++){
            shuffle(shuffle_dim1);
            for(int id = 0; id < node_num; id ++){
                // use sim_{shuffle_ids[id]}[x] to update the gradient.
                sim = singleSourceSim(shuffle_dim1[id]);
                normalize(sim);

                shuffle(shuffle_dim2);


            }
        }
    }


}