package JudgeTools;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

abstract class NodeRec extends JudgeBase {
    @Parameter(names = "--topk", description = "number of recommendations for each node")
    int topk = 10;

    HashSet<Integer> train_graph[], test_graph[];

//    BufferedWriter bw;

    abstract double calculateScore(int from, int to);

    abstract double[] singleSourceScore(int qv); // returns similarity scores to qv in a double[]

    void analysis() throws NumberFormatException, IOException {
        BufferedReader test_reader = new BufferedReader(new FileReader(
                new File(path_test_data)));
        String line;
        HashMap<Integer, Set<Integer>> predicate_set = generateTopk();
        // store the recommended nodes for each node.
        if (debug) {
            /**
             * for each node in the test graph, compute the number of hit.
             */
            int _node, _pred_num, _truth_num, _hit;
            Set _pred, _truth;
            for (Entry<Integer, Set<Integer>> entt : predicate_set.entrySet()) {
                _node = entt.getKey();
                _pred = (Set) ((HashSet) entt.getValue()).clone();
                // deep copy. Since set.retainAll() will change the set.
                _pred_num = _pred.size();
                _truth = test_graph[_node];
                if (_truth == null) {
                    System.out.printf("#predicate:%d, #truth:%d, hit:%d, rate:%f\n", _pred_num, 0,
                            0, 0.0);
                    continue;
                }
                _truth_num = _truth.size();
                _pred.retainAll(_truth);
                _hit = _pred.size();
                System.out.printf("#predicate:%d, #truth:%d, hit:%d, rate:%f\n", _pred_num, _truth_num,
                        _hit, 1.0 * _hit / _pred_num);

            }
        }
        /**
         * preds: the number of edges you predicate for all of the nodes
         * truth: the number of edges you have for all the nodes
         * hit: the number of edges you predicate do exist.
         */
        int truth = 0, hit = 0, preds = 0;
        for (Entry<Integer, Set<Integer>> entt : predicate_set.entrySet())
            preds += entt.getValue().size();

        while ((line = test_reader.readLine()) != null) {
            String[] words = line.split("\t");
            int from = Integer.parseInt(words[0]);
            int to = Integer.parseInt(words[1]);
            truth += 1;
            if (predicate_set.get(from) != null && predicate_set.get(from).contains(to))
                hit++;
        }
        double recall = 1.0 * hit / truth;
        double precision = 1.0 * hit / preds;
        double f1 = 2 * precision * recall / (precision + recall);
        System.out.printf("Precision=%f, recall=%f, F1=%f (truth:%d, pred:%d, hit:%d)\n",
                precision, recall, f1, truth, preds, hit);
        test_reader.close();
    }

    public ArrayList<NodeScore> singleTopk(int node) throws IOException{
        /**
         * compute sim(i, j) and store in (j, sim(i, j)) in score[j].
         * return topk similar nodes to the given node,
         * futhermore, edge(node, x) in train_graph is removed here.
         */
        ArrayList<NodeScore> single_topk = new ArrayList<NodeScore>();
        double single_source_score[] = singleSourceScore(node);

        /**
         * Here we store some of the singleSourceScore of different methods. Store in res/ file.
         */
//        if(node % 100 == 0){
//            for(int i = 0; i < node_num; i++)
//                bw.write(single_source_score[i] + " ");
//            bw.write("\n");
//        }

        for (int j = 0; j < node_num; j++) {
            // if edge(i, j) is included in train_file, j should not be in the predication list.
            if (node == j)
                continue;
            if (train_graph[node].contains(j))
                continue;

            single_topk.add(new NodeScore(j, single_source_score[j]));
        }
        Collections.sort(single_topk);

        return single_topk;
    }

    public HashMap<Integer, Set<Integer>> generateTopk() throws IOException {
        /**
         * stores the topk similar node for each node, the edges exist in train dataset is not added.
         * the similarity is computed via source_vec * source(dest)_vec
         */

        HashMap<Integer, Set<Integer>> resultSet = new HashMap<Integer, Set<Integer>>();
        HashSet<Integer> query_nodes = JudgeUtils.getQueryNodes(path_test_data);
        for (int i : query_nodes) {
            /**
             * for each node, compute the topk-similar nodes
             * and store the index in resultset,
             * edges exist in train dataset should be avoided.
             */
            List<NodeScore> score = singleTopk(i);
            /**
             * for each node i, we make #degree(i) * ratio predications, since different nodes
             * get different number of links in the future.
             */
            int cnt = 0;
            for (NodeScore entt : score) {
                if (cnt++ >= topk) {
                    break;
                }
                if (resultSet.get(i) == null)
                    resultSet.put(i, new HashSet<Integer>());
                resultSet.get(i).add(entt.index);

            }
            /**
             * In the following, the author use some trivial skills
             * to truncate the resultset.
             */
//            int cnt = 0;
//            for (Pair entt : score) {
//                if (cnt++ >= 10) {
//                    break;
//                }
//                double tmp_score = neg * Math.exp(entt.score) / node_num;
//                if (tmp_score < threshold)
//                    break;
//                resultSet.get(i).add(entt.index);
//            }
        }
        return resultSet;
    }

    HashSet<Integer>[] genReverseGraph(HashSet<Integer> graph[]) {
        HashSet<Integer> rs[] = new HashSet[node_num];
        for (int i = 0; i < node_num; i++)
            rs[i] = new HashSet<Integer>();
        for (int i = 0; i < node_num; i++) {
            Iterator iter = graph[i].iterator();
            while (iter.hasNext()) {
                int to = (Integer) iter.next();
                rs[to].add(i);
            }
        }
        return rs;
    }

    void init() throws IOException{
        /**
         * in method init(), each method should get prepared for the train_graph reading, reverse_graph, test_graph, etc.
         */
        train_graph = JudgeUtils.readEdgeListFromDisk(path_train_data, node_num);
        test_graph = JudgeUtils.readEdgeListFromDisk(path_test_data, node_num);
    }


    void run(String []argv) throws IOException{
        JCommander jCommander = new JCommander(this, argv);
        if(this.help){
            jCommander.usage();
            return;
        }
//        bw = new BufferedWriter(new FileWriter(new File("res/" + this.getClass())));
        init();
        analysis();
//        bw.close();
    }
}

