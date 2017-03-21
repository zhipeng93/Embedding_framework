package JudgeTools;
import com.beust.jcommander.Parameter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

abstract public class JudgeBase extends MyBase{
    public JudgeBase(String []argv){
        super(argv);
        System.out.print(node_num);
    }
    public JudgeBase(){}

    @Parameter(names = "--path_train_data", description = "path of train_graph.edgelist")
    protected String path_train_data;

    @Parameter(names = "--path_test_data", description = "path of the test_graph.edgelist")
    protected String path_test_data;

    @Parameter(names = "--node_num", description = "number of nodes.")
    protected int node_num;

    public double vec_multi_vec(double[] vi, double[] vj) {
        int len = vi.length;
        double score = 0;
        for (int kk = 0; kk < len; kk++) {
            score += vi[kk] * vj[kk];
        }
        return score;
    }

    public static HashSet<Integer> getQueryNodes(String path) throws IOException {
        /**
         * return the nodes contained in the test_data.edgelist.
         * NOTE: only "from" nodes are contained.
         */
        HashSet<Integer> result = new HashSet<Integer>();
        BufferedReader bw = new BufferedReader(new FileReader(new File(path)));
        String line;
        String words[];
        while((line = bw.readLine()) != null){
            words = line.split("\t");
            result.add(Integer.parseInt(words[0]));
        }
        return result;
    }
    public static HashSet<Integer>[] readEdgeListFromDisk(String path, int node_num)
            throws NumberFormatException, IOException {
        /**
         graph is a hashmap, with key as vertexId, value as the adjList.
         */
        HashSet<Integer> graph[] = new HashSet[node_num];
        for(int i=0; i < node_num; i++)
            graph[i] = new HashSet<Integer>();
        BufferedReader reader = new BufferedReader(new FileReader(new File(path)));

        String line;
        while ((line = reader.readLine()) != null) {
            String[] words = line.split("\t");
            int from = Integer.parseInt(words[0]);
            int to = Integer.parseInt(words[1]);
            graph[from].add(to);
        }
        reader.close();
        return graph;
    }

    public static ArrayList<Integer> [] hashsetArray2ArraylistArray
            (HashSet<Integer> train_graph[]){
        /**
         * I do not want to rewrite Set into ArrayList...
         */
        int size = train_graph.length;
        ArrayList<Integer> rs[] = new ArrayList[size];
        for(int i = 0; i < size; i ++)
            rs[i] = new ArrayList<Integer>(train_graph[i]);
        return rs;
    }


    public static double[][] read_embeddings(String path)
            throws IOException {
        /**
         * the length of the embedding has to cover all the indices.
         * the embeddings files has the form of:
         *  Node_num layer_size
         *  node_id embedding_vectors\n
         *  ...
         */
        BufferedReader reader = new BufferedReader(new FileReader
                (new File(path)));
        String line;
        line = reader.readLine();
        String words[] = line.split(" ");
        int node_num = Integer.parseInt(words[0]);
        int layer_size = Integer.parseInt(words[1]);
        double [][]embedding = new double[node_num][layer_size];

        while ((line = reader.readLine()) != null) {
            words = line.split(" ");
            int id = Integer.parseInt(words[0]);
            for (int i = 1; i < words.length; ++i) {
                embedding[id][i - 1] = Double.parseDouble(words[i]);
            }
        }
        reader.close();
        return embedding;
    }
}