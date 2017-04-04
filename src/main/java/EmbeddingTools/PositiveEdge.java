package EmbeddingTools;
class PositiveEdge implements Comparable<PositiveEdge>{
    int from, to;
    double score;
    public PositiveEdge(int from, int to, double score){
        this.from = from;
        this.to = to;
        this.score = score;
    }
    public int compareTo(PositiveEdge ls){ //descending order
        double dif = this.score - ls.score;
        if (dif == 0) {
            if (to < ls.to)
                return -1;
            else if (to > ls.to)
                return 1;
            else
                return 0;
        }
        else if (dif > 0)
            return -1;
        else
            return 1;
    }
}