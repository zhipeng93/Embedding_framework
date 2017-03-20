package EmbeddingTools;
import com.beust.jcommander.Parameter;

import java.io.*;
import java.util.ArrayList;

import java.util.Random;
import JudgeTools.MyBase;
abstract class EmbeddingBase extends MyBase{
    //ArrayList<Integer> [] graph; /* store the graph in an adjlist way */
    double source_vec[][]; /* store the source vectors */
    double dest_vec[][]; /* store the destination vectors */
    ArrayList<Integer> train_graph[];
    static Random random = new Random(System.currentTimeMillis());

    static double sum_gd = 0;
    /**
     * Efficient to compute sigmoid(f)
     */
    public static int MAX_EXP = 5;
    public static double[] expTable;
    public EmbeddingBase(){
    }
    public EmbeddingBase(String []argv) throws IOException{
        super(argv);
        source_vec = new double[node_num][layer_size];
        rand_init(source_vec, random);
        if(isDirectedEmbedding()) {
            dest_vec = new double[node_num][layer_size];
            rand_init(dest_vec, random);
        }
        train_graph = readEdgeListFromDisk(path_train_data, node_num);
    }
    public EmbeddingBase(String []argv, double learning_rate)
            throws IOException{
        this(argv);
        this.rio = learning_rate;

    }
    static {
        expTable = new double[1000];
        for (int i = 0; i < 1000; i++) {
            expTable[i] = Math.exp((i / 1000.0 * 2 - 1) * MAX_EXP); // Precompute the exp() table
            //exp[-5, -4.99, -4,98,...,0,0.01, 0.02,...,4.99]
            expTable[i] = expTable[i] / (expTable[i] + 1); // f(x) = x / (x + 1)
            // \sigmod_x = expTable[(int)((x + MAX_EXP) * (1000 / MAX_EXP / 2))];
            // if x \in [-MAX_EXP, MAX_EXP]
        }
    }
    @Parameter(names = "--path_train_data", description = "path of train_graph.edgelist")
    protected String path_train_data;

    @Parameter(names = "--path_source_vec", description = "output path of the source embeddings")
    protected String path_source_vec;

    @Parameter(names = "--path_dest_vec", description = "output path of the destination embeddings")
    protected String path_dest_vec;

    @Parameter(names = "--node_num", description = "number of nodes")
    protected int node_num;

    @Parameter(names = "--layer_size", description = "dimension of embeddings")
    protected int layer_size;

    @Parameter(names = "--neg_sample", description = "number of negative samples for each node")
    protected int neg;

    @Parameter(names = "--iter", description = "number of iterations for sgd")
    protected int ITER_NUM;

    @Parameter(names = "--learning_rate", description = "the learning rate of sgd")
    protected double rio;


    abstract void generateEmbeddings() throws IOException;
    void run() throws IOException{
        long t0 = System.nanoTime();
        generateEmbeddings();
        long t1 = System.nanoTime();
        System.out.printf("embedding time %f s\n", (t1 - t0)/1e9);
        write_array_to_disk(path_source_vec, source_vec);
        if(isDirectedEmbedding())
            write_array_to_disk(path_dest_vec, dest_vec);
    }


    public double getSigmoid(double f) {
        if (f > MAX_EXP)
            return 1;
        else if (f < -MAX_EXP)
            return -1;
        else
            return expTable[(int) (f + MAX_EXP) * (1000 / MAX_EXP / 2)];
    }
    void rand_init(double[][] w, Random random) {
        /**
         * This is essential because the initial values are zeros.
         */

        for (int i = 0; i < w.length; i++) {
            double[] tmp = w[i];
            for (int j = 0; j < tmp.length; j++) {
                tmp[j] = (random.nextDouble() - 0.5) / this.layer_size;
            }
        }
    }
    void shuffle(int a[], Random random) {
        int count = a.length;
        for(int i = count; i > 1; i--){
            swap(a, i - 1, random.nextInt(count));
        }
    }
    void swap(int a[], int i, int j){
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }

    double vecMultiVec(double []a, double []b){
        double sum = 0;
        for(int i=0; i < a.length; i++)
            sum += a[i] * b[i];
        return sum;
    }

    boolean isDirectedEmbedding(){
        if(path_dest_vec.equals("") || path_dest_vec.equals("no_input_dest"))
            return false;
        return true;
    }

    public static ArrayList<Integer>[] readEdgeListFromDisk(String path, int node_num)
            throws NumberFormatException, IOException {
        /**
         graph is a hashset, with key as vertexId, value as the adjList.
         Duplicate edges should not exist in input files.
         */
        ArrayList<Integer> graph[] = new ArrayList[node_num];
        for (int i = 0; i < node_num; i++)
            graph[i] = new ArrayList<Integer>();
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
                f.write(embeddings[i][j] + " ");
            }
            f.write("\r\n");
        }
        f.flush();
        f.close();
    }

}