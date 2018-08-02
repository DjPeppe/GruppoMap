
package data;

import java.util.*;
import database.*;
import java.sql.*;
import java.io.*;

public class Data implements Serializable {

	private List<Example> data; // una matrice formata da liste di example dove ogni riga modella una transazioni
	private int numberOfExamples; // cardinalità dell’insieme di transazioni (numero di righe in data)
	private List<Attribute> explanatorySet; // lista degli attributi in ciascuna tupla (schema della tabella di dati)
	
	public Data(String table) throws SQLException, DatabaseConnectionException
	{			
		TreeSet<Example> tempData = new TreeSet<Example>();
		
		explanatorySet = new LinkedList<Attribute>(); // diventa una lista linkata
		
		TreeSet<String> outLookValues = new TreeSet<String>();
		outLookValues.add("overcast");
		outLookValues.add("rain");
		outLookValues.add("sunny");
		
		TreeSet<String> humidityValues = new TreeSet<String>();
		humidityValues.add("high");
		humidityValues.add("normal");
		
		TreeSet<String> windValues = new TreeSet<String>();
		windValues.add("weak");
		windValues.add("strong");
		
		TreeSet<String> playTennisValues = new TreeSet<String>();
		playTennisValues.add("no");
		playTennisValues.add("yes");
		
		explanatorySet.add(new DiscreteAttribute("Outlook", 0, outLookValues));
		explanatorySet.add(new ContinuousAttribute("Temperature", 1, 3.2, 38.7));
		explanatorySet.add(new DiscreteAttribute("Humidity", 2, humidityValues));
		explanatorySet.add(new DiscreteAttribute("Wind", 3, windValues));
		explanatorySet.add(new DiscreteAttribute("Playtennis", 4, playTennisValues));
		
		DbAccess mapDb = new DbAccess();
		mapDb.initConnection();
		TableSchema attributi = new TableSchema(mapDb, table);
		Statement s = mapDb.getConnection().createStatement();
		ResultSet r = s.executeQuery("Select * from " + table);
		
		int i;
		while (r.next()) 
		{
			i = 1;
			Example temp = new Example();
			while (i <= attributi.getNumberOfAttributes()) 
			{
				temp.add(r.getObject(i));
				i++;
			}
			
			tempData.add(temp);
		}
		
		data = new ArrayList<Example>(tempData);
		
		numberOfExamples = data.size();		  		
	}
		
	public int getNumberOfExamples()
	{
		return numberOfExamples;
	}
		
	public int getNumberOfExplanatoryAttributes()
	{
	    return explanatorySet.size();
	}
	
	public List<Attribute> getAttributeSchema()
	{
		return explanatorySet;
	}
		
	public Object getAttributeValue(int exampleIndex, int attributeIndex)
	{
		return data.get(exampleIndex).get(attributeIndex);
	}
	
	public Attribute getAttribute(int index) // ritorna l'attribute identificato nella linked list come index
	{
		return explanatorySet.get(index);
	}
		
	public String toString()
	{
		int i;
		String tabella = new String (explanatorySet.get(0) + " " + explanatorySet.get(1) + " " + explanatorySet.get(2) + " " + explanatorySet.get(3) + " " + explanatorySet.get(4));
		
		i = 0;
		Iterator<Example> riga = data.iterator();
		Example temp;
		while (riga.hasNext())
		{
			temp = riga.next();
			tabella = tabella + "\n" + i + ". ";
			Iterator<Object> colonna = temp.iterator();
			while (colonna.hasNext())
			{
				tabella = tabella + " " + colonna.next();
			}	
			
			i++;
		}
		
		return tabella;
	}
	
