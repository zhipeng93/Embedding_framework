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
    RootedPageRank rootedPageRank;
    public ReverseRootedPageRank(LinkedList<Edge> graph[], int node_num){
        rootedPageRank = new RootedPageRank(genReverseGraph(graph, node_num), node_num);
    }
    public ReverseRootedPageRank(LinkedList<Edge> graph[], int node_num,
               double restart_rate, int max_step){
        rootedPageRank = new RootedPageRank(genReverseGraph(graph, node_num), node_num,
                restart_rate, max_step);
    }
    @Override
    public double[] singleSourceSim(int qv){
        return rootedPageRank.singleSourceSim(qv);
    }

    @Override
    public double calculateSim(int from, int to){return rootedPageRank.calculateSim(from, to);}
}