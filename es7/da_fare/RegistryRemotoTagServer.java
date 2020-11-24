
import java.rmi.RemoteException;

public interface RegistryRemotoTagServer extends RegistryRemotoTagClient, RegistryRemotoServer{
    
	public int associaTag(String nome_logico_server, String tag) throws RemoteException;
	
}
