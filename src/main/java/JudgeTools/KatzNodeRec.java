package JudgeTools;

import SimMeasures.Katz;

import java.io.IOException;

public class KatzNodeRec extends NodeRec{
    Katz katz;
    public KatzNodeRec(String []argv) throws IOException{
        super(argv);
        katz = new Katz(hashsetArray2LinkedList(train_graph),
                node_num);

    }

    double calculateScore(int from, int to){
        return katz.calculateSim(from, to);
    }

    double[] singleSourceScore(int qv){
        return katz.singleSourceSim(qv);
    }
    public static void main(String[] args) throws IOException {
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_test_data", "data/arxiv_adj_test.edgelist",
                "--topk", "10",
                "--debug",
                "--node_num", "5242",
        };

        if(JudgeBase.TEST_MODE)
            new KatzNodeRec(argv).run();
        else
            new KatzNodeRec(args).run();
    }

}