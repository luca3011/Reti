import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

class PutFileServerThread extends Thread
{
    private Socket clientSocket = null;

    public PutFileServerThread(Socket clientSocket){ 
        this.clientSocket = clientSocket; 
    }

public void run(){ // Processo figlio per trattare la connessione 
        DataInputStream inSock = null;
        DataOutputStream outSock = null;
        try{ 
            String nomeFile = null;
            try {   //creazione stream 
                inSock = new DataInputStream(clientSocket.getInputStream());
                outSock = new DataOutputStream(clientSocket.getOutputStream());
                nomeFile = inSock.readUTF();
            }catch (SocketTimeoutException te) {
                te.printStackTrace();
            }catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FileOutputStream outFile = null;
            String esito;// ricezione file: caso di errore
            if (nomeFile == null) {
                clientSocket.close();
                return; 
            }
            else { // controllo esistenza file
                File curFile = new File(nomeFile);
                if(curFile.exists()) {
                    try{    //distruggo il vecchio file
                        esito = "File sovrascritto"; 
                        curFile.delete();
                    }catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                } 
                else 
                    esito = "Creato nuovo file";
                outFile = new FileOutputStream(nomeFile);
            }
            try {
                FileUtility.trasferisci_a_byte_file_binario(inSock, new DataOutputStream(outFile));
                outFile.close();// chiusurafile e socket// NOTA: è il figlio che fa la close!
                clientSocket.shutdownInput();
                outSock.writeUTF(esito + ", file salvato lato server");
                System.out.println("[OK] Thread n. " + this.getId() + ": file ricevuto e salvato su server");
                clientSocket.shutdownOutput();
                clientSocket.close();
            }catch (SocketTimeoutException te) {
                te.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace(); 
            System.exit(3);
        }
    } // run
} // PutFileServerThread