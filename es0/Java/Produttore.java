
import java.io.*;
public class Produttore{
    public static void main(final String[] args)
    { 
        BufferedReader in = null; int res = 0;
        // fare controllo argomenti
        if (args.length != 1){
            System.out.println("Utilizzo: produttore <inputFilename>");
            System.exit(0);
            }
        in = new BufferedReader(new InputStreamReader(System.in));
    
    FileWriter fout;
    try { fout = new FileWriter(args[0]);
        System.out.println("Quante righe vuoi inserire?");
        res = Integer.parseInt(in.readLine());
        for (int i =0; i<res; i++)
        { 
            System.out.println("Inserisci la nuova riga");
            final String inputl = in.readLine()+"\n";
            fout.write(inputl, 0, inputl.length());
        }
        fout.close();
    }
    catch (final NumberFormatException nfe) {nfe.printStackTrace(); System.exit(1);}
    catch (final IOException e) {e.printStackTrace(); System.exit(2);}
    }
}
