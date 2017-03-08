package JudgeTools;
import com.beust.jcommander.Parameter;

abstract class JudgeBase{
    boolean TEST_MODE = true;
    @Parameter(names = "--path_train_data", description = "path of train_graph.edgelist")
    protected String path_train_data;

    @Parameter(names = "--path_test_data", description = "path of the test_graph.edgelist")
    protected String path_test_data;

    @Parameter(names = "--node_num", description = "number of nodes.")
    protected int node_num;

    @Parameter(names = "--debug")
    protected boolean debug = false;

    @Parameter(names = "--help", help = true)
    boolean help = false;
}