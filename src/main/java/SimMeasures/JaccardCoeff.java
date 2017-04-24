package SimMeasures;

import JudgeTools.Edge;

import java.util.LinkedList;

/**
 * Jaccard coefficient,
 * J(i, j) = intersection(I_i, I_j) / union(I_i, I_j)
 */
public class JaccardCoeff extends SimBase{
    int node_num;
    LinkedList<Edge> graph[];
    public JaccardCoeff(LinkedList<Edge> graph[], int node_num){
        this.node_num = node_num;
        this.graph = graph;
    }

    @Override
    public double calculateSim(int qv, int i) {
        /**
         * intersection / union
         */
        int a[] = linkedList2Neighbors(graph[qv]);
        int b[] = linkedList2Neighbors(graph[i]);

        int inter_size = computeIntersection(a, b);

        if(inter_size == 0)
            return 0;
        else
            return 1.0 * inter_size /
                    (graph[qv].size() + graph[i].size() - inter_size);
    }


    @Override
    public double[] singleSourceSim(int qv){
        double res[] = new double[node_num];
        for(int i = 0; i < node_num; i++)
            res[i] = calculateSim(qv, i);
        return res;
    }
}