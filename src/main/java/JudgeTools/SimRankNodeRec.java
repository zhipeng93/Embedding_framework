package JudgeTools;

import com.beust.jcommander.Parameter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

public class SimRankNodeRec extends NodeRec {
    @Parameter(names = "--decay_factor", description = "decay factor of simrank",
            required = false)
    double decay_factor = 0.8;
    @Parameter(names = "--max_step", description = "step of simrank")
    int max_step = 5;

    HashSet<Integer> reverse_graph[];

    @Override
    void init() throws IOException {
        super.init();
        reverse_graph = genReverseGraph(train_graph);
    }

    @Override
    double calculateScore(int from, int to) {
        return 0;
    }

    @Override
    public double[] singleSourceScore(int qv){
        /**
         * compute simrank(qv, *) w.r.t qv.
         */
        double u[][] = new double[max_step + 1][node_num];
        double v[][] = new double[2][node_num];
        u[0][qv] = 1;

        for (int l = 1; l < max_step + 1; l++) {
            /**
             * compute u[l] using u[l-1]
             */
            for (int i = 0; i < node_num; i++) {
                double temp = u[l - 1][i];
                if (temp == 0.0)
                    continue;
                else {
                    HashSet<Integer> i_list = reverse_graph[i];
                    int deg = i_list.size();
                    Iterator<Integer> iter = i_list.iterator();
                    while (iter.hasNext()) {
                        int _to = iter.next();
                        u[l][_to] += temp * 1.0 / deg;
                    }
                }
            }
        }
        /**
         * use u[l] to compute v[k]
         */
        for (int i = 0; i < node_num; i++) {
            v[0][i] = u[max_step][i];
        }
        for (int l = 1; l < max_step + 1; l++) {
            /**
             * use v[l-1] to compute v[l], use v[1-(l&1)] to compute v[l&1]
             */
            for (int i = 0; i < node_num; i++) {
                v[l & 1][i] = u[max_step - l][i];
            }
            for (int i = 0; i < node_num; i++) {
                if (v[1 - (l & 1)][i] == 0)
                    continue;
                else {
                    HashSet<Integer> i_list = train_graph[i];
                    Iterator iter = i_list.iterator();
                    while (iter.hasNext()) {
                        int to = (Integer) iter.next();
                        int deg = reverse_graph[to].size();
                        v[l & 1][to] += decay_factor * 1.0 / deg * v[1 - (1 & l)][i];
                    }
                }
            }
        }

        return v[max_step & 1];

    }

    public static void main(String[] args) throws IOException {
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_test_data", "data/arxiv_adj_test.edgelist",
                "--topk", "10",
                "--debug",
                "--node_num", "5242",
        };

        SimRankNodeRec snr = new SimRankNodeRec();
        if(snr.TEST_MODE)
            snr.run(argv);
        else
            snr.run(args);
    }
}