/**
 * Copyright 2015, Emory University
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
package edu.emory.clir.clearnlp.component.mode.srl;

import java.io.ObjectInputStream;

import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.component.mode.srl.state.AbstractSRLState;
import edu.emory.clir.clearnlp.component.mode.srl.state.EnglishSRLState;
import edu.emory.clir.clearnlp.dependency.DEPTree;

/**
 * @since 3.1.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishSRLabeler extends AbstractSRLabeler
{
	/** Creates a semantic role labeler for train. */
	public EnglishSRLabeler(SRLConfiguration configuration, SRLFeatureExtractor[] extractors, Object lexicons)
	{
		super(configuration, extractors, lexicons);
	}
	
	/** Creates a semantic role labeler for bootstrap or evaluate. */
	public EnglishSRLabeler(SRLConfiguration configuration, SRLFeatureExtractor[] extractors, Object lexicons, StringModel[] models, boolean bootstrap)
	{
		super(configuration, extractors, lexicons, models, bootstrap);
	}
	
	/** Creates a semantic role labeler for decode. */
	public EnglishSRLabeler(SRLConfiguration configuration, ObjectInputStream in)
	{
		super(configuration, in);
	}
	
	/** Creates a semantic role labeler for decode. */
	public EnglishSRLabeler(SRLConfiguration configuration, byte[] models)
	{
		super(configuration, models);
	}

	@Override
	protected AbstractSRLState getState(DEPTree tree)
	{
		return new EnglishSRLState(tree, c_flag, (SRLConfiguration)t_configuration);
	}
}
