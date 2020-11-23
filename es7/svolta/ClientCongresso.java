import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;


public class ClientCongresso
{
	public static void main(String[] args) // processo cliente
	{
		int registryRemotoPort = 1099;
		String registryRemotoName = "RegistryRemoto";
		String serviceName = "ServerCongresso";
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
			RegistryRemotoClient registryRemoto = (RegistryRemotoClient)Naming.lookup(completeRemoteRegistryName);
			ServerCongresso serverRMI = (ServerCongresso) registryRemoto.cerca(serviceName);

			//codice uguale a es6

			System.out.println("\nRichieste a EOF");
			System.out.print("Servizio(R=Registrazione, P=Programma): ");
			
			String service; 
			boolean ok;

			// Ciclo di interazione con l’utente per chiedere operazioni
			while((service=stdIn.readLine())!=null){
				if (service.equals("R")){ 
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
						if ( !sess.equals("S1") && /*...*/ !sess.equals("S12")){ 
							System.out.println("Sessione non valida!"); 
							continue; 
						} else 
							ok=true;
					}
					System.out.print("Speaker? "); // lettura speaker
					String speak = stdIn.readLine();
					// Parametri corretti, invoco il servizio remoto
					if (serverRMI.registrazione(g, sess, speak)==0)
						System.out.println("Registrazione di …");
					else 
						System.out.println("Registrazione non effettuata");
				}else if (service.equals("P")){ 
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