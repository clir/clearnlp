package edu.emory.clir.clearnlp.qa.model;

import edu.emory.clir.clearnlp.qa.interfaces.AttrType;

public abstract class Attribute
{
    private AttrType attrType;

    public Attribute(AttrType _attrType)
    {
        attrType = _attrType;
    }
}
