import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class RowSwap extends Thread {
    
	int port;
	static String nomeFile;
	
    public RowSwap(int porta, String nomefile)
    {
    	this.port=porta;
    	RowSwap.nomeFile=nomefile;
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
    		
    		int result = ScambiaLinea(nomeFile, primaLinea, secondaLinea);
    		
    		try
    		{
    			ByteArrayOutputStream boStream = new ByteArrayOutputStream();
    			DataOutputStream doStream = new DataOutputStream(boStream);
    			doStream.writeInt(result);
    			byte[] data = boStream.toByteArray();
    			packet.setData(data); socket.send(packet);
    		}
    		catch(IOException e) {};
    		
    		socket.close();
    	}
  
    }

	static int ScambiaLinea(String nomeFile2, int primaLinea, int secondaLinea) {
		
		String linea;
		String primaStringa = null;
		String secondaStringa = null;
		
		
		if(primaLinea==secondaLinea)
		{
			System.out.println("Linee uguali, scambio non eseguito");
			return -1;
		}
		
		try {
			primaStringa = getLine(nomeFile2,primaLinea);
			secondaStringa = getLine(nomeFile2,primaLinea);
		} catch (IOException e) {e.printStackTrace(); return -1;}
		
		try 
		{
			BufferedWriter fileNuovo = new BufferedWriter(new FileWriter(nomeFile+"modificato")); 
			BufferedReader fileVecchio = new BufferedReader(new FileReader(nomeFile));
			while((linea = fileVecchio.readLine()) == null)
			{
				if(linea.equals(primaStringa))
				{
					fileNuovo.write(secondaStringa);
				}
				else if(linea.equals(secondaStringa))
				{
					fileNuovo.write(primaStringa);
				}
				else
				{
					fileNuovo.write(linea);
				}
			}
			
			fileNuovo.close();
			fileVecchio.close();
			
		}
		catch (IOException e){ e.printStackTrace(); return -1;}

		File filevecchio = new File(nomeFile);
		filevecchio.delete();
		File filenuovo = new File(nomeFile+"modificato");
		filenuovo.renameTo(filevecchio);
		
		
		return 1;
	}
	
	static String getLine(String nomeFile, int numLinea) throws IOException
	{ 
		String linea = null;
		BufferedReader in = null;
		try 
		{
			in = new BufferedReader(new FileReader(nomeFile)); 
		}
		catch (FileNotFoundException e){ e.printStackTrace(); return linea = "File non trovato"; }
		
		try
		{ 
			for (int i=1; i<=numLinea; i++)
			{ 
				linea = in.readLine();
				if ( linea == null)
				{ 
					linea = "Linea non trovata"; in.close(); return linea; 
				}
			}
		}
		catch (IOException e){ e.printStackTrace(); return linea = "Linea non trovata"; }
		in.close();
		return linea;
	} 
}
