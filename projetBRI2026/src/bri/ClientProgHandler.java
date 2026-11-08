package bri;


import java.io.*;

import java.net.*;
import java.nio.charset.MalformedInputException;

import javax.security.auth.login.FailedLoginException;

import prog.IProg;
import prog.ListeProgs;
import prog.Prog;
import prog.ProgDejaPresentException;



class ClientProgHandler implements Runnable {
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private IProg progAssociated;
	
	ClientProgHandler(Socket socket) {
		try{
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
			

			boolean continueBoucle = true;
			String inputUser;
			while(continueBoucle) {
				inputUser = bufferedReader.readLine();
				System.out.println(inputUser.getClass());

				if(inputUser == null)
					break;
				continueBoucle = progAction(inputUser);
				System.out.println(continueBoucle);
			}
			closeEverything();
			System.out.println(Thread.currentThread().isInterrupted());

		} catch (IOException e) {		
			
			closeEverything();
			System.out.println(Thread.currentThread().isInterrupted());


		} 

	}
	
	
	private boolean progAction(String choix) throws IOException{	
		int userValue = Integer.parseInt(choix);
		switch (userValue) {
		case 0:
			return false;
		case 1: //FOURNIR_SERVICE
			try {
				String inputUsera;
				boolean serviceValide = false;
				while(!serviceValide) {
					try {
						inputUsera = bufferedReader.readLine();
						System.out.println(inputUsera.getClass());
						if(inputUsera == null)
							throw new IOException();
						fournirService(inputUsera);
						serviceValide= true;
						writer("true");
						
					}catch (ServiceInvalideException | NoServiceException e) {
						System.err.println(e);
						writer("false");
						writer(e.toString());
						
					}
					
					catch (IOException e) {
						throw e;					}
				}
			}catch (IOException e) {
				e.printStackTrace();
				throw e;
				}
			return true;
		case 2: //MAJ_SERVICE
			try {
				writer(Integer.toString(ServiceRegistry.getTailleRegistre()));
				writer(ServiceRegistry.toStringue(progAssociated));
				
				
				try {
					updateService(bufferedReader.readLine());					
					writer("true");
					
					}	
					catch (IndexOutOfBoundsException e) {
					//deja verifié
					}catch(ServiceInvalideException | NoServiceException e) {
						writer("false");
						
						writer(e.toString());
						
					}
			}catch (IOException e) {
				return false;
			}
			return true;
		case 3: //CHANGER_PORT_FTP
			try {
				String portFtpStr = bufferedReader.readLine();
				int portFtpNew = Integer.parseInt(portFtpStr);
				int oldPort = this.progAssociated.getFtpPort();
				System.out.println("Port du user changé : " + this.progAssociated.getFtpPort());
				changerPortFtp(portFtpNew);
				System.out.println(" ---> " + this.progAssociated.getFtpPort());
				writer("true");
				writer("Changement de port : " + oldPort 
				+ " ---> " + this.progAssociated.getFtpPort());
				
			}catch (MalformedURLException e) {
				writer("false");
				writer("Port invalide");
			}
			catch (IOException e) {
				return false;			
			}
			return true;
		case 4: //Démarrer/Arreter_SERVICE
			try {
				System.out.println(progAssociated.getNbServices());
				writer(Integer.toString(ServiceRegistry.getTailleRegistre()));
				writer(ServiceRegistry.toStringueStatus(progAssociated));
				
				String serviceChangeStr = bufferedReader.readLine();
				int serviceChangeStatus = Integer.parseInt(serviceChangeStr);
				ServiceRegistry.changeStatus(serviceChangeStatus, progAssociated);
			}catch (IOException e) {
				return false;			
			}catch (IndexOutOfBoundsException e) {
				//deja verifié côté client
			}
			return true;
		case 5: //DESINSTALLER_SERVICE
			try {
				writer(Integer.toString(progAssociated.getNbServices()));
				writer(ServiceRegistry.toStringue(progAssociated));
								
				String serviceChangeStr = bufferedReader.readLine();
				int serviceChangeStatus = Integer.parseInt(serviceChangeStr);
				ServiceRegistry.desinstallerService(serviceChangeStatus, progAssociated);
			}catch (IOException e) {
				return false;			
			}catch (IndexOutOfBoundsException e) {
				//deja verifié côté client
			}
			return true;
		default:
			return false;
		}
	}

	private synchronized void changerPortFtp(int portFtpNew) throws MalformedURLException {
		String fileNameUrlTemp =  "ftp://localhost:" +Integer.toString(portFtpNew)
		+  "/" + this.progAssociated.getLogin() + "/";
		
		if(!verifyConnection(fileNameUrlTemp)) {
			throw new MalformedURLException();
		}
		this.progAssociated.setFtpPort(portFtpNew);
	
	
	}

