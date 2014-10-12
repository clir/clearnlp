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
package edu.emory.clir.clearnlp.component.mode.pos;

import java.io.ObjectInputStream;

import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.component.evaluation.TagEval;
import edu.emory.clir.clearnlp.nlp.configuration.POSTrainConfiguration;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DefaultPOSTagger extends AbstractPOSTagger
{
	/** Creates a pos tagger for collect. */
	public DefaultPOSTagger(POSTrainConfiguration configuration)
	{
		super(configuration);
	}
	
	/** Creates a pos tagger for train. */
	public DefaultPOSTagger(POSFeatureExtractor[] extractors, Object[] lexicons)
	{
		super(extractors, lexicons);
	}
	
	/** Creates a pos tagger for bootstrap. */
	public DefaultPOSTagger(POSFeatureExtractor[] extractors, Object[] lexicons, StringModel[] models)
	{
		super(extractors, lexicons, models);
	}

	
	/** Creates a pos tagger for evaluate. */
	public DefaultPOSTagger(POSFeatureExtractor[] extractors, Object[] lexicons, StringModel[] models, TagEval eval)
	{
		super(extractors, lexicons, models, eval);
	}
	/** Creates a pos tagger for decode. */
	public DefaultPOSTagger(ObjectInputStream in)
	{
		super(in);
	}
}
