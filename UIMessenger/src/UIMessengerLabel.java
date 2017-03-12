import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class UIMessengerLabel extends JLabel {
	
	public UIMessengerLabel(String s) {
		setText(s);
		setFont(Program.getFont());
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalAlignment(SwingConstants.CENTER);
	}
}
