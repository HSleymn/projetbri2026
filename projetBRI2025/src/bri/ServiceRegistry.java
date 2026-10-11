package bri;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class ServiceRegistry {
	// cette classe est un registre de services
	// partagee en concurrence par les clients et les "ajouteurs" de services,
	// un Vector pour cette gestion est pratique

	static {
		registreServices= new Vector<Class<? extends Service>>();
	}
	private static Vector<Class<? extends Service>> registreServices;

// ajoute une classe de service apr�s contr�le de la norme BLTi
	public static void addService(Class<? extends Service> serviceAjout) {
		// vérifier la conformité par introspection
		String errorMessage = "";
		
		//implémenter l'interface BRi.Service
		boolean implementsBriService = false;
		for (Class<?> interfaze : serviceAjout.getInterfaces())
			if (interfaze.getSimpleName() == "Service")
				implementsBriService = true;
		if (implementsBriService)
			errorMessage += "Le service doit implémenter BRi.Service\n";
		
		//ne pas être abstract
		if (Modifier.isAbstract(serviceAjout.getModifiers()))
			errorMessage += "La classe de service ne doit pas être abstract \n";
		
		//être publique
		if (!Modifier.isPublic(serviceAjout.getModifiers()))
			errorMessage += "La classe de service doit être publique \n";
		
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
			errorMessage += "La classe de service doit contenir un constructeur public (Socket) sans exception \n";
		

		
		//avoir un attribut Socket private final
		boolean valideAttribux = false;
		for (Field attribux : serviceAjout.getDeclaredFields())
			if (Modifier.isPrivate(attribux.getModifiers()) && 
					Modifier.isFinal(attribux.getModifiers()) && 
					attribux.getType() == Socket.class )
				valideAttribux = true;
			
		if (!valideAttribux)
			errorMessage += "La classe de service doit contenir un attribut Socket private final \n";
		
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
					+ "une méthode public static String toStringue() sans exception \n";
		
		
		// si non conforme --> exception avec message clair
		
		if (errorMessage.length() !=0)
			throw new RuntimeException(errorMessage);
		// si conforme, ajout au vector
		registreServices.add(serviceAjout);
	}
	
// renvoie la classe de service (numService -1)	
	public static Class<? extends Service> getServiceClass(int numService) {
		int indexService = numService-1;
		
		if(indexService<0)
			throw new IndexOutOfBoundsException();
		
		return registreServices.get(indexService);
		
	}
	
// liste les activit�s pr�sentes
	public static String toStringue() {
		String result = "Activités présentesos :##";
		for (int i=0; i<registreServices.size(); i++)
			result += Integer.toString(i+1) + " - " +  registreServices.get(i).getSimpleName();
		return result;
	}

}
