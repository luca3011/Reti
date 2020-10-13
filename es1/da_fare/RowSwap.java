import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class RowSwap extends Thread {
    
	int port;
	String[] nomeFile;
	
    public RowSwap(int porta, String nomefile)
    {
    	this.port=porta;
    	this.nomeFile=nomefile;
    }

    public void run()
    {
    	byte[] buf = new byte[256];
    	DatagramSocket socket = null; DatagramPacket packet = null;
    	
    	try 
    	{
			socket = new DatagramSocket(port);
			packet = new DatagramPacket(buf,buf.length);
		} 
    	catch (SocketException e) {e.printStackTrace();}
    	
    	while(true)
    	{
    		String richiesta = null;
    		int primaLinea = 0;
    		int secondaLinea = 0;
    		
			try 
    		{ 
    			packet.setData(buf); 
    			socket.receive(packet); 
    		}
    		catch(IOException e){}

    		try
    		{
    			ByteArrayInputStream biStream = new ByteArrayInputStream(packet.getData(),0,packet.getLength());
    			DataInputStream diStream = new DataInputStream(biStream);
    			richiesta = diStream.readUTF();
    			StringTokenizer st = new StringTokenizer(richiesta);
    			primaLinea = Integer.parseInt(st.nextToken());
    			secondaLinea = Integer.parseInt(st.nextToken());
    		}
    		catch(IOException e) {};
    		
    		ScambiaLinea(nomeFile, primaLinea, secondaLinea);
    		
    		
    		
    		socket.close();
    	}
  
    }

	static int ScambiaLinea(String[] nomeFile2, int primaLinea, int secondaLinea) {
		
		if(primaLinea==secondaLinea)
		{
			System.out.println("Linee uguali, scambio non eseguito");
			return -1;
		}
		
		
		
		return 0;
	}
    
    

}
