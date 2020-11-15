public class ServerImpl
extends UnicastRemoteObject
implements RemOp
{ 
	
	public ServerImpl()throws RemoteException{
		super(); 
	}
	
	int conta_righe(String file_remoto, int soglia) throws RemoteException
	{
		int righe = 0;

		FileReader input = new FileReader(file_remoto);

		while((linea=input.readLine())!=null)
		{
			linea = linea.trim();
			if(linea.split("\\s+").lenght>soglia)
				righe++
		}
	
		return righe;	
	}
	
	String elimina_riga(File file_remoto, int soglia)  throws RemoteException
	{
		
		
		
		
		
		
		
	}
	
	public static void main (String[] args){  // Codice di avvio del Server
		
		final int REGISTRYPORT = Integer.parseInt(args[0]);
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