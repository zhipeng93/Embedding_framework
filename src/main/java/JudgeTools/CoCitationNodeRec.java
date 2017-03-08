package JudgeTools;

import com.beust.jcommander.JCommander;
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

    HashSet<Integer> train_graph[];
    void read_train_graph() throws IOException{
        train_graph = JudgeUtils.readEdgeListFromDisk(path_train_data, node_num);
    }

    public static void main(String[] args) throws IOException {
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_test_data", "data/arxiv_adj_test.edgelist",
                "--topk", "10",
                "--debug",
                "--node_num", "5242",
                "--help"
        };

        CoCitationNodeRec cnr = new CoCitationNodeRec();
        JCommander jCommander;
        if(cnr.TEST_MODE)
            jCommander =  new JCommander(cnr, argv);
        else
            jCommander =  new JCommander(cnr, args);

        if(cnr.help){
            jCommander.usage();
            return;
        }
        cnr.read_train_graph();
        cnr.validate();
    }
}