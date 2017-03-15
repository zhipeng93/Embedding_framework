
package JudgeTools;
class NodeScore implements Comparable<NodeScore>{
    double score;
    int index;
    public NodeScore(int index, double score){
        this.score = score;
        this.index = index;
    }
    public int compareTo(NodeScore o) { //descending order
        double dif = this.score - o.score;
        if (dif == 0) {
            if(index > o.index)
                return 1;
            else if(index == o.index){
                return 0;
            }
            else
                return -1;
        }
        else if (dif > 0)
            return -1;
        else
            return 1;
    }
}
