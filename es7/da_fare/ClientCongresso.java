import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;


public class ClientCongresso
{
	public static final String DEFAULT_TAG = "CONGRESSO";

	public static void main(String[] args) // processo cliente
	{
		int registryRemotoPort = 1099;
		String registryRemotoName = "RegistryRemotoTag";
		String serviceName = "ServerCongresso";
		String tipo_ricerca; 
		
		String tag;
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		if (args.length != 1 && args.length != 2) {
			System.out.println("Usage: ClientCongresso registryRemotoHost [registryRemotoPort]");
		}

		String registryRemotoHost = args[0];
		if (args.length== 2){ 
			try {
				registryRemotoPort = Integer.parseInt(args[0]); 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Recupero il riferimento al servizio remoto presso il RegistryRemoto
		try {
			String completeRemoteRegistryName = "//" +  registryRemotoHost 
												+ ":" + registryRemotoPort 
												+ "/" + registryRemotoName;
			RegistryRemotoTagClient registryRemoto = (RegistryRemotoTagClient)Naming.lookup(completeRemoteRegistryName);
			
			ServerCongresso serverRMI=null;
				
			
			while(serverRMI==null){
				System.out.println("\nRicerca Server: Tag=T or Name=N ?");
				tipo_ricerca=stdIn.readLine();
				if(tipo_ricerca.toUpperCase().equals("T")){
					String[] serverNames;
					System.out.print("Usare il tag di default '" + DEFAULT_TAG + "'? (S/N):");
					tag=stdIn.readLine();
					if(tag.toUpperCase().equals("N")){
						System.out.print("Inserire tag: ");
						tag = stdIn.readLine();
					}
					else{
						tag = DEFAULT_TAG;
					}
					
					try {
						serverNames = registryRemoto.cercaTag(tag);
						serverRMI = (ServerCongresso) registryRemoto.cerca(serverNames[0]);
						//server casuale: serverNames[(int)Math.random()*(serverNames.length)]
					}
					catch (RemoteException e) {
						System.out.println(e.getMessage());
					}
				}
				else if(tipo_ricerca.toUpperCase().equals("N"))
					serverRMI = (ServerCongresso) registryRemoto.cerca(serviceName);
				else{
					System.out.println("Selezione non valida");
					serverRMI=null;
				}
			}

			
			System.out.println("\nRichieste a EOF");
			System.out.print("Servizio(R=Registrazione, P=Programma): ");
			
			
			boolean ok;

			// Ciclo di interazione con l’utente per chiedere operazioni
			while((tipo_ricerca=stdIn.readLine())!=null){
				if (tipo_ricerca.equals("R")){ 
					ok=false; int g=1; // lettura giornata
					System.out.print("Giornata (1-3)? ");
					
					while (ok!=true){
						g = Integer.parseInt(stdIn.readLine());
						if (g < 1 || g > 3){ 
							System.out.println("Giornata non valida");
							System.out.print("Giornata (1-3)? "); continue;
						} else 
							ok=true;
					} // while interno
					
					ok=false; 
					String sess=null; // lettura sessione
					System.out.print("Sessione (S1 - S12)? ");
					while (ok!=true){
						sess = stdIn.readLine();
						if ( !sess.equals("S1") && !sess.equals("S2")
						&& !sess.equals("S3") && !sess.equals("S4")
						&& !sess.equals("S5") && !sess.equals("S6")
						&& !sess.equals("S7") && !sess.equals("S8")
						&& !sess.equals("S9") && !sess.equals("S10")
						&& !sess.equals("S11") && !sess.equals("S12"))
						{ 
							System.out.println("Sessione non valida!"); 
							continue; 
						} 
						else 
							ok=true;
					}
					System.out.print("Speaker? "); // lettura speaker
					String speak = stdIn.readLine();
					// Parametri corretti, invoco il servizio remoto
					if (serverRMI.registrazione(g, sess, speak)==0)
						System.out.println("Registrazione di …");
					else 
						System.out.println("Registrazione non effettuata");
				}else if (tipo_ricerca.equals("P")){ 
					int g=1; 
					ok=false;
					System.out.print("Giornata (1-3)? ");
					while (ok!=true){
						g = Integer.parseInt(stdIn.readLine());
						if (g < 1 || g > 3){
							System.out.println("Giornata non valida");
							System.out.print("Giornata (1-3)? ");
							continue;
						}else 
							ok=true;
					} // while
					Programma prog = serverRMI.programma(g);
					System.out.println("Programma giornata "+g+"\n");
					prog.stampa();
				} // Operazione P
				else 
					System.out.println("Servizio non disponibile");
				System.out.print("Servizio (R=Registrazione, …");
			} // while
		} //try
		catch (Exception e){
			e.printStackTrace();
		}
	} // main
} // ClientCongresso