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
    boolean is_directed_embedding;
    final String NO_DEST_VEC = "no_input_dest";

    @Override
    double calculateScore(int from, int to) {
        if(is_directed_embedding){
            return vec_multi_vec(source_vec[from], dest_vec[to]);
        }
        else{
            return vec_multi_vec(source_vec[from], source_vec[to]);
        }
    }

    @Override
    double[] singleSourceScore(int qv){
        double score[] = new double[node_num];
        for(int i = 0; i < node_num; i++)
            score[i] = calculateScore(qv, i);
        return score;
    }

    @Override
    void init() throws IOException{
        super.init();
        source_vec = JudgeUtils.read_embeddings(path_source_vec);
        if(!path_dest_vec.equals(NO_DEST_VEC))
            is_directed_embedding = true;
        else
            is_directed_embedding = false;
        if (is_directed_embedding) {
            // there should be source_vec and dest_vec
            dest_vec = JudgeUtils.read_embeddings(path_dest_vec);
        }
    }
    public static void main(String[] args) throws IOException {
        String argv[] = {"--path_train_data", "data/arxiv_adj_train.edgelist",
                "--path_test_data", "data/arxiv_adj_test.edgelist",
                "--path_source_vec", "res/arxiv_trainout_ppr_embedding_source_vec",
                "--path_dest_vec", "no_input_dest",
                "--topk", "10",
                "--debug",
                "--node_num", "5242",
        };

        EmbeddingsNodeRec enr = new EmbeddingsNodeRec();

        if(enr.TEST_MODE)
            enr.run(argv);
        else
            enr.run(args);
    }
}
