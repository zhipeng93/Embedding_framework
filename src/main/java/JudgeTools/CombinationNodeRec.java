package JudgeTools;

import SimMeasures.Combination;
import SimMeasures.CommonNeighbors;

import java.io.IOException;

public class CombinationNodeRec extends NodeRec{
    Combination combination;
    public CombinationNodeRec(String []argv) throws IOException{
        super(argv);
        combination = new Combination(train_graph, node_num);
    }

    @Override
    double calculateScore(int from, int to) {
        return combination.calculateSim(from, to);
    }

    @Override
    double[] singleSourceScore(int qv){
        return combination.singleSourceSim(qv);
    }

    public static void main(String[] args) throws IOException {
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_test_data", "data/arxiv_adj_test.edgelist",
                "--topk", "10",
                "--debug",
                "--node_num", "5242",
        };

        if(JudgeBase.TEST_MODE)
            new CombinationNodeRec(argv).run();
        else
            new CombinationNodeRec(args).run();
    }
}