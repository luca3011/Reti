import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class LineClient {
    public static void main(String[] args) {
        InetAddress addr = null;
        int port = -1;
        try {
            if (args.length == 2) {
                addr = InetAddress.getByName(args[0]);
                port = Integer.parseInt(args[1]);
            } else {
                System.out.println("Usage: java LineClient addr port");
                System.exit(1);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DatagramSocket socket = null;
        DatagramPacket packet = null;
        byte[] buf = new byte[256];
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(30000);
            packet = new DatagramPacket(buf, buf.length, addr, port);
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(2);
        }
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        try { // strutture dati varie
            ByteArrayOutputStream boStream = null;
            DataOutputStream doStream = null;
            byte[] data = null;
            String nomeFile = null;
            int numLinea = -1;
            String richiesta = null;
            String risposta = null;
            ByteArrayInputStream biStream = null;
            DataInputStream diStream = null;
            System.out.print("\n^D(Unix)/^Z(Win) invio per uscire, altrimenti nome file: ");
            while ((nomeFile = stdIn.readLine()) != null) { //Filtro ben fatto
                try {
                    System.out.print("Numero linea? ");
                    numLinea = Integer.parseInt(stdIn.readLine());
                    richiesta = nomeFile + " " + numLinea;
                } catch (Exception e) {
                    System.out.println("Problemi nell'interazione da console: ");
                    e.printStackTrace();
                    continue;
                }
                try { // preparazione e spedizione richiesta
                    boStream = new ByteArrayOutputStream();
                    doStream = new DataOutputStream(boStream);
                    doStream.writeUTF(richiesta);
                    data = boStream.toByteArray();
                    packet.setData(data);
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
                try { // inizializzazione e ricezione pacchetto
                    packet.setData(buf);
                    socket.receive(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
                try {
                    biStream = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
                    diStream = new DataInputStream(biStream);
                    risposta = diStream.readUTF();
                    System.out.println("Risposta del server:\n" + risposta); // stampa risposta
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.print("\n^D(Unix)/^Z(Win) invio per uscire, altrimenti nome file: ");
            }   // while
        } // try
          
        catch (Exception e) {
            e.printStackTrace();
        }
        socket.close();
        System.out.println("LineClient:termino...");
    }
    
    
}

