import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PutFileServerCon {
    public static final int PORT = 1050;

    public static void main(String[] args) throws IOException{
        int port = -1;
        try {   //controllo argomenti 
            if (args.length== 1){
                port = Integer.parseInt(args[0]); 
            }
            else if (args.length== 0){
                port = PORT; 
            }
            else{ 
                System.out.println("Usage: PutFileServerCon [port]");  
                System.exit(1); 
            }
        } //try
        catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        ServerSocket serverSocket = null; 
        Socket clientSocket = null;
        try{ 
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
        }catch (Exception e) {
            e.printStackTrace(); 
            System.exit(1);
        }
        try{ // CICLO PRINCIPALE
            while(true){
                try{
                    clientSocket = serverSocket.accept();
                    System.out.println("[OK] Stabilita una connessione");
                    clientSocket.setSoTimeout(30000);
                } catch (Exception e) {
                    e.printStackTrace(); 
                    continue;
                }
                try { // servizio delegato ad un nuovo thread
                    PutFileServerThread figlio = new PutFileServerThread(clientSocket);
                    figlio.start();
                    System.out.println("[OK] Connessione affidata al processo figlio n. " + figlio.getId());
                    /* NOTA!!! La close della socket di connessione viene fatta dal FIGLIO, 
                    * il PADRE NON DEVE fare la close, 
                    * altrimenti si hanno interferenze perché c’è memoria condivisa 
                    */
                } catch (Exception e) {
                    e.printStackTrace(); 
                    continue;
                }
            } // while
        } // try
        catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
    } // main
} // PutFileServerCon