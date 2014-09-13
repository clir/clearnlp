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
package com.clearnlp.lexicon.verbnet;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;

import com.clearnlp.util.Joiner;
import com.clearnlp.util.StringUtils;
import com.clearnlp.util.XmlUtils;
import com.clearnlp.util.constant.StringConst;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class VNSyntax implements Serializable
{
	private static final long serialVersionUID = 3965440701216131397L;
	
	private List<VNThematicRole> l_thematicRoles;
	private Set<String>          s_thematicRoles;
	
	public VNSyntax(Element eSyntax)
	{
		init(eSyntax);
	}
	
	private void init(Element eSyntax)
	{
		List<Element> list = XmlUtils.getChildElementList(eSyntax);
		String role;
		
		l_thematicRoles = Lists.newArrayList();
		s_thematicRoles = Sets.newLinkedHashSet();
		
		for (Element element : list)
		{
			role = StringUtils.toLowerCase(XmlUtils.getTrimmedAttribute(element, VNXml.A_VALUE));
			
			if (VNTag.contains(role))
				addThematicRole(new VNThematicRole(element, role));
		}
	}
	
	public Set<String> getThematicRoleSet()
	{
		return s_thematicRoles;
	}

	public void addThematicRole(VNThematicRole role)
	{
		l_thematicRoles.add(role);
		s_thematicRoles.add(role.toString());
	}
	
	public boolean containsThematicRole(String role)
	{
		return s_thematicRoles.contains(role);
	}
	
	public boolean containsThematicRoleAll(String... roles)
	{
		for (String role : roles)
		{
			if (!containsThematicRole(role))
				return false;
		}
		
		return true;
	}
	
	public boolean containsThematicRoleAll(Collection<String> roles)
	{
		for (String role : roles)
		{
			if (!containsThematicRole(role))
				return false;
		}
		
		return true;
	}

	@Override
	public String toString()
	{
		return toString(StringConst.SPACE, true);
	}
	
	public String toString(String delim, boolean sort)
	{
		return Joiner.join(Lists.newArrayList(s_thematicRoles), delim, sort);
	}
}