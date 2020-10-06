package es0;
import java.io.*;
public class Consumatore {
    
    public static void main(String[] args)
    {
        FileReader r = null;
        // fare controllo argomenti
        if (args.length != 2){
            System.out.println("Utilizzo: consumatore <prefixstring> <inputFilename>");
            System.exit(0);
        }

        String stringa = args[0];

        try { r = new FileReader(args[1]); }
        catch(FileNotFoundException e)
            { System.out.println("File non trovato"); System.exit(1);}
        
        int flag = 0;

        try { 
            int x; char ch;
            while ((x = r.read()) >=0){   
                ch = (char) x;

                //filtro stringa
                


                System.out.print(ch);
            }
            r.close();
            }
        catch(IOException ex)
            {System.out.println("Errore di input"); System.exit(2);}
        
    }

}
