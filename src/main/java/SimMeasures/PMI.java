package SimMeasures;

import JudgeTools.Edge;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Here Co-citation is different from common neighbors, it's Jaccard coefficient,
 * J(i, j) = intersection(I_i, I_j) / union(I_i, I_j)
 */
public class PMI extends SimBase{
    int node_num;
    LinkedList<Edge> graph[];
    float cds;
    int out_degree[];
    double in_degree[]; // x = pow(x, cds)
    double sum_total;
    public PMI(LinkedList<Edge> graph[], int node_num, float cds){
        this.node_num = node_num;
        this.graph = graph;
        this.cds = cds;
        init_degree_table();
    }

    void init_degree_table(){
        out_degree = new int[node_num];
        in_degree = new double[node_num];
        for(int i = 0; i < node_num; i++) {
            Iterator iterator = graph[i].iterator();
            while(iterator.hasNext()) {
                Edge tmp = (Edge)(iterator.next());
                int _to = tmp.getTo();
                int _weight = tmp.getWeight();
                out_degree[i] += _weight;
//                in_degree[_to] += Math.pow(_weight, cds);
                in_degree[_to] += _weight;
            }
        }
        for(int i=0; i< node_num; i++) {
            in_degree[i] = Math.pow(in_degree[i], cds);
            sum_total += in_degree[i];
        }
        sum_total /= 2;
    }

    @Override
    public double calculateSim(int from, int to) {
//        /**
//         * return exp[pmi(from, to)]
//         * pmi(from, to) = #(w, c) / [#w * #c)] * |D|
//         */
//        int a[] = linkedList2Neighbors(graph[from]);
//        int b[] = linkedList2Neighbors(graph[to]);
//
//        return computeIntersection(a, b);
        return -1;
    }


    @Override
    public double[] singleSourceSim(int qv){
        double pmi[] = new double[node_num];
        Iterator iterator = graph[qv].iterator();
        while(iterator.hasNext()){
            Edge tmp = (Edge) iterator.next();
            int _to = tmp.getTo();
            int _weight = tmp.getWeight();
            pmi[_to] = sum_total * _weight / (in_degree[_to] * out_degree[qv]);
        }

        return pmi;
    }
}