package SimMeasures;

import JudgeTools.JudgeBase;

import java.io.IOException;


public class CycleEmbedding extends SimBase{
    /**
     * compute similarity scores from the embedded vectors,
     * cycle by cycle.
     */
    double source_vec[][];
    double dest_vec [][];
    int node_num;
    public CycleEmbedding(String path_src_vec, String path_dest_vec,
                          int node_num)
    throws IOException{
        source_vec = JudgeBase.read_embeddings(path_src_vec);
        dest_vec = JudgeBase.read_embeddings(path_dest_vec);
        this.node_num = node_num;
    }
    @Override
    public double calculateSim(int from, int to){
        return vecMultiVec(source_vec[from], dest_vec[to]);
    }

    @Override
    public double[] singleSourceSim(int qv){
        double rs[]= new double[node_num];
        for(int i=0; i<node_num; i++)
            rs[i] = calculateSim(qv, i);
        return rs;
    }
    double vecMultiVec(double []a, double[] b){
        double sum = 0;
        for(int i=0; i< a.length; i++)
            sum += a[i] * b[i];
        return sum;

    }

}