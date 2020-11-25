import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class RegistryRemotoTagImpl extends RegistryRemotoImpl implements RegistryRemotoTagServer {

	final String[] TAGS = {"CONGRESSO","RIGHE","TAG3","TAG4","TAG5"};
    private Boolean[][] tag_table; //Tabella: le colonne contengono i 5 tag
	private int num_servers;
	
	public RegistryRemotoTagImpl() throws RemoteException {
		super();
		tag_table = new Boolean[tableSize][TAGS.length];
		num_servers = 0;
		for(int i=0;i<100;i++)
		{
			for(int j=0;j<5;j++)
			{
				tag_table[i][j]=false;
			}
		}
	}

	
	public String[] cercaTag(String tag) throws RemoteException {
        int cont=0;
        int tagindex=-1;
        Boolean found=false;
        for(int i=0;i<TAGS.length && tagindex == -1;i++) {
            if(tag.equals(TAGS[i])) {
                tagindex=i;
            }
        }
        if(tagindex == -1) {
        	throw new RemoteException("Tag non valido");
        }
        
        for(int i=0;i<tableSize;i++) {
            if(tag_table[i][tagindex]==true) {
                cont++;
            }
        }

        String[] risultato = new String[cont];
        cont=0;
        for(int i=0;i<tableSize;i++) {
            if(tag_table[i][tagindex]==true) {
                risultato[cont++]=(String) table[i][1];
            }
        }

        return risultato;
    }

	public int associaTag(String nome_logico_server, String tag) throws RemoteException {
		
		int index_nome=-1;
		
		for(int i=0;i<num_servers;i++)
		 {
			 if(nome_logico_server.equals(table[i][0]))
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
		
		int index_tag = -1;
		for(int i = 0; i<TAGS.length; i++){
			if(TAGS[i].equals(tag)){
				index_tag = i;
				break;
			}
		}

		if(index_tag == -1)
		{
			System.out.println("Tag non valido");
			throw new RemoteException("Tag non valido");
		}
		
		tag_table[index_nome][index_tag]=true;
		return 1;
	}

	@Override
	public static void main(String[] args){
		
	}

}