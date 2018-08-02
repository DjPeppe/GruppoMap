
package server;

import java.net.*;
import java.io.*;

class MultiServer {
	
	private static int PORT = 8080;

	public static void main(String args[])
	{
		try
		{
			@SuppressWarnings("unused")
			MultiServer server1 = new MultiServer(PORT);
		}
		catch (IOException e)
		{
			System.err.println("IO Exception");
		}
	}
	
	private MultiServer(int port) throws IOException 
	{
		PORT = port;
		this.run();
	}
	
	void run() throws IOException
	{
		ServerSocket s = new ServerSocket(PORT);
		System.out.println("Server started");
		try
		{
			while (true) // Si blocca finchè non si verifica una connessione
			{
				Socket socket = s.accept();
				try
				{
					new ServerOneClient(socket);
				}
				catch (IOException e) // Se fallisce chiude il socket, altrimenti la chiuderà il thread
				{
					throw new IOException();
				}
			}		
		}
		finally
		{
			s.close();
		}
	}
	
}
