import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

public class FileUtility {

    static protected void trasferisci_a_byte_file_binario(DataInputStream src, DataOutputStream dest)
            throws IOException {
        trasferisci_a_byte_file_binario(src, dest, Long.MAX_VALUE);
    }
    
    static protected void trasferisci_a_byte_file_binario(DataInputStream src, DataOutputStream dest, long length)
            throws IOException {
        // ciclo di lettura da sorgente e scrittura su destinazione
        int buffer = 0;
        try { // esco dal ciclo alla lettura di un valore negativo-> EOF oppure in seguito alla lettura di length caratteri
            for (long i = 0; i < length && (buffer = src.read()) >= 0; i++)
                dest.write(buffer);
            
            dest.flush();
        } catch (EOFException e) {
            System.out.println("Problemi:");
            e.printStackTrace();
        }
    }

}