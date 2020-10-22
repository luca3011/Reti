import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

public class FileUtility {

    private static final int BUFFER_SIZE = 8192;
    
    static protected void trasferisci_a_byte_file_binario(DataInputStream src, DataOutputStream dest, long length)
            throws IOException {

        byte[] buffer = new byte[BUFFER_SIZE];
        int dyn_buffer_size = length < BUFFER_SIZE ? (int) length : BUFFER_SIZE; //dimensione del buffer
        try {
            long nletti = 0; // numero di byte letti
            // esco dal ciclo alla lettura di un valore negativo-> EOF oppure in seguito alla lettura di length byte
            while (nletti < length && src.read(buffer, 0, dyn_buffer_size) >= 0) {
                dest.write(buffer, 0, dyn_buffer_size);
                nletti += dyn_buffer_size;
                if (length - nletti < BUFFER_SIZE)
                    dyn_buffer_size = (int) (length - nletti);
            }

            dest.flush();
        } catch (EOFException e) {
            System.out.println("Problemi:");
            e.printStackTrace();
        }
    }

}