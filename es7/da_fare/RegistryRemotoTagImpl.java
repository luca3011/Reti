import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class RegistryRemotoTagImpl extends RegistryRemotoImpl implements RegistryRemotoTagServer {

	final String[] TAGS = {"CONGRESSO","RIGHE","TAG3","TAG4","TAG5"};
    private boolean[][] tag_table; //Tabella: le colonne contengono i 5 tag
	
	public RegistryRemotoTagImpl() throws RemoteException {
		super();
		tag_table = new boolean[tableSize][TAGS.length];
		for(int i=0;i<tableSize;i++)
		{
			for(int j=0;j<TAGS.length;j++)
			{
				tag_table[i][j]=false;
			}
		}
	}

	
	public synchronized String[] cercaTag(String tag) throws RemoteException {
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
                risultato[cont++]=(String) table[i][0];
            }
        }

        return risultato;
    }

    public synchronized int associaTag(String nome_logico_server, String tag) 
    throws RemoteException {
		
		int index_nome=-1;
		
		for(int i=0;i<tableSize;i++)
		{
			if(nome_logico_server.equals(table[i][0]))
			{
				index_nome=i;
				break;
			}
		}
		if(index_nome == -1){
			System.out.println("Nome logico non trovato");
			throw new RemoteException("Nome logico non trovato");
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

	public static void main(String[] args){
		int registryRemotoPort = 1099;
        String registryRemotoHost = "localhost";
        String registryRemotoName = "RegistryRemotoTag";
        if (args.length!= 0 && args.length!= 1){ //Controllo args
            System.out.println("Usage: RegistryRemotoTagImpl [registryRemotoPort]"); 
            System.exit(1); 
        }
        if (args.length== 1) { 
            try {
                registryRemotoPort = Integer.parseInt(args[0]); 
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Registrazione RegistryRemoto presso rmiregistry locale
        String completeName = "//" + registryRemotoHost 
                            + ":" + registryRemotoPort 
                            + "/" + registryRemotoName;
        try{ 
            RegistryRemotoTagImpl serverRMI = new RegistryRemotoTagImpl();
            Naming.rebind(completeName, serverRMI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	

}