import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerCongressoImpl
	extends UnicastRemoteObject
	implements ServerCongresso
{ 
	/**
	 *
	 */
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
		prog = new Programma[3]; //creazione dei programmi per le giornate
		
		for (int i=0; i<3; i++) 
			prog[i]= new Programma();
		
		final int REGISTRYPORT = 1099;
		String registryHost = "localhost";
		String serviceName = "ServerCongresso";
		try{ // Registrazione del servizio RMI
			String completeName = "//" + registryHost +
			":" + REGISTRYPORT + "/" + serviceName;
			ServerCongressoImpl serverRMI = new ServerCongressoImpl();
			Naming.rebind (completeName, serverRMI);
		} // try
		catch (Exception e){
			e.printStackTrace();
		}
	} /* main */ 
} // ServerCongressoImpl