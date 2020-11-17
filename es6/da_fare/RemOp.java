import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemOp extends Remote
{
	int conta_righe(String file_remoto, int soglia) throws RemoteException;
	
	String elimina_riga(String file_remoto, int soglia, int riga_da_canc)  throws RemoteException;
}