package JudgeTools;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;


abstract public class MyBase{
    @Parameter(names = "--debug")
    public boolean debug = false;

    @Parameter(names = "--help", help = true)
    public boolean help = false;

    public static boolean TEST_MODE = true;
    public static String NO_DEST_VEC = "no_input_dest";
    public MyBase(){}
    public MyBase(String argv[]){
        JCommander jCommander = new JCommander(this, argv);
        if(this.help){
            jCommander.usage();
            return;
        }
    }

}