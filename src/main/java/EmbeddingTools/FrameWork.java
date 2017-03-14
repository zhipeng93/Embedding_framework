package EmbeddingTools;

import java.io.IOException;

abstract class FrameWork extends EmbeddingBase{


    /**
     * @param a
     * @return sim(a, x) in double[]
     */
    abstract double[] singleSourceSim(int a);

    /**
     * @param sim_array: the single source similarity array, normalize sim[],
     *                 which can be a map from [0,1] to [-1, 1], or others.
     * @return
     */
    void normalize(double []sim_array){
        /**
         * sim(u,v) --> log[sim(u,v) * |V|] - log(neg)], sim(u,v) refers to the ratio of (u,v)
         * in the whole corpus, not a similarity score.
         */
        for(int i = 0; i != sim_array.length; i++){
            sim_array[i] = translate(sim_array[i]);
        }
    }

    double translate(double sim){
        /**
         * if sim < 1e-10, we treat it as 1e-10
         */
        sim = Math.max(1e-10, sim);
        return Math.log(sim * node_num / neg);
    }

    @Override
    void generateEmbeddings(){
        int shuffle_dim1[] = new int[node_num];
        int shuffle_dim2[] = new int[node_num];
        for(int i = 0; i < node_num; i ++) {
            shuffle_dim1[i] = i;
            shuffle_dim2[i] = i;
        }

        double sim_array[];
        for(int iter = 0; iter < ITER_NUM; iter ++){
            sum_gd = 0;
            updateRio();
            System.out.printf("Iteration: %d\n", iter);
            shuffle(shuffle_dim1, random);
            for(int id = 0; id < node_num; id ++){
                // use sim_{shuffle_ids[id]}[x] to update the gradient.
                int u = shuffle_dim1[id];
                sim_array = singleSourceSim(u);
                normalize(sim_array);
                shuffle(shuffle_dim2, random);
                jointUpdateVector(u, sim_array, shuffle_dim2);
            }
            System.out.printf("sum gd is %f\n", sum_gd);
        }

    }
    static void updateRio(){
        rio -= 0.01;
    }
    void jointUpdateVector(int u, double []sim_array, int order[]){
        /**
         * use (u,x) to update vector source[u] and dest[x], the update order is specified in order[].
         *
         * compute the model by jointly optimize the model. This can also be implemented via sampling
         * with alias table.
         * Also, the normalize function is very important.
         */
        for(int i = 0; i < node_num; i++){
            int v = order[i];
            // use sim(u, v) to update source_vec[u] and dest_vec[v]
            if(Math.abs(sim_array[v]) > 0)
                UpdateVector(source_vec[u], dest_vec[v], sim_array[v]);
            else
                ;//cut off
        }

    }
    void batchUpdateVector(){
        /**
         * compute following the strict deriv, Which is a bactch form. Maybe the most important word
         * pairs should be precomputed and stored. As a result, here we can use this.
         *
         * Both from and to should be stored. Maybe in a CSR format.
         */

    }
    void genAliasTable(){
        /**
         * generate alias table for efficient sampling for all the nodes pairs. The similarity between
         * word pairs is computed via singleSourceSim(), however, the edges with medium similarities
         * should not be added here.
         */
    }


    void UpdateVector(double source[], double dest[], double sim){
        /**
         * to maximize sim(u,v)p(v|u) = sim(u, v)log\sigmoid(u*v), the gradient is calculated as:
         * 1. [1 - \sigmoid(u*v)] * (-v)
         * 2. [1 - \sigmoid(u*v)] * (-u)
         */
        double sum = vecMultiVec(source, dest);
        double tmp = rio * (1 - getSigmoid(sum)) * sim;

        sum_gd += tmp;
        /**
         * u = u1 - tmp * (-v1)
         * v = v1 - tmp * (-u1)
         */
        double tmp_source, tmp_dest;
        for(int i = 0; i < source.length; i++) {
            tmp_source = source[i];
            tmp_dest = dest[i];
            source[i] += tmp * tmp_dest;
            dest[i] += tmp * tmp_source;
        }

    }

    @Override
    void init() throws IOException{
        train_graph = readEdgeListFromDisk(path_train_data, node_num);
        source_vec = new double[node_num][layer_size];
        rand_init(source_vec, random);
        if(isDirectedEmbedding()) {
            dest_vec = new double[node_num][layer_size];
            rand_init(dest_vec, random);
        }
    }

}