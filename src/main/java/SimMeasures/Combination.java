package SimMeasures;

import java.util.LinkedList;


public class Combination extends SimBase{
    PersonalizedPageRank ppr;
    CommonNeighbors commonNeighbors;
    Katz katz;
    SimRank simrank;
    JaccardCoeff jaccardCoeff;
    int node_num;
    public Combination(LinkedList<Integer> []graph, int node_num){
        ppr = new PersonalizedPageRank(graph, node_num);
        commonNeighbors = new CommonNeighbors(graph, node_num);
        katz = new Katz(graph, node_num);
        simrank = new SimRank(graph, node_num);
        jaccardCoeff = new JaccardCoeff(graph, node_num);
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
        normalize(rs_co);
        double rs_simrank[] = simrank.singleSourceSim(qv);
        normalize(rs_simrank);
        double rs_katz[] = katz.singleSourceSim(qv);
        normalize(rs_katz);
        double rs_ppr[] = ppr.singleSourceSim(qv);
        normalize(rs_ppr);
        double rs_jacc[] = jaccardCoeff.singleSourceSim(qv);
        normalize(rs_jacc);
        double co_weight = 0.2, simrank_weight = 0.3,
                katz_weight = 0.4, ppr_weight = 0.4, jacc_weight=0.2;
        for(int i =0; i< node_num; i++){
            rs[i] = rs_co[i] * co_weight + rs_katz[i] * katz_weight +
                    rs_simrank[i] * simrank_weight + rs_ppr[i] * ppr_weight +
                    rs_jacc[i] * jacc_weight;
        }

        return rs;
    }
    void normalize(double array[]){// normalize by the max element
        double max_sim = 0;
        for(int i=0; i< array.length; i++)
            max_sim = Math.max(max_sim, array[i]);
        if(max_sim != 0){
            for(int i=0; i< array.length; i++)
                array[i] /= max_sim;
        }
    }
}