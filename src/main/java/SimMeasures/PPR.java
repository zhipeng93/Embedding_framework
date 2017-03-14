package SimMeasures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class PPR{
    public PPR(ArrayList<Integer> train_graph[], int node_num){
        this.train_graph = train_graph;
        this.node_num = node_num;
        reversedGraph = genReverseGraph();
        init_out_degree_table();
    }
    ArrayList<Integer> reversedGraph[];
    int out_degree[];
    ArrayList<Integer> train_graph[];
    int node_num;
    final double restart_rate = 0.15;
    final int max_step = 5;

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
            p[1 - (step & 1)][qv] += restart_rate;
        }
        return p[max_step & 1];
    }

    void init_out_degree_table(){
        out_degree = new int[node_num];
        for(int i = 0; i < node_num; i++)
            out_degree[i] = train_graph[i].size();
    }
    ArrayList<Integer>[] genReverseGraph() {
        ArrayList<Integer> rs[] = new ArrayList[node_num];
        for (int i = 0; i < node_num; i++)
            rs[i] = new ArrayList<Integer>();
        for (int from = 0; from < node_num; from++) {
            Iterator iter = train_graph[from].iterator();
            while (iter.hasNext()) {
                int to = (Integer) iter.next();
                rs[to].add(from);
            }
        }
        return rs;
    }

}