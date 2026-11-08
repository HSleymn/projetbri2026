package bri;

public class ServiceInvalideException extends Exception{
	private String error;
	public ServiceInvalideException(String messageError) {
		this.error=messageError;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return error;
	}

}