import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class PutFileClient {
    public static void main(String[] args){
        InetAddress addr = null;
        int port = -1;
        try{  // controllo argomenti
            if(args.length == 2){ 
                addr = InetAddress.getByName(args[0]);
                port = Integer.parseInt(args[1]);
            } 
            else{ 
                System.out.println("Usage: PutFileClient addr port"); 
                System.exit(1); 
            }
        } //try
        catch(Exception e){ 
            e.printStackTrace();
        }// oggetti per comunicazione e lettura file
        Socket socket = null; 
        String esito;
        FileInputStream inFile = null; 
        String nomeFile = null;
        DataInputStream inSock = null; 
        DataOutputStream outSock = null;
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("\n^D(Unix)/^Z(Win)+invio.... Nome file?");
        try{
            while ((nomeFile=stdIn.readLine())!=null){
                if(new File(nomeFile).exists()){
                    try{  // creazione socket
                        socket = new Socket(addr, port);
                        socket.setSoTimeout(30000);
                        inSock = new DataInputStream(socket.getInputStream());
                        outSock = new DataOutputStream(socket.getOutputStream());
                    } catch(Exception e){
                        e.printStackTrace(); 
                        continue;
                    }
                }
                else{
                    System.out.println("File non presente");
                    System.out.print("\n^D(Unix)/^Z(Win)...");
                    continue;
                }// Invio file
                try{ 
                    inFile = new FileInputStream(nomeFile);
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }
                try{ 
                    outSock.writeUTF(nomeFile);
                    FileUtility.trasferisci_a_byte_file_binario(new DataInputStream(inFile), outSock);
                    inFile.close();   // chiusura della socket e del file
                    socket.shutdownOutput();    // chiudo in upstream, cioe' invio EOF
                }  catch (SocketTimeoutException te) {
                    te.printStackTrace(); 
                    continue;
                }catch(Exception e){
                    e.printStackTrace(); 
                    continue;
                }try{ // ricezione esito
                    esito = inSock.readUTF();
                    System.out.println("Esito dell'operazione: " + esito);
                    socket.shutdownInput(); // chiudo la socket in downstream e del tutto
                    socket.close();
                }catch (SocketTimeoutException te) {
                    te.printStackTrace(); 
                    continue;
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                System.out.print("\n^D(Unix)/^Z(Win)..."); // nuova richiesta
            } // while
        } // try
        catch(Exception e){
            e.printStackTrace();
            System.exit(3);
        }
    } // main
} // class