package JudgeTools;

public class Edge{
    int to, weight;
    public Edge(int to, int weight){
        this.to = to;
        this.weight = weight;
    }
    public int getTo(){
        return to;
    }
    public int getWeight(){
        return weight;
    }
}