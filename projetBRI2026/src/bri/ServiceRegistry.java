package bri;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

import prog.IProg;
import prog.Prog;

public class ServiceRegistry {
	// cette classe est un registre de services
	// partagee en concurrence par les clients et les "ajouteurs" de services,
	// un Vector pour cette gestion est pratique

	static {
		registreServices= new Vector<ServiceUploaded>();
	}
	private static Vector<ServiceUploaded> registreServices;
	
	public static void normeBRIValide(Class<? extends Service> serviceAjout) throws ServiceInvalideException{
		// vérifier la conformité par introspection
				String errorMessage = "";
				
				//implémenter l'interface BRi.Service
				boolean implementsBriService = false;
				for (Class<?> interfaze : serviceAjout.getInterfaces())
					if (interfaze.getSimpleName() == "Service")
						implementsBriService = true;
				if (implementsBriService)
					errorMessage += "Le service doit implémenter BRi.Service #";
				
				//ne pas être abstract
				if (Modifier.isAbstract(serviceAjout.getModifiers()))
					errorMessage += "La classe de service ne doit pas être abstract #";
				
				//être publique
				if (!Modifier.isPublic(serviceAjout.getModifiers()))
					errorMessage += "La classe de service doit être publique #";
				
				//avoir un constructeur public (Socket) sans exception
				boolean valideConstructux = false;

				for (Constructor constructux : serviceAjout.getDeclaredConstructors())
					
					
						if (Modifier.isPublic(constructux.getModifiers()) && 
								constructux.getParameterTypes().length != 0 &&
								constructux.getExceptionTypes().length == 0)
							for (Class<?> parametux : constructux.getParameterTypes())
								if (parametux.getSimpleName().equals("Socket")) {
									valideConstructux = true;
									break;
								}
							
				if (!valideConstructux)
					errorMessage += "La classe de service doit contenir un constructeur public (Socket) sans exception #";
				

				
				//avoir un attribut Socket private final
				boolean valideAttribux = false;
				for (Field attribux : serviceAjout.getDeclaredFields())
					if (Modifier.isPrivate(attribux.getModifiers()) && 
							Modifier.isFinal(attribux.getModifiers()) && 
							attribux.getType() == Socket.class )
						valideAttribux = true;
					
				if (!valideAttribux)
					errorMessage += "La classe de service doit contenir un attribut Socket private final #";
				
				//avoir une méthode public static String toStringue() sans exception
				boolean valideMethux = false;
				for (Method methux : serviceAjout.getMethods())
					if (methux.getName() == "toStringue" &&
							Modifier.isPublic(methux.getModifiers()) && //methode publique
							Modifier.isStatic(methux.getModifiers()) &&  //static
							methux.getReturnType() == String.class &&	//return un string
							methux.getExceptionTypes().length == 0)		//n'a pas d'excepetion
						valideMethux = true;
				
				if (!valideMethux)
					errorMessage += "La classe de service doit contenir "
							+ "une méthode public static String toStringue() sans exception #";
				
				
				// si non conforme --> exception avec message clair
				
				if (errorMessage.length() !=0)
					throw new ServiceInvalideException(errorMessage);
				// si conforme, ajout au vector
	}
	
	
// ajoute une classe de service apr�s contr�le de la norme BLTi
	public static void addService(Class<? extends Service> serviceAjout, IProg progUploader) throws ServiceInvalideException {
		try {
			normeBRIValide(serviceAjout);
			registreServices.add(new ServiceUploaded(serviceAjout, progUploader));
		}catch (ServiceInvalideException e) {
			throw e;
		}
		
	}

// update la classe avec la norme BRI
	public static void updateService(Class<? extends Service> serviceUpdate, IProg prog, int serviceIdChangement)throws ServiceInvalideException {	
		try {	
			normeBRIValide(serviceUpdate);
			registreServices.get(serviceIdChangement).setService(serviceUpdate);	
		}catch (ServiceInvalideException e) {
			throw e;
		}
	}
// renvoie la classe de service (numService -1)	
	public static Class<? extends Service> getServiceClass(int numService) throws IndexOutOfBoundsException {
		int indexService = numService-1;
		if(indexService<0)
			throw new IndexOutOfBoundsException();
		for (int i=0, j=0; i<registreServices.size(); i++) { 
			if (registreServices.get(i).getStatus() == true) {
				if(j==indexService)
					return registreServices.get(indexService).getService();
				++j;
			}
		}
		throw new RuntimeException();
	}
	public static Class<? extends Service> getServiceClassUpdater(int numService) throws IndexOutOfBoundsException {
		if(numService<0)
			throw new IndexOutOfBoundsException();
		
		return registreServices.get(numService).getService();
				
		
	}
	public static int getServiceClassId(int numService, IProg prog) {
		int indexService = numService-1;
		
		if (numService == 0 || indexService > prog.getNbServices())
			throw new IndexOutOfBoundsException();
		
		for (int i=0, j=0; j<prog.getNbServices(); ++i) {
			System.out.println(registreServices.get(i).getProg().getLogin());
			System.out.println(i + " " + j);
			if (registreServices.get(i).getProg().equals(prog)) {
				if(j == (indexService)) {
					System.out.println(registreServices.get(i).getService().getSimpleName() +" " + registreServices.get(i).getStatus());
					
					return i;
					}
				++j;
			}
		}
			
		return -1;
	}
	
	
	
