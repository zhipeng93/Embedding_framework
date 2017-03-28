package EmbeddingTools;

import com.beust.jcommander.Parameter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

abstract class SamplingFrameWork extends EmbeddingBase {
    @Parameter(names = "--thread_num", description = "number of threads")
    protected int THREAD_NUM;
    @Parameter(names = "--threshold", description = "threshold for sampling framework")
    protected static double threshold;

    double positive_threshold;
    /**
     * In the sampling framework, we only need a sequential access of the adjlist.
     * So LinkedList is used.
     */
    LinkedList<Integer> train_graph[];


    public SamplingFrameWork(String[] argv) throws IOException {
        super(argv);
        train_graph = readEdgeListFromDisk(path_train_data, node_num);
    }

    public SamplingFrameWork() {
    }
    static int SAMPLE_EDGE_NUM = 1000000; // number of samples per iter.
    static double delta_gd = 0.0001;
    /**
     * variables to store the positive edges and alias table
     */
    int []from;
    int []to;
    double []weight;
    int []alias;
    double []prob;
    ArrayList<Integer> positive_edges[];
    ArrayList<PositiveEdge> tmp_positive_edges[];

    int genPositiveTable(){
        /**
         * TO DO parallel computing.
         * This method is different from the original PPR!!.
         */
        tmp_positive_edges = new ArrayList [THREAD_NUM];
        for(int i=0; i< THREAD_NUM; i++)
            tmp_positive_edges[i] = new ArrayList<PositiveEdge>();

        positive_edges = new ArrayList[node_num];
        for(int i=0; i<node_num; i++)
            positive_edges[i] = new ArrayList<Integer>();

        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_NUM);
        for(int threadId = 0; threadId < THREAD_NUM; threadId ++){
            threadPool.execute(new GenPositiveEdge(threadId));
        }
        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
            try {
                threadPool.awaitTermination(1, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                System.out.println("Waiting.");
                e.printStackTrace();
            }
        }
        int p_edge_num = 0;
        for(int threadId = 0; threadId < THREAD_NUM; threadId ++)
            p_edge_num += tmp_positive_edges[threadId].size();

        from = new int[p_edge_num];
        to = new int[p_edge_num];
        weight = new double[p_edge_num];
        int idx = 0;
        for(int threadId = 0; threadId < THREAD_NUM; threadId ++) {
            Iterator iter = tmp_positive_edges[threadId].iterator();
            while (iter.hasNext()) {
                PositiveEdge pe = (PositiveEdge) iter.next();
                from[idx] = pe.from;
                to[idx] = pe.to;
                weight[idx] = pe.score * train_graph[pe.from].size();//* Math.pow(i_deg, 1);
                positive_edges[pe.from].add(pe.to);
                idx++;
            }
        }
        assert idx == p_edge_num;
        return idx;
    }


