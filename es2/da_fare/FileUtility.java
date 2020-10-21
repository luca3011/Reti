import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

public class FileUtility {

    private static final int BUFFER_SIZE = 8192;

    static protected void trasferisci_a_byte_file_binario(DataInputStream src, DataOutputStream dest)
            throws IOException {
        trasferisci_a_byte_file_binario(src, dest, Long.MAX_VALUE);
    }
    
    static protected void trasferisci_a_byte_file_binario(DataInputStream src, DataOutputStream dest, long length)
            throws IOException {
        // ciclo di lettura da sorgente e scrittura su destinazione
        byte[] buffer = new byte[BUFFER_SIZE];
        int size = length < BUFFER_SIZE? (int)length : BUFFER_SIZE;
        try { // esco dal ciclo alla lettura di un valore negativo-> EOF oppure in seguito alla lettura di length caratteri
            for (long i = 0; i < length && src.read(buffer, 0, size) >= 0; i += size) {
                System.out.println("size: " + size);
                dest.write(buffer, 0, size);
                if(length - i < BUFFER_SIZE)
                    size = (int)(length - i);
            }
            dest.flush();
        } catch (EOFException e) {
            System.out.println("Problemi:");
            e.printStackTrace();
        }
    }

}