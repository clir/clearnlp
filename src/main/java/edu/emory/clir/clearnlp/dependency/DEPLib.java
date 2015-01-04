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
package edu.emory.clir.clearnlp.dependency;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPLib
{
	private DEPLib() {}
	
	/** The node ID of an artificial root. */
	static public final int ROOT_ID = 0;
	/** The node ID of a null node. */
	static public final int NULL_ID = -1;
	/** A dummy tag for the root node. */
	static public final String ROOT_TAG = "_R_";
	
	/** The feat-key of semantic function tags. */
	static public final String FEAT_SEM	= "sem";
	/** The feat-key of syntactic function tags. */
	static public final String FEAT_SYN	= "syn";
	/** The feat-key of sentence types. */
	static public final String FEAT_SNT	= "snt";
	/** The feat-key of PropBank rolesets. */
	static public final String FEAT_PB	= "pb";
	/** The feat-key of VerbNet classes. */
	static public final String FEAT_VN	= "vn";
	/** The feat-key of word senses. */
	static public final String FEAT_WS	= "ws";
	/** The feat-key of word senses. */
	static public final String FEAT_POS2 = "p2";
}