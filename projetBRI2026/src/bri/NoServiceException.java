package bri;

public class NoServiceException extends Exception{
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Le service entré n'est pas présent dans votre serveur FTP. Veuillez vérifier l'état de votre serveur FTP.";
	}

}
