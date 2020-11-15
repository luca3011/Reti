public class ServerImpl
extends UnicastRemoteObject
implements RemOp
{ 
	
	public ServerImpl()throws RemoteException{
		super(); 
	}
	
	int conta_righe(File file_remoto, int soglia) throws RemoteException
	{
		
		
		
	}
	
	String elimina_riga(File file_remoto, int soglia)  throws RemoteException
	{
		
		
		
		
		
		
		
	}
	
	public static void main (String[] args){  // Codice di avvio del Server
		
		final int REGISTRYPORT = Integer.parseInt(args[]);
		String registryHost = "localhost";
		String serviceName = "ServerCImpl";
		try{ // Registrazione del servizio RMI
			String completeName = "//" + registryHost +
			":" + REGISTRYPORT + "/" + serviceName;
			ServerImpl serverRMI = new ServerImpl();
			Naming.rebind (completeName, serverRMI);
		} // try
		catch (Exception e){"Errore inzializzazione RMI"}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}