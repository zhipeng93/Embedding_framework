package SimMeasures;

import java.util.LinkedList;

/**
 * Adamic Adar.
 * Score(x, y) = \sum_{common neighbors i} \frac{1}{log(deg(i))}
 * Assign more weights to rare features.
 */


public class AA extends SimBase{
    double _logd[];
    /* _logd[i] = 1 / log(deg(i))*/
    LinkedList<Integer> graph[];
    int node_num;
    public AA(LinkedList<Integer> graph[], int node_num){
        this.graph = graph;
        this.node_num = node_num;
        _logd = new double[node_num];
        initDiag(_logd);
    }

    void initDiag(double []d){
        for(int i=0; i< node_num; i++){
            int size = graph[i].size();
            if(size == 0)
                d[i] = 0;
            else
                d[i] = 1.0 / Math.log(size);
        }
    }
    @Override
    public double calculateSim(int from, int to){
        int a[] = linkedList2Array(graph[from]);
        int b[] = linkedList2Array(graph[to]);
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
                sum += _logd[a[ida]];
                ida ++;
                idb ++;
            }
        }
        return sum;
    }


    @Override
    public double[] singleSourceSim(int qv){
        double rs[] = new double[node_num];
        for(int i=0; i< node_num; i++)
            rs[i] = calculateSim(qv, i);
        return rs;
    }

}