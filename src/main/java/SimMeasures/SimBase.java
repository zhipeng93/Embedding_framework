package SimMeasures;

import java.util.ArrayList;
import java.util.Iterator;

abstract class SimBase{
    static ArrayList<Integer>[] genReverseGraph(ArrayList<Integer> train_graph[],
                                         int node_num) {
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
    public abstract double calculateSim(int from, int to);
    public abstract double [] singleSourceSim(int qv);
}