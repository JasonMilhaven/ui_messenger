import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends Thread {

	private String username;
	private String ip;
	private int port;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private JTextArea disp;
	private JPanel p;
	private JFrame f;
	
	private JTextField inpF;
	
	private void clientUI() {
		f = new JFrame(username + " " + ip + ":" + Integer.toString(port));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setResizable(false);
		f.setSize(1280, 720);
		
		disp = new JTextArea();
		disp.setEditable(false);
		disp.setFont(Program.getFont());
		
		p = new JPanel();
		p.setFont(Program.getFont());
		p.setSize(1280, 60);
		p.setLocation(0, (int)f.getSize().getHeight() - (int)p.getSize().getHeight());
		
		inpF = new JTextField();
		// inpF.setSize(); // can't use setSize for some reason, it simply does not work
		inpF.setPreferredSize(new Dimension(1260, 25)); // this caused a massive headache, but it works		
		inpF.setLocation(0, 700);
		
		f.add(p);
		p.add(inpF);
		
		f.add(disp);
		
		f.setVisible(true);
		f.repaint();
	}
	
	private class InputHandler extends Thread {
		
		@Override
		public void run() {
			inpF.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent event) {
					String line = inpF.getText();
					String preText = username + ": ";
					if (!line.trim().equals("")) {
						try {
							inpF.setText("");
							out.writeObject(new CmdMsg(preText + line + "\n"));
							p.repaint();
							p.revalidate();
						} catch (Exception e) { Program.writeErrorLog(e); }
					}
				}
			});
		}
		
		private InputHandler() {
			start();
		}
	}
	
	@Override
	public void run() {
		clientUI();
		new InputHandler();
		try {
			Socket socket = new Socket(ip, port);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			out.writeObject(new CmdInitVars(username, System.getProperty("user.name")));
			//while (Program.isRunning) {
			while (socket.isConnected()) {
				Command cmd = (Command)in.readObject();
				if (cmd != null) {
					if (cmd instanceof CmdMsg) {
						CmdMsg cmdMsg = (CmdMsg)cmd;
						cmdMsg.readMsg(disp);
						Program.popChat(disp, false);
					}
				}
			}
			socket.close();
			f.dispose();
		} catch (Exception e) { Program.writeErrorLog(e); }
	}
	
	public Client(String _username, String _ip, int _port) {
		username = _username;
		ip = _ip;
		port = _port;
		start();
	}
}
