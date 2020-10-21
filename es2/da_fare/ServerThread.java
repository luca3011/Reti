import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
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
            FileOutputStream outFile = null;
            try {
                while (true) { //per verificare la chiusura della connessione si gestisce la EOFException (probabilmente possibili altre soluzioni)
                    nomeFile = inSock.readUTF();
                    String risposta = null;
                    long length;
                    if (nomeFile != null && nomeFile != "") {
                        File file = new File(nomeFile);
                        System.out.println("nomefile: " + nomeFile);
                        if (file.createNewFile()) {
                            // restituisce true se un file con quel nome non era esistente ed è stato creato, 
                            // false se era esistente. Si noti che il metodo verifica e crea un file in modo atomico 
                            // ---> non c'è necessità di gestire conflitti tra i vari thread (?)
                            try {
                                outSock.writeUTF("attiva");
                                length = inSock.readLong();
                                outFile = new FileOutputStream(nomeFile);
                                FileUtility.trasferisci_a_byte_file_binario(inSock, new DataOutputStream(outFile),
                                        length);
                            } catch (SocketTimeoutException te) {
                                te.printStackTrace();
                                outFile.close();
                            }
                        } else {
                            outSock.writeUTF("salta");
                        }
                    }
                }

            } catch (EOFException eofe) {   //lanciata da readUTF, segnala la chiusura della connessione da parte del client
                System.out.println(
                        "[OK] ServerThread n. " + this.getId() + ": connessione chiusa dal client. Termino...");
                clientSocket.close();
                System.exit(0);
            } catch (SocketException e) {
                outFile.close();
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(3);
        }

    }
}
