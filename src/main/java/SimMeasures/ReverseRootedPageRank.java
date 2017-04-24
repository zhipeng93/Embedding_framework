package SimMeasures;

import JudgeTools.Edge;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * THIS is reversed ROOTED PageRank, which use the reversed graph of the original one to test
 * whether the opposite link does help to predicate good results.
 */
public class ReverseRootedPageRank extends SimBase{
    PersonalizedPageRank ppr;
    public ReverseRootedPageRank(LinkedList<Edge> graph[], int node_num){
        ppr = new PersonalizedPageRank(genReverseGraph(graph, node_num), node_num);
    }
    public ReverseRootedPageRank(LinkedList<Edge> graph[], int node_num,
               double restart_rate, int max_step){
        ppr = new PersonalizedPageRank(genReverseGraph(graph, node_num), node_num,
                restart_rate, max_step);
    }
    @Override
    public double[] singleSourceSim(int qv){
        return ppr.singleSourceSim(qv);
    }

    @Override
    public double calculateSim(int from, int to){return ppr.calculateSim(from, to);}
}