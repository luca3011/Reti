import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;

class Client
{
	public static void main(String[] args) // processo cliente
	{
		final int REGISTRYPORT = Integer.parseInt(args[0]);;
		String registryHost = null;
		String serviceName = "ServerImpl";
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		
		try{ // Controllo dei parametri della riga di comando
			if (args.length != 1){
				System.out.println("Sintassi:…");System.exit(1);
			}
			registryHost = "localhost";
			
			// Connessione al servizio RMI remoto
			String completeName = "//" + registryHost + ":" +
			REGISTRYPORT + "/" + serviceName;
			ServerImpl serverRMI=(ServerImpl)Naming.lookup(completeName);
			
			String service;
			
			System.out.println("Servizio di eliminazione e conteggio righe");
			System.out.println("Digita C per conta_righe o E per elimina_righe: ");

			// Ciclo di interazione con l’utente per chiedere operazioni
			while((service=stdIn.readLine())!=null){
				
				if(service.equals("C"))
				{
					
				}
				else if(service.equals("E"))
				{
					
				}
				else System.out.println("ERRORE INPUT");
				
				System.out.println("Digita C per conta_righe o E per elimina_righe: ");
			} // while
		} //try
		catch (Exception e){
			System.out.println("LEZZO");
			e.printStackTrace();
		};
	} // main
} // ClientCongresso