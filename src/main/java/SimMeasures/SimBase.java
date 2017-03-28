package SimMeasures;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

abstract class SimBase{
    static LinkedList<Integer>[] genReverseGraph(LinkedList<Integer> train_graph[],
                                         int node_num) {
        LinkedList<Integer> rs[] = new LinkedList[node_num];
        for (int i = 0; i < node_num; i++)
            rs[i] = new LinkedList<Integer>();
        for (int from = 0; from < node_num; from++) {
            Iterator iter = train_graph[from].iterator();
            while (iter.hasNext()) {
                int to = (Integer) iter.next();
                rs[to].add(from);
            }
        }
        return rs;
    }
    public abstract double calculateSim(int from, int to);
    public abstract double [] singleSourceSim(int qv);
    int [] linkedList2Array(LinkedList<Integer> list){
        int []rs = new int[list.size()];
        Iterator iter = list.iterator();
        int idx = 0;
        while(iter.hasNext()){
            rs[idx ++] = (Integer)(iter.next());
        }
        Arrays.sort(rs);
        return rs;
    }
}