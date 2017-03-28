package JudgeTools;

import SimMeasures.AA;

import java.io.IOException;

public class AANodeRec extends NodeRec{
    AA aa;
    public AANodeRec(String []argv) throws IOException{
        super(argv);
        aa = new AA(
                hashsetArray2LinkedList(train_graph), node_num);

    }

    @Override
    double calculateScore(int from, int to) {
        return aa.calculateSim(from, to);
    }

    @Override
    double[] singleSourceScore(int qv){
        return aa.singleSourceSim(qv);
    }

    public static void main(String[] args) throws IOException {
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_test_data", "data/arxiv_adj_test.edgelist",
                "--topk", "10",
                "--debug",
                "--node_num", "5242",
        };

        if(JudgeBase.TEST_MODE)
            new AANodeRec(argv).run();
        else
            new AANodeRec(args).run();
    }
}