package prog;

public interface IProg {
	public String getLogin();

	public void setLogin(String login);
	
	public String getMdp();
	
	public void setMdp(String mdp);

	public int getFtpPort();

	public void setFtpPort(int ftpPort);
	
	public void incrementsNbServices();
	
	public void decrementsNbServices();
	
	public void setNbServices(int nb);
	
	public int getNbServices();
	
	public String getUrlProg();
	
	public void setUrlProg(int port);
}

