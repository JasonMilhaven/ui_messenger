import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class Server extends Thread {

	private static ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
	// CopyOnWriteList does not seem to make any difference
	
	private int port;
	private JTextArea chat;
	
	private static void sendCmd(Command cmd) {
		try {
			for (ClientHandler client : clients) {
				client.getOut().writeObject(cmd);
			}
		} catch (Exception e) { Program.writeErrorLog(e); }
	}
	
	private class ClientHandler extends Thread {
		
		private Socket clientSocket;
		private ObjectInputStream in;
		private ObjectOutputStream out;		
		private String username;
		private String realName;
		
		public ObjectOutputStream getOut() { return out; }
		
		@Override
		public void run() {
			try {
				out = new ObjectOutputStream(clientSocket.getOutputStream());
				in = new ObjectInputStream(clientSocket.getInputStream());
				
				while (!clientSocket.isClosed()) {
				//while (Program.isRunning) {
					Command cmd = (Command)in.readObject();
					if (cmd != null) {
						sendCmd(cmd);
						if (cmd instanceof CmdInitVars) {
							CmdInitVars cmdInitVars = (CmdInitVars)cmd;
							username = cmdInitVars.getUsername();
							realName = cmdInitVars.getRealName();
							CmdMsg cmdMsg = new CmdMsg(realName + " has joined as " + "\"" + username + "\"" + "\n");
							sendCmd(cmdMsg);
							cmdMsg.readMsg(chat);
							Program.popChat(chat, true);
						} else if (cmd instanceof CmdMsg) {
							CmdMsg cmdMsg = (CmdMsg)cmd;
							cmdMsg.readMsg(chat);
							Program.popChat(chat, true);
						}
					}
				}
			} catch (SocketException sExcept) {
				CmdMsg cmdMsg = new CmdMsg(realName + " " + "\"" + username + "\"" + " has left" + "\n");
				sendCmd(cmdMsg);
				cmdMsg.readMsg(chat);
				Program.popChat(chat, true);
				if (clients.contains(this)) {
					clients.remove(this);
				}
				Program.writeErrorLog(sExcept);
				stop();
			} catch (Exception e) { Program.writeErrorLog(e); }
		}
		
		
		private ClientHandler(Socket _clientSocket) {
			clientSocket = _clientSocket;
			clients.add(this);
			start();
		}
	}
	
	private void serverUI() {
		JFrame f = new JFrame("server " + Integer.toString(port));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(800, 400);
		f.setResizable(false);
		
		chat = new JTextArea();
		chat.setEditable(false);
		chat.setFont(Program.getFont());
		
		f.add(chat);
		
		f.setVisible(true);
		f.repaint();
	}
	
	@Override
	public void run() {
		serverUI();
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (Program.isRunning) {
				Socket clientSocket = serverSocket.accept();
				new ClientHandler(clientSocket);
			}			
			serverSocket.close();
		} catch (Exception e) { Program.writeErrorLog(e); }
	}
	
	public Server(int _port) {
		port = _port;
		start();
	}
}
