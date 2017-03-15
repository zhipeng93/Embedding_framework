package JudgeTools;

import SimMeasures.CoCitation;

import java.io.IOException;
import java.util.HashSet;

public class CoCitationNodeRec extends NodeRec{
    CoCitation coci;
    public CoCitationNodeRec(String []argv) throws IOException{
        super(argv);
        coci = new CoCitation(
                hashsetArray2ArraylistArray(train_graph), node_num);

    }

    @Override
    double calculateScore(int from, int to) {
        return coci.calculateSim(from, to);
    }

    @Override
    double[] singleSourceScore(int qv){
        return coci.singleSourceSim(qv);
    }

    public static void main(String[] args) throws IOException {
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_test_data", "data/arxiv_adj_test.edgelist",
                "--topk", "10",
                "--debug",
                "--node_num", "5242",
        };

        if(JudgeBase.TEST_MODE)
            new CoCitationNodeRec(argv).run();
        else
            new CoCitationNodeRec(args).run();
    }
}