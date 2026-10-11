package prog;

import java.util.ArrayList;
import java.util.List;

public class ListeProgs {
	private static ArrayList<Prog> ListeProgs;
	
	static {
		ListeProgs = new ArrayList<Prog>();
	}
	
	public static synchronized void addProg(Prog progAJout) throws ProgDejaPresentException {
		if (searchProg(progAJout.getLogin())!=null)
			throw new ProgDejaPresentException();
		ListeProgs.add(progAJout);
	}
	
	public static Prog searchProg(String log) {
		for (Prog prog : ListeProgs)
			if (prog.getLogin().equals(log))
				return prog;
		return null;
	}
	
}
