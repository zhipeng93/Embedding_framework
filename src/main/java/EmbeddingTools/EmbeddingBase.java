package EmbeddingTools;
import com.beust.jcommander.Parameter;

abstract class EmbeddingBase{
    boolean TEST_MODE = false;
    @Parameter(names = "--path_train_data", description = "path of train_graph.edgelist")
    protected String path_train_data;

    @Parameter(names = "--path_source_vec", description = "output path of the source embeddings")
    protected String path_source_vec;

    @Parameter(names = "--path_dest_vec", description = "output path of the destination embeddings")
    protected String path_dest_vec;

    @Parameter(names = "--node_num", description = "number of nodes")
    protected int node_num;

    @Parameter(names = "--debug")
    protected boolean debug = false;

    @Parameter(names = "--help", help = true)
    boolean help = false;

    @Parameter(names = "--layer_size", description = "dimension of embeddings")
    protected int layer_size = 64;

    @Parameter(names = "--neg_sample", description = "number of negative samples for each node")
    protected int neg = 5;

    @Parameter(names = "--iter", description = "number of iterations for sgd")
    protected int ITER_NUM = 100;

    @Parameter(names = "--starting_alpha", description = "the learning rate of sgd")
    protected double starting_alpha = 0.05f;

}