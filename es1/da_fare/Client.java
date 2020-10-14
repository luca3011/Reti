import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {
    		
	public static void main(final String[] args)
    {
		InetAddress addr=null;
		int port=-1, numLinea1=-1, numLinea2=-1;
		String fileName = null, richiesta = null, numBuf = null;
		int result = -1; 
		byte[] buf=new byte[256];
		
		DatagramSocket socket=null;
		DatagramPacket packet=null;
		ByteArrayOutputStream boStream=null;
		DataOutputStream doStream=null;
		ByteArrayInputStream biStream=null;
		DataInputStream diStream=null;
		
		try {
			if (args.length == 3)
			{
<<<<<<< HEAD
				addr = InetAddress.getByName(args[0]);
				port = Integer.parseInt(args[1]);
=======
				addr = InetAddress.getByName (args[0]);
				port = Integer.parseInt (args[1]);
>>>>>>> 84c9de46fbac7bf3d02399b38d1fa333c072705e
				fileName=args[2];
			}
		}catch(UnknownHostException e) {System.out.println("errore con gli argomenti");e.printStackTrace();System.exit(-1);}
		
        try 
        {
        	socket = new DatagramSocket();
			socket.setSoTimeout(30000);
			packet = new DatagramPacket(buf, buf.length, addr, port);
		}catch (SocketException e) {System.out.println("errore con il datagram");e.printStackTrace();System.exit(-2);}
        
        richiesta=fileName;
        
        try {
        	boStream = new ByteArrayOutputStream();
        	doStream = new DataOutputStream(boStream);
        	doStream.writeUTF(richiesta);
        	byte[] data = boStream.toByteArray();
        
        	packet.setData(data);
        	socket.send(packet);
        }catch (IOException e){System.out.println("errore nell'invio al DS");e.printStackTrace();System.exit(-3);}
        
        try {
        	packet.setData(buf);
        	socket.receive(packet);
        }catch(IOException e) {System.out.println("errore nella ricevuta dal DS");e.printStackTrace();System.exit(-4);}
        
        try{
        	biStream = new ByteArrayInputStream( packet.getData(),0,packet.getLength());
        	diStream = new DataInputStream(biStream);
        	port = Integer.parseInt(diStream.readUTF()); 
        	System.out.println("Porta ricevuta: " + port);
        }catch (IOException e){ System.out.println("errore nella traduzione dal DS");e.printStackTrace();System.exit(-5);}
        
        System.out.println("Connessione avvenuta, inserire il numero delle righe:");
		
		while ((numBuf=System.console().readLine())!= null)
        { 
        	try
			{
				numLinea1 = Integer.parseInt(numBuf);		//lettura numeri righe da console
        		numLinea2 = Integer.parseInt(System.console().readLine());
        		richiesta = numLinea1+" "+numLinea2;
        	}catch (Exception e) {
        		System.out.println("Problemi nell'interazione da console: ");
        		e.printStackTrace();
<<<<<<< HEAD
				continue;}
=======
				continue;
			}
>>>>>>> 84c9de46fbac7bf3d02399b38d1fa333c072705e
        	
        	try {
                socket = new DatagramSocket();
        		socket.setSoTimeout(30000);
        		packet = new DatagramPacket(buf, buf.length, addr, port);
<<<<<<< HEAD
        	}catch (SocketException e) {System.out.println("errore con il datagram2");e.printStackTrace();System.exit(-6);}
=======
        	} 
            catch (SocketException e) {System.out.println("errore con il datagram2");e.printStackTrace();System.exit(-6);}
>>>>>>> 84c9de46fbac7bf3d02399b38d1fa333c072705e
                
           	try {
                boStream = new ByteArrayOutputStream();
                doStream = new DataOutputStream(boStream);
                doStream.writeUTF(richiesta);
            	byte[] data = boStream.toByteArray();
                
                packet.setData(data);
                socket.send(packet);
            }catch (IOException e){System.out.println("errore nell'invio al RS");e.printStackTrace();System.exit(-7);}
                
            try {
<<<<<<< HEAD
                packet.setData(buf);s
=======
                packet.setData(buf);
>>>>>>> 84c9de46fbac7bf3d02399b38d1fa333c072705e
                socket.receive(packet);
            }catch(IOException e) {System.out.println("errore nella ricevuta dal RS");e.printStackTrace();System.exit(-8);}
                
            try{
                biStream = new ByteArrayInputStream( packet.getData(),0,packet.getLength());
                diStream = new DataInputStream(biStream);
                result = diStream.readInt(); 
            }catch (IOException e){ System.out.println("errore nella traduzione dal RS");e.printStackTrace();System.exit(-9);}
                
			switch (result) {
				case 1:
					System.out.println("tutto bene");
					break;
<<<<<<< HEAD
				case -1:
					System.out.println("errore nella scrittura su file");
=======
				case 2:
					System.out.println("errore 1");
					break;
				case 3:
					System.out.println("errore 2");
>>>>>>> 84c9de46fbac7bf3d02399b38d1fa333c072705e
					break;
				default:
					System.out.println("You should not read this");
					break;
			}
        	System.out.print("\\n^D(Unix)/^Z(Win) invio per uscire, altrimenti inserisci i numeri delle righe(con invio tra l'uno e 'altro)");
        }	// while
        socket.close();
	}
}
