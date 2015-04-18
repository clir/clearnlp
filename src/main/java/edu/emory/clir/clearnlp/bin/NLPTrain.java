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
package edu.emory.clir.clearnlp.bin;

import java.io.InputStream;

import edu.emory.clir.clearnlp.bin.helper.AbstractNLPTrain;
import edu.emory.clir.clearnlp.component.mode.dep.DEPTrainer;
import edu.emory.clir.clearnlp.component.mode.pos.POSTrainer;
import edu.emory.clir.clearnlp.component.trainer.AbstractNLPTrainer;
import edu.emory.clir.clearnlp.component.utils.NLPMode;

/**
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPTrain extends AbstractNLPTrain
{
	public NLPTrain() {}
	
	public NLPTrain(String[] args) throws Exception
	{
		super(args);
	}
	
	static public void main(String[] args)
	{
		try 
		{
			new NLPTrain(args);
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	@Override
	protected AbstractNLPTrainer getTrainer(NLPMode mode, InputStream configuration, InputStream[] features)
	{
		switch (mode)
		{
		case pos: return new POSTrainer(configuration, features);
		case dep: return new DEPTrainer(configuration, features);
		case srl: return null;
		default : throw new IllegalArgumentException("Invalid mode: "+mode.toString()); 
		}
	}
}
