#!/bin/bash
jar cf clearnlp.jar edu
rsync -avc clearnlp.jar choi@mithril.mathcs.emory.edu:/local/scratchir/choi/lib
