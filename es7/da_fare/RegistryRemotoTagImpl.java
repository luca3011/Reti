import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RegistryRemotoTagImpl extends RegistryRemotoImpl implements RegistryRemotoTagServer {

	public RegistryRemotoTagImpl() throws RemoteException {
		super();
	}

	@Override
	public Remote[] cercaTag(String tag) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int associaTag(String nome_logico_server, String tag) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	
	
	

}