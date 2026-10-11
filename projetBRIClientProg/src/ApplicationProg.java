import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;

class ApplicationProg {
	
	private final static int PORT_SERVICE = 3000;
	private final static String HOST = "localhost"; 
	
	public static void main(String[] args) {
		Socket s = null;		
		try {
			s = new Socket(HOST, PORT_SERVICE);

			BufferedReader sin = new BufferedReader (new InputStreamReader(s.getInputStream ( )));
			PrintWriter sout = new PrintWriter (s.getOutputStream ( ), true);
			
			
			BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in));			
		
			System.out.println("Connecté au serveur " + s.getInetAddress() + ":"+ s.getPort());
			System.out.println("Bienvenue sur l'interface de connexion des programmeurs.\n" 
					+ "Veuillez taper :\n" +
					" - i : Inscription\n" + 
					" - c : Connexion\n");
			
			boolean choixValide = false;
			String choix = null;
			while(!choixValide) {

				choix = (String) clavier.readLine();
				choixValide = choix.length()==1 && (choix.equals("c") || choix.equals("i"));
				if(!choixValide) {
					System.out.println("Veuillez faire un choix valide." + choix.length() + choix.equals("c"));
				}

			}				
			boolean connexionEtablie = false;

			if (choix.equals("c"))	{	
				sout.println(choix);

				while(!connexionEtablie) {
					String login;
					String mdp;
					System.out.println("Formulaire de connexion, veuillez renseigner votre login :");
					login = clavier.readLine();
					System.out.println("Veuillez renseigner votre mot de passe :");
					mdp = clavier.readLine();
					sout.println(login);
					sout.println(mdp);
					connexionEtablie = Boolean.parseBoolean(sin.readLine());
					if (!connexionEtablie)
						System.out.println("Compte inexistant");
				}
				System.out.println("Connexion etablie");
			}
			else if (choix.equals("i")) {
				sout.println(choix);
				while(!connexionEtablie) {
					String login;
					String mdp;
					int portFTP=0;
					System.out.println("Formulaire d'inscription, veuillez renseigner votre login :");
					login = clavier.readLine();
					System.out.println("Veuillez renseigner votre mot de passe :");
					mdp = clavier.readLine();
					System.out.println("Veuillez renseigner le port de votre serveur FTP (localhost admis par défaut) :");
					boolean numberFormatValide = false;
					while(!numberFormatValide) {
						try {
							portFTP = Integer.parseInt(clavier.readLine());
							numberFormatValide=true;
						}catch(NumberFormatException e) {
							System.err.println("Erreur de format, veuillez entrer un nombre.");
	
						}
					}
					sout.println(login);
					sout.println(mdp);
					sout.println(portFTP);
					connexionEtablie = Boolean.parseBoolean(sin.readLine());
					if (!connexionEtablie)
						System.out.println("Le compte existe déjà, veuillez vous connecter.");

				}
			}
			System.out.println("Bienvenue " + sin.readLine());
			System.out.println("1 - Fournir un nouveau service");
			System.out.println("2 - Mettre à jour un service");
			System.out.println("3 - Déclarer un changement d'adresse de son serveur ftp");
			System.out.println("4 - Démarrer/arrêter un service");
			System.out.println("5 - Désinstaller un service");
			
			int choice = 0;
			boolean numberFormatValide = false;
			while(!numberFormatValide) {
				try {
					choice = Integer.parseInt(clavier.readLine());
					numberFormatValide=true;
				}catch(NumberFormatException e) {
					System.err.println("Erreur de format, veuillez entrer un nombre.");

				}
			}
			sout.println(choice);

		}
		catch (IOException e) { System.err.println("Fin de la connexion"); }
		// Refermer dans tous les cas la socket
		try { if (s != null) s.close(); } 
		catch (IOException e2) { ; }		
	
	
	
	
}
	}
