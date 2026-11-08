package bri;


import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Set;

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

	
	public void run() {
		System.out.println("j'ouvre le serveur BRI aux prog sur le port " + listen_socket.getLocalPort());	
		while(!listen_socket.isClosed()) {

			try {
					Socket socket = listen_socket.accept();
					Thread.startVirtualThread(new ClientProgHandler(socket));
					}
				
					
			catch (Exception e) {
					e.printStackTrace();				
			}
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
		(new Thread(this, "prog")).start();		
	}
}
