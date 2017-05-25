package SimMeasures;

import JudgeTools.Edge;

import java.util.*;

/**
 * This can only be used in Bayes. Actually this class sets restart_rate=0; but sums the probability of each step.
 */
public class WeightedRPR extends SimBase{
    public WeightedRPR(){

    }
    public WeightedRPR(LinkedList<Edge> graph[], int node_num,
               double restart_rate, int max_step){
        this.graph = graph;
        this.node_num = node_num;
        this.reversedGraph = genReverseGraph(graph, node_num);
        init_out_weight_table();
        this.restart_rate = restart_rate;
        this.max_step = max_step;
    }
    public WeightedRPR(LinkedList<Edge> graph[], int node_num){
        this(graph, node_num, 0.5, 5);
    }
    LinkedList<Edge> reversedGraph[];
    int out_weight[];
    LinkedList<Edge> graph[];
    int node_num;
    double restart_rate;
    int max_step;

    @Override
    public double[] singleSourceSim(int qv){
        double p[][] = new double[2][node_num];
        double result[] = new double[node_num];

        p[0][qv] = 1;
        for (int step = 0; step < max_step; step++) {
            //use p[step & 1] to update p[1 - (step & 1)]
            Arrays.fill(p[1 - (step & 1)], 0);
            for (int i = 0; i < node_num; i++) {
                Iterator iter = reversedGraph[i].iterator();
                while (iter.hasNext()) {
                    Edge tmp = (Edge) iter.next();
                    int tmp_j = tmp.getTo();
                    if(p[step & 1][tmp_j] < 1e-5) /* \accelerate computing via some approximation.*/
                        continue;
                    int tmp_weight = tmp.getWeight();
                    // (tmp_j, i) is in the original graph, i.e., train_graph
                    p[1 - (step & 1)][i] += (1- restart_rate) * p[step & 1][tmp_j] * tmp_weight
                            / out_weight[tmp_j];
                }
            }
            p[1 - (step & 1)][qv] += restart_rate;
            for(int i = 0; i < node_num; i ++)
                result[i] += p[1 - (step & 1)][i];
        }

        return result;
    }

    /**
     * this method should never be used since PPR cannot handle this query
     * efficiently.
     */
    @Override
    public double calculateSim(int from, int to){return 0;}

    void init_out_weight_table(){
        out_weight = new int[node_num];
        for(int i=0; i < node_num; i++){
            LinkedList<Edge> tmp_adj_list = graph[i];
            Iterator iterator = tmp_adj_list.iterator();
            while(iterator.hasNext()){
                Edge tmp = (Edge) iterator.next();
                out_weight[i] += tmp.getWeight();
            }
        }
    }

}