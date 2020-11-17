import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
	
	//servono tre parametri (nomefile, soglia minima righe, riga da cancellare)
	public synchronized String elimina_riga(String file_remoto, int soglia, int riga_da_canc)  throws RemoteException
	{
		int righe = 0;
		String linea = null;

		//controllo num righe (in modo analogo a conta_righe)
		try{
			BufferedReader fileIn = new BufferedReader(new FileReader(file_remoto));

			while((linea = fileIn.readLine()) != null)
			{
				linea = linea.trim();
				if(linea.split("\\s+").length>soglia)
					righe++;
			}
			fileIn.close();
		}catch(IOException e){
			throw new RemoteException();
		}
	
		if(righe>soglia)
		{
			try{
				File file_temp = new File(file_remoto + "temp");
				File file_rem = new File(file_remoto);
				file_temp.createNewFile();
				BufferedWriter fileOut = new BufferedWriter(new FileWriter(file_temp));
				BufferedReader fileIn = new BufferedReader(new FileReader(file_rem));
				
				//eliminazione riga...
				for(int numl = 1; (linea = fileIn.readLine()) != null; numl++){
					if(numl != riga_da_canc)
						fileOut.write(linea + "\n");
				}
				fileOut.flush();
				fileOut.close();
				fileIn.close();
				
				if(!file_rem.delete())
					throw new RemoteException("Impossibile sovrascrivere il vecchio file");
				
				file_temp.renameTo(file_rem);

				return file_remoto + ": " + righe + " righe";
			}catch(IOException e){
				throw new RemoteException();
			}
		}
		else
		{
			throw new RemoteException("Il numero di righe non supera la soglia");
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