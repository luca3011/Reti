import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

public class DiscoveryServer {

    public static void main(String[] args)
    {
        DatagramSocket socket = null;
        DatagramPacket packet = null;
        byte[] buf = new byte[256];
        int discoveryServerPort;
        
        if(args.length % 2 == 0){    //non torna il numero di argomenti
                System.out.println("Usage: DiscoveryServer port {file1 port1 ... fileN portN}");
                System.exit(1);
        }
        
        try{
            discoveryServerPort = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Usage: DiscoveryServer port {file1 port1 ... fileN portN}");
            System.exit(2);
        }

        HashMap portMap = new HashMap<String, Integer>();
        for (int i = 1; i < ((args.length)/2)+1; i++) {     // una iterazione per ogni coppia file-porta
            int rowSwapPort = args[i + 1];
            if (portMap.containsValue(rowSwapPort)) {
                System.out.println("Errore: inserita una o più porte duplicate");
                System.exit(3);
            }
            RowSwap rowSwap = new RowSwap(rowSwapPort, args[i]);
            portMap.put(args[i], rowSwapPort);
            rowSwap.start();
        }
        
        try{
            socket = new DatagramSocket(discoveryServerPort);
            packet = new DatagramPacket(buf, buf.length);
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(4);
        }
        try{
            String richiesta = null;
            String portaRisposta = null;
            ByteArrayInputStream biStream = null;
            DataInputStream diStream = null;
            ByteArrayOutputStream boStream = null;
            DataOutputStream doStream = null;
            byte[] data = null;
            while (true) {
                try{
                    packet.setData(buf);
                    socket.receive(packet);
                }
                catch (SocketException e) {
                    e.printStackTrace();
                    continue;
                }
                try{
                    biStream = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
                    diStream = new DataInputStream(biStream);
                    richiesta = diStream.readUTF();
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                try{
                    boStream = new ByteArrayOutputStream();
                    doStream = new DataOutputStream(boStream);
                    portaRisposta = portMap.get(richiesta); //attenzione: può restituire null
                    if (portaRisposta == null) {
                        System.out.println("Errore: file richiesto non disponibile");
                        System.exit(5);
                    }
                    doStream.writeUTF(portaRisposta);
                    data = boStream.toByteArray();
                    packet.setData(data);
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("DiscoveryServer: termino...");
        socket.close();
    }
    
}
