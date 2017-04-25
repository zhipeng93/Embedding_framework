package JudgeTools;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;


abstract public class MyBase {
    @Parameter(names = "--debug")
    public boolean debug = false;

    @Parameter(names = "--help", help = true)
    public boolean help = false;

    public static boolean TEST_MODE = false;
    public static String NO_DEST_VEC = "no_input_dest";

    public MyBase() {
    }

    public MyBase(String argv[]) {
        JCommander jCommander = new JCommander(this, argv);
        if (this.help) {
            jCommander.usage();
            return;
        }
    }

    public LinkedList<Edge>[] readEdgeListFromDisk(String path, int node_num)
            throws NumberFormatException, IOException {
        /**
         graph is an array of  linkedlist, with key as vertexId, value as the adjList.
         */
        LinkedList<Edge> graph[] = new LinkedList[node_num];
        for (int i = 0; i < node_num; i++)
            graph[i] = new LinkedList<Edge>();
        BufferedReader reader = new BufferedReader(new FileReader(new File(path)));

        String line;
        while ((line = reader.readLine()) != null) {
            if(line.startsWith("#"))
                continue;
            String[] words = line.split("\\s+");
            int from = Integer.parseInt(words[0]);
            int to = Integer.parseInt(words[1]);
            int weight = 1;
            if(words.length >= 3){
                weight = Integer.parseInt(words[2]);
            }
            graph[from].add(new Edge(to, weight));
        }
        reader.close();
        return graph;
    }

}