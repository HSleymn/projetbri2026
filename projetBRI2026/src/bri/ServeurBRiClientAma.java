package bri;


import java.io.*;
import java.net.*;


public class ServeurBRiClientAma implements Runnable {
	private ServerSocket listen_socket;
	
	// Cree un serveur TCP - objet de la classe ServerSocket
	public ServeurBRiClientAma(int port) {
		try {
			listen_socket = new ServerSocket(port);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// Le serveur ecoute et accepte les connections.
	// pour chaque connection, il cree un ServiceInversion, 
	// qui va la traiter.
	public void run() {
		System.out.println("j'ouvre le serveur BRI aux amateurs sur le port " + listen_socket.getLocalPort());	
		while(!listen_socket.isClosed()) {

			try {
					Socket socket = listen_socket.accept();
					Thread.startVirtualThread(new ClientAmaHandler(socket));
					System.out.println("Un amateur s'est connecté.");
				}
			catch (Exception e) {
					e.printStackTrace();				
					}
//			 catch (IOException e) {
//				e.printStackTrace();
//			} catch (NullPointerException e) {
//				System.err.println("aaaaaaaaaaaaaaaaaaaaaaa");
//			}
		}
			
	}

	 // restituer les ressources --> finalize
	protected void finalize() throws Throwable {
		try {this.listen_socket.close();} catch (IOException e1) {}
	}

	// lancement du serveur
	public void lancer() {
		(new Thread(this)).start();		
	}
}
