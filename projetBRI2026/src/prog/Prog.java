package prog;

public class Prog implements IProg {
	private String login;
	private String mdp;
	private int ftpPort;
	private int nbServices;
	private String urlProg;
	
	public Prog(String log, String pswd, int port, String url) {
		this.login = log;
		this.mdp = pswd;
		this.ftpPort = port;
		this.urlProg = url;
		this.nbServices=0;
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
		this.setUrlProg(ftpPort);
	}
	
	public void incrementsNbServices() {
		++this.nbServices;
	}
	
	public void decrementsNbServices() {
		--this.nbServices;
	}
	
	public void setNbServices(int nb) {
		this.nbServices=nb;
	}
	
	public int getNbServices() {
		return this.nbServices;
	}

	public String getUrlProg() {
		return urlProg;
	}

	public void setUrlProg(int port) {
		this.urlProg = "ftp://localhost:" +Integer.toString(port)
		+  "/" + this.getLogin() + "/";
				
	}
}
