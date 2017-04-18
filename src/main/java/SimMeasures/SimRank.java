package SimMeasures;

import java.util.Iterator;
import java.util.LinkedList;

public class SimRank extends SimBase{
    double decay_factor = 1;
    int max_step = 5;
    int node_num;
    LinkedList<Integer> graph[];
    LinkedList<Integer> reverse_graph[];
    public SimRank(LinkedList<Integer> []graph, int node_num){
        this.graph = graph;
        this.node_num = node_num;
        reverse_graph = genReverseGraph(graph, node_num);
    }
    public SimRank(LinkedList<Integer> []graph, int node_num,
                   int max_step, double decay_factor){
        this(graph, node_num);
        this.decay_factor = decay_factor;
        this.max_step = max_step;
    }

    /**
     * this method should never be used since PPR cannot handle this query
     * efficiently.
     */
    @Override
    public double calculateSim(int from, int to) {
        return 0;
    }

    @Override
    public double[] singleSourceSim(int qv){
        /**
         * compute simrank(qv, *) w.r.t qv.
         */
        double u[][] = new double[max_step + 1][node_num];
        double v[][] = new double[2][node_num];
        u[0][qv] = 1;

        for (int l = 1; l < max_step + 1; l++) {
            /**
             * compute u[l] using u[l-1]
             */
            for (int i = 0; i < node_num; i++) {
                double temp = u[l - 1][i];
                if (temp == 0.0)
                    continue;
                else {
                    LinkedList<Integer> i_list = reverse_graph[i];
                    int deg = i_list.size();
                    Iterator<Integer> iter = i_list.iterator();
                    while (iter.hasNext()) {
                        int _to = iter.next();
                        u[l][_to] += temp * 1.0 / deg;
                    }
                }
            }
        }
        /**
         * use u[l] to compute v[k]
         */
        for (int i = 0; i < node_num; i++) {
            v[0][i] = u[max_step][i];
        }
        for (int l = 1; l < max_step + 1; l++) {
            /**
             * use v[l-1] to compute v[l], use v[1-(l&1)] to compute v[l&1]
             */
            for (int i = 0; i < node_num; i++) {
                v[l & 1][i] = u[max_step - l][i];
            }
            for (int i = 0; i < node_num; i++) {
                if (v[1 - (l & 1)][i] == 0)
                    continue;
                else {
                    LinkedList<Integer> i_list = graph[i];
                    Iterator iter = i_list.iterator();
                    while (iter.hasNext()) {
                        int to = (Integer) iter.next();
                        int deg = reverse_graph[to].size();
                        v[l & 1][to] += decay_factor * 1.0 / deg * v[1 - (1 & l)][i];
                    }
                }
            }
        }

        return v[max_step & 1];

    }

}