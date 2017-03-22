package SimMeasures;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Here Co-citation is different from common neighbors, it's Jaccard coefficient,
 * J(i, j) = intersection(I_i, I_j) / union(I_i, I_j)
 */
public class CoCitation extends SimBase{
    int node_num;
    ArrayList<Integer> graph[];
    public CoCitation(ArrayList<Integer> graph[], int node_num){
        this.node_num = node_num;
        this.graph = graph;
    }

    @Override
    public double calculateSim(int from, int to) {
        /**
         * intersection / union
         */
        ArrayList<Integer> tmp_adj_from = (ArrayList<Integer>)graph[from].clone();
        tmp_adj_from.retainAll(graph[to]);
        int inter_size = tmp_adj_from.size();
        return 1.0 * inter_size /
                (graph[from].size() + graph[to].size() - inter_size);
    }


    @Override
    public double[] singleSourceSim(int qv){
        double res[] = new double[node_num];
        for(int i = 0; i < node_num; i++)
            res[i] = calculateSim(qv, i);
        return res;
    }
}