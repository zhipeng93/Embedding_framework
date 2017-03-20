package EmbeddingTools;

import com.beust.jcommander.Parameter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

abstract class SamplingFrameWork extends EmbeddingBase {
    /**
     * variables to store the positive edges and alias table
     */
    int []from;
    int []to;
    double []weight;
    int []alias;
    double []prob;
    ArrayList<Integer> positive_edges[];

    @Parameter(names = "--threshold", description = "threshold for sampling framework")
    protected static double positive_threshold = 5e-4;

    @Parameter(names = "--samplePerNodePair", description = "sample number for each node-pair during each iteration.")
    protected static int num_per_node_pair_per_iter = 5;


    int genPositiveTable(){

        ArrayList<Integer> from_list = new ArrayList<Integer>();
        ArrayList<Integer> to_list = new ArrayList<Integer>();
        ArrayList<Double> weight_list = new ArrayList<Double>();

        positive_edges = new ArrayList[node_num];
        for(int i=0; i<node_num; i++)
            positive_edges[i] = new ArrayList<Integer>();

        int idx = 0;
        for(int i=0; i< node_num; i++){
            int i_deg = train_graph[i].size();
            double rs[] = singleSourceSim(i);
            for(int j=0; j< node_num; j++){
                if(i==j || rs[j] < positive_threshold) {
                    continue;
                }
                else{
                    from_list.add(i);
                    to_list.add(j);
                    weight_list.add(rs[j] * Math.pow(i_deg, 1));
                    idx ++;
                    positive_edges[i].add(j);
                }
            }
        }

        from = arrayList2IntArray(from_list);
        to = arrayList2IntArray(to_list);
        weight = arrayList2DoubleArray(weight_list);

        return idx;
    }

    double[] arrayList2DoubleArray(ArrayList<Double> list){
        double []rs = new double[list.size()];
        Iterator iter = list.iterator();
        int idx = 0;
        while(iter.hasNext())
            rs[idx ++] = (Double)iter.next();
        return rs;
    }

    int[] arrayList2IntArray(ArrayList<Integer> list){
        int []rs = new int[list.size()];
        Iterator iter = list.iterator();
        int idx = 0;
        while(iter.hasNext())
            rs[idx ++] = (Integer) iter.next();
        return rs;
    }

    public SamplingFrameWork(String[] argv) throws IOException {
        super(argv);
    }

    public SamplingFrameWork() {
    }

    abstract double[] singleSourceSim(int a);

    int sampleAnPositiveEdge(int edge_num){
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
            int SAMPLE_EDGE_NUM = p_edge_num * num_per_node_pair_per_iter;
            for (int id = 0; id < SAMPLE_EDGE_NUM; id++) {
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