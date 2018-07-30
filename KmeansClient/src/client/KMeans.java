
package client;

import javax.swing.*;
import java.awt.Container;
import java.awt.FlowLayout;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class KMeans {
	
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	public void init(String ip, int port) throws IOException
	{
		JFrame f = new JFrame("Pippo");
		Container c = f.getContentPane();
		TabbedPane tab = new TabbedPane();
		f.add(tab);
		InetAddress addr = InetAddress.getByName(ip); // ip 
		System.out.println("addr = " + addr);
		Socket socket = new Socket(addr, port); // Port
		System.out.println(socket);
		
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream()); // stream con richieste del client
	}
	
	class TabbedPane extends JPanel {
		
		private JPanelCluster panelDB;
		private JPanelCluster panelFile;
		
		TabbedPane()
		{
			
		}
		
		private void learningFromDBAction() throws SocketException, IOException, ClassNotFoundException
		{
			int k;
			try 
			{
				k = new Integer(panelDB.kText.getText()).intValue();
			}
			catch (NumberFormatException e) 
			{
				JOptionPane.showMessageDialog(this, e.toString());
				return;
			}
			String nomeTab = panelDB.tableText();
			out.writeObject(0);
			out.writeObject(nomeTab);
			String result = (String)in.readObject();
			try 
			{
				if (result.equals("OK"))
				{
					out.writeObject(1);
					out.writeObject(k);
					result = (String)in.readObject();
					try 
					{
						if (result.equals("OK"))
						{
							int numIter = (int)in.readObject();
							String cluster = (String)in.readObject();
							panelDB.clusterOutput(numIter, cluster);
							out.writeObject(2);
							result = (String)in.readObject();
							try 
							{
								if (result.equals("OK"))
								{
									JOptionPane.showMessageDialog(this, "Complimenti, Sei un cavaliere dello Zodiaco");								
								}
							}
							catch (IOException e)
							{
								JOptionPane.showMessageDialog(this, e.toString());
								return;
							}
						}
					}
					catch (IOException e)
					{
						JOptionPane.showMessageDialog(this, e.toString());
						return;
					}
				}
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(this, e.toString());
				return;
			}
		}
		
		private void learningFromFileAction() throws SocketException, IOException, ClassNotFoundException
		{
			int k = panelFile.kText();
			String nomeTab = panelFile.tableText();
			out.writeObject(3);
			out.writeObject(nomeTab);
			out.writeObject(k);
			String result = (String)in.readObject();
			try 
			{
				if (result.equals("OK"))
				{
					JOptionPane.showMessageDialog(this, "Complimenti, Sei un cavaliere dello Zodiaco");
				}
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(this, e.toString());
				return;
			}	
		}
		
		class JPanelCluster extends JPanel {
			
			private JTextField tableText = new JTextField(20);
			private JTextField kText = new JTextField(10);
			private JTextArea clusterOutput = new JTextArea();
			private JButton executeButton;
			
			public JPanelCluster(String buttonName, java.awt.event.ActionListener a)
			{
				c.setLayout(new FlowLayout());
			}
			
		}
	}
}
