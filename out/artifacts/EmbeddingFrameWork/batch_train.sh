#! /bin/bash
id=20170406_topk
sh test_AL.sh | tee  logs/AL_$id.log
sh test_OD.sh | tee  logs/OD_$id.log
sh test_CG.sh | tee  logs/CG_$id.log
sh test_PG.sh | tee  logs/PG_$id.log
sh test_WV.sh | tee  logs/WV_$id.log
sh test_arxiv.sh | tee  logs/arxiv_$id.log

ls test_* | xargs sed -i "s/topk\_sampling=20/topk\_sampling=0/g"
id=20170406_threshold
sh test_AL.sh | tee  logs/AL_$id.log
sh test_OD.sh | tee  logs/OD_$id.log
sh test_CG.sh | tee  logs/CG_$id.log
sh test_PG.sh | tee  logs/PG_$id.log
sh test_WV.sh | tee  logs/WV_$id.log
sh test_arxiv.sh | tee  logs/arxiv_$id.log
