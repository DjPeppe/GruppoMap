
package data;

class ContinuousItem extends Item {
	
	ContinuousItem(Attribute attribute, Double value)
	{
		super(attribute, value);
	}

	double distance(Object a)
	{
		double first = ((ContinuousAttribute)getAttribute()).getScaledValue((double)getValue());
		double second = ((ContinuousAttribute)getAttribute()).getScaledValue((double)a);

		return Math.abs(first-second); // valore assoluto
	}
}
