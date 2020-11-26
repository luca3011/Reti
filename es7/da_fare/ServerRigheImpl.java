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

	public static final String DEFAULT_TAG = "RIGHE";

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

		if(args.length == 0){
			System.out.println("Usage: java ServerRigheImpl registryRemotoHost [registryRemotoPort] [Tag1 Tag2...TagN]");
			System.exit(1);
		}

		registryHost = args[0];
		if(args.length == 2){
			registryPort = Integer.parseInt(args[1]);
		}

		try{
			// ottengo l'oggetto remoto del Registry 
			// su cui chiamare i metodi di aggiunta tag
			String registryServiceName = "RegistryRemotoTag";
			String completeNameRegistry = "//" + registryHost +
					":" + registryPort + "/" + registryServiceName;
								
			String[] tags = null;
			if (args.length > 2) {
				tags = new String[args.length - 2];
				for (int i = 2; i < args.length; i++) {
					tags[i-2] = args[i];
				}
			}
			ServerRigheImpl serverRMI = new ServerRigheImpl();

			RegistryRemotoTagServer registryRMI = 
			(RegistryRemotoTagServer)Naming.lookup(completeNameRegistry);
			registryRMI.aggiungi(serviceName, serverRMI);

			if(tags == null)
				registryRMI.associaTag(serviceName, DEFAULT_TAG);
			else {
				for (int i = 0; i < tags.length; i++) {
					registryRMI.associaTag(serviceName, tags[i]);
				}
			}
		}
		catch (Exception e){
			System.out.println("Errore inzializzazione RMI");
			e.printStackTrace();
		}
	}
}