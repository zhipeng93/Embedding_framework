package JudgeTools;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.IntegerConverter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class EmbeddingsNodeRec extends NodeRec{

    @Parameter(names = "--path_source_vec", description = "path of source_embedding_vecs")
    String path_source_vec;

    @Parameter(names = "--path_dest_vec",
            description = "path of dest_embedding_vecs, if not specified, this is a symmetric embedding")
    String path_dest_vec;


    double source_vec[][];
    double dest_vec[][];

    @Override
    double calculateScore(int from, int to) {
        if(path_dest_vec.equals("no_input_dest")){
            return vec_multi_vec(source_vec[from], source_vec[to]);
        }
        else{
            return vec_multi_vec(source_vec[from], dest_vec[to]);
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
        JCommander jCommander;
        if(enr.TEST_MODE)
            jCommander =  new JCommander(enr, argv);
        else
            jCommander =  new JCommander(enr, args);

        if(enr.help){
            jCommander.usage();
            return;
        }
        enr.readEmbeddings();
        enr.validate();
    }

    void readEmbeddings() throws IOException {
        source_vec = JudgeUtils.read_embeddings(path_source_vec);
        if (!path_dest_vec.equals("no_input_dest")) {
            // there should be source_vec and dest_vec
            dest_vec = JudgeUtils.read_embeddings(path_dest_vec);
        }
    }

    public double vec_multi_vec(double[] vi, double[] vj) {
        int len = vi.length;
        double score = 0;
        for (int kk = 0; kk < len; kk++) {
            score += vi[kk] * vj[kk];
        }
        return score;
    }
}
