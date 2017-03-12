public class CmdInitVars extends Command {

	// variables
	private String username;
	private String realName;
	
	// properties
	public String getUsername() { return username; }
	public String getRealName() { return realName; }
	
	public CmdInitVars(String _username, String _realName) {
		username = _username;
		realName = _realName;
	}
}
