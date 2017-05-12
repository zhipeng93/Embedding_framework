package SimMeasures;

import JudgeTools.Edge;

import java.util.*;

/**
 * THIS is ROOTED PageRank, not a Personalized PageRank. Maybe a *special case*.
 * This is a class to compute personalize PageRank, the restart_rate and
 * max_step is specified in this file, also, users can set them via the
 * constructor.
 * If walk starts from node *root*, then p[i], i\neq root, p[i] is computed as:
 * p[i] = (1 - restart_rate) *\sum_{j \in :q(i)} { p[j] / out_degree[j] }
 */
public class RootedPageRank extends SimBase{
    public RootedPageRank(LinkedList<Edge> graph[], int node_num){
        this.graph = graph;
        this.node_num = node_num;
        this.reversedGraph = genReverseGraph(graph, node_num);
        init_out_degree_table();
        /* changing the formula of Rooted PageRank.*/
//        init_in_degree_table();
//        init_random_walk_table();
//        init_pagerank();
    }
    public RootedPageRank(LinkedList<Edge> graph[], int node_num,
               double restart_rate, int max_step){
        this(graph, node_num);
        this.restart_rate = restart_rate;
        this.max_step = max_step;
    }
    LinkedList<Edge> reversedGraph[];
    int out_degree[];
    LinkedList<Edge> graph[];

//    int in_degree[];

    int node_num;
    double restart_rate = 0.9;
    int max_step = 3;
//    double pagerank[][];
//    int pagerank_iter = 10;
    /*store pagerank value for each node */
//    void init_pagerank(){
//        double damping_factor = 0.15;
//        pagerank = new double[2][node_num];
//        Random random = new Random(System.currentTimeMillis());
//        for(int i=0; i < node_num; i ++)
//            pagerank[0][i] = random.nextDouble();
//        int iter = 0;
//        while(iter ++ < pagerank_iter){
//            //use pagerank[1 - (iter & 1)] to compute pagerank[(iter & 1)]
//            for(int i = 0; i < node_num; i++){
//                //update pagerank[iter & 1][i]
//                pagerank[iter & 1][i] = damping_factor / node_num;
//                Iterator graph_iterator = reversedGraph[i].iterator();
//                while(graph_iterator.hasNext()){
//                    int in_neigh_i = (Integer) graph_iterator.next();
//                    int in_neigh_i_out_deg = graph[in_neigh_i].size();
//                    pagerank[iter & 1][i] += (1 - damping_factor) * pagerank[1 - (iter & 1)][in_neigh_i] / in_neigh_i_out_deg;
//                }
//            }
//
//        }
//        // the result is store in pagerank[1 - (iter & 1)][*], but it does not matter.
//    }
    /* Store number of random walks starting from each node*/
//    int random_walk_num[][];
//    void init_random_walk_table(){
//        random_walk_num = new int[node_num][max_step];
//        /**
//         * random_walk[nodeId][step]: number of random walks starting from each node with step=step
//         */
//        for(int i=0; i< node_num; i++)
//            random_walk_num[i][0] = 1;
//        for(int step=1; step < max_step; step++){
//            for(int node = 0; node < node_num; node ++){
//                Iterator iteror= graph[node].iterator();
//                while(iteror.hasNext()){
//                    random_walk_num[node][step] += random_walk_num[(Integer)iteror.next()][step - 1];
//                }
//                random_walk_num[node][step] += random_walk_num[node][step - 1]; //cumulate each step.
//            }
//        }
//    }
    @Override
    public double[] singleSourceSim(int qv){
        double p[][] = new double[2][node_num];
        p[0][qv] = 1;
        for (int step = 0; step < max_step; step++) {
            //use p[step & 1] to update p[1 - (step & 1)]
            Arrays.fill(p[1 - (step & 1)], 0);
            for (int i = 0; i < node_num; i++) {
                Iterator iter = reversedGraph[i].iterator();
                while (iter.hasNext()) {
                    Edge tmp = (Edge) iter.next();
                    int tmp_j = tmp.getTo();
                    int weight_j = tmp.getWeight();
                    // (j, i) is in the original graph, i.e., train_graph
                    p[1 - (step & 1)][i] += weight_j * (1 - restart_rate) * p[step & 1][tmp_j] * 1.0
                            / out_degree[tmp_j];
                }
            }
//            p[1 - (step & 1)][qv] += restart_rate;
            p[1 - (step & 1)][qv] += restart_rate;
        }
//        for(int i=0; i< node_num; i++)
//            p[max_step & 1][i] *= Math.sqrt(random_walk_num[qv][max_step - 1]);
//            p[max_step & 1][i] *= Math.pow(out_degree[qv], 1.05);
//            p[max_step & 1][i] *= random_walk_num[qv][max_step - 1];
//            p[max_step & 1][i] *= pagerank[1 - (1 & pagerank_iter)][qv];
        return p[max_step & 1];
    }

    /**
     * this method should never be used since PPR cannot handle this query
     * efficiently.
     */
    @Override
    public double calculateSim(int from, int to){return 0;}

    void init_out_degree_table(){
        out_degree = new int[node_num];
        for(int i = 0; i < node_num; i++) {
            Iterator iterator = graph[i].iterator();
            while(iterator.hasNext()) {
                out_degree[i] += ((Edge)iterator.next()).getWeight();
            }
        }
    }
//    void init_in_degree_table(){
//        in_degree = new int[node_num];
//        for(int i=0; i< node_num; i++)
//            in_degree[i] = reversedGraph[i].size();
//    }

}