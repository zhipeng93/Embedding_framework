package JudgeTools;

import SimMeasures.AA;
import SimMeasures.CoCitation;

import java.io.IOException;
import java.util.HashSet;

public class AALinkPred extends LinkPred{

    AA aa;
    public AALinkPred(String argv[]) throws IOException{
        super(argv);
        aa = new AA(
                hashsetArray2ArraylistArray(train_graph), node_num);
    }

    @Override
    double calculateScore(int from, int to) {
        return aa.calculateSim(from, to);
    }

    public static void main(String []args) throws IOException{
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_test_data", "data/arxiv_adj_test.edgelist",
                "--negative_ratio", "10",
                "--debug",
                "--node_num", "5242"
        };


        if(JudgeBase.TEST_MODE)
            new AALinkPred(argv).run();
        else
            new AALinkPred(args).run();
    }

}