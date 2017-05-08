package Compute;

import JudgeTools.MyBase;
import SimMeasures.RootedPageRank;


import java.io.IOException;

public class CRootedPageRank extends ComputeBase{
    RootedPageRank rpr;
    public CRootedPageRank(String []argv) throws IOException{
        super(argv);
        rpr = new RootedPageRank(weighted_graph, node_num);
    }
    public double[] singleSourceSim(int qv){
        return rpr.singleSourceSim(qv);
    }

    public static void main(String []args) throws IOException{
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_simi", "data/rpr.simi",
                "--node_num", "5242",
                "--thread_num", "2",
                "--debug"
        };

        if(MyBase.TEST_MODE)
            new CRootedPageRank(argv).run();
        else
            new CRootedPageRank(args).run();
    }
}