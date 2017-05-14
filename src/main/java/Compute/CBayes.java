package Compute;

import JudgeTools.MyBase;
import SimMeasures.Bayes;
import SimMeasures.PMI;
import com.beust.jcommander.Parameter;


import java.io.IOException;

public class CBayes extends ComputeBase{
    @Parameter(names = "--restart_rate")
    protected float restart_rate;
    @Parameter(names = "--max_step")
    protected int max_step;
    Bayes bayes;
    public CBayes(String []argv) throws IOException{
        super(argv);
        bayes = new Bayes(weighted_graph, node_num, restart_rate, max_step);
    }
    public double[] singleSourceSim(int qv){
        return bayes.singleSourceSim(qv);
    }

    public static void main(String []args) throws IOException{
        String argv[] = {"--path_train_data", "data/toy.edgelist",
                "--path_simi", "data/rpr.simi",
                "--node_num", "6",
                "--thread_num", "1",
                "--restart_rate", "0",
                "--max_step", "1",
        };

        if(MyBase.TEST_MODE)
            new CBayes(argv).run();
        else
            new CBayes(args).run();

//        double res[] = new CBayes(argv).singleSourceSim(0);
//        for(int i = 0; i< res.length; i++)
//            System.out.print(res[i] + " ");
//        System.out.print("\n");
    }
}