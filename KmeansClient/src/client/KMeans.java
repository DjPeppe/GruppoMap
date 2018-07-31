
package client;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.*;
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
		JFrame finestra = new JFrame("Pippo");
		finestra.setBounds(300, 300, 600, 500);
		Container cp = finestra.getContentPane();
		TabbedPane tab = new TabbedPane();
		tab.setLayout(new BoxLayout(tab, BoxLayout.PAGE_AXIS));
		cp.add(tab);
		finestra.setVisible(true);
		InetAddress addr = InetAddress.getByName(ip); // ip 
		System.out.println("addr = " + addr);
		Socket socket = new Socket(addr, port); // Port
		System.out.println(socket);
		
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream()); // stream con richieste del client
		
		finestra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // chiudere tutto al tasto X
	}
	
	class TabbedPane extends JPanel 
	{
		private JPanelCluster panelDB;
		private JPanelCluster panelFile;
		
		class JPanelCluster extends JPanel
		{
			private JTextField tableText = new JTextField(20);
			private JTextField kText = new JTextField(10);
			private JTextArea clusterOutput = new JTextArea();
			private JButton executeButton;
			
			public JPanelCluster(String buttonName, java.awt.event.ActionListener a)
			{
				JPanel upper = new JPanel();
				JPanel central = new JPanel();
				JPanel down = new JPanel();
				JLabel first = new JLabel("Table:");
				JLabel second = new JLabel("k:");
				
				upper.setLayout(new FlowLayout(1));
				central.setLayout(new FlowLayout());
				
				upper.add(first);
				upper.add(tableText);
				upper.add(second);
				upper.add(kText);
				central.add(clusterOutput);
				executeButton = new JButton(buttonName);
				down.add(executeButton);
				
				executeButton.addActionListener(a);
				
				add(upper);
				add(central);
				add(down);
			}	
		}	
		
		TabbedPane()
		{
			JPanel superupper = new JPanel();
			JButton db = new JButton("DB");
			JButton file = new JButton("FILE");
			ImageIcon icon1 = new ImageIcon("db.png");
			ImageIcon icon2 = new ImageIcon("file.png");
			
			db.setIcon(icon1);
			file.setIcon(icon2);
			superupper.add(db);
			superupper.add(file);
			add(superupper);
			panelDB = new JPanelCluster("MINE", new ButtonMine());
			add(panelDB);
			panelFile = new JPanelCluster("STORE FROM FILE", new ButtonStoreFromFile());
			add(panelFile);
			db.addActionListener(new ButtonDB());
			panelFile.setVisible(false);
			file.addActionListener(new ButtonFile());
		}
		
		private class ButtonDB implements ActionListener
		{
			public void actionPerformed(ActionEvent evento)
			{
				panelDB.setVisible(true);
				panelFile.setVisible(false);
			}
		}
		
		private class ButtonFile implements ActionListener
		{
			public void actionPerformed(ActionEvent evento)
			{
				panelDB.setVisible(false);
				panelFile.setVisible(true);
			}
		}
		
		private class ButtonMine implements ActionListener 
		{
			public void actionPerformed(ActionEvent evento)
			{
				try 
				{
					learningFromDBAction();
				} 
				catch (ClassNotFoundException e) 
				{
					e.printStackTrace();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		private class ButtonStoreFromFile implements ActionListener 
		{
			public void actionPerformed(ActionEvent evento)
			{
				try 
				{
					learningFromFileAction();
				} 
				catch (ClassNotFoundException e) 
				{
					e.printStackTrace();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		private void learningFromDBAction() throws SocketException, IOException, ClassNotFoundException
		{
			int k;
			try 
			{
				k = new Integer (panelDB.kText.getText()).intValue();
			}
			catch (NumberFormatException e) 
			{
				JOptionPane.showMessageDialog(this, e.toString());
				return;
			}
			String nomeTab = panelDB.tableText.getText();
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
							// Mancano le iterazioni di Maximo
							panelDB.clusterOutput.setText(cluster);
							out.writeObject(2);
							result = (String)in.readObject();
							if (result.equals("OK"))
							{
								
								JOptionPane.showMessageDialog(this, "Complimenti, Sei un cavaliere dello Zodiaco");	
							}
							/*try 
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
							*/
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
			String nomeTab = panelFile.tableText.getText();
			int k = new Integer (panelFile.kText.getText()).intValue();
			out.writeObject(3);
			out.writeObject(nomeTab);
			out.writeObject(k);
			String result = (String)in.readObject();
			if (result.equals("OK"))
			{
				String cluster = (String)in.readObject();
				panelFile.clusterOutput.setText(cluster);
				JOptionPane.showMessageDialog(this, "Complimenti, Sei un cavaliere dello Zodiaco");								
			}
			/*try 
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
			*/
		}
	}
}

