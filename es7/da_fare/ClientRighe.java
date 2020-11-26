import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;

class ClientRighe
{

	public static final String DEFAULT_TAG = "RIGHE";

	public static void main(String[] args) // processo cliente
	{
		int registryPort = 1099;
		String registryHost = null;
		String serviceName = "RegistryRemotoTag";
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		
		try{ // Controllo dei parametri della riga di comando
			if (args.length != 1 && args.length != 2){
				System.out.println("Error: Inserire l'indirizzo IP");
				System.exit(1);
			}
			registryHost = args[0];
			if(args.length == 2)
				registryPort = Integer.parseInt(args[1]);
			
			// Connessione al servizio RMI remoto
			String completeName = "//" + registryHost + ":" +
			registryPort + "/" + serviceName;
			RegistryRemotoTagClient registryRMI = (RegistryRemotoTagClient)Naming.lookup(completeName);
			ServerRighe serverRMI = null;
			String tipo_ricerca, tag;
			
			while(serverRMI==null){
				System.out.print("\nRicerca Server: Tag=T or Name=N? ");
				tipo_ricerca=stdIn.readLine();
				if(tipo_ricerca.toUpperCase().equals("T")){
					String[] serverNames;
					System.out.print("Usare il tag di default '" + DEFAULT_TAG + "'? (S/N): ");
					tag=stdIn.readLine();
					if(tag.toUpperCase().equals("N")){
						System.out.print("Inserire tag: ");
						tag = stdIn.readLine();
					}
					else{
						tag = DEFAULT_TAG;
					}
					
					try {
						serverNames = registryRMI.cercaTag(tag);
						if (serverNames.length == 0) {
							System.out.println("Nessun server trovato");
							continue;
						}

						System.out.println("Servizi trovati:");
						for (int i = 0; i < serverNames.length; i++) {
							System.out.println("(" + i + ")\t" + serverNames[i]);
						}
						System.out.print("\nSelezionare il server da raggiungere: (0.." + (serverNames.length - 1) + "): ");
						int sel = Integer.parseInt(stdIn.readLine());
						serverRMI = (ServerRighe) registryRMI.cerca(serverNames[sel]);
					}
					catch (RemoteException e) {
						System.out.println(e.getMessage());
					}
					catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				else if(tipo_ricerca.toUpperCase().equals("N"))
					serverRMI = (ServerRighe) registryRMI.cerca(serviceName);
				else{
					System.out.println("Selezione non valida");
					serverRMI=null;
				}
			}

			String service;

			System.out.println("Servizio di eliminazione e conteggio righe");
			System.out.print("Digita C per conta_righe o E per elimina_righe: ");

			// Ciclo di interazione con l’utente per chiedere operazioni
			while((service=stdIn.readLine())!=null){
				try{
					if(service.toUpperCase().equals("C"))
					{
						System.out.print("Inserire il nome del file: ");
						String file_remoto = stdIn.readLine();
						System.out.print("Inserire soglia minima di parole per riga: ");
						int soglia = Integer.parseInt(stdIn.readLine());
						try{
							//chiamata remota
							int result = serverRMI.conta_righe(file_remoto, soglia);
							System.out.println("Il file " + file_remoto + " contiene " + result + " righe con più di " + soglia + " parole");
						}catch(RemoteException e){
							System.out.println("Errore lato server nella lettura del file. " + e.getMessage());
						}
					}
					else if(service.toUpperCase().equals("E"))
					{
						System.out.print("Inserire il nome del file: ");
						String file_remoto = stdIn.readLine();
						
						System.out.print("Numero di riga da eliminare: ");
						int riga_da_canc = Integer.parseInt(stdIn.readLine());
						try{
							//chiamata remota
							String result = serverRMI.elimina_riga(file_remoto,riga_da_canc);
							System.out.println("Operazione riuscita: " + result);
						}catch(RemoteException e){
							System.out.println("Errore lato server nella lettura/scrittura del file. " + e.getMessage());
						}
					}
					else System.out.println("ERRORE INPUT");
				}catch(Exception e){
					e.printStackTrace();
				}
				System.out.print("Digita C per conta_righe o E per elimina_righe: ");
			} // while
		} //try
		catch (Exception e){
			e.printStackTrace();
		}
	} // main
} // ClientCongresso