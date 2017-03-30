#!/usr/bin/env python
"""scoring.py: Script that demonstrates the multi-label classification used."""

import numpy
from sklearn.multiclass import OneVsRestClassifier
from sklearn.linear_model import LogisticRegression
from itertools import izip
from sklearn.metrics import f1_score
from scipy.io import loadmat
from sklearn.utils import shuffle as skshuffle

from collections import defaultdict
from gensim.models import Word2Vec
import sys
from sklearn.preprocessing import MultiLabelBinarizer 

class TopKRanker(OneVsRestClassifier):
    def predict(self, X, top_k_list):
    # return the result in a binary matrix
        assert X.shape[0] == len(top_k_list)
        probs = numpy.asarray(super(TopKRanker, self).predict_proba(X))

        for i, k in enumerate(top_k_list):
            labels = self.classes_[probs[i].argsort()[-k:]].tolist()
            for nonlabel in xrange(0, len(probs[i])):
                probs[i][nonlabel] = 0
            for label in labels:
                probs[i][label] = 1
        return probs

def get_node_num(group_f):
    d = set()
    vertex_cnt = 0
    for line in open(group_f).readlines():
        if line.startswith('#') or line.strip() == "":
            continue
        else:
            line = line.split(" ")
            _node = line[0]
            if _node in d:
                continue
            else:
                vertex_cnt = vertex_cnt + 1
                d.add(_node)

    return vertex_cnt

if __name__ == '__main__':
  # 0. Files
  if len(sys.argv) < 3:
      print 'python scoring.py <embedding file> <group file>'
      sys.exit(1)
  embeddings_file = str(sys.argv[1])
  group_file = str(sys.argv[2])
 
  print embeddings_file, group_file
  # 1. Load Embeddings
  model = Word2Vec.load_word2vec_format(embeddings_file, binary=False, norm_only=False)
  
  # 2. Load labels
  node_num = get_node_num(group_file)
  
  # Map nodes to their features (note:  assumes nodes are labeled as integers 1:N)
  features_matrix = numpy.asarray([model[str(node)] for node in range(node_num)])
  

  # create the y(label) matrix
  labels_seq_seq = [[] for x in xrange(node_num)]
  for line in open(group_file).readlines():
      if line.startswith('#') or line.strip() == "":
          continue
      else:
          line = line.split(" ")
          _node = int(line[0])
          _group = int(line[1])
          labels_seq_seq[_node].append(_group)
  
   
  MLB = MultiLabelBinarizer()
  labels_matrix_bin = MLB.fit_transform(labels_seq_seq)

  # 2. Shuffle, to create train/test groups
  shuffles = []
  number_shuffles = 2
  for x in range(number_shuffles):
    shuffles.append(skshuffle(features_matrix, labels_matrix_bin))
  
  # 3. to score each train/test group
  all_results = defaultdict(list)
  
  training_percents = [0.5]
  # uncomment for all training percents
  #training_percents = numpy.asarray(range(1,10))*.1
  for train_percent in training_percents:
    for shuf in shuffles:
      X, y = shuf
      training_size = int(train_percent * X.shape[0])
      X_train = X[:training_size, :]
      y_train = y[:training_size]
  
      X_test = X[training_size:, :]
      y_test = y[training_size:]
  

      clf = TopKRanker(LogisticRegression())
      clf.fit(X_train, y_train)
  
      top_k_list = []
      for ls in y_test:
          cnt = 0
          for i in ls:
              if i != 0:
                assert i == 1
                cnt += 1
          top_k_list.append(cnt)
      preds = clf.predict(X_test, top_k_list)
      
      results = {}
      averages = ["micro", "macro", "weighted"]
      for average in averages:
          results[average] = f1_score(y_test,  preds, average=average)
  
      all_results[train_percent].append(results)
  
  print 'Results, using embeddings of dimensionality', X.shape[1]
  print '-------------------'
  for train_percent in sorted(all_results.keys()):
    print 'Train percent:', train_percent
    for x in all_results[train_percent]:
      print  x
    print '-------------------'
