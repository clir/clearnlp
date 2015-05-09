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
 * @since 3.0.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Kmeans
{
//	private List<SparseVector> s_points;
//	private final int RAND_SEED = 1;
//	
//	public Kmeans()
//	{
//		s_points = new ArrayList<>();
//	}
//	
//	public void addPoint(SparseVector point)
//	{
//		s_points.add(point);
//	}
//	
//	public SparseVector getPoint(int index)
//	{
//		return s_points.get(index);
//	}
//	
//	/**
//	 * @param K the number of clusters to return.
//	 * @param threshold the minimum RSS.
//	 */
//	public Cluster[] cluster(int K, double threshold)
//	{
//		ObjectDoublePair<Cluster[]> previous = null, current = new ObjectDoublePair<Cluster[]>(null, Double.MAX_VALUE);
//		SparseVector[] centroids = initCentroids(K);
//		int i, max = s_points.size() / K;
//		
//		for (i=0; i<max; i++) 
//		{
//			BinUtils.LOG.info(String.format("===== Iteration: %d =====\n", i));
//
//			previous = current;
//			current  = maximize();
//			estimate(current.o);
//			
//			if (previous.d - current.d < threshold)
//				break;
//		}
//
//		return current.o;
//	}
//	
//	/** Initializes random centroids. */
//	private SparseVector[] initCentroids(int K)
//	{
//		SparseVector[] centroids = new SparseVector[K];
//		Random rand = new Random(RAND_SEED);
//		int k = 0, N = s_points.size();
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
//	private ObjectDoublePair<Cluster[]> maximize(SparseVector[] centroids)
//	{
//		DoubleIntPair max = new DoubleIntPair(0, 0);
//		Cluster[] cluster = new Cluster[K];
//		double[] rss = new double[K];
//		int i, k;
//		
//		for (k=0; k<K; k++)
//			cluster[k] = new Cluster();
//		
//		BinUtils.LOG.info("Maximizing:\n");
//		
//		for (i=0; i<N; i++)
//		{
//			max = max(s_points.get(i));
//			cluster[max.i].addPoint(i);
//			rss[max.i] += max.d;
//		}
//		
//		for (k=0; k<K; k++)
//			BinUtils.LOG.info(String.format("- %4d: size = %d, rss = %5.4f\n", k, cluster[k].size(), rss[k]/cluster[k].size()));
//		
//		return new ObjectDoublePair<Cluster[]>(cluster, MathUtils.sum(rss));
//	}
//	
//	private SparseVector[] estimate(Cluster[] clusters)
//	{
//		SparseVector[] centroids = new SparseVector[K];
//		BinUtils.LOG.info("Estimating:");
//		
//		for (int k=0; k<K; k++)
//		{
//			BinUtils.LOG.info(".");
//			centroids[k] = estimate(clusters[k], k);
//		}
//
//		BinUtils.LOG.info("\n");
//		return centroids;
//	}
//	
//	private SparseVector estimate(Cluster cluster, int k)
//	{
//		SparseVector centroid = new SparseVector(k);
//		int len = cluster.size();
//		
//		for (IntCursor c : cluster.getPointSet())
//			centroid.add(s_points.get(c.value));
//
//		for (ObjectIntPair<Term> p : centroid.getTermMap())
//			p.o.setScore(p.o.getScore()/len);
//		
//		return centroid;
//	}
//	
//	private double cosineSimilarity(SparseVector point, int k)
//	{
//		return 0;
//	}
//	
//	private DoubleIntPair max(SparseVector point)
//	{
//		DoubleIntPair max = new DoubleIntPair(0, -10000);
//		double d;
//		
//		for (int k=0; k<K; k++)
//		{
//			d = cosineSimilarity(point, k);
//			if (d > max.d) max.set(d, k); 
//		}
//		
//		return max;
//	}
//	
//	private int index(int id, int k)
//	{
//		return id * K + k;
//	}
}
