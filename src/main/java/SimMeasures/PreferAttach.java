package SimMeasures;

import java.util.ArrayList;

/**
 * Score(i, j) = deg(i) * deg(j)
 */

public class PreferAttach extends SimBase{
    ArrayList<Integer> graph[];
    int node_num;
    int deg[];


    public PreferAttach(ArrayList<Integer> graph[], int node_num){
        this.graph = graph;
        this.node_num = node_num;
        deg = new int[node_num];
        initDeg();
    }
    void initDeg(){
        for(int i=0; i< node_num; i++)
            deg[i] = graph[i].size();
    }
    public double calculateSim(int from, int to){
        return deg[from] * deg[to];
    }
    public double [] singleSourceSim(int qv){
        double rs [] = new double[node_num];
        for(int i=0; i<node_num; i++)
            rs[i] = calculateSim(qv, i);
        return rs;
    }
}