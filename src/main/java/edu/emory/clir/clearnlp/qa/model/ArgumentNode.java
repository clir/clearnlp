package edu.emory.clir.clearnlp.qa.model;

import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.qa.interfaces.AttrType;

import java.util.Map;

public class ArgumentNode
{
    private DEPNode depNode;
    private Map<AttrType, Attribute> attrTypeAttributeMap;
    private String label;
}
