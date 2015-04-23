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
//	private final int RAND_SEED = 0;
//	private int K, N, D;
//	
//	private ObjectIntOpenHashMap<String> m_lexica;
//	private List<int[]> v_points;
//	private double[] d_centroid;
//	private double[] d_scala;
//	
//	public Kmeans()
//	{
//		m_lexica = new ObjectIntOpenHashMap<String>();
//		v_points  = new ArrayList<int[]>();
//	}
//	
//	/** @param lexica term-frequency map. */
//	public void addPoint(ObjectIntHashMap<String> lexica)
//	{
//		int index, i = 0, size = lexica.size();
//		int[] unit = new int[size];
//		
//		for (String lexicon : lexica)
//		{
//			if (m_lexica.containsKey(lexicon))
//			{
//				index = m_lexica.get(lexicon);
//			}
//			else
//			{
//				index = m_lexica.size();
//				m_lexica.put(lexicon, index);
//			}
//			
//			unit[i++] = index;
//		}
//
//		Arrays.sort(unit);
//		v_points.add(unit);
//	}
//	
//	public void addUnit(DEPTree tree)
//	{
//		Set<String> lexica = new HashSet<String>();
//		int i, size = tree.size();
//		
//		for (i=1; i<size; i++) {
//			DEPNode node = tree.get(i);
//			lexica.add(node.getLemma());
//			lexica.add(node.getPOSTag());
//		}
//		addPoint(lexica);
//	}
//
//	/**
//	 * K-means clustering.
//	 * @param threshold minimum RSS.
//	 * @return each row represents a cluster, and
//	 *         each column represents a pair of (index of a unit vector, similarity to the centroid).
//	 */
//	public List<List<DoubleIntPair>> cluster(int k, double threshold)
//	{
//		List<List<DoubleIntPair>> currCluster = null;
//		List<List<DoubleIntPair>> prevCluster = null;
//		double prevRss = -1, currRss;
//		
//		K = k;
//		N = v_points.size();
//		D = m_lexica.size();
//		
//		initCentroids();
//		int iter, max = N / K;
//		
//		for (iter=0; iter<max; iter++) 
//		{
//			System.out.printf("===== Iteration: %d =====\n", iter);
//			
//			currCluster = getClusters();
//			updateCentroids(currCluster);
//			currRss = getRSS(currCluster);
//			
//			if (prevRss >= currRss)		return prevCluster;
//			if (currRss >= threshold)	break;
//			
//			prevRss     = currRss;
//			prevCluster = currCluster;
//		}
//
//		return currCluster;
//	}
//	
//	/** Initializes random centroids. */
//	private void initCentroids()
//	{
//		IntOpenHashSet set = new IntOpenHashSet();
//		Random rand = new Random(RAND_SEED);
//		d_centroid  = new double[K*D];
//		d_scala     = new double[K];
//		
//		while (set.size() < K)
//			set.add(rand.nextInt(N));
//
//		int[] unit;
//		int k = 0;
//		
//		for (IntCursor cur : set)
//		{
//			unit = v_points.get(cur.value);
//			
//			for (int index : unit)
//				d_centroid[getCentroidIndex(k, index)] = 1;
//			
//			d_scala[k++] = Math.sqrt(unit.length);
//		}
//	}
//	
//	/** @return centroid of each cluster. */
//	private void updateCentroids(List<List<DoubleIntPair>> cluster)
//	{
//		List<DoubleIntPair> ck;
//		int i, k, size;
//		double scala;
//		
//		Arrays.fill(d_centroid, 0);
//		Arrays.fill(d_scala   , 0);
//		
//		System.out.print("Updating centroids: ");
//		
//		for (k=0; k<K; k++)
//		{
//			ck = cluster.get(k);
//			
//			for (DoubleIntPair p : ck)
//			{
//				for (int index : v_points.get(p.i))
//					d_centroid[getCentroidIndex(k, index)] += 1;
//			}
//			
//			size  = ck.size();
//			scala = 0;
//			
//			for (i=k*D; i<(k+1)*D; i++)
//			{
//				if (d_centroid[i] > 0)
//				{
//					d_centroid[i] /= size;
//					scala += d_centroid[i] * d_centroid[i];	
//				}
//			}
//			
//			d_scala[k] = Math.sqrt(scala);
//			System.out.print(".");
//		}
//		
//		System.out.println();
//	}
//	
//	/** Each cluster contains indices of {@link Kmeans#v_points}. */
//	private List<List<DoubleIntPair>> getClusters()
//	{
//		List<List<DoubleIntPair>> cluster = new ArrayList<List<DoubleIntPair>>(K);
//		DoubleIntPair max = new DoubleIntPair(-1, -1);
//		int[] unit;
//		int i, k;	double sim;
//		
//		for (k=0; k<K; k++)
//			cluster.add(new ArrayList<DoubleIntPair>());
//		
//		System.out.print("Clustering: ");
//		
//		for (i=0; i<N; i++)
//		{
//			unit = v_points.get(i);
//			max.set(-1, -1);
//			
//			for (k=0; k<K; k++)
//			{
//				if ((sim = cosine(unit, k)) > max.d)
//					max.set(sim, k);
//			}
//			
//			cluster.get(max.i).add(new DoubleIntPair(max.d, i));
//			if (i%10000 == 0)	System.out.print(".");
//		}
//		
//		System.out.println();
//		
//		for (k=0; k<K; k++)
//			System.out.printf("- %4d: %d\n", k, cluster.get(k).size());
//		
//		return cluster;
//	}
//	
//	/**
//	 * @param k     [0, K-1].
//	 * @param index [0, D-1].
//	 */
//	private int getCentroidIndex(int k, int index)
//	{
//		return k * D + index;
//	}
//	
//	private double getRSS(List<List<DoubleIntPair>> cluster)
//	{
//		double sim = 0;
//		System.out.print("Calulating RSS: ");
//		
//		for (int k=0; k<K; k++)
//		{
//			for (DoubleIntPair tup : cluster.get(k))
//				sim += cosine(v_points.get(tup.i), k);
//			
//			System.out.print(".");
//		}
//		
//		System.out.println();
//		sim /= N;
//		
//		System.out.println("RSS = "+sim);
//		return sim / N;
//	}
}
