package EmbeddingTools;

import java.io.IOException;
import java.util.ArrayList;

abstract class SamplingFrameWork extends EmbeddingBase {
    static long gd_time;
    static long sim_computing_time;
    final static int MAX_POSITIVE_EDGE_NUM = 10000000;
    static int MAX_SAMPLE_EDGE_NUM = 1000000;

    int []from = new int[MAX_POSITIVE_EDGE_NUM];
    int []to = new int[MAX_POSITIVE_EDGE_NUM];
    double []weight = new double[MAX_POSITIVE_EDGE_NUM];
    int []alias;
    double []prob;
    ArrayList<Integer> positive_edges[];
    static double positive_threshold = 5e-4;


    int genPositiveTable(){
        positive_edges = new ArrayList[node_num];
        for(int i=0; i<node_num; i++)
            positive_edges[i] = new ArrayList<Integer>();

        int idx = 0;
        for(int i=0; i< node_num; i++){
            int i_deg = train_graph[i].size();
            double rs[] = singleSourceSim(i);
            for(int j=0; j< node_num; j++){
//                if(rs[j] < 5e-4)
//                    negative_edges[i].add(j);
                if(i==j || rs[j] < positive_threshold) {
                    continue;
                }
                else{
                    from[idx] = i;
                    to[idx] = j;
                    weight[idx] = rs[j] * Math.pow(i_deg, 1);
                    idx ++;
                    positive_edges[i].add(j);
                }
            }
        }
        return idx;
    }

    public SamplingFrameWork(String[] argv) throws IOException {
        super(argv);
    }

    public SamplingFrameWork() {
    }

    abstract double[] singleSourceSim(int a);

    int sampleAnPositiveEdge(int edge_num){
//        double max_prob = prob[edge_num - 1];
//        double x = random.nextDouble() * max_prob;
//        int left = 0, right = edge_num - 1;
//        while(left < right){
//            int mid = (left + right + 1) / 2;
//            if(prob[mid] < x){
//                left = mid + 1;
//            }
//            else {
//                right = mid;
//            }
//        }
//        return left;
        int k = (int)(edge_num * random.nextDouble());
        double tmp = random.nextDouble();
        if(tmp < prob[k])
            return k;
        else
            return alias[k];

    }

    @Override
    void generateEmbeddings() {
        long start, end;
        start = System.nanoTime();
        int p_edge_num = genPositiveTable();
        genAliasTable(p_edge_num);
        end = System.nanoTime();

        if(debug) {
            System.out.printf("similarity computing time is: %f \n",
                    (end - start) / 1e9);
            System.out.printf("positive edge num: %d\n", p_edge_num);
        }
        start = System.nanoTime();
        for (int iter = 0; iter < ITER_NUM; iter++) {
            sum_gd = 0;
            MAX_SAMPLE_EDGE_NUM = p_edge_num * 5;
            for (int id = 0; id < MAX_SAMPLE_EDGE_NUM; id++) {
                // use sim_{shuffle_ids[id]}[x] to update the gradient.
                int edge_id = sampleAnPositiveEdge(p_edge_num);

                double []e = new double[layer_size];
                UpdateVector(source_vec[from[edge_id]], dest_vec[to[edge_id]], 1, e);
                for(int neg_id=0; neg_id < neg; neg_id ++){
                    int neg_edge_id = sampleAnNegativeEdge(from[edge_id]);
                    UpdateVector(source_vec[from[edge_id]], dest_vec[neg_edge_id], 0, e);
                }
                for(int lay_id = 0; lay_id < layer_size; lay_id ++)
                    source_vec[from[edge_id]][lay_id] += e[lay_id];

            }
            if(debug)
                System.out.printf("sum gd is %f\n", sum_gd);
        }
        end = System.nanoTime();
        if(debug)
            System.out.printf("gd time is %f\n", (end - start) / 1e9);
    }

//    void jointUpdateVector(int u, double[] sim_array, int order[]) {
//        /**
//         * use (u,x) to update vector source[u] and dest[x], the update order is specified in order[].
//         *
//         * compute the model by jointly optimize the model. This can also be implemented via sampling
//         * with alias table.
//         * Also, the normalize function is very important.
//         */
//        for (int i = 0; i < node_num; i++) {
//            int v = order[i];
//            if (v == u)
//                continue;
//            // use sim(u, v) to update source_vec[u] and dest_vec[v]
//            if (sim_array[v] > 0)
//                batchUpdateVector(u, v, sim_array[v] * 200, 1);
//            else {
////                    if(random.nextDouble() < 0.2){
////                        UpdateVector(source_vec[u], dest_vec[v], 1, 0);
////                    }
//                //cut off
//            }
//
//        }

//    }
//
//    void batchUpdateVector(int source, int dest, double sim, int label) {
//        /**
//         * compute following the strict deriv, Which is a bactch form. Maybe the most important word
//         * pairs should be precomputed and stored. As a result, here we can use this.
//         *
//         * Both from and to should be stored. Maybe in a CSR format.
//         */
//        int batch_size = 1, idx = 0;
//        int batch_num = (int) (sim / batch_size);
//        while (idx < batch_num) {
//            idx++;
//            UpdateVector(source_vec[source], dest_vec[dest], batch_size, 1);
//            for (int i = 0; i < neg; i++) {
//                int neg_id = sampleAnNegativeEdge(source);
//                UpdateVector(source_vec[source], dest_vec[neg_id], 1, 0);
//            }
//        }
//
//    }

