import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;

class Client
{
	public static void main(String[] args) // processo cliente
	{
		final int REGISTRYPORT = 1099;
		String registryHost = null;
		String serviceName = "ServerImpl";
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		
		try{ // Controllo dei parametri della riga di comando
			if (args.length != 1){
				System.out.println("Sintassi:…");
				System.exit(1);
			}
			else{
				registryHost = stdIn.readLine();
			}
			
			// Connessione al servizio RMI remoto
			String completeName = "//" + registryHost + ":" +
			REGISTRYPORT + "/" + serviceName;
			RemOp serverRMI=(RemOp)Naming.lookup(completeName);
			
			String service;
			
			System.out.println("Servizio di eliminazione e conteggio righe");
			System.out.print("Digita C per conta_righe o E per elimina_righe: ");

			// Ciclo di interazione con l’utente per chiedere operazioni
			while((service=stdIn.readLine())!=null){
				
				if(service.equals("C"))
				{
					System.out.print("Inserire il nome del file di cui contare le righe: ");
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
				else if(service.equals("E"))
				{
					System.out.print("Inserire il nome del file da cui eliminare la riga: ");
					String file_remoto = stdIn.readLine();
					System.out.print("Inserire soglia minima di righe: ");
					int soglia = Integer.parseInt(stdIn.readLine());
					System.out.print("Numero di riga da eliminare: ");
					int riga_da_canc = Integer.parseInt(stdIn.readLine());
					try{
						//chiamata remota
						String result = serverRMI.elimina_riga(file_remoto, soglia, riga_da_canc);
						System.out.println("Operazione riuscita: " + result);
					}catch(RemoteException e){
						System.out.println("Errore lato server nella lettura/scrittura del file. " + e.getMessage());
					}
				}
				else System.out.println("ERRORE INPUT");
				
				System.out.println("Digita C per conta_righe o E per elimina_righe: ");
			} // while
		} //try
		catch (Exception e){
			e.printStackTrace();
		}
	} // main
} // ClientCongresso