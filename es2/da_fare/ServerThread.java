import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerThread extends Thread {
    
    private Socket clientSocket;

    public ServerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    
    public void run() {
        DataInputStream inSock = null;
        DataOutputStream outSock = null;
        String nomeFile = null;
        try {
            try{
                inSock = new DataInputStream(clientSocket.getInputStream());
                outSock = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            try{
                while ((nomeFile = inSock.readUTF()) != null) { //TODO: verificare funzionamento (= che effettivamente si fermi solo alla chiusura dell'output da parte del client)
                    String risposta = null;
                    int length;
                    File file = new File(nomeFile);
                    FileOutputStream outFile = null;
                    if (file.createNewFile()) {
                        // restituisce true se un file con quel nome non era esistente ed è stato creato, 
                        // false se era esistente. Si noti che il metodo verifica e crea un file in modo atomico 
                        // ---> non c'è necessità di gestire conflitti tra i vari thread
                        outSock.writeUTF("attiva");
                        length = inSock.readInt();
                        outFile = new FileOutputStream(nomeFile);
                        FileUtility.trasferisci_a_byte_file_binario(inSock, new DataOutputStream(outFile), length);
                    } else {
                        outSock.writeUTF("salta");
                    }
                }
            
                clientSocket.close();
            } catch (SocketTimeoutException te) {
                te.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(3);
        }

    }
}
