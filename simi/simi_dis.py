import matplotlib.pyplot as plt
import matplotlib
from matplotlib.font_manager import FontProperties

global line_id, x_index_id, y_val_id

colors = {'g': (0.0, 0.5, 0.0), 'b': (0.0, 0.0, 1.0), 'k': (0.0, 0.0, 0.0),
          'w': (1.0, 1.0, 1.0), 'm': (0.75, 0, 0.75), 'c': (0.0, 0.75, 0.75),
          'r': (1.0, 0.0, 0.0), 'y': (0.75, 0.75, 0)}
filled_markers = ('o', 'v', '^', '<', '>', '8', 's',
                  'p', '*', 'h', 'H', 'D', 'd')
hatchs = ('+', 'x', '/', '\ ', '-', '+', 'x', '//',
          '\ \ ', '.', 'o', 'O', '*')

global len_line

def read_in_tuples(infile):
    global len_line
    tuples = []
    for line in open(infile).readlines():
        if line.startswith('#'):
            continue
        elif len(line) == 0:
            continue
        tmp = line.split()
        len_line = len(tmp)
        line_data = map(float, tmp)
        x = sorted(line_data, reverse=True)

        tuples.append(x)
    return tuples



def attr_access(id, tuples):
    return [x[id] for x in tuples]


def plt_line(infile, outfile):
    tuples = read_in_tuples(infile)
    fig = plt.figure()
    frame = fig.add_subplot(111)
    line_cnt = 0
    global len_line
    print len_line
    x = xrange(len_line)
    for line in tuples:
        # print line
        frame.plot(line)
        line_cnt += 1

    frame.set_yscale("log")
    plt.savefig(outfile)
    plt.show()

    return
if __name__ == "__main__":
    path_aa_dis = "aa"
    path_cocitation_dis = "cocitation"
    path_katz_dis = "katz"
    path_ppr_dis = "ppr"
    path_simrank_dis = "simrank"
    # path_combine_dis = "combine"

    plt_line(path_aa_dis, "aa.png")
    plt_line(path_cocitation_dis, "cc.png")
    plt_line(path_katz_dis, "katz.png")
    plt_line(path_ppr_dis, "ppr.png")
    plt_line(path_simrank_dis, "simrank.png")


