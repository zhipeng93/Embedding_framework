package JudgeTools;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class JudgeUtils{

    public static ArrayList<Integer> readQueryFile(String path) throws IOException{
        /**
         * the query file should contains one node index each line exactly.
         */
        ArrayList<Integer> result = new ArrayList<Integer>();
        BufferedReader bw = new BufferedReader(new FileReader(new File(path)));
        String line;
        while((line = bw.readLine()) != null){
            result.add(Integer.parseInt(line));

        }
        return result;
    }
    public static HashSet<Integer> getQueryNodes(String path) throws IOException{
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
class NodeScore implements Comparable<NodeScore>{
    double score;
    int index;
    public NodeScore(int index, double score){
        this.score = score;
        this.index = index;
    }
    public int compareTo(NodeScore o) { //descending order
        double dif = this.score - o.score;
        if (dif == 0) {
            if(index > o.index)
                return 1;
            else if(index == o.index){
                return 0;
            }
            else
                return -1;
        }
        else if (dif > 0)
            return -1;
        else
            return 1;
    }
}

class LineScore implements Comparable<LineScore>{
    int src, dest;
    double score;
    boolean positive; // false for negative, true for position
    public LineScore(int src, int dest, double score, boolean positive){
        this.src = src;
        this.score = score;
        this.dest = dest;
        this.positive = positive;
    }
    public int compareTo(LineScore ls){ //ascending order
        double dif = this.score - ls.score;
        if (dif == 0) {
            if (src > ls.src)
                return 1;
            else if (src < ls.src)
                return -1;
                else{
                    if(dest < ls.dest)
                        return -1;
                    else if(dest > ls.dest)
                        return 1;
                    else
                        return 0;
                }
        }

        else if (dif > 0)
            return 1;
        else
            return -1;
    }
}
