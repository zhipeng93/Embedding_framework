package JudgeTools;

import com.beust.jcommander.Parameter;

import java.io.IOException;

public class EmbeddingsNodeRec extends NodeRec{

    @Parameter(names = "--path_source_vec", description = "path of source_embedding_vecs")
    String path_source_vec;

    @Parameter(names = "--path_dest_vec",
            description = "path of dest_embedding_vecs, if not specified, this is a symmetric embedding")
    String path_dest_vec;

    double source_vec[][];
    double dest_vec[][];

    public EmbeddingsNodeRec(String []argv) throws IOException{
        super(argv);
        source_vec = read_embeddings(path_source_vec);
        normalize_matrix((source_vec));
        if(is_directed_embedding()) {
            dest_vec = read_embeddings(path_dest_vec);
            normalize_matrix(dest_vec);
        }
    }

    void normalize_matrix(double [][] vec){
        /**
         * normalize the embedded matrix such that can be cosine between vertex i and j.
         */
        for(int i=0; i < vec.length; i++){
            double sum = 0;
            for(int j=0; j< vec[i].length; j++){
                sum += Math.pow(vec[i][j], 2);
            }
            double sqrt = Math.sqrt(sum);
            if(sqrt > 1e-20) {
                for (int j = 0; j < vec[i].length; j++) {
                    vec[i][j] /= sqrt;
                }
            }
        }
    }
    boolean is_directed_embedding(){
        if(path_dest_vec.equals("") || path_dest_vec.equals(NO_DEST_VEC))
            return false;
        else
            return true;
    }

    @Override
    double calculateScore(int from, int to) {
        if(is_directed_embedding()){
            return vec_multi_vec(source_vec[from], dest_vec[to]);
        }
        else{
            return vec_multi_vec(source_vec[from], source_vec[to]);
        }
    }

    @Override
    double[] singleSourceScore(int qv){
        double score[] = new double[node_num];
        for(int i = 0; i < node_num; i++) {
            score[i] = calculateScore(qv, i);
        }
        return score;
    }


    public static void main(String[] args) throws IOException {
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_test_data", "data/arxiv_adj_test.edgelist",
                "--path_source_vec", "res/arxiv_trainout_ppr_embedding_source_vec",
//                "--path_dest_vec", "res/arxiv_trainout_ppr_embedding_dest_vec",
                "--path_dest_vec", "no_input_dest",
                // this really matters.
                "--topk", "10",
                "--debug",
                "--node_num", "5242",
                "--thread_num", "2",
        };

        if(JudgeBase.TEST_MODE)
            new EmbeddingsNodeRec(argv).run();
        else
            new EmbeddingsNodeRec(args).run();
    }
}
