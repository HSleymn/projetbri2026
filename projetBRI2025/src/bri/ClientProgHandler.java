package bri;


import java.io.*;

import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.text.ParseException;
import java.util.Vector;

import javax.security.auth.login.FailedLoginException;

import prog.IProg;
import prog.ListeProgs;
import prog.Prog;
import prog.ProgDejaPresentException;


class ClientProgHandler implements Runnable {
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private Prog progAssociated;
	static {
		registreServices= new Vector<Class<? extends Service>>();
	}
	private static Vector<Class<? extends Service>> registreServices;
	ClientProgHandler(Socket socket) {
		try{
			closeEverything();

			this.socket = socket;
			this.bufferedReader = new BufferedReader (new InputStreamReader(socket.getInputStream()));
			this.bufferedWriter = new BufferedWriter (new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			closeEverything();
		}
	}

	public void run() {
		try {
			
			associateProg(bufferedReader.readLine());
			progAction(bufferedReader.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	
	private void progAction(String choix) {	
		int userValue = Integer.parseInt(choix);
		switch (userValue) {
		case 1: //FOURNIR_SERVICE
			try {
			fournirService(bufferedReader.readLine());
			}catch (NoServiceException e) {
				System.err.println(e);
			}catch (MalformedURLException e) {
				
			}catch (IOException e) {
				e.printStackTrace();
				closeEverything();
			}
			break; 
		case 2: //MAJ_SERVICE
			break;
		case 3: //CHANGER_PORT_FTP
			break;
		case 4: //Désintaller/Arreter_SERVICE
			break;
		case 5: //DESINSTALLER_SERVICE
			break;
		default:
			break;
		}
	}

	private void fournirService(String inputUser) throws NoServiceException, MalformedURLException {
		String fileNameURL = "ftp://localhost:" +Integer.toString(this.progAssociated.getFtpPort())
		+  "/" + this.progAssociated.getLogin();
		URLClassLoader urlcl = URLClassLoader.newInstance(
				new URL[] { new URL(fileNameURL)});

		try {
			ServiceRegistry.addService(
					urlcl.loadClass(inputUser).asSubclass(Service.class));
			System.out.println("Classe chargée");
		} catch (Exception e) {
			throw new NoServiceException();		
			}
	}

	private void associateProg(String choix) throws IOException, InterruptedException {
		try {
			while(socket.isConnected()) {
				if (choix.equals("c")) {
					try {
						this.progAssociated = connexion();
						bufferedWriter.write("true");
						bufferedWriter.newLine();
						bufferedWriter.flush();
						break;
	
					} catch (FailedLoginException e) {
						
						e.printStackTrace();
						bufferedWriter.write("false");
						bufferedWriter.newLine();
						bufferedWriter.flush();		
						}
					}
						
				else if (choix.equals("i")) {
					try {
						this.progAssociated = inscription();
						bufferedWriter.write("true");
						bufferedWriter.newLine();
						bufferedWriter.flush();
						break;
	
					}
					catch (ProgDejaPresentException e) {
						e.printStackTrace();
						bufferedWriter.write("false");
						bufferedWriter.newLine();
						bufferedWriter.flush();
						}
					}
				} 
			System.out.println("le programmeur "  +progAssociated.getLogin()+ " est connecté.");
			bufferedWriter.write(progAssociated.getLogin());
			bufferedWriter.newLine();
			bufferedWriter.flush();
		}catch (IOException e) {
				e.printStackTrace();
				closeEverything();
								
			}
		
	}
	private Prog inscription() throws ProgDejaPresentException {
		Prog progInput = null;
		try {

			String log = bufferedReader.readLine();
			String pswd = bufferedReader.readLine();
			String strPort = bufferedReader.readLine();

			System.out.println(log==null);

			System.out.println(log.equals("null"));
			System.out.println(pswd.equals("null"));

			if (log.equals("null") || pswd.equals("null") || strPort.equals("null"))
				throw new IOException();
			
			if(!(ListeProgs.searchProg(log) == null))
				throw new ProgDejaPresentException();
			int port = Integer.parseInt(strPort); // on valide le format chez le client

			progInput = new Prog(log, pswd, port);
		}catch (IOException e) {
			closeEverything();		
		}
		ListeProgs.addProg(progInput);
		System.out.println("Compte crée");
		return progInput;
	}
	private Prog connexion() throws FailedLoginException {
		Prog prog = null;
		try {
			
			String log = bufferedReader.readLine();
			String pswd = bufferedReader.readLine();
			System.out.println(log==null);

			System.out.println(log.equals("null"));
			System.out.println(pswd.equals("null"));
			
			if (log.equals("null") || pswd.equals("null"))
				throw new IOException();
			
			System.out.println("l'utilisateur a entrer ses logins " + log + pswd + bufferedReader.ready());
			if(ListeProgs.searchProg(log) == null)
				throw new FailedLoginException("Compte inexistant");
			
			prog = ListeProgs.searchProg(log);

		}	
		
		catch (IOException e) {
			closeEverything();	
		}
		return prog;
	}
	private void closeEverything() {
		try {
			if (bufferedReader != null)
				bufferedReader.close();
			if (bufferedWriter != null)
				bufferedWriter.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
