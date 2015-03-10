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
package edu.emory.clir.clearnlp.component.mode.sequence;

import java.io.ObjectInputStream;

import edu.emory.clir.clearnlp.classification.model.StringModel;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DefaultSequenceClassifier extends AbstractSequenceClassifier
{
	/** Creates a pos tagger for train. */
	public DefaultSequenceClassifier(SeqFeatureExtractor[] extractors, Object[] lexicons, SeqTrainConfiguration configuration)
	{
		super(extractors, lexicons, configuration);
	}
	
	/** Creates a pos tagger for bootstrap or evaluate. */
	public DefaultSequenceClassifier(SeqFeatureExtractor[] extractors, Object[] lexicons, StringModel[] models, boolean bootstrap, SeqTrainConfiguration configuration)
	{
		super(extractors, lexicons, models, bootstrap, configuration);
	}

	/** Creates a pos tagger for decode. */
	public DefaultSequenceClassifier(ObjectInputStream in)
	{
		super(in);
	}
	
	/** Creates a pos tagger for decode. */
	public DefaultSequenceClassifier(byte[] models)
	{
		super(models);
	}
}
