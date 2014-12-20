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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;

import edu.emory.clir.clearnlp.component.mode.dep.AbstractDEPParser;
import edu.emory.clir.clearnlp.component.mode.morph.AbstractMPAnalyzer;
import edu.emory.clir.clearnlp.component.mode.morph.DefaultMPAnalyzer;
import edu.emory.clir.clearnlp.component.mode.morph.EnglishMPAnalyzer;
import edu.emory.clir.clearnlp.component.mode.pos.AbstractPOSTagger;
import edu.emory.clir.clearnlp.component.mode.pos.DefaultPOSTagger;
import edu.emory.clir.clearnlp.component.mode.pos.EnglishPOSTagger;
import edu.emory.clir.clearnlp.conversion.AbstractC2DConverter;
import edu.emory.clir.clearnlp.conversion.EnglishC2DConverter;
import edu.emory.clir.clearnlp.conversion.headrule.HeadRuleMap;
import edu.emory.clir.clearnlp.nlp.trainer.AbstractNLPTrainer;
import edu.emory.clir.clearnlp.nlp.trainer.POSTrainer;
import edu.emory.clir.clearnlp.tokenization.AbstractTokenizer;
import edu.emory.clir.clearnlp.tokenization.EnglishTokenizer;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;
import edu.emory.clir.clearnlp.util.lang.TLanguage;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPUtils
{
	static public String MODEL_ROOT = "edu/emory/clir/clearnlp/model";
	
	private NLPUtils() {}
	
	/** @param in the inputstream for a headrule file. */
	static public AbstractC2DConverter getC2DConverter(TLanguage language, InputStream in)
	{
		HeadRuleMap headrules = new HeadRuleMap(in);
		return new EnglishC2DConverter(headrules);
	}
	
	static public AbstractTokenizer getTokenizer(TLanguage language)
	{
		return new EnglishTokenizer();
	}
	
	static public AbstractMPAnalyzer getMPAnalyzer(TLanguage language)
	{
		switch (language)
		{
		case ENGLISH: return new EnglishMPAnalyzer();
		default     : return new DefaultMPAnalyzer();
		}
	}
	
	static public AbstractPOSTagger getPOSTagger(TLanguage language, ObjectInputStream in)
	{
		switch (language)
		{
		case ENGLISH: return new EnglishPOSTagger(in);
		default     : return new DefaultPOSTagger(in);
		}
	}
	
	static public AbstractDEPParser getDEPParser(TLanguage language, ObjectInputStream in)
	{
		return null;
	}
	
	static public AbstractPOSTagger getPOSTagger(TLanguage language, String modelPath)
	{
		return getPOSTagger(language, getObjectInputStream(modelPath, NLPMode.pos));
	}
	
	static public AbstractDEPParser getDEPParser(TLanguage language, String modelPath)
	{
		return getDEPParser(language, getObjectInputStream(modelPath, NLPMode.dep));
	}
	
	static private ObjectInputStream getObjectInputStream(String modelPath, NLPMode mode)
	{
		try
		{
			String path = MODEL_ROOT + StringConst.FW_SLASH + modelPath + StringConst.FW_SLASH + mode;
			return new ObjectInputStream(new GZIPInputStream(new BufferedInputStream(IOUtils.getInputStreamsFromClasspath(path))));
		}
		catch (IOException e) {e.printStackTrace();}

		return null;
	}
	
	static public AbstractNLPTrainer getTrainer(NLPMode mode, InputStream configuration, InputStream[] features)
	{
		switch (mode)
		{
		case pos: return new POSTrainer(configuration, features);
		case dep: return null;
		case srl: return null;
		default : throw new IllegalArgumentException("Invalid mode: "+mode.toString()); 
		}
	}
}
