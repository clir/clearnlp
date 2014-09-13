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
package com.clearnlp.util;

/**
 * @since 3.0.0
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class MathUtils
{
	private MathUtils() {}
	
	static public double average(double... array)
	{
		double sum = 0;
		
		for (double d : array)
			sum += d;
		
		return sum / array.length;
	}
	
	static public double variance(double... array)
	{
		double avg = average(array), sum = 0;
		
		for (double d : array)
			sum += sq(d - avg);
		
		return sum / (array.length - 1);
	}
	
	static public double stdev(double... array)
	{
		return Math.sqrt(variance(array));
	}
	
	static public int ceil(double d)
	{
		return (int)Math.ceil(d);
	}
	
	static public double divide(int numerator, int denominator)
	{
		return (double)numerator / denominator;
	}
	
	static public double divide(long numerator, long denominator)
	{
		return (double)numerator / denominator;
	}
	
	static public double accuracy(int correct, int total)
	{
		return 100d * correct / total;
	}
	
	static public double reciprocal(int number)
	{
		return divide(1, number);
	}

	/** @param exponent non-negative integer. */
	static public double pow(double base, int exponent)
	{
		if (exponent ==  0) return 1;
		if (exponent ==  1) return base;
		if (exponent == -1) return 1/base;
		
		boolean negative;
		
		if (exponent < 0)
		{
			negative = true;
			exponent = -exponent;
		}
		else
			negative = false;
		
		double mod = (exponent%2 == 0) ? 1 : base;
		double p = base;
		
		exponent /= 2;
		
		while (exponent > 0)
		{
			p = p * p;
			exponent /= 2;
		}
		
		mod *= p;
		return negative ? 1/mod : mod;
	}
	
	static public long sq(long l)
	{
		return l * l;
	}
	
	static public double sq(double d)
	{
		return d * d;
	}
	
	static public int signum(double d)
	{
		return (int)Math.signum(d);
	}
	
	static public double getF1(double precision, double recall)
	{
		return (precision + recall == 0) ? 0 : 2 * (precision * recall) / (precision + recall);
	}
	
	static public double sumOfSquares(double[] vector)
	{
		double sum = 0;
		
		for (double d : vector)
			sum += sq(d);
		
		return sum;
	}
	
	static public void multiply(float[] array, int multiplier)
	{
		int i, size = array.length;
		
		for (i=0; i<size; i++)
			array[i] *= multiplier;
	}
	
	/** @param n a positive integer. */
	static public boolean isPrimeNumber(long n)
	{
		if (n < 2) return false;
		if (n == 2 || n == 3) return true;
		if (n%2 == 0 || n%3 == 0) return false;
		
		long i, sqrt = (long)Math.sqrt(n) + 1;
	    
		for (i=6; i<=sqrt; i+=6)
		{
			if (n%(i-1) == 0 || n%(i+1) == 0)
				return false;
		}
		
		return true;
	}
	
	/**
	 * @param n inclusive.
	 * @return the next prime number if exists; otherwise, {@code -1}.
	 */
	static public long nextPrimeNumber(long n)
	{
		while (true)
		{
			if (n >= Long.MAX_VALUE)
				return -1;
			
			if (isPrimeNumber(n))
				return n;
			
			n++;
		}
	}
	
	/**
	 * @param a either a positive or negative integer.
	 * @param b a positive integer.
	 */
	static public int divisor(int a, int b)
	{
		return (a < 0) ? ((a % b) + b) % b : a % b;
	}
}