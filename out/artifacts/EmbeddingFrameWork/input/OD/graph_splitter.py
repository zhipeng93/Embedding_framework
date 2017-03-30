import sys
import random

if len(sys.argv) < 5:
    print "Usage: python _.py graph.edgelist ratio graph_train.edgelist graph_test.edgelist"
    sys.exit(1)
adj_dict = dict()

ratio = float(sys.argv[2])

for line in open(sys.argv[1]).readlines():
    line = line.strip().split()
    from_v = int(line[0])    
    to_v = int(line[1])
    if not from_v in adj_dict:
        adj_dict[from_v] = list()
    adj_dict[from_v].append(to_v)

f_train = open(sys.argv[3], 'w')
f_test = open(sys.argv[4], 'w')
for key in adj_dict:
    adjlist = adj_dict[key]
    train_has_edge = False
    for i in range(len(adjlist)):
        if (not train_has_edge) and (i == len(adjlist) - 1):
            f_train.write(str(key) + "\t" + str(adj_dict[key][0]) + "\n")
        f = random.random()
        if f > ratio:
            f_test.write(str(key) + "\t" + str(adj_dict[key][i]) + "\n")
        else:
            f_train.write(str(key) + "\t" + str(adj_dict[key][i]) + "\n")
            train_has_edge = True

f_train.close()
f_test.close()

