package edu.emory.clir.clearnlp.qa.model;

import edu.emory.clir.clearnlp.dependency.DEPNode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Document
{
    List<DEPNode> depNodeList = new LinkedList<>();
    List<Argument> attributeList = new LinkedList<>();
    Map<String, List<Predicate>> predicateList = new HashMap<String, List<Predicate>>();
}
