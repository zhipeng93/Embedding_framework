package JudgeTools;

import SimMeasures.PersonalizedPageRank;

import java.io.IOException;

public class RootedPR extends NodeRec {
    PersonalizedPageRank ppr;
    public RootedPR(String []argv) throws IOException{
        super(argv);
        ppr = new PersonalizedPageRank(
                hashsetArray2ArraylistArray(train_graph), node_num);
    }


    @Override
    double calculateScore(int i, int j) {
        return 0;
    }

    @Override
    double[] singleSourceScore(int qv){
        return ppr.singleSourceSim(qv);
    }

    public static void main(String[] args) throws IOException {
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_test_data", "data/arxiv_adj_test.edgelist",
                "--topk", "10",
                "--debug",
                "--node_num", "5242",
        };


        if(JudgeBase.TEST_MODE)
            new RootedPR(argv).run();
        else
            new RootedPR(args).run();
    }

}