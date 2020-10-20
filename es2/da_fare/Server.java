import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Server {
    public static final int PORT = 54321;

    public static void main(String[] args) {
        
        int port = -1;

        try {//controllo parametri
            if (args.length == 1)
                port = Integer.parseInt(args[0]);
            else if (args.length == 0)
                port = PORT;
            else {System.out.println("Usage: Server [port]");}
        }catch (Exception e) {System.out.println("errore nella lettura dei parametri");System.exit(-1);}
        
        ServerSocket serverSocket = null;
        try {//inizializzo socket
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
        }catch(Exception e) {System.out.println("Errore nell'inizializzazione della socket");System.exit(-2);}
        
        try {
            while(true) {
                Socket clientSocket = null;
                
                try {
                    clientSocket = serverSocket.accept();
                    clientSocket.setSoTimeout(10000);
                } catch (SocketTimeoutException e) {
                    System.out.println("Scattato il timeout");
                    e.printStackTrace();
                    continue;
                } catch (Exception e) {
                    System.out.println("Errore nell'accettazione della socket");
                    e.printStackTrace();
                    continue;
                }
                
                ServerThread ST1 = new ServerThread(clientSocket);
                ST1.start();
                System.out.println("[OK] Server: nuova connessione stabilita, affidata al thread n. " + ST1.getId());
            }    
        }catch(Exception e) {System.out.println("Errore nell'invio al figlio");System.exit(-3);}

    }
}