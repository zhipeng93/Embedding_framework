package JudgeTools;

import com.beust.jcommander.Parameter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

public class RootedPR extends NodeRec {
    @Parameter(names = "--restart_rate", description = "restart rate of rooted-pagerank")
    double restart_rate = 0.2;
    @Parameter(names = "--max_step", description = "step of rooted-pagerank")
    int max_step = 5;

    int out_degree[];
    HashSet<Integer> reverse_graph[];

    @Override
    void init() throws IOException {
        super.init();
        init_out_degree_table();
        reverse_graph = genReverseGraph(train_graph);
    }

    void init_out_degree_table(){
        out_degree = new int[node_num];
        for(int i = 0; i < node_num; i++)
            out_degree[i] = train_graph[i].size();
    }

    @Override
    double calculateScore(int i, int j) {
        return 0;
    }

    @Override
    double[] singleSourceScore(int qv){
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
        return p[max_step & 1];
    }

    public static void main(String[] args) throws IOException {
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_test_data", "data/arxiv_adj_test.edgelist",
                "--topk", "10",
                "--debug",
                "--node_num", "5242",
                "--restart_rate", "0.5",
        };

        RootedPR rpr = new RootedPR();
        if(rpr.TEST_MODE)
            rpr.run(argv);
        else
            rpr.run(args);
    }
}