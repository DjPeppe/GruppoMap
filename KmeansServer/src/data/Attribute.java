
package data;

import java.io.*;

abstract class Attribute implements Serializable {
	
	private String name; // nome simbolico attributo
	private int index; // identificativo attributo
	
	Attribute(String name, int index)
	{
		this.name = name;
		this.index = index;
	}
	
	String getName()
	{
		return name;
	}
	
	int getIndex()
	{
		return index;
	}
	
	public String toString()
	{
		return name;
	}

}
