package SimMeasures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * THIS is ROOTED PageRank, not a Personalized PageRank. Maybe a *special case*.
 * This is a class to compute personalize PageRank, the restart_rate and
 * max_step is specified in this file, also, users can set them via the
 * constructor.
 * If walk starts from node *root*, then p[i], i\neq root, p[i] is computed as:
 * p[i] = (1 - restart_rate) *\sum_{j \in :q(i)} { p[j] / out_degree[j] }
 */
public class PersonalizedPageRank extends SimBase{
    public PersonalizedPageRank(LinkedList<Integer> graph[], int node_num){
        this.graph = graph;
        this.node_num = node_num;
        this.reversedGraph = genReverseGraph(graph, node_num);
        init_out_degree_table();
        /* changing the formula of Rooted PageRank.*/
        init_in_degree_table();
    }
    public PersonalizedPageRank(LinkedList<Integer> graph[], int node_num,
               double restart_rate, int max_step){
        this(graph, node_num);
        this.restart_rate = restart_rate;
        this.max_step = max_step;
    }
    LinkedList<Integer> reversedGraph[];
    int out_degree[];
    int in_degree[];
    LinkedList<Integer> graph[];
    int node_num;
    double restart_rate = 0.9;
    int max_step = 3;
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
                    int j = (Integer) iter.next();
                    // (j, i) is in the original graph, i.e., train_graph
                    p[1 - (step & 1)][i] += (1- restart_rate) * p[step & 1][j] * 1.0 / out_degree[j];
                }
            }
//            p[1 - (step & 1)][qv] += restart_rate;
            p[1 - (step & 1)][qv] += restart_rate;
        }
//        for(int i=0; i< node_num; i++)
//            p[max_step & 1][i] *= out_degree[i];
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
        for(int i = 0; i < node_num; i++)
            out_degree[i] = graph[i].size();
    }
    void init_in_degree_table(){
        in_degree = new int[node_num];
        for(int i=0; i< node_num; i++)
            in_degree[i] = reversedGraph[i].size();
    }

}