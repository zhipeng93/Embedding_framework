# Embedding TOOL:
A tool to generate embedding vectors with different graph embedding methods.

# Judge TOOL:
A tool to analyze the results of different methods on link predication and node recommendation.
    (1) For link predication, we sample (neg_ratio * #truth) negative edges to calculate the AUC.
    (2) For node recommendation, we recommend topk similar nodes for the queries in the specified file.

## METHODS SUPPORTED.
Here the embedding methods include three types.
    1. the input are embedding vectors of embedding methods, like deepwalk, LINE, node2vec, APP, etc.
    2. the methods for single pair similarity computation is fast, like co-citation, etc.
    3. the methods for single pair similarity computation is hard, but the topk similar nodes are easy to compute. These only support Node Recommendation task.

# Note to myself:
Do not give up on your work. It will find a conference anyway.
