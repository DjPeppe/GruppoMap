
package data;

import java.io.*;

class DiscreteItem extends Item {
	
	DiscreteItem(DiscreteAttribute attribute, String value)
	{
		super(attribute, value);
	}

	double distance(Object a) // per stabilire la distanza di un valore dall'altro
	{
		if (getValue().equals(a))
		{
			return 0;
		}
		else
		{
			return 1;
		}
	}	

}
