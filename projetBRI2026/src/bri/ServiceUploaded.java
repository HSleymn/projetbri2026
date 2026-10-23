package bri;
import java.util.Vector;

import prog.Prog;

public class ServiceUploaded {
	private Class<? extends Service>  service;
	private Prog prog;
	private boolean status;
	
	
	public ServiceUploaded(Class<? extends Service> service, Prog prog) {
		this.service = service;
		this.prog = prog;
		this.status = true;
	}
	
	public Class<? extends Service>  getService() {
		return service;
	}
	public void setService(Class<? extends Service>  serviceInput) {
		this.service = serviceInput;
	}
	public Prog getProg() {
		return prog;
	}
	public void setProg(Prog progInput) {
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
