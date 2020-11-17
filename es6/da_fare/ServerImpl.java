import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl
	extends UnicastRemoteObject
	implements RemOp
{ 
	
	/**
	 *
	 */
	private static final long serialVersionUID = -6818538881474066631L;

	public ServerImpl() throws RemoteException {
		super(); 
	}
	
	public synchronized int conta_righe(String file_remoto, int soglia) throws RemoteException
	{
		int righe = 0;
		String linea = null;
		try{
			BufferedReader fileIn = new BufferedReader(new FileReader(file_remoto));

			while((linea=fileIn.readLine())!=null)
			{
				linea = linea.trim();
				if(linea.split("\\s+").length>soglia)
					righe++;
			}

			fileIn.close();
		}catch(IOException e){
			throw new RemoteException();
		}
		return righe;	
	}
	
	public synchronized String elimina_riga(String file_remoto, int soglia)  throws RemoteException
	{
		int righe = 0;
		String result;
		String linea = null;

		FileReader input = new FileReader(file_remoto);

		while((linea=input.readLine())!=null)
		{
				righe++;
		}
	
		if(righe>soglia)
		{
			
			
			
			//elimiazione righe...
			
			
			return file_modificato + " " + righe;
		}
		else
		{
			return file_remoto + " -1";
		}

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
		catch (Exception e){
			System.out.println("Errore inzializzazione RMI");
			e.printStackTrace();
		}
	}
}