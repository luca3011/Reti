import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerRigheImpl
	extends UnicastRemoteObject
	implements ServerRighe
{ 

	private static final long serialVersionUID = -6818538881474066631L;

	public static final String TAGNAME = "RIGHE";

	public ServerRigheImpl() throws RemoteException {
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
	
	public synchronized String elimina_riga(String file_remoto, int riga_da_canc)  throws RemoteException
	{
		String linea = null;
		System.out.println("Elimino riga:...");
		
		File file_temp = new File(file_remoto + "temp");
		File file_rem = new File(file_remoto);
		int numl;
		try{
			file_temp.createNewFile();
			BufferedWriter fileOut = new BufferedWriter(new FileWriter(file_temp));
			BufferedReader fileIn = new BufferedReader(new FileReader(file_rem));
				
			//eliminazione riga...
			
			for(numl = 1; (linea = fileIn.readLine()) != null; numl++){
				if(numl != riga_da_canc)
					fileOut.write(linea + "\n");
			}
			System.out.println("Righe dopo l'eliminazione: "+(numl-2));

			fileOut.flush();
			fileOut.close();
			fileIn.close();
		} catch (IOException e) {
			System.out.println("Errore nell'accesso al file");
			throw new RemoteException("Errore nell'accesso al file");
		}
		
		if(riga_da_canc>=(numl)){
			file_temp.delete();
			System.out.println("riga da cancellare out of bound");
			throw new RemoteException("riga da cancellare out of bound");
		}
			
		if(!file_rem.delete()){
			System.out.println("Impossibile sovrascrivere il vecchio file");
			throw new RemoteException("Impossibile sovrascrivere il vecchio file");
		}
		file_temp.renameTo(file_rem);

		return file_remoto + ": " + (numl-2) + " righe";
	}
	
	public static void main (String[] args){  // Codice di avvio del Server
		int registryPort = 1099;
		String registryHost;
		String serviceName = "ServerRighe";
		System.out.println("Server: starting...");

		if(args.length != 1 && args.length != 2){
			System.out.println("Usage: java ServerRigheImpl registryRemotoHost [registryRemotoPort]");
			System.exit(1);
		}

		registryHost = args[0];
		if(args.length == 2){
			registryPort = Integer.parseInt(args[1]);
		}

		try{

			// ottengo l'oggetto remoto del Registry su cui chiamare i metodi di aggiunta tag
			String registryServiceName = "RegistryRemotoTag";
			String completeNameRegistry = "//" + registryHost +
										":" + registryPort + "/" + registryServiceName;
										
			ServerRigheImpl serverRMI = new ServerRigheImpl();

			RegistryRemotoTagServer registryRMI = (RegistryRemotoTagServer)Naming.lookup(completeNameRegistry);
			System.out.println(registryRMI.aggiungi(serviceName, serverRMI));
			registryRMI.associaTag(serviceName, TAGNAME);
		} // try
		catch (Exception e){
			System.out.println("Errore inzializzazione RMI");
			e.printStackTrace();
		}
	}
}