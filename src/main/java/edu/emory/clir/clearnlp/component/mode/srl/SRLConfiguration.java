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
package edu.emory.clir.clearnlp.component.mode.srl;

import java.io.InputStream;

import edu.emory.clir.clearnlp.component.configuration.AbstractConfiguration;
import edu.emory.clir.clearnlp.component.utils.NLPMode;

/**
 * @since 3.2.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SRLConfiguration extends AbstractConfiguration
{
//	============================== Initialization ==============================
	
	public SRLConfiguration()
	{
		super(NLPMode.srl);
	}
	
	public SRLConfiguration(InputStream in)
	{
		super(NLPMode.srl, in);
	}
}
