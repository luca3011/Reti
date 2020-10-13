import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.StringTokenizer;

public class LineServer {
    private static final int PORT = 4445;

    public static void main(String[] args) {
        DatagramSocket socket = null; 
        DatagramPacket packet = null;
        int port = -1;
        byte[] buf = new byte[256];  // Controllo argomenti input: 0 oppure 1 argomento(porta)
        if ((args.length== 0)) { 
            port = PORT; 
        }else if (args.length== 1) {
            try {
                port = Integer.parseInt(args[0]);
                // controllo argomento e che la porta sia nel range consentito 1024-65535
                if (port < 1024 || port > 65535) {
                    System.out.println("Usage: java LineServer [serverPort>1024]");
                    System.exit(1);
                }
            } catch (NumberFormatException e) {
                 e.printStackTrace();
            }
        } else {
            System.out.println("Usage: java LineServer [serverPort>1024]");  
            System.exit(1); 
        }
        try{
            socket = new DatagramSocket(port);
            packet = new DatagramPacket(buf, buf.length);
        } catch (SocketException e) {
            e.printStackTrace();
        }try { 
            String nomeFile = null;
            int numLinea = -1;
            String richiesta = null;
            ByteArrayInputStream biStream = null;
            DataInputStream diStream = null;
            StringTokenizer st = null;
            ByteArrayOutputStream boStream = null;
            DataOutputStream doStream = null;
            String linea = null;
            byte[] data = null;
            while (true){
                try {
                    packet.setData(buf); 
                    socket.receive(packet); 
                }catch(IOException e){
                    e.printStackTrace();
                    continue;
                }
                try{ 
                    biStream = new ByteArrayInputStream(packet.getData(),0 ,packet.getLength());
                    diStream =new DataInputStream(biStream);
                    richiesta = diStream.readUTF();
                    st = new StringTokenizer(richiesta);
                    nomeFile = st.nextToken();
                    numLinea = Integer.parseInt(st.nextToken());
                } catch(Exception e){
                    e.printStackTrace();
                    continue;
                }try{
                    linea = LineUtility.getLine(nomeFile, numLinea);
                    boStream= new ByteArrayOutputStream();
                    doStream= new DataOutputStream(boStream);
                    doStream.writeUTF(linea);
                    data = boStream.toByteArray();
                    packet.setData(data); 
                    socket.send(packet);
                } catch(IOException e){
                    e.printStackTrace();
                    continue;
                }
            }  // while 
        } catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("LineServer: termino..");
        socket.close();
    } // main 
} // class 

