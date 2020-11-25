
import java.rmi.RemoteException;

public interface RegistryRemotoTagClient extends RegistryRemotoClient{
	
	//Restituisce i nomi logici dei server come stringa
	public String[] cercaTag(String tag) throws RemoteException;
	
}
