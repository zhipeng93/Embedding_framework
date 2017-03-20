package SimMeasures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class AA extends SimBase{
    /**
     * S_j = \beta \cdot A \cdot S_j + \beta \cdot A_j
     */
    ArrayList<Integer> reversedGraph[];
    int D[];
    ArrayList<Integer> graph[];
    int node_num;
    public AA(ArrayList<Integer> graph[], int node_num){
        this.graph = graph;
        this.reversedGraph = genReverseGraph(graph, node_num);
        this.node_num = node_num;
        D = new int[node_num];
        initDiag(D);
    }

    void initDiag(int []d){
        for(int i=0; i< node_num; i++){
            d[i] += graph[i].size() + reversedGraph[i].size();
        }
    }
    @Override
    public double calculateSim(int from, int to){
        int a[] = arrayList2Array(graph[from]);
        int b[] = arrayList2Array(reversedGraph[to]);
        double sum = 0;
        /**
         * compute the intersection of a[] and b[], normalized by d[x]
         */
        int ida=0, idb = 0;
        while(ida < a.length && idb < b.length){
            if(a[ida] < b[idb])
                ida ++;
            else if(a[ida] > b[idb])
                idb ++;
            else{
                sum += 1.0 / D[a[ida]];
                ida ++;
                idb ++;
            }
        }
        return sum;
    }
    int [] arrayList2Array(ArrayList<Integer> list){
        int []rs = new int[list.size()];
        Iterator iter = list.iterator();
        int idx = 0;
        while(iter.hasNext()){
            rs[idx ++] = (Integer)(iter.next());
        }
        Arrays.sort(rs);
        return rs;
    }

    @Override
    public double[] singleSourceSim(int qv){
        double rs[] = new double[node_num];
        for(int i=0; i< node_num; i++)
            rs[i] = calculateSim(qv, i);
        return rs;
    }

}