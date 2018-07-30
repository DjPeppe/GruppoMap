
package server;

import java.net.*;
import java.io.*;
import mining.KmeansMiner;
import java.sql.*;

import database.*;
import data.Data;
import data.OutOfRangeSampleSize;

class ServerOneClient extends Thread {
	
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private KmeansMiner kmeans;
	
	public ServerOneClient(Socket s) throws IOException
	{
		socket = s;
		in = new ObjectInputStream(socket.getInputStream());
		out = new ObjectOutputStream(socket.getOutputStream()); 
		start();
	}
	
	public void run()
	{
		try
		{
			Data data = null;
			int k;
			KmeansMiner kmeans = null;
			int scelta;
			while (true)
			{
				scelta = (int)in.readObject();
				if (scelta == 0) 
				{ 
					String nomeTabella = (String)in.readObject();
					try 
					{
						data = new Data(nomeTabella);
						out.writeObject("OK");
					}
					catch (SQLException e)
					{
						System.err.println("Database not found");
					}
					catch (DatabaseConnectionException e)
					{
						System.err.println("Connection failed");
					}
				}
				else if (scelta == 1) 
				{
					k = (int)in.readObject();
					kmeans = new KmeansMiner(k);
					try 
					{
						int numIter = kmeans.kmeans(data);
						out.writeObject("OK");
						out.writeObject(numIter);
						out.writeObject(kmeans.getC().toString());
					}
					catch (OutOfRangeSampleSize e)
					{
						e.printStackTrace();;
					}
				}
				else if (scelta == 2) 
				{
					try
					{						
						kmeans.salva("Salvataggio.dmp");
						out.writeObject("OK");
					}
					catch (FileNotFoundException e)
					{
						e.printStackTrace();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				else if (scelta == 3) 
				{
					try 
					{
						String nomeTab = (String)in.readObject();
						FileInputStream inFile = new FileInputStream(nomeTab + ".dmp");
						ObjectInputStream inStream = new ObjectInputStream(inFile);
						data = (Data)inStream.readObject();
						inStream.close();
						k = (int)in.readObject();
						kmeans = new KmeansMiner(k);
						try 
						{
							kmeans.kmeans(data);
							out.writeObject("OK");
							out.writeObject(kmeans.toString());
						}
						catch (OutOfRangeSampleSize e)
						{
							e.printStackTrace();
						}
					}
					catch (FileNotFoundException e)
					{
						e.printStackTrace();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					catch (ClassNotFoundException e)
					{
						System.out.println(e.getMessage());
					}
				}
			}	
		}
		catch (ClassNotFoundException e)
		{
			System.err.println("Class not found");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.err.println("IO Exception");
			e.printStackTrace();
		}
		finally 
		{
			try 
			{
				socket.close();
			}
			catch (IOException e)
			{
				System.err.println("Socket not closed");
			}
		}
	}

}
