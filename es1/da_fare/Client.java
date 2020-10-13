import java.net.DatagramSocket;
import java.net.SocketException;

public class Client {
    		
	public static void main(final String[] args)
    {
        String IP = args[0];
        String fileName = args[1];
        
        DatagramSocket socket;
		
        try 
        {
			socket = new DatagramSocket();
			socket.setSoTimeout(30000);
		} 
        catch (SocketException e) {e.printStackTrace();}
        

        
    }
	
}
