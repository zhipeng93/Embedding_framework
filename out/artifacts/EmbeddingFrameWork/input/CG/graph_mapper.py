import sys

def convert(in_f, node2id_f, edge2idid_f):
    vertex_cnt = 0
    inf = open(in_f)
    nodef = open(node2id_f, 'w')
    edgef = open(edge2idid_f, 'w')
    nodef.write("#original_node converted_node \n")
    # edgef.write("#from to weight \n")
    node2id = dict()
    for line in inf.readlines():
        if line.startswith('#') or line.strip() == "":
           continue 
        else:
            line = line.strip().split()
            _from = line[0]
            _to = line[1]
            _weight = ""
            if len(line) > 2:
                _weight = line[2]
            if node2id.has_key(_from):
                _from_id = node2id[_from]
            else:
                _from_id = vertex_cnt
                node2id[_from] = vertex_cnt
                vertex_cnt += 1

            if node2id.has_key(_to):
                _to_id = node2id[_to]
            else:
                _to_id = vertex_cnt
                node2id[_to] = vertex_cnt
                vertex_cnt += 1

            edgef.write(str(_from_id) + " " + str(_to_id) + " " + _weight + "\n")
    
    for k in node2id:
        nodef.write(str(k) + " " + str(node2id[k]) + "\n")


if __name__ == '__main__':
    if len(sys.argv) < 4:
        print 'Usage: python _.py input_file node_corr edge_corr'
        sys.exit(1)
    convert(sys.argv[1], sys.argv[2], sys.argv[3])
