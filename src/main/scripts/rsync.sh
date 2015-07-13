#!/bin/bash
jar cf clearnlp.jar$1 edu
#rsync -avc clearnlp.jar$1 jdchoi@ainos.mathcs.emory.edu:/home/jdchoi/lib
rsync -avc clearnlp.jar choi@lab0z.mathcs.emory.edu:/home/choi/lib
scp choi@lab0z.mathcs.emory.edu:/home/choi/lib/clearnlp.jar jdchoi@ainos.mathcs.emory.edu:/home/jdchoi/lib/clearnlp.jar
