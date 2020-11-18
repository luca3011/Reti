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
	

	private static final long serialVersionUID = -6818538881474066631L;

	public ServerImpl() throws RemoteException {
		super(); 
	}
	
	public synchronized int conta_righe(String file_remoto, int soglia) throws RemoteException
	{
		System.out.println("Lettura Righe:...");
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
			System.out.println("Righe lette: "+righe);

			fileIn.close();
		}catch(IOException e){
			System.out.println("Errore nella lettura del buffer");
			throw new RemoteException();
		}
		return righe;	
	}
	
	//servono tre parametri (nomefile, soglia minima righe, riga da cancellare)
	public synchronized String elimina_riga(String file_remoto, int riga_da_canc)  throws RemoteException
	{
		String linea = null;
		System.out.println("Elimino riga:...");
		//controllo num righe (in modo analogo a conta_righe)
		
		try{
			File file_temp = new File(file_remoto + "temp");
			File file_rem = new File(file_remoto);
			file_temp.createNewFile();
			BufferedWriter fileOut = new BufferedWriter(new FileWriter(file_temp));
			BufferedReader fileIn = new BufferedReader(new FileReader(file_rem));
				
			//eliminazione riga...
			int numl;
			for(numl = 1; (linea = fileIn.readLine()) != null; numl++){
				if(numl != riga_da_canc)
					fileOut.write(linea + "\n");
			}
			System.out.println("Righe totali: "+(numl-1));

			fileOut.flush();
			fileOut.close();
			fileIn.close();

			if(riga_da_canc>=(numl)){
				System.out.println("riga da cancellare out of bound");
				throw new RemoteException("riga da cancellare out of bound");
			}
			
			if(!file_rem.delete()){
				System.out.println("Impossibile sovrascrivere il vecchio file");
				throw new RemoteException("Impossibile sovrascrivere il vecchio file");
			}
			file_temp.renameTo(file_rem);

			return file_remoto + ": " + (numl-2) + " righe";
		
		}catch(RemoteException e){
			throw e;
		}catch(IOException e){	
			System.out.println("Errore nella rimozione della riga");
			throw new RemoteException("Errore nella rimozione della riga");
		}
	}
	
	public static void main (String[] args){  // Codice di avvio del Server
		final int REGISTRYPORT = 1099;
		String registryHost = "localhost";
		String serviceName = "ServerImpl";
		System.out.println("Server: starting...");

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