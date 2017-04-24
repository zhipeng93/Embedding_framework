package SimMeasures;

import JudgeTools.Edge;

import java.util.LinkedList;

/**
 * Here Co-citation is different from common neighbors, it's Jaccard coefficient,
 * J(i, j) = intersection(I_i, I_j) / union(I_i, I_j)
 */
public class CommonNeighbors extends SimBase{
    int node_num;
    LinkedList<Edge> graph[];
    public CommonNeighbors(LinkedList<Edge> graph[], int node_num){
        this.node_num = node_num;
        this.graph = graph;
    }

    @Override
    public double calculateSim(int from, int to) {
        /**
         * intersection
         */
        int a[] = linkedList2Neighbors(graph[from]);
        int b[] = linkedList2Neighbors(graph[to]);

        return computeIntersection(a, b);
    }


    @Override
    public double[] singleSourceSim(int qv){
        double res[] = new double[node_num];
        for(int i = 0; i < node_num; i++)
            res[i] = calculateSim(qv, i);
        return res;
    }
}