import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Produttore{
    public static void main(String[] args){
        BufferedReader in = null;
        // Controllo argomenti
        if (args.length != 1){
            System.out.println("Utilizzo: produttore <filename>");
            System.exit(0);
        }
        in = new BufferedReader(new InputStreamReader(System.in));
        FileWriter fout;
        int read_char;
        try { fout = new FileWriter(args[0]);
            while ((read_char = in.read()) != -1){
                fout.write(read_char);
            }
            fout.close();
        }
        catch (NumberFormatException nfe) {nfe.printStackTrace(); System.exit(1);}
        catch (IOException e) {e.printStackTrace(); System.exit(2);}
    }
}
