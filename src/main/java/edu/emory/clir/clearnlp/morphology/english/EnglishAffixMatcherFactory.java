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
package edu.emory.clir.clearnlp.morphology.english;

import java.util.List;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;

import edu.emory.clir.clearnlp.morphology.AbstractAffixMatcher;
import edu.emory.clir.clearnlp.morphology.AbstractAffixReplacer;
import edu.emory.clir.clearnlp.util.Splitter;
import edu.emory.clir.clearnlp.util.XmlUtils;
import edu.emory.clir.clearnlp.util.constant.StringConst;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class EnglishAffixMatcherFactory
{
	final String ELEM_AFFIX             = "affix";
	final String ELEM_RULE              = "rule";
	final String ATTR_TYPE              = "type";
	final String ATTR_FORM              = "form";
	final String ATTR_POS               = "pos";
	final String ATTR_ORG_POS           = "org_pos";
	final String ATTR_BASE_POS          = "base_pos";
	final String ATTR_AFFIX_FORM        = "affix_form";
	final String ATTR_REPLACEMENTS      = "replacements";
	final String ATTR_DOUBLE_CONSONANTS = "doubleConsonants";
	final String VAL_SUFFIX             = "suffix";
	
	public List<AbstractAffixMatcher> createAffixMatchers(Element eAffixes)
	{
		List<AbstractAffixMatcher> affixes = Lists.newArrayList();
		NodeList list = eAffixes.getElementsByTagName(ELEM_AFFIX);
		int i, size = list.getLength();
		Element eAffix;
		
		for (i=0; i<size; i++)
		{
			eAffix = (Element)list.item(i);
			affixes.add(createAffixMatcher(eAffix));
		}
		
		return affixes;
	}
	
	public AbstractAffixMatcher createAffixMatcher(Element eAffix)
	{
		String   type = XmlUtils.getTrimmedAttribute(eAffix, ATTR_TYPE);
		String   form = XmlUtils.getTrimmedAttribute(eAffix, ATTR_FORM);
		String    pos = XmlUtils.getTrimmedAttribute(eAffix, ATTR_POS);
		String orgPOS = XmlUtils.getTrimmedAttribute(eAffix, ATTR_ORG_POS);
		Pattern  oPOS = orgPOS.equals(StringConst.EMPTY) ? null : Pattern.compile("^("+orgPOS+")$");
		
		boolean bSuffix = type.equals(VAL_SUFFIX);
		AbstractAffixMatcher matcher;
		
		if (bSuffix)	matcher = new EnglishSuffixMatcher(form, pos, oPOS);
		else			throw new IllegalArgumentException("Invalid affix type: "+type);
		
		NodeList list = eAffix.getElementsByTagName(ELEM_RULE);
		AbstractAffixReplacer replacer;
		int i, size = list.getLength();
		
		for (i=0; i<size; i++)
		{
			replacer = getAffixReplacer(bSuffix, (Element)list.item(i));
			if (replacer != null) matcher.addReplacer(replacer);
		}
		
		return matcher;
	}
	
	/** Called by {@link #createAffixMatcher(Element)}. */
	private AbstractAffixReplacer getAffixReplacer(boolean bSuffix, Element eRule)
	{
		String   basePOS      = XmlUtils.getTrimmedAttribute(eRule, ATTR_BASE_POS); 
		String   affixForm    = XmlUtils.getTrimmedAttribute(eRule, ATTR_AFFIX_FORM);
		String[] replacements = Splitter.splitCommas(XmlUtils.getTrimmedAttribute(eRule, ATTR_REPLACEMENTS), true);
		
		String dc = XmlUtils.getTrimmedAttribute(eRule, ATTR_DOUBLE_CONSONANTS);
		boolean doubleConsonants = dc.equals(StringConst.EMPTY) ? false : Boolean.parseBoolean(dc);
		
		return bSuffix ? new EnglishSuffixReplacer(basePOS, affixForm, replacements, doubleConsonants) : null;
	}
}
