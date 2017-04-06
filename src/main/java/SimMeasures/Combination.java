package SimMeasures;

import java.util.LinkedList;


public class Combination extends SimBase{
    PersonalizedPageRank ppr;
    CommonNeighbors commonNeighbors;
    Katz katz;
    SimRank simrank;
    int node_num;
    public Combination(LinkedList<Integer> []graph, int node_num){
        ppr = new PersonalizedPageRank(graph, node_num);
        commonNeighbors = new CommonNeighbors(graph, node_num);
        katz = new Katz(graph, node_num);
        simrank = new SimRank(graph, node_num);
        this.node_num = node_num;
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
    public double[] singleSourceSim(int qv) {
        /**
         * This method needs a careful parameterization.
         */
        double rs[] = new double[node_num];
        double rs_co[] = commonNeighbors.singleSourceSim(qv);
        double rs_simrank[] = simrank.singleSourceSim(qv);
        double rs_katz[] = katz.singleSourceSim(qv);
        double rs_ppr[] = ppr.singleSourceSim(qv);

        double co_weight = 0.2, simrank_weight = 0.3,
                katz_weight = 0.3, ppr_weight = 0.4;
        for(int i =0; i< node_num; i++){
            rs[i] = rs_co[i] * co_weight + rs_katz[i] * katz_weight +
                    rs_simrank[i] * simrank_weight + rs_ppr[i] * ppr_weight;
        }

        return rs;
    }
}