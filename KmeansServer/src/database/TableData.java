
package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import database.TableSchema.Column;
import database.Example;

public class TableData {

	DbAccess db;
	
	public TableData(DbAccess db) 
	{
		this.db = db;
	}
	
	public List<Example> getDistinctTransazioni(String table) throws SQLException, EmptySetException
	{
		TableSchema colonne = new TableSchema(db, table);
		List<Example> tabella = new ArrayList<Example>(); // più veloce nell'accedere ma lenta nell'inserire al centro
		Statement s = db.getConnection().createStatement();
		ResultSet r = s.executeQuery("Select distinct * from " + table);
		Example temp = new Example();
		int i;
		
		while (r.next())
		{
			i = 1;
			while (i < colonne.getNumberOfAttributes()) 
			{
				if (colonne.getColumn(i).isNumber())
				{
					temp.add(r.getDouble(i)); // Cambiato il tipo della colonna temperature su MySql in Double
				}
				else 
				{
					temp.add(r.getString(i));
				}
				
				i++;
			} 
			
			tabella.add(temp);
		}
		
		if (r.first() == false)
		{
			throw new EmptySetException("Set vuoto");
		}
		
		r.close();
		db.closeConnection();
		
		return tabella;
	}

	public Set<Object> getDistinctColumnValues(String table,Column column) throws SQLException
	{
		Set<Object> valCol = new TreeSet<Object>();
		Statement s = db.getConnection().createStatement();
		ResultSet r = s.executeQuery("Select distinct " + column.getColumnName() + "from " + table + "order by " + column.getColumnName() + "asc");
		
		if (column.isNumber())
		{
			while(r.next()) 
			{
				valCol.add(r.getDouble(column.getColumnName())); // Cambiato il tipo della colonna temperature su MySql in Double
			}	
		}
		else 
		{
			while(r.next()) 
			{
				valCol.add(r.getString(column.getColumnName()));
			}
		}
		
		r.close();
		db.closeConnection();
			
		return valCol;	
	}

	public Object getAggregateColumnValue(String table, Column column, QUERY_TYPE aggregate) throws SQLException, NoValueException
	{
		Statement s = db.getConnection().createStatement();
		ResultSet r;
		
		if (aggregate.equals(QUERY_TYPE.MAX)) 
		{	
			r = s.executeQuery("Select max(" + column.getColumnName() + ") from " + table );	
		}
		else 
		{
			r = s.executeQuery("Select min(" + column.getColumnName() + ") from " + table );	
		}
		
		if (r.first() == false)
		{
			throw new NoValueException("Set vuoto");
		}
		
		r.close();
		db.closeConnection();
		
		return r.next();
	}
}
