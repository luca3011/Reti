package es0;
import java.io.*;
public class Consumatore {
    
    public static void main(String[] args)
    {
        FileReader r = null;
        // fare controllo argomenti
        if (args.length != 1){
            System.out.println("Utilizzo: produttore <inputFilename>");
            System.exit(0);
        }
        try { r = new FileReader(args[0]); }
        catch(FileNotFoundException e)
            { System.out.println("File non trovato"); System.exit(1);}
        
        try { 
            int x; char ch;
            while ((x = r.read()) >=0){   
                ch = (char) x;
                System.out.print(ch);
            }
            r.close();
            }
        catch(IOException ex)
            {System.out.println("Errore di input"); System.exit(2);}
    }

}
