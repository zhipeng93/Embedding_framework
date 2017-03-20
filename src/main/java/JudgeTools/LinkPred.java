package JudgeTools;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.IOException;
import java.util.*;
/**
 * For link predication, we use the embeddings trained from training_data to predicate the links that would exist
 * possibly in the test data.
 * We sample negative edges that do not exist in both train data and test data, the number of negative edges is
 * ratio * #test_edges.
 * For these positive and negative samples, we calulate AUC.
 */
abstract class LinkPred extends JudgeBase{
    @Parameter(names = "--negative_ratio",
            description = "ratio of negative samples and test data: #(negative_samples) / #(test_sample)")
    protected double neg_ratio = 3;

    HashSet<Integer> train_graph[], test_graph[];
    public LinkPred(){}

    public LinkPred(String []argv) throws IOException{
        super(argv);
        train_graph = readEdgeListFromDisk(path_train_data, node_num);
        test_graph = readEdgeListFromDisk(path_test_data, node_num);

    }

    abstract double calculateScore(int from, int to);

    void addNegativeEdges(ArrayList<LineScore> line_score_list) {
        /**
         * generate edges that do not exist in train_graph or test_graph and store the corresponding scores in
         * "line_score_list".
         * Note that nodes in the graph is indexed in [0, node_num - 1]
         */
        Random random = new Random(System.currentTimeMillis());
        int k = 0, truth_num = 0;
        for(int i=0; i < node_num; i++)
            truth_num += test_graph[i].size();
        int from, to;
        double score;

        while (k < neg_ratio * truth_num) {
            from = random.nextInt(node_num);
            to = random.nextInt(node_num);
            if (train_graph[from].contains(to)) {
                //(from, to) is in the train_graph

            } else if (test_graph[from].contains(to)) {
                //(from, to) is in the test_graph
            } else {
                //(from, to) is an negative edge.
                score = calculateScore(from, to);
                line_score_list.add(new LineScore(from, to, score, false));
                k++;
            }
        }
    }

    void addPositiveEdges(ArrayList<LineScore> line_score_list) {
        int from = 0, to = 0;
        double score;
        for(int i=0; i< node_num; i++){
            from = i;
            Set<Integer> to_list = test_graph[from];
            Iterator iter = to_list.iterator();
            while (iter.hasNext()) {
                to = (Integer) iter.next();
                score = calculateScore(from, to);
                line_score_list.add(new LineScore(from, to, score, true));
            }
        }
    }

    void calculateAUC()
            throws NumberFormatException, IOException {
        ArrayList<LineScore> line_score_list = new ArrayList<LineScore>();
        addNegativeEdges(line_score_list);
        addPositiveEdges(line_score_list);

        Collections.sort(line_score_list);

        int predLineNum = line_score_list.size();
        long positive_num = 0, negative_num = 0;
        long idx = 0;
        long sum_rank_auc = 0;
        if(debug)
            System.out.println("from to label score");
        for (LineScore ls : line_score_list) {
            idx++;
            if(debug){
                //output the score of each line
                System.out.printf("%d %d %b %f\n", ls.src, ls.dest, ls.positive, ls.score);
            }
            if (ls.positive) {
                // this is an true predication.
                sum_rank_auc += idx;
                positive_num++;
            } else
                negative_num++;
        }
        System.out.printf("sum_rank_auc = %d\n", sum_rank_auc);
        double auc_score = (1.0 * sum_rank_auc - (positive_num + 1) * positive_num / 2.0)
                / (positive_num * negative_num);


        System.out.printf("Number of positive instance = %d,\n" +
                "Number of negative instance = %d\n" +
                "Total number of instances we computed is %d\n" +
                "%s AUC is %f\n", positive_num, negative_num, predLineNum,
                this.getClass(), auc_score);
    }

    void run() throws IOException{
        calculateAUC();
    }
}