package prog;

public class ProgDejaPresentException extends Exception {
	
	@Override
	public String toString() {
		return "Le programmeur est déjà présent dans la liste";
	}

}
