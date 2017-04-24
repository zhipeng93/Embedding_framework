package JudgeTools;

import SimMeasures.JaccardCoeff;

import java.io.IOException;

public class JaccardLinkPred extends LinkPred{
    JaccardCoeff jaccardCoeff;
    public JaccardLinkPred(String argv[]) throws IOException{
        super(argv);
        jaccardCoeff = new JaccardCoeff(train_graph, node_num);
    }

    @Override
    double calculateScore(int from, int to) {
        return jaccardCoeff.calculateSim(from, to);
    }

    public static void main(String []args) throws IOException{
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_test_data", "data/arxiv_adj_test.edgelist",
                "--negative_ratio", "10",
                "--debug",
                "--node_num", "5242"
        };


        if(JudgeBase.TEST_MODE)
            new JaccardLinkPred(argv).run();
        else
            new JaccardLinkPred(args).run();
    }

}