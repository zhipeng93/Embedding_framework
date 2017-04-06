package JudgeTools;

import SimMeasures.CommonNeighbors;

import java.io.IOException;

public class CommonNeighborsLinkPred extends LinkPred{

    CommonNeighbors commonNeighbors;
    public CommonNeighborsLinkPred(String argv[]) throws IOException{
        super(argv);
        commonNeighbors = new CommonNeighbors(
                hashsetArray2LinkedList(train_graph), node_num);
    }

    @Override
    double calculateScore(int from, int to) {
        return commonNeighbors.calculateSim(from, to);
    }

    public static void main(String []args) throws IOException{
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_test_data", "data/arxiv_adj_test.edgelist",
                "--negative_ratio", "10",
                "--debug",
                "--node_num", "5242"
        };


        if(JudgeBase.TEST_MODE)
            new CommonNeighborsLinkPred(argv).run();
        else
            new CommonNeighborsLinkPred(args).run();
    }

}