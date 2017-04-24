package SimMeasures;

import JudgeTools.Edge;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class ReverseKatz extends SimBase{
    /**
     * S_j = \beta \cdot A \cdot S_j + \beta \cdot A_j (1)
     * S = \beta \cdot A \cdot S + \beta \cdot A. (2)
     * S = (1 - \beta cdot A)^{-1} * \beta \cdot A, which can be expanded as equation (2).
     * This is a paper.
     */
    Katz katz;
    public ReverseKatz(LinkedList<Edge> graph[], int node_num){
        katz = new Katz(genReverseGraph(graph, node_num), node_num);
    }
    public ReverseKatz(LinkedList<Edge> graph[], int node_num, double beta, int iter_num){
        katz = new Katz(genReverseGraph(graph, node_num), node_num, beta, iter_num);
    }
    @Override
    public double calculateSim(int from, int to){return katz.calculateSim(from, to);}

    @Override
    public double[] singleSourceSim(int qv){
        return katz.singleSourceSim(qv);
    }

}