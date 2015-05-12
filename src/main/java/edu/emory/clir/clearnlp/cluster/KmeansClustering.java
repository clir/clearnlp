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
package edu.emory.clir.clearnlp.cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.carrotsearch.hppc.cursors.IntCursor;

import edu.emory.clir.clearnlp.collection.pair.DoubleIntPair;
import edu.emory.clir.clearnlp.collection.pair.ObjectDoublePair;
import edu.emory.clir.clearnlp.collection.set.IntHashSet;
import edu.emory.clir.clearnlp.util.BinUtils;
import edu.emory.clir.clearnlp.util.MathUtils;


/**
 * Kmeans++ algorithm.
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class KmeansClustering extends AbstractCluster
{
	final protected int K;
	final protected int NUM_THREADS;
	final protected int MAX_ITERATIONS;
	final protected double RSS_THRESHOLD;
	
	volatile double[] D2;
	
	public KmeansClustering(int k, int maxIterations, double rssThreshold, int numThreads)
	{
		super();
		
		K = k;
		MAX_ITERATIONS = maxIterations;
		RSS_THRESHOLD  = rssThreshold;
		NUM_THREADS    = numThreads;
	}
	
	@Override
	public List<Cluster> cluster()
	{
		ObjectDoublePair<List<Cluster>> current = new ObjectDoublePair<>(null, 0);
		List<SparseVector> centroids = initialization();
		double previousRSS;
		
		for (int i=0; i<MAX_ITERATIONS; i++) 
		{
			BinUtils.LOG.info(String.format("Iteration: %d\n", i));
			previousRSS = current.d;
			current     = maximization(centroids);
			centroids   = expectation(current.o);
			if (current.d - previousRSS < RSS_THRESHOLD) break;
		}

		return current.o;
	}
	
//	==================================== Initialization ====================================
	
	private List<SparseVector> initialization()
	{
		IntHashSet centroidSet = new IntHashSet();
		int newCentroid, i, N = s_points.size();
		Random rand = new Random(1);
		double[] cum;
		double   sum;
		
		BinUtils.LOG.info("Initialization:");
		
		D2 = new double[N];
		Arrays.fill(D2, Double.MAX_VALUE);
		centroidSet.add(newCentroid = rand.nextInt(N));
		D2[newCentroid] = 0;
		
		while (centroidSet.size() < K)
		{
			sum = computeD2(centroidSet, newCentroid);
			cum = cumulativeD2(N, sum);
			
			for (i=0; i<N; i++)
			{
				if (!centroidSet.contains(i) && rand.nextDouble() < cum[i])
				{
					centroidSet.add(newCentroid = i);
					D2[newCentroid] = 0;
					break;
				}
			}
		}
		
		D2 = null;
		BinUtils.LOG.info(centroidSet.toString()+"\n");
		List<SparseVector> centroids = new ArrayList<>(K);
		for (IntCursor c : centroidSet) centroids.add(s_points.get(c.value));
		return centroids;
	}
	
	private double computeD2(IntHashSet centroidSet, int newCentroidID)
	{
		ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
		List<Future<Double>> list = new ArrayList<>(NUM_THREADS);
		SparseVector newCentroid = s_points.get(newCentroidID);
		double centroidNorm = newCentroid.euclideanNorm();
		int i, j, N = s_points.size(), GAP = gap();
		Callable<Double> task;
		
		for (i=0; i<N; i+=GAP)
		{
			if ((j = i + GAP) > N) j = N;
			task = new IntializationTask(centroidSet, newCentroid, centroidNorm, i, j);
			list.add(pool.submit(task));
		}
		
		double sum = 0;
		try {for (Future<Double> f : list) sum += f.get();}
		catch (Exception e) {e.printStackTrace();}
		
		return sum;
	}
	
	private double[] cumulativeD2(int N, double sum)
	{
		double[] c = new double[N];
		c[0] = D2[0] / sum;
		
		for (int i=1; i<N; i++)
			c[i] = c[i-1] + D2[i]/sum;
		
		return c;
	}
	
	private class IntializationTask implements Callable<Double>
	{
		private IntHashSet   centroid_set;
		private SparseVector new_centroid;
		private int          begin_index;
		private int          end_index;
		private double       centroid_norm;
		
		public IntializationTask(IntHashSet centroidSet, SparseVector newCentroid, double centroidNorm, int beginIndex, int endIndex)
		{
			centroid_set  = centroidSet;
			new_centroid  = newCentroid;
			centroid_norm = centroidNorm;
			begin_index   = beginIndex;
			end_index     = endIndex;
		}

		public Double call()
		{
			SparseVector point;
			double sum = 0;
			
			for (int i=begin_index; i<end_index; i++)
			{
				if (centroid_set.contains(i)) continue;
				point = s_points.get(i);
				D2[i] = Math.min(D2[i], 1-cosineSimilarity(new_centroid, centroid_norm, point, point.euclideanNorm()));
				sum += D2[i];
			}
			
			return sum;
		}
	}
	
//	==================================== Maximization ====================================
	
	private ObjectDoublePair<List<Cluster>> maximization(List<SparseVector> centroids)
	{
		List<Future<ObjectDoublePair<List<Cluster>>>> list = new ArrayList<>(NUM_THREADS);
		ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
		double[] centroidNorms = euclideanNorms(centroids);
		Callable<ObjectDoublePair<List<Cluster>>> task;
		int i, j, N = s_points.size(), GAP = gap();
		
		BinUtils.LOG.info("- Maximization: ");
		
		for (i=0; i<N; i+=GAP)
		{
			if ((j = i + GAP) > N) j = N;
			task = new MaximizationTask(centroids, centroidNorms, i, j);
			list.add(pool.submit(task));
		}
		
		List<Cluster> clusters = centroids.stream().map(c -> new Cluster()).collect(Collectors.toCollection(ArrayList::new));
		ObjectDoublePair<List<Cluster>> p;
		double rss = 0;
		
		try
		{
			for (Future<ObjectDoublePair<List<Cluster>>> f : list)
			{
				p = f.get();
				rss += p.d;
				for (i=0; i<K; i++) clusters.get(i).merge(p.o.get(i));
			}
		}
		catch (Exception e) {e.printStackTrace();}
		
		BinUtils.LOG.info(String.format("%f\n", rss));
		return new ObjectDoublePair<List<Cluster>>(clusters, rss);
	}
	
	private class MaximizationTask implements Callable<ObjectDoublePair<List<Cluster>>>
	{
		List<SparseVector> centroid_list;
		private double[]   centroid_norms;
		private int        begin_index;
		private int        end_index;
		
		public MaximizationTask(List<SparseVector> centroidList, double[] centNorms, int beginIndex, int endIndex)
		{
			centroid_list  = centroidList;
			centroid_norms = centNorms;
			begin_index    = beginIndex;
			end_index      = endIndex;
		}

		public ObjectDoublePair<List<Cluster>> call()
		{
			List<Cluster> clusters = centroid_list.stream().map(c -> new Cluster()).collect(Collectors.toCollection(ArrayList::new));
			DoubleIntPair max = new DoubleIntPair(0, 0);
			double rss = 0;
			
			for (int i=begin_index; i<end_index; i++)
			{
				max = max(centroid_list, centroid_norms, s_points.get(i));
				clusters.get(max.i).addPoint(s_points.get(i));
				rss += max.d;
			}
			
			return new ObjectDoublePair<>(clusters, rss);
		}
	}
	
	private DoubleIntPair max(List<SparseVector> centroids, double[] centroidNorms, SparseVector point)
	{
		DoubleIntPair max = new DoubleIntPair(-10000d, 0);
		double d, pointNorm = point.euclideanNorm();
		
		for (int k=centroidNorms.length-1; k>=0; k--)
		{
			d = cosineSimilarity(centroids.get(k), centroidNorms[k], point, pointNorm);
			if (d > max.d) max.set(d, k); 
		}
		
		return max;
	}
	
	private double[] euclideanNorms(List<SparseVector> points)
	{
		return points.stream().mapToDouble(point -> point.euclideanNorm()).toArray();
	}
	
//	==================================== Expectation ====================================

	private List<SparseVector> expectation(List<Cluster> clusters)
	{
		ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
		List<Future<SparseVector>> list = new ArrayList<>(K);
		Callable<SparseVector> task;
		
		BinUtils.LOG.info("- Expectation\n");
		
		for (int i=0; i<K; i++)
		{
			task = new ExpectationTask(clusters.get(i));
			list.add(pool.submit(task));
		}
		
		List<SparseVector> centroids = new ArrayList<>();
		try {for (Future<SparseVector> f : list) centroids.add(f.get());}
		catch (Exception e) {e.printStackTrace();}
		return centroids;
	}
	
	private class ExpectationTask implements Callable<SparseVector>
	{
		private Cluster cluster;

		public ExpectationTask(Cluster cluster)
		{
			this.cluster = cluster;
		}

		public SparseVector call()
		{
			SparseVector centroid = new SparseVector(-1);
			for (SparseVector v : cluster.getPointSet()) centroid.add(v);
			centroid.divide(cluster.size());
			return centroid;			
		}
	}
	
	private double cosineSimilarity(SparseVector centroid, double centNorm, SparseVector point, double pointNorm)
	{
		return centroid.dotProduct(point) / (centNorm * pointNorm);
	}
	
	private int gap()
	{
		return (int)Math.ceil(MathUtils.divide(s_points.size(), NUM_THREADS));		
	}
}
