package JudgeTools;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.IOException;
import java.util.*;

public class RootedPR extends NodeRec {

    HashSet<Integer> train_graph[];
    HashSet<Integer> reverse_graph[];
    @Parameter(names = "--restart_rate", description = "restart rate of rooted-pagerank")
    double restart_rate = 0.2;
    @Parameter(names = "--max_step", description = "step of rooted-pagerank")
    int max_step = 5;

    int out_degree[];

    void read_train_graph() throws IOException {
        train_graph = JudgeUtils.readEdgeListFromDisk(path_train_data, node_num);
        init_out_degree_table();
        reverse_graph = genReverseGraph(train_graph);
    }

    void init_out_degree_table(){
        out_degree = new int[node_num];
        for(int i=0; i < node_num; i++)
            out_degree[i] = train_graph[i].size();

    }
    @Override
    double calculateScore(int i, int j) {
        return 0;
    }

    public ArrayList<NodeScore> singleTopk(HashSet<Integer> train_graph[], int qv) {
        /**
         * compute simrank(qv, j) w.r.t qv.
         */
        double p[][] = new double[2][node_num];

        p[0][qv] = 1;
        for (int step = 0; step < max_step; step++) {
            //use p[step & 1] to update p[1 - (step & 1)]
            Arrays.fill(p[1 - (step & 1)], 0);
            for (int i = 0; i < node_num; i++) {
                Iterator iter = reverse_graph[i].iterator();
                while (iter.hasNext()) {
                    int j = (Integer) iter.next();
                    // (j, i) is in the original graph, i.e., train_graph
                    p[1 - (step & 1)][i] += (1- restart_rate) * p[step & 1][j] * 1.0 / out_degree[j];
                }
            }
            p[1 - (step & 1)][qv] += restart_rate;
        }
        /**
         * add the computed scores w.r.t qv to Arraylist single_topk.
         * edge(qv, x) in train_graph is removed here.
         */
        ArrayList<NodeScore> single_topk = new ArrayList<NodeScore>();
        for (int j = 0; j < node_num; j++) {
            if (qv == j)
                continue;
            if (train_graph[qv].contains(j))
                continue;

            single_topk.add(new NodeScore(j, p[max_step & 1][j]));
        }
        Collections.sort(single_topk);
        return single_topk;
    }

    public static void main(String[] args) throws IOException {
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_test_data", "data/arxiv_adj_test.edgelist",
                "--topk", "10",
                "--debug",
                "--node_num", "5242",
                "--restart_rate", "0.5",
                "--help"
        };

        RootedPR rpr = new RootedPR();
        JCommander jCommander;
        if(rpr.TEST_MODE)
            jCommander =  new JCommander(rpr, argv);
        else
            jCommander =  new JCommander(rpr, args);

        if(rpr.help){
            jCommander.usage();
            return;
        }
        rpr.read_train_graph();
        rpr.validate();
    }
}