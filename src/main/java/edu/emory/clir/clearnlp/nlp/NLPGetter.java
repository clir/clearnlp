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
package edu.emory.clir.clearnlp.nlp;

import java.io.ObjectInputStream;

import edu.emory.clir.clearnlp.component.mode.pos.AbstractPOSTagger;
import edu.emory.clir.clearnlp.component.mode.pos.DefaultPOSTagger;
import edu.emory.clir.clearnlp.component.mode.pos.EnglishPOSTagger;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPGetter
{
	static public AbstractPOSTagger getPOSTagger(TLanguage language, ObjectInputStream in)
	{
		switch (language)
		{
		case ENGLISH: return new EnglishPOSTagger(in);
		default     : return new DefaultPOSTagger(in);
		}
	}
}
