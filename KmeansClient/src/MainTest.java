
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import client.KMeans;

public class MainTest {

	/**
	 * @param args 
	 */
	
	public static void main(String[] args)
	{
		String ip = args[0];
		int port = new Integer(args[1]).intValue();
		KMeans vito = new KMeans();
		
		try 
		{
			vito.init(ip, port);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}