    int sampleAnNegativeEdge(int root) {
        int y = random.nextInt(node_num);
        while (positive_edges[root].contains(y))
            y = random.nextInt(node_num);
        return y;
    }

    void genAliasTable(int edge_num) {
        /**
         * generate alias table for efficient sampling for all the nodes pairs. The similarity between
         * word pairs is computed via singleSourceSim(), however, the edges with medium similarities
         * should not be added here.
         */
        alias = new int[edge_num];
        prob = new double[edge_num];
        double norm_prob[] = new double[edge_num];
        int large_block[] = new int[edge_num];
        int small_block[] = new int[edge_num];
        double sum = 0;
        int cur_small_blk, cur_large_blk;
        int num_small_blk = 0, num_large_blk = 0;
        for(int i=0; i< edge_num; i++)
            sum += weight[i];

        for(int i=0; i< edge_num; i++)
            norm_prob[i] = weight[i] *  edge_num / sum;
        for(int k = edge_num -1; k >= 0; k--){
            if(norm_prob[k] < 1)
                small_block[num_small_blk++] = k;
            else
                large_block[num_large_blk++] = k;
        }

        while( num_large_blk >0 && num_small_blk >0){
            cur_small_blk = small_block[--num_small_blk];
            cur_large_blk = large_block[--num_large_blk];
            prob[cur_small_blk] = norm_prob[cur_small_blk];
            alias[cur_small_blk] = cur_large_blk;
            norm_prob[cur_large_blk] = norm_prob[cur_large_blk] +
                    norm_prob[cur_small_blk] - 1;
            if(norm_prob[cur_large_blk] < 1)
                small_block[num_small_blk ++] = cur_large_blk;
            else
                large_block[num_large_blk ++] = cur_large_blk;

        }
        while(num_large_blk > 0)
            prob[large_block[--num_large_blk]] = 1;
        while(num_small_blk > 0)
            prob[small_block[--num_small_blk]] = 1;

    }


    void UpdateVector(double source[], double dest[],
                      int label, double []e) {
        /**
         * to maximize sim(u,v)p(v|u) = sim(u, v)log\sigmoid(u*v), the gradient is calculated as:
         * 1. [1 - \sigmoid(u*v)] * (-v)
         * 2. [1 - \sigmoid(u*v)] * (-u)
         */
        double sum = vecMultiVec(source, dest);
        double tmp = rio * (getSigmoid(sum) - label);

        sum_gd += Math.abs(tmp);
        /**
         * u = u1 - tmp * (-v1)
         * v = v1 - tmp * (-u1)
         */
        double tmp_source, tmp_dest;
        for (int i = 0; i < source.length; i++) {
            e[i] -= tmp * dest[i];
            dest[i] -= tmp * source[i];
//            tmp_source = source[i];
//            tmp_dest = dest[i];
//            source[i] -= tmp * tmp_dest;
//            dest[i] -= tmp * tmp_source;
        }

    }
}