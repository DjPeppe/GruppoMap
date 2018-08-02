
package data;

class ContinuousAttribute extends Attribute {
	
	private double max;
	private double min;
	
	ContinuousAttribute(String name, int index, double minimo, double maximo)
	{
		super(name, index); // stiamo invocando il costruttore della classe padre
		min = minimo;
		max = maximo; 
	}
	
	double getScaledValue(double v)
	{
		v = (v - min) / (max - min);
		
		return v;
	}

}
