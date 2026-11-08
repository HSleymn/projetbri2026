package bri;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;

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
				

				if(inputUser == null)
					break;
				continueBoucle = progAction(inputUser);
				
			}
			closeEverything();
			

		} catch (IOException e) {		
			
			closeEverything();
			


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
				writer(Integer.toString(progAssociated.getNbServices()));
				if (progAssociated.getNbServices() !=0)	{			

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
				
				changerPortFtp(portFtpNew);
				
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
				
				writer(Integer.toString(progAssociated.getNbServices()));
				if (progAssociated.getNbServices() !=0)	{			

					writer(ServiceRegistry.toStringueStatus(progAssociated));

				String serviceChangeStr = bufferedReader.readLine();
				int serviceChangeStatus = Integer.parseInt(serviceChangeStr);
				ServiceRegistry.changeStatus(serviceChangeStatus, progAssociated);}
			}catch (IOException e) {
				return false;			
			}catch (IndexOutOfBoundsException e) {
				//deja verifié côté client
			}
			return true;
		case 5: //DESINSTALLER_SERVICE
			try {
				writer(Integer.toString(progAssociated.getNbServices()));
				if (progAssociated.getNbServices() !=0)	{			

				writer(ServiceRegistry.toStringue(progAssociated));
				String serviceChangeStr = bufferedReader.readLine();
				int serviceChangeStatus = Integer.parseInt(serviceChangeStr);
				ServiceRegistry.desinstallerService(serviceChangeStatus, progAssociated);}
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
		
		
		try {
		URLClassLoader urlcl = URLClassLoader.newInstance(
				new URL[] { new URL(progAssociated.getUrlProg())});
		
			ServiceRegistry.addService(
					urlcl.loadClass(inputUser).asSubclass(Service.class), this.progAssociated);
			
		}catch (MalformedURLException e) {
			System.err.println(e);
			e.printStackTrace();
		}catch(ServiceInvalideException e) {
			throw new ServiceInvalideException(e.toString());		

		}catch (ClassNotFoundException e) {
			e.printStackTrace();
			
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
			
			URLClassLoader urlcl = URLClassLoader.newInstance(
					new URL[] { new URL(progAssociated.getUrlProg())});

			ServiceRegistry.updateService(
					urlcl.loadClass(serviceName).asSubclass(Service.class), this.progAssociated , idService);
			

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
			
			
			
			writer(progAssociated.getLogin());
			
	
		}catch (IOException e) {
				
			throw e;
								
			}
		
	}
	
	private synchronized Prog inscription() throws ProgDejaPresentException, IOException {
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
		
		return progInput;
	}
	
	private Prog connexion() throws FailedLoginException, IOException {
		Prog prog = null;
		try {
			
			String log = bufferedReader.readLine();
			String pswd = bufferedReader.readLine();
			
			if (log.equals("null") || pswd.equals("null"))
				throw new IOException();
			
			
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
