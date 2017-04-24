package JudgeTools;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.IntegerConverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

abstract class NodeRec extends JudgeBase {
    @Parameter(names = "--topk", description = "number of recommendations for each node")
    int topk;


    // for judging whether an edge exists in the train_graph

    int qvs[]; // records the query nodes, i.e., qvs[i] is the i-th query node
    int rec[][]; // rec[i][*] is the node recommended for qvs[i]

    public NodeRec() throws IOException{
    }
    public NodeRec(String []argv) throws IOException{
        /**
         * in method(), each method should get prepared for the train_graph reading, reverse_graph, test_graph, etc.
         */
        super(argv);
    }

    abstract double calculateScore(int from, int to);

    abstract double[] singleSourceScore(int qv); // returns similarity scores to qv in a double[]

    void analysis() throws NumberFormatException, IOException {
        HashSet<Integer> querys = getQueryNodes(path_test_data);
        qvs = new int[querys.size()];
        int idx = 0;
        Iterator iter = querys.iterator();
        while(iter.hasNext()){
            qvs[idx ++] = (Integer) iter.next();
        }
        rec = new int[qvs.length][topk];

        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_NUM);
        for(int threadId = 0; threadId < THREAD_NUM; threadId ++){
            threadPool.execute(new SingleSourceQuery(threadId));
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
        /**
         *  compute the Precision, recall, and F1 using rec[][], qvs[]
         *  HashSet<Integer, Set<Integer> >
         */
        int pred = 0, truth = 0, hit = 0; //global
        int _pred = 0, _truth = 0, _hit = 0; //local
        for(int i=0; i< qvs.length;i++){
            _pred = topk;
            HashSet<Integer> neigh_test_graph_ids = test_graph_ids[qvs[i]];
            _truth = neigh_test_graph_ids.size();

            _hit = 0;
            for(int pp = 0; pp < _pred; pp ++){
                if(neigh_test_graph_ids.contains(rec[i][pp]))
                    _hit ++;
            }
            System.out.printf("#predicate:%d, nodeId: %d, #truth:%d, hit:%d, rate:%f\n",
                    _pred, qvs[i], _truth, _hit, 1.0 * _hit / _pred);
            pred += _pred;
            truth += _truth;
            hit += _hit;
        }
        double recall = 1.0 * hit / truth;
        double precision = 1.0 * hit / pred;
        double f1 = 2 * precision * recall / (precision + recall);
        System.out.printf("%s Precision=%f\trecall=%f\tF1=%f\n", this.getClass(), precision, recall, f1);
        System.out.printf("%s truth:\t%d\tpred:\t%d\thit:\t%d\n", this.getClass(), truth, pred, hit);
    }

    void run() throws IOException{
        long start, end;
        start = System.nanoTime();
        analysis();
        end = System.nanoTime();
        System.out.printf("nodeRec needs time %f\n", (end - start) / 1e9);

    }
    public class SingleSourceQuery implements Runnable{
        int threadId;
        public SingleSourceQuery(int threadId){
            this.threadId = threadId;
        }
        public void run(){
            int qv_num = qvs.length;
            int len = qv_num / THREAD_NUM + 1;
            int start = threadId * len;
            int end = Math.min((threadId + 1) * len, qv_num);
            for(int i=start; i<end; i++){
                int qv_id = qvs[i];
                double []rs = singleSourceScore(qv_id);

                ArrayList<NodeScore> tmp_nodescore = new ArrayList<NodeScore>();
                for(int xx =0; xx< node_num; xx++){
                    /* I mis-wrote train_graph_ids for train_graph, quite hard to find
                    * the bugs. */
                    if( (!train_graph_ids[qv_id].contains(xx)) &&
                            (xx != qv_id) )
                    tmp_nodescore.add(new NodeScore(xx, rs[xx]));
                }

                Collections.sort(tmp_nodescore);
                int idx = 0;
                Iterator iter = tmp_nodescore.iterator();
                while(idx < topk){
                    rec[i][idx ++] = ((NodeScore)iter.next()).index;
                }
            }
        }
    }
}

