
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

        
        try { r = new FileReader(args[1]); }
        catch(FileNotFoundException e)
        { System.out.println("File non trovato"); System.exit(1);}
        
        String stringa = args[0];
        String appoggio = "";
        int flag = 0;
        char nextChar = stringa.charAt(0);
        int l = stringa.length();

        try { 
            int x; 
            char ch;
            while ((x = r.read()) >=0){   
                ch = (char) x;

                //filtro stringa
                if(ch==nextChar)
                {
                    flag++;
                    appoggio = appoggio + ch;
                    nextChar = stringa.charAt(flag);
                    while(((x=r.read())>=0) && ((char)x==nextChar) && (flag < l-1) )
                    {   
                        ch=(char) x;
                        flag++;
                        appoggio = appoggio+ch;
                        nextChar = stringa.charAt(flag);
                    }
                    appoggio="";
                    flag=0;
                    nextChar = stringa.charAt(0);
                } 
                else
                {
                    System.out.print(ch);
                }
            }
            r.close();
            }
        catch(IOException ex)
            {System.out.println("Errore di input"); System.exit(2);}
        
    }

}
