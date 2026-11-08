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
		BufferedReader in;
		PrintWriter out;
		try {
			 in = new BufferedReader (new InputStreamReader(client.getInputStream ( )));
			 out = new PrintWriter (client.getOutputStream ( ), true);
			out.println(ServiceRegistry.toStringue()+"#Tapez le numéro de service désiré :");
			int choix = Integer.parseInt(in.readLine());
			Class<? extends Service> classeService = null;
			String url= "";
			try {
				try {
					classeService = ServiceRegistry.getServiceClass(choix);
					url = ServiceRegistry.getUrlService(choix);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}

				Service serviceChoisi = null;
				serviceChoisi = classeService.getConstructor(Socket.class, String.class).newInstance(client,url);
				serviceChoisi.service();

			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				out.println(e);
				out.println("Veuillez redémarrer l'application");
			}
			
			
			}
		
		
		
		catch (IOException e) {

			return;		
		} catch (RuntimeException e) {
			return;
		}

		try {client.close();} catch (IOException e2) {}
	}
	
	
	
	
	protected void finalize() throws Throwable {
		 client.close(); 
	}

	public void start() {
		(new Thread(this)).start();		
	}

}