	public static int getTailleRegistre() {
		return registreServices.size();
	}
	
	public static void changeStatus(int choix, IProg prog) {
		if (choix == 0 || choix > prog.getNbServices())
			throw new IndexOutOfBoundsException();
		System.out.println(prog.getNbServices());
		for (int i=0, j=0; j<prog.getNbServices(); ++i) {
			System.out.println(registreServices.get(i).getProg().getLogin());
			System.out.println(i + " " + j);
			if (registreServices.get(i).getProg().equals(prog)) {
				if(j == (choix-1)) {
					registreServices.get(i).setStatus(!registreServices.get(i).getStatus());
					System.out.println(registreServices.get(i).getService().getSimpleName() +" " + registreServices.get(i).getStatus());

					return;
					}
				++j;
			}
		}
	}
	
	

	
// liste les activit�s pr�sentes
	public static String toStringue() {
		String result = "Activités présentes :\n";
		for (int i=0, j=0; i<registreServices.size(); i++)  
			if (registreServices.get(i).getStatus() == true) 
				//result += Integer.toString(i+1) + " - " +  registreServices.get(i).getService().getSimpleName() + " -- " + registreServices.get(i).getProg().getLogin();
				result += Integer.toString(++j) + " - " +  registreServices.get(i).getService().getSimpleName() + "#";

		return result;
	}
	
	public static String toStringue(IProg prog) {
		String result = "";
		
		for (int i=0, j=0; j<prog.getNbServices(); i++) 
			if (registreServices.get(i).getProg().equals(prog))
				result += Integer.toString(++j) + " - " +  
			registreServices.get(i).getService().getSimpleName() + "#";
		
		return result;
	}
	public static String toStringueStatus(IProg prog) {
		String result = "";
		
		for (int i=0, j=0; j<prog.getNbServices(); i++) 
			if (registreServices.get(i).getProg().equals(prog))
				result += Integer.toString(++j) + " - " +  
			registreServices.get(i).getService().getSimpleName() + " -- " + registreServices.get(i).getStatusString() + "#";
		
		return result;
	}
	public static void desinstallerService(int choix, IProg prog) {
		if (choix == 0 || choix > prog.getNbServices())
			throw new IndexOutOfBoundsException();
		for (int i=0, j=0; j<prog.getNbServices(); i++) {
			if (registreServices.get(i).getProg().equals(prog) ) {
				if (j == (choix-1)) {
					registreServices.remove(i);
					registreServices.get(i).getProg().decrementsNbServices();
					return;
				}
				++j;
			}}
	}

	public static String getUrlService(int numService) throws RuntimeException{
		int indexService = numService-1;
		if(indexService<0)
			throw new IndexOutOfBoundsException();
		for (int i=0, j=0; i<registreServices.size(); i++) { 
			if (registreServices.get(i).getStatus() == true) {
				if(j==indexService)
					return registreServices.get(indexService).getProg().getUrlProg();
				++j;
			}
		}
		throw new RuntimeException();
		
	}
}
