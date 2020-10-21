import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;



public class Client {

	public static void main(String[] args)
	{
	
		InetAddress addr = null;
        int port = -1;
        
        try
        {  
            if(args.length == 2)
            { 
                addr = InetAddress.getByName(args[0]);
                port = Integer.parseInt(args[1]);
            } 
            else
            { 
                System.out.println("Usage: Client addr port"); 
                System.exit(1); 
            }
        }catch(Exception e){e.printStackTrace();}
			
    
		Path dir = null;
		int soglia = 0;		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
        try 
        {
        	System.out.println("Inserisci path della directory:");
            dir = Paths.get(in.readLine());
            System.out.println("Inserisci soglia minima in bytes:");
            soglia = Integer.parseInt(in.readLine());
		} 
        catch (NumberFormatException | IOException e1) {e1.printStackTrace();}
        

        Socket socket = null;
        DataInputStream inSock = null; 
        DataOutputStream outSock = null;
        String esito = "";
        
        
        try
        {  
        	// creazione socket
            socket = new Socket(addr, port);
            socket.setSoTimeout(30000);
            inSock = new DataInputStream(socket.getInputStream());
            outSock = new DataOutputStream(socket.getOutputStream());
        }catch(Exception e){e.printStackTrace();}
        
        FileInputStream inFile = null;
        

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path file : stream) {

                if (Files.isRegularFile(file) && file.toFile().length() > soglia) {
                    System.out.println("nomefile: " + file.getFileName()); //fai roba

                    outSock.writeUTF(file.getFileName().toString());
                    esito = inSock.readUTF();

                    if (esito.contentEquals("attiva")) {
                        long fileLength = file.toFile().length();
                        outSock.writeLong(fileLength);
                        inFile = new FileInputStream(file.toFile());
                        long start = System.currentTimeMillis();
                        FileUtility.trasferisci_a_byte_file_binario(new DataInputStream(inFile), outSock, fileLength);
                        System.out.println(
                                "tempo di trasferimento: " + (System.currentTimeMillis() - start) + " millisecondi");
                    }

                }

            }

            System.out.println("Client: termino...");
		    socket.close();
		    
		} catch (IOException e) {e.printStackTrace();}
		
		
		  
	}
		
	
	
}
