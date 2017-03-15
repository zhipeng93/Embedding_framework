package JudgeTools;
class LineScore implements Comparable<LineScore>{
    int src, dest;
    double score;
    boolean positive; // false for negative, true for position
    public LineScore(int src, int dest, double score, boolean positive){
        this.src = src;
        this.score = score;
        this.dest = dest;
        this.positive = positive;
    }
    public int compareTo(LineScore ls){ //ascending order
        double dif = this.score - ls.score;
        if (dif == 0) {
            if (src > ls.src)
                return 1;
            else if (src < ls.src)
                return -1;
            else{
                if(dest < ls.dest)
                    return -1;
                else if(dest > ls.dest)
                    return 1;
                else
                    return 0;
            }
        }

        else if (dif > 0)
            return 1;
        else
            return -1;
    }
}
