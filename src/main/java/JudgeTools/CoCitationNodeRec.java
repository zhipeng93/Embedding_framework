package JudgeTools;

import java.io.IOException;
import java.util.HashSet;

public class CoCitationNodeRec extends NodeRec{

    @Override
    double calculateScore(int from, int to) {
        /**
         * intersection / union
         */
        HashSet<Integer> tmp_adj_from = (HashSet<Integer>)train_graph[from].clone();
        tmp_adj_from.retainAll(train_graph[to]);
        return 1.0 * tmp_adj_from.size() /
                (train_graph[from].size() + train_graph[to].size());
    }

    @Override
    void init() throws IOException{
        super.init();
    }

    @Override
    double[] singleSourceScore(int qv){
        double res[] = new double[node_num];
        for(int i = 0; i < node_num; i++)
            res[i] = calculateScore(qv, i);
        return res;
    }

    public static void main(String[] args) throws IOException {
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_test_data", "data/arxiv_adj_test.edgelist",
                "--topk", "10",
                "--debug",
                "--node_num", "5242",
        };

        CoCitationNodeRec cnr = new CoCitationNodeRec();
        if(cnr.TEST_MODE)
            cnr.run(argv);
        else
            cnr.run(args);
    }
}