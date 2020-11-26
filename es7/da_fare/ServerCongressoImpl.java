import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerCongressoImpl extends UnicastRemoteObject 
								implements ServerCongresso { 

	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_TAG = "CONGRESSO";

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
		if (args.length == 0) {
			System.out.println("Usage: java ServerCongressoImpl registryRemotoHost [registryRemotoPort] [Tag1 Tag2...TagN]");
		} 
		// Controlloargomenti
		String registryRemotoHost = args[0];
		if (args.length == 2) {
			try {
				registryRemotoPort = Integer.parseInt(args[1]); 
			}catch (Exception e) {
				e.printStackTrace();
			} 
		} // if
		// Registrazione servizio presso RegistryRemoto
		String completeRemoteRegistryName = "//" + registryRemotoHost 
											+ ":" + registryRemotoPort 
				+ "/" + registryRemotoName;
		
		String[] tags = null;
		if (args.length > 2) {
			tags = new String[args.length - 2];
			for (int i = 2; i < args.length; i++) {
				tags[i - 2] = args[i];
			}
		}
		try {
			
			RegistryRemotoTagServer registryRemoto = (RegistryRemotoTagServer)Naming.lookup(completeRemoteRegistryName);
			ServerCongressoImpl serverRMI = new ServerCongressoImpl();
			registryRemoto.aggiungi(serviceName, serverRMI);
			if(tags == null)
				registryRemoto.associaTag(serviceName, DEFAULT_TAG);
			else {
				for (int i = 0; i < tags.length; i++) {
					registryRemoto.associaTag(serviceName, tags[i]);
				}
			}
			registryRemoto.associaTag(serviceName, DEFAULT_TAG);
		} catch (Exception e) {
			e.printStackTrace();
		}
	} /* main */ 
} // ServerCongressoImpl