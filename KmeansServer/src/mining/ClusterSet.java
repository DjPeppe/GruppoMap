
package mining;

import java.io.*;
import data.Data;
import data.OutOfRangeSampleSize;
import data.Tuple;

public class ClusterSet implements Serializable {
	
	private Cluster C[]; // vettore di cluster inizialmente vuoti
	private int i = 0; // numero di Cluster in ClusterSet
 
	ClusterSet(int k)
	{
		C = new Cluster[k];
	}

	void add(Cluster c)
	{
		C[i] = c; 
		i++;
	}

	Cluster get(int i)
	{
		return C[i];
	}

	void initializeCentroids(Data data) throws OutOfRangeSampleSize
	{
		int centroidIndexes[] = data.sampling(C.length);
		for (int i = 0; i < centroidIndexes.length; i++)
		{
			Tuple centroidI = data.getItemSet(centroidIndexes[i]);
			add(new Cluster(centroidI));
		}
				
	}

	Cluster nearestCluster(Tuple tuple) // ricerca del cluster con la distanza minore, per questo più vicino
	{
		double distanza,best;
		int k;
		Cluster A = new Cluster(tuple);
		int j = k = 0;
		distanza = tuple.getDistance(C[j].getCentroid());
		best = distanza;
		j++;
		while (j < i)
		{
			distanza = tuple.getDistance(C[j].getCentroid());
			if (best > distanza)
			{
				best = distanza;
				k = j;
			}
			
			j++;
		}
		
		A = C[k]; // A cluster appena creato, diventa quello più vicino, quindi con distanza minore a quello passato
		
		return A;
	}

	Cluster currentCluster(int id)
	{
		int k; // k indice per il vettore di Cluster in ClusterSet
		
		k = 0;
		while (k < i) // cicla per tutta la lunghezza del vettore ClusterSet
		{
			if (get(k).contain(id)) /* controlla che in posizione id del Cluster k presente in ClusterSet ci sia Vero
									 * perché è un vettore di booleani
									 */
			{
				return get(k);
			}
			
			k++;
		}
			
		return null;
	}
	
	void updateCentroids(Data data)
	{
		int k; // contatore per tutti i Cluster in ClusterSet
		
		k = 0;
		while (k < i)
		{
			C[k].computeCentroid(data);
			k++;
		}
	}
	
	public String toString()
	{
		String str = "Centroids = \n";
		int k; // contatore per tutti i centroidi nel vettore di ClusterSet
		
		k = 0;
		while (k < i)
		{
			str += k + " " + get(k).toString() + "\n";
			k++;
		}
		
		return str;
	}
	
	public String toString(Data data)
	{
		String str = " ";
		for (int i = 0; i < C.length; i++)
		{
			if (get(i) != null)
			{
				str += i + ":" + get(i).toString(data) + "\n";
			}
		}
		
		return str;
	}
}
