package SimMeasures;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import JudgeTools.Edge;

abstract class SimBase{
    static LinkedList<Edge>[] genReverseGraph(LinkedList<Edge> train_graph[],
                                         int node_num) {
        LinkedList<Edge> rs[] = new LinkedList[node_num];
        for (int i = 0; i < node_num; i++)
            rs[i] = new LinkedList<Edge>();
        for (int from = 0; from < node_num; from++) {
            Iterator iter = train_graph[from].iterator();
            while (iter.hasNext()) {
                Edge tmp = (Edge)iter.next();
                int to = tmp.getTo();
                int weight = tmp.getWeight();
                rs[to].add(new Edge(from, weight));
            }
        }
        return rs;
    }
    public abstract double calculateSim(int from, int to);
    public abstract double [] singleSourceSim(int qv);
    int [] linkedList2Neighbors(LinkedList<Edge> list){
        int []rs = new int[list.size()];
        Iterator iter = list.iterator();
        int idx = 0;
        while(iter.hasNext()){
            Edge tmp = (Edge)iter.next();
            rs[idx ++] = tmp.getTo();
        }
        Arrays.sort(rs);
        return rs;
    }

    int computeIntersection(int []a, int[]b){
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
}