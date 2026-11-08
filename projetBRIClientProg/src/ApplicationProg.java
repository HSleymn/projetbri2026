import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.text.ParseException;


class ApplicationProg {
	
	private final static int PORT_SERVICE = 3000;
	private final static String HOST = "localhost"; 
	
	public static void main(String[] args) throws InterruptedException {
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
						System.err.println(sin.readLine());				}
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
					portFTP = numberFormatChecker(clavier);
					
					sout.println(login);
					sout.println(mdp);
					sout.println(portFTP);
					connexionEtablie = Boolean.parseBoolean(sin.readLine());
					if (!connexionEtablie)
						System.err.println(sin.readLine());

				}
			}
			
			
	
			System.out.println("Bienvenue " + sin.readLine());

			int choice = 0;

			boolean continueBoucle = true;
			
			while (continueBoucle) {
				choixDisplay();
				choice = numberFormatChecker(clavier);
				sout.println(choice);
				switch (choice) {
					case 0:
						continueBoucle = false;
						break;
					case 1: //FOURNIR_SERVICE
						boolean serviceValide = false;
						while(!serviceValide) {
							String input = clavier.readLine();
							sout.println(input);
							serviceValide = Boolean.parseBoolean(sin.readLine());
							System.out.println(serviceValide);
							if (!serviceValide) {
								String str = sin.readLine();

								str = str.substring(0, str.length() - 1);
								System.err.println(str.replaceAll("#", "\n"));
							}
						}
						break; 
					case 2: //MAJ_SERVICE
	
							int nbServiceU = Integer.parseInt(sin.readLine());
							if(nbServiceU==0) {
								System.err.println("Vous devez fournir un service pour pouvoir le mettre à jour.");
								break;
							}
							String str = sin.readLine();
							str = str.substring(0, str.length() - 1);
							System.out.println(str.replaceAll("#", "\n"));
							int choixServiceU = numberFormatChecker(clavier, nbServiceU);
							sout.println(choixServiceU);
							boolean servicePresent = Boolean.parseBoolean(sin.readLine());
							if(!servicePresent) {
								String errorUpdate = sin.readLine();
								
								errorUpdate = errorUpdate.substring(0, errorUpdate.length() - 1);
								System.err.println(errorUpdate.replaceAll("#", "\n"));
							}
						
						
						break;
					case 3: //CHANGER_PORT_FTP
					
						int newPort = numberFormatChecker(clavier);

						sout.println(newPort);
						if(!Boolean.parseBoolean(sin.readLine())) {
							System.err.println(sin.readLine());
						}

						System.out.println(sin.readLine());
						break;
					case 4: //Démarrer/Arreter_SERVICE
						int nbService = Integer.parseInt(sin.readLine());
						System.out.println(nbService);
						if(nbService==0) {
							System.err.println("Vous devez fournir un service pour pouvoir modifier son état.");
							break;
						}
						String listeServiceU = sin.readLine();

						listeServiceU = listeServiceU.substring(0, listeServiceU.length() - 1);
						System.out.println(listeServiceU.replaceAll("#", "\n"));
						int choixService = numberFormatChecker(clavier, nbService);
						sout.println(choixService);
		
						break;
					case 5: //DESINSTALLER_SERVICE
						int nbServiceD = Integer.parseInt(sin.readLine());
						System.out.println(sin.readLine());

						if(nbServiceD==0) {
							System.err.println("Vous devez fournir un service pour pouvoir le désinstaller.");
							break;
						}
						int choixServiceD = numberFormatChecker(clavier, nbServiceD);
						sout.println(choixServiceD);
						
						break;
				
				}
			}
			System.out.println("fin");
			return;
		}
		catch (IOException e) { System.err.println("Fin de la connexion"); }
		// Refermer dans tous les cas la socket
		try { if (s != null) s.close(); } 
		catch (IOException e2) { ; }		
	
	
	
	
	}
	private static void choixDisplay() {
		System.out.println("1 - Fournir un nouveau service");
		System.out.println("2 - Mettre à jour un service");
		System.out.println("3 - Déclarer un changement d'adresse de son serveur ftp");
		System.out.println("4 - Démarrer/arrêter un service");
		System.out.println("5 - Désinstaller un service");
		System.out.println("0 - Quitter l'application");
	}
	private static int numberFormatChecker(BufferedReader clavier) throws IOException {
		int number=0;
		boolean numberFormatValide = false;
		while(!numberFormatValide) {
			try {
				number = Integer.parseInt(clavier.readLine());
				numberFormatValide=true;
			}catch(NumberFormatException e) {
				System.err.println("Erreur de format, veuillez entrer un nombre.");

			}
		}
		return number;
	}
	private static int numberFormatChecker(BufferedReader clavier, int max) throws IOException {
		int number=0;
		boolean numberFormatValide = false;
		while(!numberFormatValide) {
			try {
				number = Integer.parseInt(clavier.readLine());
				if(number > 0 && number <= max)
					numberFormatValide=true;
				else
					System.err.println("Veuillez entrer un nombre parmi les choix possibles.");
			}catch(NumberFormatException e) {
				System.err.println("Erreur de format, veuillez entrer un nombre.");

			}
		}
		return number;
	}

}
