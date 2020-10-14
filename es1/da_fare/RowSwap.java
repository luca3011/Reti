import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class RowSwap extends Thread {
    
	private int port;
	private String nomeFile;
	private int id;
	
    public RowSwap(int porta, String nomefile, int id)
    {
    	this.port=porta;
		this.nomeFile = nomefile;
		this.id = id;
    }

    public void run()
    {
    	byte[] buf = new byte[256];
    	DatagramSocket socket = null; DatagramPacket packet = null;
    	
    	try {
			socket = new DatagramSocket(port);
			packet = new DatagramPacket(buf,buf.length);
		} 
		catch (SocketException e) {
			e.printStackTrace();
		}
		
		try{
			while(true){
				String richiesta = null;
				int primaLinea = 0;
				int secondaLinea = 0;
				
				try 
				{ 
					packet.setData(buf); 
					socket.receive(packet);
				}
				catch(IOException e){e.printStackTrace();}

				try
				{
					ByteArrayInputStream biStream = new ByteArrayInputStream(packet.getData(),0,packet.getLength());
					DataInputStream diStream = new DataInputStream(biStream);
					richiesta = diStream.readUTF();
					StringTokenizer st = new StringTokenizer(richiesta);
					primaLinea = Integer.parseInt(st.nextToken());
					secondaLinea = Integer.parseInt(st.nextToken());
					System.out.println("RowSwap" + id + ": ricevute linee " + primaLinea + " e " + secondaLinea
								+ " file " + nomeFile);
				}
				catch(IOException e) {
					e.printStackTrace();};
				
				int result = ScambiaLinea(primaLinea, secondaLinea);
				
				try
				{
					ByteArrayOutputStream boStream = new ByteArrayOutputStream();
					DataOutputStream doStream = new DataOutputStream(boStream);
					doStream.writeInt(result);
					byte[] data = boStream.toByteArray();
					packet.setData(data); socket.send(packet);
				}
				catch(IOException e) {
					e.printStackTrace();};
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		socket.close();
    }

	private int ScambiaLinea(int primaLinea, int secondaLinea) {
		
		String linea;
		String primaStringa = null;
		String secondaStringa = null;
		
		
		if(primaLinea==secondaLinea)
		{
			System.out.println("Linee uguali, scambio non eseguito");
			return -1;
		}
		
		try {
			primaStringa = getLine(primaLinea);
			secondaStringa = getLine(secondaLinea);
		} catch (IOException e) {e.printStackTrace(); return -1;}
		
		try 
		{
			BufferedWriter fileNuovo = new BufferedWriter(new FileWriter(nomeFile+"modificato")); 
			BufferedReader fileVecchio = new BufferedReader(new FileReader(nomeFile));
			while((linea = fileVecchio.readLine()) != null)
			{
				if(linea.equals(primaStringa))
				{
					fileNuovo.write(secondaStringa + "\n");
				}
				else if(linea.equals(secondaStringa))
				{
					fileNuovo.write(primaStringa + "\n");
				}
				else
				{
					fileNuovo.write(linea + "\n");
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
	
	private String getLine(int numLinea) throws IOException
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
