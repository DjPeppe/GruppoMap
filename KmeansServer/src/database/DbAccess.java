
package database;

import java.sql.*;

public class DbAccess {
	
	String DRIVER_CLASS_NAME = "org.gjt.mm.mysql.Driver";
	final String DBMS = "jdbc:mysql";
	final String SERVER = "localhost";
	final String DATABASE = "MapDB";
	final int PORT = 3306;
	final String USER_ID = "MapUser";
	final String PASSWORD = "map";
	Connection conn;
	
	public void initConnection() throws SQLException, DatabaseConnectionException
	{
		conn = DriverManager.getConnection(DBMS+"://" + SERVER + ":" + PORT + "/" + DATABASE, USER_ID, PASSWORD);
		if (conn == null)
		{
			throw new DatabaseConnectionException("Fallimento nella connessione al Database");
		}
	}
	
	public Connection getConnection()
	{
		return conn; // DOYLE
	}
	
	void closeConnection() throws SQLException
	{
		conn.close();
	}

}
