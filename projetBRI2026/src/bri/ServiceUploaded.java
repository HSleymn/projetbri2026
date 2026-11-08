package bri;

import prog.IProg;

public class ServiceUploaded {
	private Class<? extends Service>  service;
	private IProg prog;
	private boolean status;
	
	
	public ServiceUploaded(Class<? extends Service> service, IProg prog) {
		this.service = service;
		this.prog = prog;
		this.status = true;
		prog.incrementsNbServices();
	}
	
	public Class<? extends Service>  getService() {
		return service;
	}
	public void setService(Class<? extends Service>  serviceInput) {
		this.service = serviceInput;
	}
	public IProg getProg() {
		return prog;
	}
	public void setProg(IProg progInput) {
		this.prog = progInput;
	}
	public boolean getStatus() {
		return status;
	}
	public void setStatus(boolean newStatus) {
		this.status = newStatus;
	}
	public String getStatusString() {
		if (this.status)
			return "Fonctionnel";
		else if(!this.status)
			return "À l'arrêt";
		return null;
	}
}
