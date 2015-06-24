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
package edu.emory.clir.clearnlp.component.mode.future;

import java.io.ObjectInputStream;

import edu.emory.clir.clearnlp.classification.model.StringModel;

/**
 * @since 3.1.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DefaultFutureClassifier extends AbstractFutureClassifier
{
	/** Creates a pos tagger for train. */
	public DefaultFutureClassifier(Object lexicons)
	{
		super(lexicons);
	}
	
	/** Creates a pos tagger for bootstrap or evaluate. */
	public DefaultFutureClassifier(Object lexicons, StringModel[] models, boolean bootstrap)
	{
		super(lexicons, models, bootstrap);
	}
	
	/** Creates a pos tagger for decode. */
	public DefaultFutureClassifier(ObjectInputStream in)
	{
		super(in);
	}
	
	/** Creates a pos tagger for decode. */
	public DefaultFutureClassifier(byte[] models)
	{
		super(models);
	}
}