package Compute;

import JudgeTools.MyBase;
import SimMeasures.WeightedRPR;
import com.beust.jcommander.Parameter;


import java.io.IOException;

public class CRootedPageRank extends ComputeBase{
    WeightedRPR weightedRPR;
    @Parameter(names = "--restart_rate")
    protected float restart_rate;
    @Parameter(names = "--max_step")
    protected int max_step;
    public CRootedPageRank(String []argv) throws IOException{
        super(argv);
        weightedRPR = new WeightedRPR(weighted_graph, node_num, restart_rate, max_step);
    }
    public double[] singleSourceSim(int qv){
        return weightedRPR.singleSourceSim(qv);
    }

    public static void main(String []args) throws IOException{
        String argv[] = {"--path_train_data", "data/toy.edgelist",
                "--path_simi", "data/rpr.simi",
                "--node_num", "6",
                "--thread_num", "2",
                "--debug"
        };

        if(MyBase.TEST_MODE)
            new CRootedPageRank(argv).run();
        else
            new CRootedPageRank(args).run();
    }
}