import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/*
 * Ce client se connecte � un serveur dont le protocole est 
 * menu-choix-question-r�ponse client-r�ponse service
 * il n'y a qu'un �change (pas de boucle)
 * la r�ponse est saisie au clavier en String
 * 
 * Le protocole d'�change est suffisant pour le tp4 avec ServiceInversion 
 * ainsi que tout service qui pose une question, traite la donn�e du client et envoie sa r�ponse 
 * mais est bien sur susceptible de (nombreuses) am�liorations
 */
class ApplicationAma {
		private final static int PORT_SERVICE = 3030;
		private final static String HOST = "localhost"; 
		
	public static void main(String[] args) {
		Socket s = null;		
		try {
			s = new Socket(HOST, PORT_SERVICE);
			
			BufferedReader sin = new BufferedReader (new InputStreamReader(s.getInputStream ( )));
			PrintWriter sout = new PrintWriter (s.getOutputStream ( ), true);
			BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in));			
		
			System.out.println("Connecté au serveur " + s.getInetAddress() + ":"+ s.getPort());
			
			
				listen(s, sin);
				while (s.isConnected())
				sout.println(clavier.readLine());
								
				
			
		}
		catch (IOException e) { System.err.println("Fin de la connexion"); }
		// Refermer dans tous les cas la socket
		try { if (s != null) s.close(); } 
		catch (IOException e2) { ; }	
		return;
	}
	
	public static void listen(Socket s, BufferedReader sin) {
		new Thread(new Runnable() {
			String msgFromSocket = "";
			@Override
			public void run() {
				while(msgFromSocket != null) {
					try {
						msgFromSocket = sin.readLine();
						if(msgFromSocket!=null)
							System.out.println(msgFromSocket.replaceAll("#", "\n"));
					} catch (IOException e) {
						//closeEverything();
						System.err.println("aaaaaaaaa");
					}
					
				}
				
			}
		}).start(); 
	}
}
