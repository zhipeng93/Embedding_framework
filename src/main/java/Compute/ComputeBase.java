package Compute;
import JudgeTools.Edge;
import JudgeTools.MyBase;
import com.beust.jcommander.Parameter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class ComputeBase extends MyBase{
    @Parameter(names = "--path_train_data")
    protected String path_weighted_graph;

    @Parameter(names = "--node_num")
    protected int node_num;

    @Parameter(names = "--thread_num")
    protected int THREAD_NUM;

    @Parameter(names = "--path_simi")
    protected String simi_path;

    public ComputeBase(){

    }
    public ComputeBase(String []argv) throws IOException{
        super(argv);
        weighted_graph = readEdgeListFromDisk(path_weighted_graph, node_num);
        sim = new double[node_num][node_num];
    }
    LinkedList<Edge> weighted_graph[];
    double sim[][];

    public void run() throws IOException{
        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_NUM);
        for(int threadId = 0; threadId < THREAD_NUM; threadId ++){
            threadPool.execute(new ParallelSingleSourceSim(threadId));
        }
        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
            try {
                threadPool.awaitTermination(1, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                System.out.println("Waiting.");
                e.printStackTrace();
            }
        }
        write_array_to_disk(simi_path, sim);
    }
    public abstract double[] singleSourceSim(int qv);

    public static void write_array_to_disk(String path, double[][] embeddings)
            throws IOException {
        /**
         * the embeddings files has the form of:
         *  Node_num layer_size
         *  node_id embedding_vectors\n
         *  ...
         */
        BufferedWriter f = new BufferedWriter(new FileWriter(path));
        int node_num = embeddings.length;
        int layer_size = embeddings[0].length;
        f.write(node_num + " " + layer_size + "\n");
        for (int i = 0; i < embeddings.length; i++) {
            f.write(i + " ");
            for (int j = 0; j < embeddings[i].length; j++) {
                f.write((float)embeddings[i][j] + " ");
            }
            f.write("\r\n");
        }
        f.flush();
        f.close();
    }

    public class ParallelSingleSourceSim implements Runnable{
        int threadId;
        public ParallelSingleSourceSim(int threadId){
            this.threadId = threadId;
        }
        public void run(){
            int len = node_num / THREAD_NUM + 1;
            int start = threadId * len;
            int end = Math.min((threadId + 1) * len, node_num);
            for(int i=start; i<end; i++){
                sim[i] = singleSourceSim(i);
            }
        }
    }
}
