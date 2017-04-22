package JudgeTools;

import SimMeasures.ReverseKatz;

import java.io.IOException;

public class ReverseKatzNodeRec extends NodeRec{
    ReverseKatz reverseKatz;
    public ReverseKatzNodeRec(String []argv) throws IOException{
        super(argv);
        reverseKatz = new ReverseKatz(train_graph, node_num);
    }

    double calculateScore(int from, int to){
        return reverseKatz.calculateSim(from, to);
    }

    double[] singleSourceScore(int qv){
        return reverseKatz.singleSourceSim(qv);
    }
    public static void main(String[] args) throws IOException {
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_test_data", "data/arxiv_adj_test.edgelist",
                "--topk", "10",
                "--debug",
                "--node_num", "5242",
        };

        if(JudgeBase.TEST_MODE)
            new ReverseKatzNodeRec(argv).run();
        else
            new ReverseKatzNodeRec(args).run();
    }

}