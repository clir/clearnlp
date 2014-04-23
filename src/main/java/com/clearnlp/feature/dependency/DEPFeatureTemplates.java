/**
 * Copyright 2014, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.clearnlp.feature.dependency;

import org.w3c.dom.Element;

import com.clearnlp.classification.model.StringModel;
import com.clearnlp.classification.vector.SparseFeatureVector;
import com.clearnlp.dependency.DEPTree;
import com.clearnlp.feature.AbstractFeatureTemplate;
import com.clearnlp.feature.AbstractFeatureTemplates;


/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
abstract public class DEPFeatureTemplates extends AbstractFeatureTemplates<DEPFeatureToken>
{
	private static final long serialVersionUID = 6827890734032631727L;

	public DEPFeatureTemplates(Element eRoot)
	{
		super(eRoot);
	}

	@Override
	protected AbstractFeatureTemplate<DEPFeatureToken> createFeatureTemplate(Element eFeature)
	{
		return new DEPFeatureTemplate(eFeature);
	}
	
	public SparseFeatureVector createSparseFeatureVector(StringModel model, DEPTree tree)
	{
		SparseFeatureVector vector = new SparseFeatureVector();
		addBooleanFeatures(vector);
		
		int i, size = g_templates.size();
		
		for (i=0; i<size; i++)
		{
			
		}
		
		return vector;
	}
	
	abstract void addBooleanFeatures(SparseFeatureVector vector);
	
//	public String getFeature(DEP)
//	{
//		
//	}
//	
//	private void addFeatures(SparseFeatureVector vector, DEPFeatureTemplate template)
//	{
//		FeatureToken[] tokens = template.tokens;
//		int i, size = tokens.length;
//		
//		if (template.isSetFeature())
//		{
//			String[][] fields = new String[size][];
//			String[]   tmp;
//			
//			for (i=0; i<size; i++)
//			{
//				tmp = getFields(tokens[i], state);
//				if (tmp == null)	return;
//				fields[i] = tmp;
//			}
//			
//			addFeatures(vector, template.type, fields, 0, "");
//		}
//		else
//		{
//			StringBuilder build = new StringBuilder();
//			String field;
//			
//			for (i=0; i<size; i++)
//			{
//				field = getField(tokens[i], state);
//				if (field == null)	return;
//				
//				if (i > 0)	build.append(AbstractColumnReader.BLANK_COLUMN);
//				build.append(field);
//			}
//			
//			vector.addFeature(template.type, build.toString());			
//		}
//    }
//	
//	/** Called by {@link #getFeatureVector(JointFtrXml)}. */
//	private void addFeatures(StringFeatureVector vector, String type, String[][] fields, int index, String prev)
//	{
//		if (index < fields.length)
//		{
//			for (String field : fields[index])
//			{
//				if (prev.isEmpty())
//					addFeatures(vector, type, fields, index+1, field);
//				else
//					addFeatures(vector, type, fields, index+1, prev + AbstractColumnReader.BLANK_COLUMN + field);
//			}
//		}
//		else
//			vector.addFeature(type, prev);
//	}
	
	
}