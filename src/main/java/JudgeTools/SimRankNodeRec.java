package JudgeTools;

import SimMeasures.SimRank;

import java.io.IOException;

public class SimRankNodeRec extends NodeRec {
    SimRank simRank;
    public SimRankNodeRec(String []argv) throws IOException{
        super(argv);
        simRank = new SimRank(
                hashsetArray2ArraylistArray(train_graph), node_num);
    }

    @Override
    double calculateScore(int from, int to){
        return simRank.calculateSim(from, to);
    }

    @Override
    double[] singleSourceScore(int qv){
        return simRank.singleSourceSim(qv);
    }


    public static void main(String[] args) throws IOException {
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_test_data", "data/arxiv_adj_test.edgelist",
                "--topk", "10",
                "--debug",
                "--node_num", "5242",
        };

        if(JudgeBase.TEST_MODE)
            new SimRankNodeRec(argv).run();
        else
            new SimRankNodeRec(args).run();
    }
}