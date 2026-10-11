package appli;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Scanner;

import bri.ServeurBRiClientAma;
import bri.ServeurBRiClientProg;
import bri.Service;
import bri.ServiceRegistry;

public class BRiLaunch {
	private final static int PORT_PROG = 3000;
	private final static int PORT_AMA = 3030;

	public static void main(String[] args) throws MalformedURLException {
		
	
		
		System.out.println("Bienvenue dans votre gestionnaire dynamique d'activité BRi");
		System.out.println("Pour ajouter une activité, celle-ci doit être présente sur votre serveur ftp");
		
		new Thread(new ServeurBRiClientProg(PORT_PROG)).start();
		new Thread(new ServeurBRiClientAma(PORT_AMA)).start();

			
	}
}
