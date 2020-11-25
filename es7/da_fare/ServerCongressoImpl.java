import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerCongressoImpl extends UnicastRemoteObject 
								implements ServerCongresso { 

	private static final long serialVersionUID = 1L;
	static Programma prog[]; // si istanzia un programma per giornata
	
	// Costruttore
	public ServerCongressoImpl()throws RemoteException{
		super(); 
	}
	
	// METODO REMOTO: Richiesta di prenotazione
	public int registrazione (int giorno, String sessione,String speaker) throws RemoteException{ 
		int numSess = -1;
		System.out.println("Server RMI: richiesta registrazione:");
		if (sessione.equals("S1")) 
			numSess = 0;
		else if (sessione.equals("S2")) numSess = 1;
		//...
		else if (sessione.equals("S12")) numSess = 11;
		/* Se i dati sono sbagliati significa che sono stati trasmessi male e quindi si solleva una
		eccezione */
		if (numSess == -1) 
			throw new RemoteException();
		if (giorno < 1 || giorno > 3) 
			throw new RemoteException();
		
		return prog[giorno-1].registra(numSess,speaker);
	}

	// METODO REMOTO: Richiesta di programma
	public Programma programma (int giorno)throws RemoteException{ 
		System.out.println("Server RMI: programma giorno"+giorno);
		if (giorno < 1 || giorno > 3) 
			throw new RemoteException();
		return prog[giorno-1];
	}
	
	public static void main (String[] args){  // Codice di avvio del Server
		prog = new Programma[3]; // creazione programma
		for (int i = 0; i < 3; i++) {
			prog[i] = new Programma();
		}
		int registryRemotoPort = 1099;// default
		String registryRemotoName = "RegistryRemotoTag";
		String serviceName = "ServerCongresso";
		if (args.length!= 1 && args.length!= 2) {
			System.out.println("Usage: java ServerCongressoImpl registryRemotoHost [registryRemotoPort]");
		} 
		// Controlloargomenti
		String registryRemotoHost = args[0];
		if (args.length == 2){ 
			try {
				registryRemotoPort = Integer.parseInt(args[0]); 
			}catch (Exception e) {
				e.printStackTrace();
			} 
		} // if
		// Registrazione servizio presso RegistryRemoto
		String completeRemoteRegistryName = "//" + registryRemotoHost 
											+ ":" + registryRemotoPort 
											+ "/"+registryRemotoName;
		try{
			RegistryRemotoTagServer registryRemoto = (RegistryRemotoTagServer)Naming.lookup(completeRemoteRegistryName);
			ServerCongressoImpl serverRMI = new ServerCongressoImpl();
			registryRemoto.aggiungi(serviceName, serverRMI);
		} catch (Exception e) {
			e.printStackTrace();
		}
	} /* main */ 
} // ServerCongressoImpl