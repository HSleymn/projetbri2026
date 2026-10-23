package bri;


import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;


class ClientAmaHandler implements Runnable {
	
	private Socket client;
	
	ClientAmaHandler(Socket socket) {
		client = socket;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader (new InputStreamReader(client.getInputStream ( )));
			PrintWriter out = new PrintWriter (client.getOutputStream ( ), true);
			out.println(ServiceRegistry.toStringue()+"##Tapez le numéro de service désiré :");
			int choix = Integer.parseInt(in.readLine());
			// instancier le service numéro "choix" en lui passant la socket "client"
			Class<? extends Service> classeService = ServiceRegistry.getServiceClass(choix);
			Service serviceChoisi = null;
			try {
				serviceChoisi = classeService.getConstructor(Socket.class).newInstance(client);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			// invoquer run() pour cette instance ou la lancer dans un thread à part 
			serviceChoisi.service();
			}
		
		
		
		catch (IOException e) {
			//Fin du service
		}

		try {client.close();} catch (IOException e2) {}
	}
	
	
	
	
	protected void finalize() throws Throwable {
		 client.close(); 
	}

	// lancement du service
	public void start() {
		(new Thread(this)).start();		
	}

}
