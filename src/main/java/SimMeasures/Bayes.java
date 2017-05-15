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
public class Bayes extends WeightedRPR{
    public Bayes(LinkedList<Edge> graph[], int node_num,
               double restart_rate, int max_step){
        super(graph, node_num, restart_rate, max_step);
        sum_total = 0;
        for(int i=0; i < node_num; i++)
            sum_total += out_weight[i];
//        sum_total /= 2;
    }

    float sum_total;


    @Override
    public double[] singleSourceSim(int qv){
        double result[] = super.singleSourceSim(qv);
        for(int i =0; i< node_num; i++){
            result[i] *= sum_total / out_weight[i];
        }
        return result;
    }

}