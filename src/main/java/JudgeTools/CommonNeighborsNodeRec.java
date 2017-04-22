package JudgeTools;

import SimMeasures.CommonNeighbors;

import java.io.IOException;

public class CommonNeighborsNodeRec extends NodeRec{
    CommonNeighbors commonNeighbors;
    public CommonNeighborsNodeRec(String []argv) throws IOException{
        super(argv);
        commonNeighbors = new CommonNeighbors(train_graph, node_num);

    }

    @Override
    double calculateScore(int from, int to) {
        return commonNeighbors.calculateSim(from, to);
    }

    @Override
    double[] singleSourceScore(int qv){
        return commonNeighbors.singleSourceSim(qv);
    }

    public static void main(String[] args) throws IOException {
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_test_data", "data/arxiv_adj_test.edgelist",
                "--topk", "10",
                "--debug",
                "--node_num", "5242",
        };

        if(JudgeBase.TEST_MODE)
            new CommonNeighborsNodeRec(argv).run();
        else
            new CommonNeighborsNodeRec(args).run();
    }
}