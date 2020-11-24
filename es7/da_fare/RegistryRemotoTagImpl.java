import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class RegistryRemotoTagImpl extends RegistryRemotoImpl implements RegistryRemotoTagServer {

	final int tableSize = 100; //Tabella: le colonne contengono i 5 tag
	final int tagNumber = 5;
	String[] nomi_tag = {"pippo","paperino","pluto","tizio","caio"};
    Boolean[][] tag_table = new Boolean[100][5];
	
	
	public RegistryRemotoTagImpl() throws RemoteException {
		super();
		 for(int i=0;i<100;i++)
		 {
			 for(int j=0;j<5;j++)
			 {
				 tag_table[i][j]=false;
			 }
		 }
	}

	@Override
	public Remote[] cercaTag(String tag) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int associaTag(String nome_logico_server, String tag) throws RemoteException {
		
		int index_nome=-1;
		
		for(int i=0;i<101;i++)
		 {
			 if(table[i][0].equals(nome_logico_server))
			 {
				 index_nome=i;
				 break;
			 }
			 else if(i==101)
			 {
				 System.out.println("Nome logico non trovato");
				 return -1;
			 }
		 }
		
		int index_tag = Arrays.binarySearch(nomi_tag, tag);
		
		if(index_tag<0)
		{
			System.out.println("Tag non valido");
			return -1;
		}
		
		tag_table[index_nome][index_tag]=true;
		return 1;
	}

	
	
	

}