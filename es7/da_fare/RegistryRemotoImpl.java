import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RegistryRemotoImpl extends UnicastRemoteObject implements RegistryRemotoServer {

    private static final long serialVersionUID = 1L;
    
    final int tableSize = 100; //Tabella: la prima colonna contiene inomi, la seconda i riferimenti remoti
    Object[][] table = new Object[100][2];
    
    // Costruttore
    public RegistryRemotoImpl() throws RemoteException {
        super();
        for (int i = 0; i < tableSize; i++) {
            table[i][0] = null;
            table[i][1] = null;
        }
    }

    public synchronized Remote cerca(String nomeLogico) throws RemoteException {
        Remote risultato = null;
        if (nomeLogico == null)
            return null;

        for (int i = 0; i < tableSize; i++) {
            if (nomeLogico.equals((String) table[i][0])) {
                risultato = (Remote) table[i][1];
                break;
            }
        }
        return risultato;
    }
    
    public synchronized Remote[] cercaTutti(String nomeLogico) throws RemoteException {
        int cont = 0;
        if (nomeLogico == null)
            return new Remote[0];

        for (int i = 0; i < tableSize; i++) {
            if (nomeLogico.equals((String) table[i][0]))
                cont++;
        }
        Remote[] risultato = new Remote[cont];
        // usato come indice per il riempimento
        cont = 0;
        for (int i = 0; i < tableSize; i++) {
            if (nomeLogico.equals((String) table[i][0]))
                risultato[cont++] = (Remote) table[i][1];
        }
        return risultato;
    }
    
    public synchronized Object[][] restituisciTutti() throws RemoteException {
        int cont = 0;
        for (int i = 0; i < tableSize; i++) {
            if (table[i][0] != null)
                cont++;
        }
        Object[][] risultato = new Object[cont][2];

        // usato come indice per il riempimento
        cont = 0;
        for (int i = 0; i < tableSize; i++) {
            if (table[i][0] != null) {
                risultato[cont][0] = table[i][0];
                risultato[cont][1] = table[i][1];
            }
        }

        return risultato;
    }
    
    public synchronized boolean aggiungi(String nomeLogico, Remote riferimento) throws RemoteException {
        boolean result = false;
        // Cerco la prima posizione libera e la riempio
        if ((nomeLogico == null) || (riferimento == null))
            return result;

        for (int i = 0; i < tableSize; i++) {
            if (table[i][0] == null) {
                table[i][0] = nomeLogico;
                table[i][1] = riferimento;
                result = true;
                break;
            }
        }
        return result;
    }
    
    public synchronized boolean eliminaPrimo(String nomeLogico) throws RemoteException {
        boolean risultato = false;
        if (nomeLogico == null)
            return risultato;

        for (int i = 0; i < tableSize; i++) {
            if (nomeLogico.equals((String) table[i][0])) {
                table[i][0] = null;
                table[i][1] = null;
                risultato = true;
                break;
            }
        }

        return risultato;
    }

    public synchronized boolean eliminaTutti(String nomeLogico) throws RemoteException {
        boolean risultato = false;
        if (nomeLogico == null)
            return risultato;

        for (int i = 0; i < tableSize; i++) {
            if (nomeLogico.equals((String) table[i][0])) {
                if (risultato == false)
                    risultato = true;
                table[i][0] = null;
                table[i][1] = null;
            }
        }
        return risultato;
    }
    
    public static void main (String[] args) {   
        int registryRemotoPort = 1099;
        String registryRemotoHost = "localhost";
        String registryRemotoName = "RegistryRemoto";
        if (args.length!= 0 && args.length!= 1){ //Controllo args
            System.out.println("Usage: RegistryRemotoImpl [registryRemotoPort]"); 
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
            RegistryRemotoImpl serverRMI = new RegistryRemotoImpl();
            Naming.rebind(completeName, serverRMI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
