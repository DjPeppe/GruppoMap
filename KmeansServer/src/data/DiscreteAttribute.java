
package data;

import java.util.*;

class DiscreteAttribute extends Attribute implements Iterable<String> {
	
	private TreeSet<String> values;
	
	DiscreteAttribute(String name, int index, TreeSet<String> valori)
	{
		super (name, index); // invoca il costruttore della classe madre
		values = valori;
	}
	
	public Iterator<String> iterator()
	{
		Iterator<String> itr = values.iterator(); // mi restituisce l'iteratore all'oggetto voluto, nel caso alla stringa
		
		return itr;
	}
	
	int getNumberOfDistinctValues()
	{
		return values.size();
	}

	int frequency(Data data, HashSet<Integer> idList, String v) 
	{
		int frequency, index;
		
		index = findColumn(data, v); /* trova la colonna in cui si troverà nella matrice data la stringa v
		                              * servendosi di DiscreteAttribute
		                              */
		frequency = 0;
		Iterator<Integer> itr = idList.iterator();
		while (itr.hasNext()) 
		{	
			if (v.equals(data.getAttributeValue(itr.next(), index))) 
			{
				frequency += 1;
			}
		}
		
		return frequency;
	}

	private int findColumn(Data data, String v) /* trovare la colonna di Data in cui poter trovare la stringa v
												  * trovando prima v a che discreteAttribute appartiene
												  */
	{
		int i, index;
		
		i = 0;
		Iterator<Attribute> colonna = data.getAttributeSchema().iterator();
		while (colonna.hasNext())
		{
			Attribute temp = colonna.next();
			if (temp instanceof DiscreteAttribute) 
			{
				DiscreteAttribute temp1 = (DiscreteAttribute)temp;
				Iterator<String> itr = temp1.iterator();
				while (itr.hasNext())
				{
					if (v.equals(itr.next()))
					{
						index = i;
						return index;
					}
								
				}
				
			}
			
			i++;
		}
		
		return -1;
	}
	
}
