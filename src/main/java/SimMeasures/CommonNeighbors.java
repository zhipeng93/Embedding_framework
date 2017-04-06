package SimMeasures;

import java.util.LinkedList;

/**
 * Here Co-citation is different from common neighbors, it's Jaccard coefficient,
 * J(i, j) = intersection(I_i, I_j) / union(I_i, I_j)
 */
public class CommonNeighbors extends SimBase{
    int node_num;
    LinkedList<Integer> graph[];
    public CommonNeighbors(LinkedList<Integer> graph[], int node_num){
        this.node_num = node_num;
        this.graph = graph;
    }

    @Override
    public double calculateSim(int from, int to) {
        /**
         * intersection
         */
        int a[] = linkedList2Array(graph[from]);
        int b[] = linkedList2Array(graph[to]);
        int inter_size = 0;
        int ida=0, idb = 0;
        while(ida < a.length && idb < b.length){
            if(a[ida] < b[idb])
                ida ++;
            else if(a[ida] > b[idb])
                idb ++;
            else{
                inter_size ++;
                ida ++;
                idb ++;
            }
        }
        return inter_size;
    }


    @Override
    public double[] singleSourceSim(int qv){
        double res[] = new double[node_num];
        for(int i = 0; i < node_num; i++)
            res[i] = calculateSim(qv, i);
        return res;
    }
}