	public Tuple getItemSet(int index) /* restituisce una tupla, vettore di Item, composto di 5 Item, quanto è 
								        * explanatorySet, 
								        */
	{
		Tuple tuple = new Tuple(explanatorySet.size());
		Iterator<Attribute> itr = explanatorySet.iterator();
		Iterator<Object> colonna = data.get(index).iterator();
		Attribute temp;
		int i = 0;
		
		while (itr.hasNext())
		{
			temp = itr.next();
			if (temp instanceof ContinuousAttribute) 
			{
				tuple.add(new ContinuousItem((ContinuousAttribute)temp, (double)colonna.next()), i);
				
			}
			else 
			{	
				tuple.add(new DiscreteItem((DiscreteAttribute)temp, (String)colonna.next()), i);	
			}
			
			i++;
		}
		
		return tuple;
	}

	public int[] sampling(int k) throws OutOfRangeSampleSize 
	{
		if (k <= 0 || k > getNumberOfExamples()) 
		{
			throw new OutOfRangeSampleSize("\nNumero di cluster non valido\n");
		}
		int centroidIndexes[] = new int[k]; 
		//choose k random different centroids in data.
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		for (int i = 0; i < k; i++)
		{
			boolean found = false;
			int c;
			do
			{
				found = false;
				c = rand.nextInt(getNumberOfExamples());
				// verify that centroid[c] is not equal to a centroide already stored in CentroidIndexes
				for (int j = 0; j < i; j++)
				{
					if (compare(centroidIndexes[j], c))
					{
						found = true;
						break;
					}
				}
				
			} while(found);
			
			centroidIndexes[i] = c;
		}
		
		return centroidIndexes;
	}

	private boolean compare(int i, int j)
	{	
		Iterator<Object> colonna1 = data.get(i).iterator();
		Iterator<Object> colonna2 = data.get(j).iterator();
		
		while(colonna1.hasNext())
		{
			if (!(colonna1.next().equals(colonna2.next())))
			{
				return false;
			}
		}
		
		return true;
	}

	String computePrototype(HashSet<Integer> idList, DiscreteAttribute attribute) /* calcola e restituisce la stringa
	 																			   * più presente di un DiscreteAttribute
	 																			   */
	{
		int max;
		int i; // contatore per scandire di quante posizioni l'iteratore dovrà andare avanti nel TreeSet di attribute
		String word;

		List<Integer> temp = new ArrayList<Integer>(attribute.getNumberOfDistinctValues());
		
		Iterator<String> itr = attribute.iterator();
		while (itr.hasNext())
		{
			word = itr.next(); 
			temp.add(attribute.frequency(this, idList, word));
		}
		max = max(temp); /* temp grande quanto parole di attribute, e contiene le occorrenze solo
		 				  * di ognuna nelle righe di idList
						  */
		itr = attribute.iterator(); // per far ricominciare l'iteratore dall'inizio del TreeSet, fino a j
		i = 0;
		while (i < max) // ciclo per far andare avanti l'iteratore del TreeSet di j volte, perché j è l'indice del vettore					
		{
			itr.next();
			i++;
		}
		
		return itr.next();
	}

	private int max(List<Integer> list) /* calcolo del massimo per un vettore, 
										 * contiene le occorrenze di tutte le stringhe
										 * di list, che è un solo Discrete Attribute
										 */
	{
		int i, j, max, best; // j serve per ritornare l'indice del best
		i = best = j = 0;
		
		Iterator<Integer> itr = list.iterator();
		while (itr.hasNext())
		{
			max = itr.next();
			if (best < max)
			{
				best = max;
				j = i;
			}
			
			i++;
		}
		
		return j;
	}

	Double computePrototype(HashSet<Integer> idList, ContinuousAttribute attribute)
    {
		double somma = 0;
		int colonna = attribute.getIndex();
		
		Iterator<Integer> riga = idList.iterator();
		while (riga.hasNext())
		{
			somma = somma + (double)getAttributeValue(riga.next(),colonna);
		}
		
		Double result = somma / idList.size();
		
		return  result;
	}
	
	Object computePrototype(HashSet<Integer> idList, Attribute attribute)
	{
		if (attribute instanceof ContinuousAttribute) 
		{
			return computePrototype(idList, (ContinuousAttribute)attribute);
		}
		else 
		{	
			return computePrototype(idList, (DiscreteAttribute)attribute);	
		}
	}
}
