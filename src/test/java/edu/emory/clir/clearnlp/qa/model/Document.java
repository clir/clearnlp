package edu.emory.clir.clearnlp.qa.model;

import edu.emory.clir.clearnlp.dependency.DEPNode;

import java.util.LinkedList;
import java.util.List;

public class Document
{
    List<DEPNode> depNodeList = new LinkedList<>();
    List<Argument> attributeList = new LinkedList<>();
    List<Predicate> predicateList = new LinkedList<>();
}
