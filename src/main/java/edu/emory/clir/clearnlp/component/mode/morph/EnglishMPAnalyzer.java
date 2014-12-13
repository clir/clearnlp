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

package edu.emory.clir.clearnlp.component.mode.morph;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.w3c.dom.Element;

import com.google.common.collect.Maps;

import edu.emory.clir.clearnlp.dependency.DEPNode;
import edu.emory.clir.clearnlp.dictionary.PathEnglishMPAnalyzer;
import edu.emory.clir.clearnlp.morphology.AbstractAffixMatcher;
import edu.emory.clir.clearnlp.morphology.english.EnglishAffixMatcherFactory;
import edu.emory.clir.clearnlp.morphology.english.EnglishInflection;
import edu.emory.clir.clearnlp.pos.POSLibEn;
import edu.emory.clir.clearnlp.util.DSUtils;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.emory.clir.clearnlp.util.Splitter;
import edu.emory.clir.clearnlp.util.XmlUtils;
import edu.emory.clir.clearnlp.util.constant.MetaConst;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishMPAnalyzer extends AbstractMPAnalyzer implements PathEnglishMPAnalyzer
{
	final String FIELD_DELIM = StringConst.UNDERSCORE;

	private EnglishInflection inf_verb;
	private EnglishInflection inf_noun;
	private EnglishInflection inf_adjective;
	private EnglishInflection inf_adverb;
	
	/** Abbreviation replacement rules */
	private Map<String,String> rule_abbreviation;
	private Set<String> base_cardinal;
	/** Ordinal base-forms */
	private Set<String> base_ordinal;
	
//	====================================== CONSTRUCTORS ======================================
	
	/** Constructs an English morphological analyzer from the dictionary in a classpath. */
	public EnglishMPAnalyzer()
	{
		Element inflection = XmlUtils.getDocumentElement(IOUtils.getInputStreamsFromClasspath(INFLECTION_SUFFIX));
		
		try
		{
			inf_verb      = getInflectionRules(inflection, VERB     , POSLibEn.POS_VB);
			inf_noun      = getInflectionRules(inflection, NOUN     , POSLibEn.POS_NN);
			inf_adjective = getInflectionRules(inflection, ADJECTIVE, POSLibEn.POS_JJ);
			inf_adverb    = getInflectionRules(inflection, ADVERB   , POSLibEn.POS_RB);
			
			base_cardinal     = DSUtils.createStringHashSet(IOUtils.getInputStreamsFromClasspath(CARDINAL_BASE));
			base_ordinal      = DSUtils.createStringHashSet(IOUtils.getInputStreamsFromClasspath(ORDINAL_BASE));
			rule_abbreviation = getAbbreviationMap(IOUtils.getInputStreamsFromClasspath(ABBREVIATOIN_RULE));
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	public EnglishMPAnalyzer(ZipFile file)
	{
		try
		{
			Element inflection = XmlUtils.getDocumentElement(file.getInputStream(new ZipEntry(INFLECTION_SUFFIX)));
			
			inf_verb      = getInflectionRules(file, inflection, VERB     , POSLibEn.POS_VB);
			inf_noun      = getInflectionRules(file, inflection, NOUN     , POSLibEn.POS_NN);
			inf_adjective = getInflectionRules(file, inflection, ADJECTIVE, POSLibEn.POS_JJ);
			inf_adverb    = getInflectionRules(file, inflection, ADVERB   , POSLibEn.POS_RB);

			base_cardinal     = DSUtils.createStringHashSet(file.getInputStream(new ZipEntry(CARDINAL_BASE)));
			base_ordinal      = DSUtils.createStringHashSet(file.getInputStream(new ZipEntry(ORDINAL_BASE)));
			rule_abbreviation = getAbbreviationMap(file.getInputStream(new ZipEntry(ABBREVIATOIN_RULE)));
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	/** @param stream this input-stream becomes a parameter of {@link ZipInputStream}. */
	public EnglishMPAnalyzer(InputStream stream)
	{
		try
		{
			ZipInputStream zin = new ZipInputStream(stream);
			Map<String,byte[]> map = IOUtils.toByteMap(zin);

			Element inflection = XmlUtils.getDocumentElement(new ByteArrayInputStream(map.get(INFLECTION_SUFFIX)));
			
			inf_verb      = getInflectionRules(map, inflection, VERB     , POSLibEn.POS_VB);
			inf_noun      = getInflectionRules(map, inflection, NOUN     , POSLibEn.POS_NN);
			inf_adjective = getInflectionRules(map, inflection, ADJECTIVE, POSLibEn.POS_JJ);
			inf_adverb    = getInflectionRules(map, inflection, ADVERB   , POSLibEn.POS_RB);
			
			base_cardinal     = DSUtils.createStringHashSet(new ByteArrayInputStream(map.get(CARDINAL_BASE)));
			base_ordinal      = DSUtils.createStringHashSet(new ByteArrayInputStream(map.get(ORDINAL_BASE)));
			rule_abbreviation = getAbbreviationMap  (new ByteArrayInputStream(map.get(ABBREVIATOIN_RULE)));
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	/** Called by {@link #EnglishMPAnalyzer()}. */
	private EnglishInflection getInflectionRules(Element eInflection, String type, String basePOS) throws IOException
	{
		Element     eAffixes        = XmlUtils.getFirstElementByTagName(eInflection, type);
		InputStream baseStream      = IOUtils.getInputStreamsFromClasspath(ROOT + type + EXT_BASE);
		InputStream exceptionStream = IOUtils.getInputStreamsFromClasspath(ROOT + type + EXT_EXCEPTION);
		
		return getInflection(baseStream, exceptionStream, eAffixes, basePOS);
	}
	
	/** Called by {@link #EnglishMPAnalyzer(ZipFile)}. */
	private EnglishInflection getInflectionRules(ZipFile file, Element eInflection, String type, String basePOS) throws IOException
	{
		Element     eAffixes        = XmlUtils.getFirstElementByTagName(eInflection, type);
		InputStream baseStream      = file.getInputStream(new ZipEntry(ROOT + type + EXT_BASE));
		InputStream exceptionStream = file.getInputStream(new ZipEntry(ROOT + type + EXT_EXCEPTION));
		
		return getInflection(baseStream, exceptionStream, eAffixes, basePOS);
	}
	
	/** Called by {@link #EnglishMPAnalyzer(InputStream)}. */
	private EnglishInflection getInflectionRules(Map<String,byte[]> map, Element eInflection, String type, String basePOS) throws IOException
	{
		Element     eAffixes        = XmlUtils.getFirstElementByTagName(eInflection, type);
		InputStream baseStream      = new ByteArrayInputStream(map.get(ROOT+type+EXT_BASE));
		InputStream exceptionStream = new ByteArrayInputStream(map.get(ROOT+type+EXT_EXCEPTION));
		
		return getInflection(baseStream, exceptionStream, eAffixes, basePOS);
	}
	
	private EnglishInflection getInflection(InputStream baseStream, InputStream exceptionStream, Element eAffixes, String basePOS) throws IOException
	{
		Map<String,String> exceptionMap = (exceptionStream != null) ? DSUtils.createStringHashMap(exceptionStream, Splitter.T_SPACE) : null;
		List<AbstractAffixMatcher> affixMatchers = new EnglishAffixMatcherFactory().createAffixMatchers(eAffixes);
		Set<String> baseSet = DSUtils.createStringHashSet(baseStream);
		return new EnglishInflection(basePOS, baseSet, exceptionMap, affixMatchers);
	}

	private Map<String,String> getAbbreviationMap(InputStream stream) throws IOException
	{
		BufferedReader fin = new BufferedReader(new InputStreamReader(stream));
		Map<String,String> map = Maps.newHashMap();
		String line, abbr, pos, key, base;
		String[] tmp;
		
		while ((line = fin.readLine()) != null)
		{
			tmp  = Splitter.splitSpace(line.trim());
			abbr = tmp[0];
			pos  = tmp[1];
			base = tmp[2];
			key  = abbr + FIELD_DELIM + pos;
			
			map.put(key, base);
		}
			
		return map;
	}
	
	/**
	 * Analyzes the lemma and morphemes of the word-form in the specific node.
	 * PRE: the word-form and the POS tag of the node. 
	 */
	@Override
	public void analyze(DEPNode node)
	{
		String lswf = node.getLowerSimplifiedWordForm(); 
		String pos  = node.getPOSTag();
		String lemma;
		
		if ((lemma = getAbbreviation(lswf, pos)) != null || (lemma = getBaseFormFromInflection(lswf, pos)) != null)
			node.setLemma(lemma);
		else
			node.setLemma(lswf);
		
		if      (isCardinal(node.getLemma()))	node.setLemma(MetaConst.CARDINAL);
		else if (isOrdinal (node.getLemma()))	node.setLemma(MetaConst.ORDINAL);
	}
	
	/** Called by {@link #analyze(DEPNode)}. */
	private String getAbbreviation(String form, String pos)
	{
		String key = form + FIELD_DELIM + pos;
		return rule_abbreviation.get(key);
	}
	
	/** @param form the lower simplified word-form. */
	private String getBaseFormFromInflection(String form, String pos)
	{
		
		if (POSLibEn.isVerb(pos))
			return inf_verb.getBaseForm(form, pos);
			
		if (POSLibEn.isNoun(pos))
			return inf_noun.getBaseForm(form, pos);
		
		if (POSLibEn.isAdjective(pos))
			return inf_adjective.getBaseForm(form, pos);
		
		if (POSLibEn.isAdverb(pos))
			return inf_adverb.getBaseForm(form, pos);
			
		return null;
	}
	
	private boolean isCardinal(String lower)
	{
		return base_cardinal.contains(lower);
	}
	
	private boolean isOrdinal(String lower)
	{
		return lower.equals("0st") || lower.equals("0nd") || lower.equals("0rd") || lower.equals("0th") || base_ordinal.contains(lower);
	}
}
