import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Program {
	
	// constants
	private final static String CONFIG_FILE_LOCATION = "UIMessenger_config.txt";
	private final static String ERROR_LOG_FILE_LOCATION = "UIMessenger_error_log.txt";
	private final static int MAX_SERVER_LINES = 23;
	private final static int MAX_CLIENT_LINES = 42;
	
	// variables
	private static String defaultName = "username";
	private static String defaultIp = "localhost";
	private static int defaultPort = 8888;
	public static boolean isRunning = true;
	private static Font font = new Font("MS Sans Serif", Font.PLAIN, 12);
	private static JTextField nameF;
	private static JTextField ipF;
	private static JTextField portF;
	
	// properties
	public static Font getFont() { return font; }
	public static String getName() { return nameF.getText(); }
	public static String getIp() { return ipF.getText(); }
	public static int getPort() {
		int doReturn = defaultPort;
		if (!portF.getText().trim().isEmpty()) {
			doReturn = Integer.parseInt(portF.getText());
		}
		return doReturn;
	}
	
	private static void loadSettings() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(CONFIG_FILE_LOCATION));
			defaultName = br.readLine();
			defaultIp = br.readLine();
			defaultPort = Integer.parseInt(br.readLine());
			br.close();
		} catch (Exception e) { writeErrorLog(e); }
	}
	
	private static void saveSettings() {
		try {
			PrintWriter pw = new PrintWriter(CONFIG_FILE_LOCATION, "UTF-8");
			pw.println(getName());
			pw.println(getIp());
			pw.println(getPort());
			pw.close();
		} catch (Exception e) { writeErrorLog(e); }
	}
	
	public static void writeErrorLog(Exception exc) {
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(ERROR_LOG_FILE_LOCATION, "UTF-8");
			exc.printStackTrace(new PrintWriter(sw));
			
			pw.write(sw.toString());
			
			pw.close();
			sw.close();
			
			exc.printStackTrace();
		} catch (Exception e) { writeErrorLog(e); }
	}
	
	public static void popChat(JTextArea chat, boolean isServer) {
		//String[] lines = chat.getText().split("\n");
		ArrayList<String> lines = new ArrayList<String>();
		lines.addAll(Arrays.asList(chat.getText().split("\n")));
		int tempMax = MAX_CLIENT_LINES;
		if (isServer) {
			tempMax = MAX_SERVER_LINES;
		}
		if (lines.size() >= tempMax) {
			lines.remove(0);
			StringBuilder newLines = new StringBuilder();
			for (String line : lines) {
				newLines.append(line + "\n");
			}
			chat.setText(newLines.toString());
		}
	}
	
	public static void main(String[] args) {
		
		loadSettings();
		
		JFrame frame = new JFrame("UIMessenger");
		frame.getContentPane().setLayout(null);
		frame.setSize(300, 250);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel nameL = new UIMessengerLabel("name");
		nameL.setSize(200, 25);
		nameL.setLocation(50, 25);
		
		nameF = new JTextField(defaultName);
		nameF.setSize(200, 25);
		nameF.setLocation(50, 50);
		
		JLabel ipL = new UIMessengerLabel("address:port");
		ipL.setSize(200, 25);
		ipL.setLocation(50, 75);
		
		ipF = new JTextField(defaultIp);
		ipF.setSize(150, 25);
		ipF.setLocation(50, 100);
		
		portF = new JTextField(Integer.toString(defaultPort));
		portF.setSize(50, 25);
		portF.setLocation(200, 100);
		
		JButton bConnect = new JButton("connect");
		bConnect.setSize(100, 50);
		bConnect.setLocation(50, 150);
		
		JButton bServer = new JButton("server");
		bServer.setSize(100, 50);
		bServer.setLocation(150, 150);
		
		frame.add(nameL);
		frame.add(nameF);
		frame.add(ipL);
		frame.add(ipF);
		frame.add(portF);
		frame.add(bConnect);
		frame.add(bServer);
		
		frame.setVisible(true);
		frame.repaint();
		
		bConnect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSettings();
				new Client(getName(), getIp(), getPort());
				frame.dispose();
			}
		});
		
		bServer.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSettings();
				new Server(getPort());
				frame.dispose();
			}
		});
	}
}
