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
package com.clearnlp.constant;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jdchoi77@gmail.com})
 */
public class PatternConstTest
{
	@Test
	public void test()
	{
		assertEquals("[a, b]", Arrays.toString(PatternConst.COLON.split("a:b")));
		assertEquals("[a, b]", Arrays.toString(PatternConst.COMMA.split("a,b")));
		assertEquals("[a, b]", Arrays.toString(PatternConst.HYPHEN.split("a-b")));
		assertEquals("[a, b]", Arrays.toString(PatternConst.SEMICOLON.split("a;b")));
		assertEquals("[a, b]", Arrays.toString(PatternConst.UNDERSCORE.split("a_b")));
	}
}