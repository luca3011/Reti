import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class PutFileServerSeq {
    public static final int PORT = 54321; // porta di default

    public static void main(String[] args) throws IOException{
        int port = -1; 
        String nomeFile; 
        FileOutputStream outFile = null;
        String esito; 
        try{ 
            if (args.length == 1) {
                port = Integer.parseInt(args[0]);
            } else if (args.length == 0) {
                port = PORT;
            } else { 
                System.out.println("Usage: PutFileServerSeq [port]");
                System.exit(1);
            }
        } //try
        catch (Exception e) {
            e.printStackTrace();
        }
        ServerSocket serverSocket = null; // preparazione socket e in/out stream
        try{
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
        }catch (Exception e) {
            e.printStackTrace();
        }try{ 
            while (true){ //ciclo infinito del server
                Socket clientSocket = null;
                DataInputStream inSock = null;  
                DataOutputStream outSock = null;
                try{ 
                    clientSocket = serverSocket.accept();
                    System.out.println("[OK] Stabilita una connessione");
                    clientSocket.setSoTimeout(30000);
                }catch (Exception e) {
                    e.printStackTrace(); 
                    continue;
                }
                try{ //creazione stream di I/O
                    inSock = new DataInputStream(clientSocket.getInputStream());
                    outSock = new DataOutputStream(clientSocket.getOutputStream());
                    nomeFile = inSock.readUTF();
                }catch (SocketTimeoutException te) {
                    te.printStackTrace(); 
                    continue;
                }catch (IOException e) {
                    e.printStackTrace(); 
                    continue;
                }// ricezione file su file nuovo
                if(nomeFile == null) { 
                    clientSocket.close(); 
                    continue;
                } else {
                    File curFile = new File(nomeFile);
                    if(curFile.exists()) {
                        try{ 
                            esito = "File sovrascritto";
                            curFile.delete();// distruggo il file
                        }catch (Exception e) {
                            e.printStackTrace(); 
                            continue;
                        }
                    } 
                    else 
                        esito = "Creato nuovo file";
                    outFile= new FileOutputStream(nomeFile);
                }
                try{  //ricezione file
                    FileUtility.trasferisci_a_byte_file_binario(inSock, new DataOutputStream(outFile)); // N.B. la funzione consuma l’EOF
                    outFile.close();// chiusura file
                    clientSocket.shutdownInput();
                    outSock.writeUTF(esito + ", file salvato su server");
                    System.out.println("[OK] File ricevuto e salvato su server");
                    clientSocket.shutdownOutput();
                    clientSocket.close();
                }catch (SocketTimeoutException te) {
                    te.printStackTrace(); 
                    continue;
                }catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            } 
        }catch (Exception e) {
            e.printStackTrace();
            System.exit(3);
        }
    }
}