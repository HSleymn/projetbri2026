package prog;

import java.io.PipedWriter;

public class Prog implements IProg {
	private String login;
	private String mdp;
	private int ftpPort;
	
	public Prog(String log, String pswd, int port) {
		this.login = log;
		this.mdp = pswd;
		this.ftpPort = port;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getMdp() {
		return mdp;
	}

	public void setMdp(String mdp) {
		this.mdp = mdp;
	}

	public int getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(int ftpPort) {
		this.ftpPort = ftpPort;
	}
	
	
}
