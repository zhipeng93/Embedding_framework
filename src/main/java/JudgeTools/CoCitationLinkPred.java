package JudgeTools;

import SimMeasures.CoCitation;

import java.io.IOException;
import java.util.HashSet;

public class CoCitationLinkPred extends LinkPred{

    CoCitation coCitation;
    public CoCitationLinkPred(String argv[]) throws IOException{
        super(argv);
        coCitation = new CoCitation(
                hashsetArray2LinkedList(train_graph), node_num);
    }

    @Override
    double calculateScore(int from, int to) {
        return coCitation.calculateSim(from, to);
    }

    public static void main(String []args) throws IOException{
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_test_data", "data/arxiv_adj_test.edgelist",
                "--negative_ratio", "10",
                "--debug",
                "--node_num", "5242"
        };


        if(JudgeBase.TEST_MODE)
            new CoCitationLinkPred(argv).run();
        else
            new CoCitationLinkPred(args).run();
    }

}