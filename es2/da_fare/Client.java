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
                System.out.println("Usage: PutFileClient addr port"); 
                System.exit(1); 
            }
        }catch(Exception e){e.printStackTrace();}
			
    
		Path dir = null;
		int soglia = 0;		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
        try 
        {
        	System.out.println("Inserisci soglia in bytes");
        	System.out.println("Inserisci path");
        	dir = Paths.get(in.readLine());
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
		    for (Path file: stream) {
		    	   	
		    	if(file.toFile().length()>soglia)
		    	{	
		    		System.out.println(file.getFileName());  //fai roba
		    		
		    		outSock.writeUTF(file.getFileName().toString());
		    		esito = inSock.readUTF();
		    		
		    		if(esito.contentEquals("attiva"))
		    		{
		    			outSock.writeLong(file.toFile().length());
		    			inFile = new FileInputStream(file.toFile());
		    			FileUtility.trasferisci_a_byte_file_binario(new DataInputStream(inFile), outSock);
		    		}

		    	}	 		
		    }
		    socket.close();
		    
		} catch (IOException e) {e.printStackTrace();;}
		
		
		  
	}
		
	
	
}
