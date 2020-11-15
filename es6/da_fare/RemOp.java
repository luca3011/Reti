import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemOp extends Remote
{
	int conta_righe(File file_remoto, int soglia) throws RemoteException;
	
	String elimina_riga(File file_remoto, int soglia)  throws RemoteException;
}