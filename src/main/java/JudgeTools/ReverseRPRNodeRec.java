package JudgeTools;

import SimMeasures.ReverseRootedPageRank;

import java.io.IOException;

public class ReverseRPRNodeRec extends NodeRec {
    ReverseRootedPageRank rppr;
    public ReverseRPRNodeRec(String []argv) throws IOException{
        super(argv);
        rppr = new ReverseRootedPageRank(train_graph, node_num);
    }


    @Override
    double calculateScore(int i, int j) {
        return 0;
    }

    @Override
    double[] singleSourceScore(int qv){
        return rppr.singleSourceSim(qv);
    }

    public static void main(String[] args) throws IOException {
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_test_data", "data/arxiv_adj_test.edgelist",
                "--topk", "10",
                "--debug",
                "--node_num", "5242",
                "--thread_num", "1",
        };

        if(JudgeBase.TEST_MODE)
            new ReverseRPRNodeRec(argv).run();
        else
            new ReverseRPRNodeRec(args).run();
    }

}