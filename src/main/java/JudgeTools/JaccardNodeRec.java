package JudgeTools;

import SimMeasures.JaccardCoeff;

import java.io.IOException;

public class JaccardNodeRec extends NodeRec{
    JaccardCoeff jaccardCoeff;
    public JaccardNodeRec(String []argv) throws IOException{
        super(argv);
        jaccardCoeff = new JaccardCoeff(train_graph, node_num);
    }

    @Override
    double calculateScore(int from, int to) {
        return jaccardCoeff.calculateSim(from, to);
    }

    @Override
    double[] singleSourceScore(int qv){
        return jaccardCoeff.singleSourceSim(qv);
    }

    public static void main(String[] args) throws IOException {
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_test_data", "data/arxiv_adj_test.edgelist",
                "--topk", "10",
                "--debug",
                "--node_num", "5242",
        };

        if(JudgeBase.TEST_MODE)
            new JaccardNodeRec(argv).run();
        else
            new JaccardNodeRec(args).run();
    }
}