	private synchronized void fournirService(String inputUser) throws NoServiceException, ServiceInvalideException, IOException {
		
		System.out.println(progAssociated.getUrlProg());
		try {
		URLClassLoader urlcl = URLClassLoader.newInstance(
				new URL[] { new URL(progAssociated.getUrlProg())});
		
			ServiceRegistry.addService(
					urlcl.loadClass(inputUser).asSubclass(Service.class), this.progAssociated);
			System.out.println("Classe chargée");
		}catch (MalformedURLException e) {
			System.err.println(e);
			e.printStackTrace();
		}catch(ServiceInvalideException e) {
			throw new ServiceInvalideException(e.toString());		

		}catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println(e.getCause().getMessage());
			throw new NoServiceException();		
		}catch (RuntimeException e) {
			System.err.println(e);
		}
	
	}
	
	private synchronized void updateService(String inputUser) throws NoServiceException, ServiceInvalideException {
		try {
			String serviceUpdateStr = inputUser;
			int serviceToUpdate = Integer.parseInt(serviceUpdateStr);
			int idService = ServiceRegistry.getServiceClassId(serviceToUpdate, progAssociated);
			Class<? extends Service> service = ServiceRegistry.getServiceClassUpdater(idService);
			String serviceName = service.getPackageName() + "." + service.getSimpleName();
			System.out.println(serviceName);
			URLClassLoader urlcl = URLClassLoader.newInstance(
					new URL[] { new URL(progAssociated.getUrlProg())});

			ServiceRegistry.updateService(
					urlcl.loadClass(serviceName).asSubclass(Service.class), this.progAssociated , idService);
			System.out.println("Classe chargée");

		}catch(ServiceInvalideException e) {
			throw new ServiceInvalideException(e.toString());		

		}catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new NoServiceException();	
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

	}
	private void writer(String s) throws IOException {
		bufferedWriter.write(s);
		bufferedWriter.newLine();
		bufferedWriter.flush();
		
	}
	private boolean verifyConnection(String urlS) {
		try {
			URL url = new URL(urlS);
		    URLConnection conn = url.openConnection();
		    conn.connect();
			return true;
		}
		catch (IOException e) {
			return false;		
			}
	}
	
	private void associateProg(String choix) throws IOException {
		try {
			while(socket.isConnected()) {
				if (choix.equals("c")) {
					try {
						this.progAssociated = connexion();
						writer("true");
						break;
	
					} catch (FailedLoginException e) {
						writer("false");
						writer(e.getMessage());

					}catch (IOException e) {
						throw e;
											
						}
					}
						
				else if (choix.equals("i")) {
					try {
						this.progAssociated = inscription();
						writer("true");
						
						break;
	
					}catch(MalformedURLException e) {
						writer("false");
						writer("Port invalide");

					}
					catch (ProgDejaPresentException e) {
						e.printStackTrace();
						writer("false");
						writer(e.toString());
					}catch (IOException e) {
						
						throw e;
											
						}
					}
				} 
			
			System.out.println("le programmeur "  +progAssociated.getLogin()+ " est connecté.");
			
			writer(progAssociated.getLogin());
			
	
		}catch (IOException e) {
				
			throw e;
								
			}
		
	}
	
	private Prog inscription() throws ProgDejaPresentException, IOException {
		Prog progInput = null;
		String fileNameURLtemp = "";
		try {

			String log = bufferedReader.readLine();
			String pswd = bufferedReader.readLine();
			String strPort = bufferedReader.readLine();



			if (log==null || pswd== null || strPort== null)
				throw new IOException();
			
			if(!(ListeProgs.searchProg(log) == null))
				throw new ProgDejaPresentException();
			int port = Integer.parseInt(strPort); // on valide le format chez le client

			fileNameURLtemp= "ftp://localhost:" +Integer.toString(port)
			+  "/" + log + "/";
			System.out.println(fileNameURLtemp);
			if(!verifyConnection(fileNameURLtemp)) {
				throw new MalformedURLException();
			}
			progInput = new Prog(log, pswd, port, fileNameURLtemp);
			
		}catch( MalformedURLException e) {
			throw new MalformedURLException();
		}
		catch (IOException e) {
			throw e;
		}
		ListeProgs.addProg(progInput);
		System.out.println("Compte crée");
		return progInput;
	}
	
	private Prog connexion() throws FailedLoginException, IOException {
		Prog prog = null;
		try {
			
			String log = bufferedReader.readLine();
			String pswd = bufferedReader.readLine();
			
			if (log.equals("null") || pswd.equals("null"))
				throw new IOException();
			
			System.out.println("l'utilisateur a entrer ses logins " + log + pswd + bufferedReader.ready());
			if(ListeProgs.searchProg(log) == null)
				throw new FailedLoginException("Compte inexistant");
			else if(!ListeProgs.searchProg(log).getMdp().equals(pswd))
				throw new FailedLoginException("Mot de passe erroné");
			prog = ListeProgs.searchProg(log);

		}	
		
		catch (IOException e) {
			throw e; }
		return prog;			
	}
	private void closeEverything() {
		
		System.err.println("ON FERME TOUT");
		try {
			writer("false");
			if (bufferedReader != null)
				bufferedReader.close();
			if (bufferedWriter != null)
				bufferedWriter.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Thread.currentThread().interrupt();

	}
}
