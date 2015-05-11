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


/**
 * Kmeans++ algorithm.
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class KmeansPPClustering extends AbstractCluster
{
//	final public int K;
//	final public double RSS;
//	
//	/**
//	 * @param k the number of clusters to return.
//	 * @param rss the threshold for RSS.
//	 */
//	public KmeansPPClustering(int k, double rss)
//	{
//		super();
//		this.K   = k;
//		this.RSS = rss;
//	}
//	
//	@Override
	public Cluster[] cluster()
	{
//		ObjectDoublePair<Cluster[]> previous = null, current = new ObjectDoublePair<Cluster[]>(null, Double.MAX_VALUE);
//		SparseVector[] centroids = initCentroids();
//		
//		for (int i=s_points.size() / K; i>=0; i--) 
//		{
//			BinUtils.LOG.info(String.format("===== Iteration: %d =====\n", i));
//
//			previous = current;
//			current  = maximization(centroids);
//			expectation(current.o);
//			
//			if (previous.d - current.d < RSS)
//				break;
//		}
//
//		return current.o;
		return null;
	}
//	
//	private SparseVector[] initCentroids()
//	{
//		SparseVector[] centroids = new SparseVector[K];
//		int k = 0, N = s_points.size();
//		Random rand = new Random(1);
//		
//		
//		
//		IntHashSet set = new IntHashSet();
//		while (set.size() < K) set.add(rand.nextInt(N));
//
//		for (IntCursor c : set)
//			centroids[k++] = s_points.get(c.value);
//		
//		return centroids;
//	}
//	
//	private double[] D2(IntHashSet centroids)
//	{
//		int i, N = s_points.size();
//		double[] d2 = new double[N];
//		SparseVector point;
//		
//		for (i=0; i<N; i++)
//		{
//			if (centroids.contains(i)) continue;
//			
//			for (IntCursor c : centroids)
//			{
//				
//			}
//		}
//	}
//	
//	private ObjectDoublePair<List<Cluster>> maximization(List<SparseVector> centroids)
//	{
//		List<Cluster> clusters = centroids.stream().map(c -> new Cluster()).collect(Collectors.toCollection(ArrayList::new));
//		double[] centNorms = euclideanNorms(centroids);
//		double[] rss = new double[centNorms.length];
//		DoubleIntPair max = new DoubleIntPair(0, 0);
//		
//		BinUtils.LOG.info("Maximizing:");
//		
//		for (int i=s_points.size()-1; i>=0; i--)
//		{
//			if (i%10000 == 0) BinUtils.LOG.info(".");
//			max = max(centroids, centNorms, s_points.get(i));
//			clusters.get(max.i).addPoint(i);
//			rss[max.i] += max.d;
//		}	BinUtils.LOG.info("\n");
//		
//		
//		double d = MathUtils.sum(rss);
//		int k = 0;
//		
//		clusters.stream().forEach(cluster -> BinUtils.LOG.info(String.format("%4d: size = %6d, avg-rss = %5.4f\n", k++, cluster.size(), rss[k]/cluster.size())));
//		BinUtils.LOG.info(String.format("%4s: size = %6d, sum-rss = %5.4f\n", "ALL", s_points.size(), d));
//		return new ObjectDoublePair<List<Cluster>>(clusters, d);
//	}
//	
//	private List<SparseVector> expectation(List<Cluster> clusters)
//	{
//		BinUtils.LOG.info("Calculating centroids: "+clusters.size()+"\n");
//		List<SparseVector> centroids = clusters.stream().map(cluster -> computeCentroid(cluster)).collect(Collectors.toCollection(ArrayList::new));
//		BinUtils.LOG.info("\n");
//		return centroids;
//	}
//	
//	private SparseVector computeCentroid(Cluster cluster)
//	{
//		SparseVector centroid = new SparseVector(-1);
//		BinUtils.LOG.info(".");
//		
//		for (IntCursor c : cluster.getPointSet())
//			centroid.add(s_points.get(c.value));
//
//		centroid.divide(cluster.size());
//		return centroid;
//	}
//	
//	private double[] euclideanNorms(List<SparseVector> points)
//	{
//		return points.stream().mapToDouble(point -> point.euclideanNorm()).toArray();
//	}
//	
//	private DoubleIntPair max(List<SparseVector> centroids, double[] centNorms, SparseVector point)
//	{
//		DoubleIntPair max = new DoubleIntPair(-10000d, 0);
//		double d, pointNorm = point.euclideanNorm();
//		
//		for (int k=centNorms.length-1; k>=0; k--)
//		{
//			d = cosineSimilarity(centroids.get(k), centNorms[k], point, pointNorm);
//			if (d > max.d) max.set(d, k); 
//		}
//		
//		return max;
//	}
//	
//	private double cosineSimilarity(SparseVector centroid, double centNorm, SparseVector point, double pointNorm)
//	{
//		return centroid.dotProduct(point) / (centNorm * pointNorm);
//	}
}
