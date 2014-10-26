#!/bin/bash
jar cf clearnlp.jar com

for ((i=1; i<=5; i++))
do
    rsync -avc clearnlp.jar choij@nlp0$i.nj3.ipsoft.com:/home/choij/lib
done

#rsync -avc clearnlp-$1.jar choij@nlp01.nj3.ipsoft.com:/apps/data/clearnlp/lib
#rsync -avc clearnlp.jar choij@nlp0$1.nj3.ipsoft.com:/home/choij/lib