//    int genPositiveTable(){
//        positive_edges = new ArrayList[node_num];
//        for(int i=0; i<node_num; i++)
//            positive_edges[i] = new ArrayList<Integer>();
//
//        ArrayList<Integer> from_list = new ArrayList<Integer>();
//        ArrayList<Integer> to_list = new ArrayList<Integer>();
//        ArrayList<Double> weight_list = new ArrayList<Double>();
//        int idx = 0;
//        for(int i=0; i< node_num; i++){
//            int i_deg = train_graph[i].size();
//            double rs[] = singleSourceSim(i);
//            for(int j=0; j< node_num; j++){
//                if(i==j || rs[j] < positive_threshold) {
//                    continue;
//                }
//                else{
//                    from_list.add(i);
//                    to_list.add(j);
//                    weight_list.add(rs[j] * Math.pow(i_deg, 1));
////                    weight_list.add(rs[j]);
//                    idx ++;
//                    positive_edges[i].add(j);
//                }
//            }
//        }
//        from = arrayList2IntArray(from_list);
//        to = arrayList2IntArray(to_list);
//        weight = arrayList2DoubleArray(weight_list);
//
//        return idx;
//    }

    double getThresholdBysketch(double ratio){
        /**
         * get the a threshold that satisfies:
         * 1. scores larger than the threshold are preserved;
         * 2. The number of the preserved node-pairs / |NODE| = ratio.
         */
        int sample_node_num = 100;
        int sample_score_each_node = 100;
        double scores[] = new double[100 * 100];
        int idx = 0;
        for(int i = 0; i < sample_node_num; i++){
            int qv = random.nextInt(node_num);
            double rs[] = singleSourceSim(qv);
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
        /**
         * get positive threshold first
         */
        positive_threshold = getThresholdBysketch(threshold);
        System.out.printf("threshold is %f\n", positive_threshold);

        long start, end;
        start = System.nanoTime();
//        int p_edge_num = genPositiveTable();
        int p_edge_num = genPositiveTable();

        genAliasTable(p_edge_num);
        end = System.nanoTime();

        if(debug) {
            System.out.printf("similarity computing time is: %f \n",
                    (end - start) / 1e9);
            System.out.printf("positive edge num: %d\n", p_edge_num);
        }
        start = System.nanoTime();
        /**
         * TO DO parallel updating source_vec[][], dest_vec[][], no-lock
         */
        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_NUM);
        for(int threadId = 0; threadId < THREAD_NUM; threadId ++){
            threadPool.execute(new UpdateViaSampling(rio, threadId, SAMPLE_EDGE_NUM, p_edge_num));
        }
        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
            try {
                threadPool.awaitTermination(1, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                System.out.println("Waiting.");
                e.printStackTrace();
            }
        }
//        for (int iter = 0; iter < ITER_NUM; iter++) {
//            for (int id = 0; id < SAMPLE_EDGE_NUM; id++) {
//                // use sim_{shuffle_ids[id]}[x] to update the gradient.
//                int edge_id = sampleAnPositiveEdge(p_edge_num);
//
//                double []e = new double[layer_size];
//                UpdateVector(source_vec[from[edge_id]], dest_vec[to[edge_id]], 1, e);
//                for(int neg_id=0; neg_id < neg; neg_id ++){
//                    int neg_edge_id = sampleAnNegativeEdge(from[edge_id]);
//                    UpdateVector(source_vec[from[edge_id]], dest_vec[neg_edge_id], 0, e);
//                }
//                for(int lay_id = 0; lay_id < layer_size; lay_id ++)
//                    source_vec[from[edge_id]][lay_id] += e[lay_id];
//
//            }
//            if(debug)
//                System.out.printf("sum gd is %f\n", sum_gd);
//
//            if(Math.abs(sum_gd - last_sum_gd) < delta_gd)
//                break;
//            else{
//
//
//            }
//        }
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


    double UpdateVector(double source[], double dest[],
                      int label, double []e) {
        /**
         * to maximize sim(u,v)p(v|u) = sim(u, v)log\sigmoid(u*v), the gradient is calculated as:
         * 1. [1 - \sigmoid(u*v)] * (-v)
         * 2. [1 - \sigmoid(u*v)] * (-u)
         * return "part gd"
         */
        double sum = vecMultiVec(source, dest);
        double tmp = rio * (getSigmoid(sum) - label);

//        sum_gd += Math.abs(tmp);
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
        return Math.abs(tmp);

    }
    public class UpdateViaSampling implements Runnable{
        int threadID, sampleNum, total_edge_num;
        double _sum_gd=0, _last_gd=Double.MAX_VALUE;
        double rio;
        public UpdateViaSampling(double rio, int threadID, int sampleNum, int total_edge_num){
            this.threadID = threadID;
            this.sampleNum = sampleNum;
            this.total_edge_num = total_edge_num;
            this.rio = rio;
        }
        void updateRio(){
            rio *= 0.9;
        }
        public void run(){
            for(int iter = 0; iter < ITER_NUM; iter ++) {
                updateRio();
                for (int i = 0; i < sampleNum; i++) {
                    updateOnce();
                }
                if(debug)
                    System.out.printf("Thread %d _sum_gd is %f\n", threadID, _sum_gd);

                if(Math.abs(_sum_gd - _last_gd) < delta_gd)
                    break;
                else{
                    _last_gd = _sum_gd;
                    _sum_gd = 0;
                }
            }
        }
        public void updateOnce(){
            int edge_id = sampleAnPositiveEdge(total_edge_num);
            double []e = new double[layer_size];
            _sum_gd += UpdateVector(source_vec[from[edge_id]], dest_vec[to[edge_id]], 1, e);
            for(int neg_id=0; neg_id < neg; neg_id ++){
                int neg_edge_id = sampleAnNegativeEdge(from[edge_id]);
                _sum_gd += UpdateVector(source_vec[from[edge_id]], dest_vec[neg_edge_id], 0, e);
            }
            for(int lay_id = 0; lay_id < layer_size; lay_id ++)
                source_vec[from[edge_id]][lay_id] += e[lay_id];
        }
    }
    public class GenPositiveEdge implements Runnable{
        int threadId;
        public GenPositiveEdge(int threadId){
            this.threadId = threadId;
        }
        public void run(){
            /**
             * len = node_num / threadId + 1;
             * answer singelSourceSim[qv], qv \in
             * [threadId * len, min(threadId *(len+1), node_num)
             */
            int len = node_num / THREAD_NUM + 1;
            int start = threadId * len;
            int end = Math.min((threadId + 1) * len, node_num);
            for(int i=start; i<end; i++){
                double []rs = singleSourceSim(i);
                for(int j = 0; j< node_num; j++) {
                    if (i == j || rs[j] < positive_threshold) {
                        continue;
                    } else {
                        tmp_positive_edges[threadId].add(new PositiveEdge(i, j, rs[j]));
                    }
                }
            }
        }
    }
}