
package mining;

import java.util.*;
import java.io.*;
import data.*;

public class Cluster implements Serializable {
	
	private Tuple centroid; // vettore di item, contenenti un Attribute e un oggetto
	private HashSet<Integer> clusteredData; // vettore inizialmente di booleani

	Cluster() // costruttore con valori casuali
	{
		centroid = new Tuple(5); 
		clusteredData = new HashSet<Integer>();
	}

	Cluster(Tuple centroid)
	{
		this.centroid = centroid;
		clusteredData = new HashSet<Integer>();
	}

	Tuple getCentroid()
	{
		return centroid;
	}

	void computeCentroid(Data data)
	{
		for (int i = 0; i < centroid.getLength(); i++)
		{
			centroid.get(i).update(data, clusteredData);
		}
	}
	
	//return true if the tuple is changing cluster
	boolean addData(int id)
	{
		return clusteredData.add(id);
	}

	//verifica se una transazione Ë clusterizzata nell'array corrente
	boolean contain(int id)
	{
		Iterator<Integer> itr = clusteredData.iterator();
		
		while (itr.hasNext())
		{
			if (itr.next() == id)
			{
				return true;
			}
		}
		
		return false;
	}

	//remove the tuple that has changed the cluster
	void removeTuple(int id)
	{ 
		Iterator<Integer> itr = clusteredData.iterator();
		
		while (itr.hasNext())
		{
			if (itr.next() == id)
			{
				clusteredData.remove((Integer)id);
				
				return;
			}
		}
		
	}

	public String toString()
	{
		String str = "Centroid = ( ";
		for (int i = 0; i < centroid.getLength(); i++)
		{
			str += centroid.get(i) + " ";
		}
		str += ")";
		
		return str;
	}

	public String toString(Data data)
	{
		String str = "Centroid = (";
		
		for (int i = 0; i < centroid.getLength(); i++)
			str += centroid.get(i) + " ";
		str += ")\nExamples:\n";
		
		Iterator<Integer> itr = clusteredData.iterator();
		for (int i = 0; i < clusteredData.size(); i++)
		{
			int riga = itr.next();
			str += "[ ";
			for (int j = 0; j < data.getNumberOfExplanatoryAttributes(); j++)
			{
				str += data.getAttributeValue(riga, j) + " ";
			}
			str += "] dist = " + getCentroid().getDistance(data.getItemSet(riga)) + "\n";
		}
		str += "\n AvgDistance = "+ getCentroid().avgDistance(data, clusteredData) + "\n";
		
		return str;
	}
	
}
