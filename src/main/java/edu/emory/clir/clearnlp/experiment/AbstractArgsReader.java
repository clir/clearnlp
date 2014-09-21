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
package edu.emory.clir.clearnlp.experiment;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
abstract public class AbstractArgsReader
{
	public AbstractArgsReader(String[] args, Object obj)
	{
		CmdLineParser cmd = new CmdLineParser(obj);
		
		try
		{
			cmd.parseArgument(args);
			String msg = getErrorMessage();
			if (msg != null) throw new CmdLineException(cmd, msg);
		}
		catch (CmdLineException e)
		{
			System.err.println(e.getMessage());
			cmd.printUsage(System.err);
			System.exit(1);
		}
	}
	
	abstract protected String getErrorMessage();
}