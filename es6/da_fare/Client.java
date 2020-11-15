class Client
{
	public static void main(String[] args) // processo cliente
	{
		inal int REGISTRYPORT = Integer.parseInt(args[0]);;
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
			ServerCongresso serverRMI =(ServerCongresso) Naming.lookup (completeName);
			

			// Ciclo di interazione con l’utente per chiedere operazioni
			while((service=stdIn.readLine())!=null){
				
				
				
				
				
			} // while
		} //try
		catch (Exception e){"LEZZO"}
	} // main
} // ClientCongresso