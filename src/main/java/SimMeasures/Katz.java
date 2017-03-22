package SimMeasures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Katz extends SimBase{
    /**
     * S_j = \beta \cdot A \cdot S_j + \beta \cdot A_j
     */
    double beta = 0.1;
    ArrayList<Integer> graph[];
    int ITER_NUM = 3;
    int node_num;
    public Katz(ArrayList<Integer> graph[], int node_num){
        this.graph = genReverseGraph(graph, node_num);
        this.node_num = node_num;
    }
    @Override
    public double calculateSim(int from, int to){return 0;}

    @Override
    public double[] singleSourceSim(int qv){
        double rs[][] = new double[2][node_num];
        ArrayList<Integer> qv_adj = graph[qv];
        for(int kk = 0; kk < ITER_NUM; kk++){
            Arrays.fill(rs[kk & 1], 0);
            for(int i = 0; i< node_num; i++){
                Iterator iter = graph[i].iterator();

                while(iter.hasNext()) {
                    int neigh = (Integer) iter.next();
                    rs[kk & 1][i] += rs[1 - (kk & 1)][neigh];
                }
                if(qv_adj.contains(i))
                    rs[kk & 1][i] += 1;
                rs[kk & 1][i] *= beta;
            }

        }
        return rs[ITER_NUM &1];
    }

}