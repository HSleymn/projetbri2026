package bri;


import java.io.*;
import java.net.*;
import java.util.Scanner;

import prog.IProg;


public class ServeurBRiClientProg implements Runnable {
	private ServerSocket listen_socket;

	public ServeurBRiClientProg(int port) {
		
		try {
			listen_socket = new ServerSocket(port);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	
	@SuppressWarnings("unused")
	public void run() {
		//Scanner clavier = new Scanner(System.in);
		//String fileNameURL = "ftp://localhost:2121/";
		System.out.println("j'ouvre le serveur BRI aux prog sur le port " + listen_socket.getLocalPort());	
		while(!listen_socket.isClosed()) {

			try {
					Socket socket = listen_socket.accept();
					new Thread(new ClientProgHandler(socket)).start();
					System.out.println("Un client s'est connecté.");
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
	
	public void closeServerSocket() {
		try {
			if (listen_socket != null)
				listen_socket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	protected void finalize() throws Throwable {
		try {this.listen_socket.close();} catch (IOException e1) {}
	}

	public void lancer() {
		(new Thread(this)).start();		
	}
}
