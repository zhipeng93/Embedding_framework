import networkx as nx
import sys
import random
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt

def plt_line(tuples, outfile):
    fig = plt.figure()
    frame = fig.add_subplot(111)

    y = sorted(tuples, reverse=True)
    frame.plot(y)
    plt.xlabel('nodes')
    plt.ylabel('Distribution')
    plt.title(outfile)
    plt.savefig(outfile)
    #plt.show()

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print "_.py graph.edgelist out_path"
        exit(1)
    f = open(sys.argv[1])
    g = nx.read_edgelist(f, nodetype=int, create_using=nx.DiGraph())
    node_num = g.number_of_nodes()
    print node_num
    # sample_list = []
    # idx = 0
    # sample_num = 15
    # while idx < sample_num:
    #    idx += 1
    #    tmp_id = random.randint(0, node_num-1)
    #    sample_list.append(tmp_id)
    #    print tmp_id

    closeness = nx.closeness_centrality(g)
    closeness_values = closeness.values()

    degree = nx.degree_centrality(g)
    degree_values = degree.values()

    betweenness = nx.betweenness_centrality(g)
    betweenness_values = betweenness.values()

    plt_line(closeness_values, sys.argv[2] + "closeness.png")
    plt_line(degree_values, sys.argv[2] + "degree.png")
    plt_line(betweenness_values, sys.argv[2] + "betweenness.png")

