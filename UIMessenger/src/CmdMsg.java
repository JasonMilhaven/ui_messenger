import javax.swing.JTextArea;

public class CmdMsg extends Command {

	private String msg = "the msg was lost in serialization";
	
	public String getMsg() { return msg; }
	
	public void readMsg(JTextArea out) {
		out.setText(out.getText() + msg);
	}
	
	public CmdMsg(String _msg) {
		msg = _msg;
	}
}
